package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Place
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PlaceRepository(
    private var jdbcTemplate: NamedParameterJdbcTemplate
): PlaceRepositoryInterface {

    override fun getPlacesByGroupId(groupId: Long): List<Place> {

        val rowMapper = RowMapper<Place> { rs: ResultSet, _: Int ->
            Place(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                description = rs.getString("description"),
                groupID = rs.getLong("group_id"),
                gradingSystem = rs.getLong("grading_system_id")
            )
        }
        val places =  jdbcTemplate.query("SELECT * FROM places WHERE group_id = :groupId",
            mapOf("groupId" to groupId),
            rowMapper
        )
        return places.toList()
    }
}