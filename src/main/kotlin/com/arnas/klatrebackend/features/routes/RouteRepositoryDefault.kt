package com.arnas.klatrebackend.features.routes

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class RouteRepositoryDefault(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : RouteRepository {

    override fun getRouteById(routeId: Long): Optional<Route> {
        val route = jdbcTemplate.query(
            "SELECT * FROM routes WHERE id = :routeId",
            MapSqlParameterSource().addValue("routeId", routeId)
        ) { rs, _ ->
            Route(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                gradeId = rs.getLong("grade_id"),
                placeId = rs.getLong("place_id"),
                description = rs.getString("description"),
                active = rs.getBoolean("active"),
                imageId = rs.getString("image_id")
            )
        }.firstOrNull()
        return Optional.ofNullable(route)
    }

    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Route> {
        var sql = """
            SELECT b.id, b.name, b.description, g.id AS gId, b.place_id, b.active AS active, b.image_id AS image_id 
            FROM routes AS b INNER JOIN places AS p ON b.place_id = p.id 
            INNER JOIN grades AS g ON g.system_id = p.grading_system_id 
            WHERE b.place_id=:placeId AND g.id = b.grade_id 
            ORDER BY b.date_added DESC
        """.trimIndent()
        if (pagingEnabled) {
            sql += " LIMIT $limit OFFSET ${page * limit}"
        }
        val parameters = MapSqlParameterSource().addValue("placeId", placeId)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            Route(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                gradeId = rs.getLong("gId"),
                placeId = rs.getLong("place_id"),
                description = rs.getString("description"),
                active = rs.getBoolean("active"),
                imageId = rs.getString("image_id")
            )
        }
    }

    override fun getRoutesByPlace(placeId: Long): List<Route> {
        return getRoutesByPlace(placeId, 0, 0, false)
    }

    override fun addRoute(routeDTO: RouteDTO, imageId: String?, userId: Long): Long {
        val sql = "INSERT INTO routes(name, grade_id, place_id, active, description, image_id, user_id) VALUES (:name, :grade, :place, :active, :description, :imageId, :userId)"
        val parameters = MapSqlParameterSource()
            .addValue("name", routeDTO.name)
            .addValue("grade", routeDTO.gradeId)
            .addValue("place", routeDTO.placeId)
            .addValue("active", routeDTO.active)
            .addValue("description", routeDTO.description)
            .addValue("imageId", imageId)
            .addValue("userId", userId)
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(sql, parameters, keyHolder, arrayOf("id"))
        return keyHolder.key!!.toLong()
    }

    override fun updateRoute(route: Route): Int {
        val sql = "UPDATE routes SET name = :name, grade_id = :grade, place_id = :place, active = :active, description = :description, image_id = :image WHERE id = :routeId"
        val parameters = MapSqlParameterSource()
            .addValue("name", route.name)
            .addValue("grade", route.gradeId)
            .addValue("place", route.placeId)
            .addValue("active", route.active)
            .addValue("description", route.description)
            .addValue("image", route.imageId)
            .addValue("routeId", route.id)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun deleteRoute(routeId: Long): Int {
        val sql = "DELETE FROM routes WHERE id = :routeId"
        val parameters = MapSqlParameterSource().addValue("routeId", routeId)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun getNumRoutesInPlace(placeId: Long, countActive: Boolean): Int {
        val sql = "SELECT COUNT(*) AS count FROM routes WHERE place_id = :placeId AND active = :active"
        val parameters = MapSqlParameterSource()
            .addValue("placeId", placeId)
            .addValue("active", countActive)
        return jdbcTemplate.query(sql, parameters) { rs, _ -> rs.getInt("count") }.first()
    }
}

