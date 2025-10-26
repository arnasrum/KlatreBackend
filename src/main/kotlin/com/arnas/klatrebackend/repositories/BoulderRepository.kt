package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Route
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder

@Repository
class RouteRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
): RouteRepositoryInterface {


    override fun getRouteById(routeId: Long): Route? {
        val boulder = jdbcTemplate.query("SELECT * FROM routes WHERE id = :routeId",
            mapOf("routeId" to routeId)
        ) { rs, _ ->
            Route(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                gradeId = rs.getLong("grade"),
                placeId = rs.getLong("place"),
                active = rs.getBoolean("active"),
                image = rs.getString("image_id")
            )
        }
        return boulder.firstOrNull()
    }

    override fun addRoute(name: String, grade: Long, place: Long, description: String?, active: Boolean?, imageUrl: String?, userId: Long): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        val sql = """
            INSERT INTO routes (name, grade, place, description, image_id, userId)
            VALUES (:name, :grade, :place, :description, :imageId, :userId)
        """

        val parameters = MapSqlParameterSource()
            .addValue("name", name)
            .addValue("grade", grade)
            .addValue("place", place)
            .addValue("description", description)
            .addValue("imageId", imageUrl)
            .addValue("userId", userId)

        jdbcTemplate.update(sql, parameters, keyHolder)

        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    override fun updateRoute(routeId: Long, name: String?, grade: Long?, place: Long?, description: String?, active: Boolean?, imageId: String?): Int {

        val updates = mutableListOf<String>()
        val parameters = mutableMapOf<String, Any>("routeId" to routeId)

        if (!name.isNullOrEmpty()) {
            updates.add("name = :name")
            parameters["name"] = name
        }
        if (grade != null) {
            updates.add("grade = :grade")
            parameters["grade"] = grade
        }
        if (place != null) {
            updates.add("place = :place")
            parameters["place"] = place
        }
        if (imageId != null) {
            updates.add("image_id = :imageId")
            parameters["imageId"] = imageId
        }
        if (!description.isNullOrEmpty()) {
            updates.add("description = :description")
            parameters["description"] = description
        }
        if (active != null) {
            updates.add("active = :active")
            parameters["active"] = active
        }
        if (updates.isEmpty()) {
            return 0
        }

        val sql = "UPDATE routes SET ${updates.joinToString(", ")} WHERE id = :routeId"
        val rowsAffected = jdbcTemplate.update(sql, parameters)
        return rowsAffected
    }

    override fun deleteRoute(routeId: Long): Int {
        val rowAffected = jdbcTemplate.update("DELETE FROM routes WHERE id=:routeId",
            MapSqlParameterSource()
                .addValue("routeId", routeId)
        )
        return rowAffected
    }


    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route> {

        val routes: MutableList<Route> = mutableListOf()
        val sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place, b.active AS active, b.image_id AS image_id " +
                "FROM routes AS b INNER JOIN places AS p ON b.place = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place=:placeID AND g.id = b.grade " +
                "ORDER BY b.date_added DESC"
        if(pagingEnabled) sql.plus(" LIMIT $limit OFFSET ${(page-1)*limit}")

        jdbcTemplate.query(
            sql,
            MapSqlParameterSource()
                .addValue("placeID", placeId)
        ) { rs, _ ->
            routes.add(
                Route(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    gradeId = rs.getLong("gId"),
                    placeId = rs.getLong("place"),
                    active = rs.getBoolean("active"),
                    image = rs.getString("image_id")
                )
            )
        }
        return routes
    }

    override fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int {
        val sql = "SELECT COUNT(*) FROM routes WHERE place=:placeID AND active=:active"
        val result = jdbcTemplate.queryForObject(
            sql,
            MapSqlParameterSource()
                .addValue("placeID", placeId)
                .addValue("active", countActive)
        ) { rs, _ ->
            rs.getInt(1)
        }
        return result?: 0
    }
}