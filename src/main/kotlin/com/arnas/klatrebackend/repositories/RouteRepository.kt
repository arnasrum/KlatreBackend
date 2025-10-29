package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.Route
import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface
import com.arnas.klatrebackend.util.toSnakeCase
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import kotlin.reflect.full.memberProperties

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
                gradeId = rs.getLong("grade_id"),
                placeId = rs.getLong("place_id"),
                active = rs.getBoolean("active"),
                imageId = rs.getString("image_id")
            )
        }
        return boulder.firstOrNull()
    }

    override fun addRoute(routeDTO: RouteDTO, imageId: String?, userId: Long): Long {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        val sql = """
            INSERT INTO routes (name, grade_id, place_id, description, image_id, user_id)
            VALUES (:name, :grade, :place, :description, :imageId, :userId)
        """

        val parameters = MapSqlParameterSource()
            .addValue("name", routeDTO.name)
            .addValue("grade", routeDTO.gradeId)
            .addValue("place", routeDTO.placeId)
            .addValue("description", routeDTO.description)
            .addValue("imageId", imageId)
            .addValue("userId", userId)

        jdbcTemplate.update(sql, parameters, keyHolder)

        val keys = keyHolder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    override fun updateRoute(route: Route): Int {

        val updates = mutableListOf<String>()
        val parameters = MapSqlParameterSource()
            .addValue("routeId", route.id)

        Route::class.memberProperties.forEach { prop ->
            if(prop.getter.call(route) == null || prop.name == "id") return@forEach
            updates.add("${prop.name.toSnakeCase()} = :${prop.name}")
            parameters.addValue(prop.name, prop.getter.call(route))
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

    override fun getRoutesByPlaceId(placeId: Long): List<Route> {
        val sql = "SELECT * FROM routes WHERE place_id=:placeId"
        val route = jdbcTemplate.query(sql, MapSqlParameterSource().addValue("placeId", placeId)) { rs, _ ->
            Route(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                gradeId = rs.getLong("grade_id"),
                placeId = rs.getLong("place_id"),
                active = rs.getBoolean("active"),
                imageId = rs.getString("image_id"),
            )
        }
        return route
    }

    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route> {

        val routes: MutableList<Route> = mutableListOf()
        val sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place_id, b.active AS active, b.image_id AS image_id " +
                "FROM routes AS b INNER JOIN places AS p ON b.place_id = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place_id=:placeId AND g.id = b.grade_id " +
                "ORDER BY b.date_added DESC"
        if(pagingEnabled) sql.plus(" LIMIT $limit OFFSET ${(page-1)*limit}")

        jdbcTemplate.query(
            sql,
            MapSqlParameterSource()
                .addValue("placeId", placeId)
        ) { rs, _ ->
            routes.add(
                Route(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description"),
                    gradeId = rs.getLong("gId"),
                    placeId = rs.getLong("place_id"),
                    active = rs.getBoolean("active"),
                    imageId = rs.getString("image_id")
                )
            )
        }
        return routes
    }

    override fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int {
        val sql = "SELECT COUNT(*) FROM routes WHERE place_id=:placeId AND active=:active"
        val result = jdbcTemplate.queryForObject(
            sql,
            MapSqlParameterSource()
                .addValue("placeId", placeId)
                .addValue("active", countActive)
        ) { rs, _ ->
            rs.getInt(1)
        }
        return result?: 0
    }
}