package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.Grade
import com.arnas.klatrebackend.dataclass.GradingSystem
import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.Place
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.dataclass.ServiceResult
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import kotlin.reflect.full.memberProperties

@Repository
 class GroupRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val placeRepository: PlaceRepository
) {

    open fun getGroups(userID: Long): Array<GroupWithPlaces> {
        val groups: ArrayList<Group> = arrayListOf()

        jdbcTemplate.query("SELECT kg.id AS klatreID, kg.name AS klatreName, kg.personal AS personal, kg.description AS description, kg.uuid AS uuid, " +
                "kg.owner AS owner " +
                "FROM klatre_groups AS kg " +
                "INNER JOIN user_groups AS ug ON kg.id = ug.group_id " +
                "WHERE ug.user_id = :userID",
            mapOf("userID" to userID),
            ) { rs, _ ->
                Group(
                    id = rs.getLong("klatreID"),
                    owner = rs.getLong("owner"),
                    name = rs.getString("klatreName"),
                    personal = rs.getBoolean("personal"),
                    description = rs.getString("description"),
                    uuid = rs.getString("uuid")
                ).let { groups.add(it) }
        }

        // Do not like this
        val groupsWithPlaces: ArrayList<GroupWithPlaces> = arrayListOf()
        groups.forEach { group ->
            val places = placeRepository.getPlacesByGroupId(group.id)
            groupsWithPlaces.add(GroupWithPlaces(group, places.toTypedArray()))
        }
        return groupsWithPlaces.toTypedArray()
    }

    open fun addGroup(group: AddGroupRequest): Long {
        val keyholder = GeneratedKeyHolder()

        val parameters = MapSqlParameterSource()
        val tableColumns: MutableList<String> = mutableListOf()
        val placeholders: MutableList<String> = mutableListOf()

        group::class.memberProperties.forEach { prop ->
            val value = prop.getter.call(group)
            if(value != null) {
                tableColumns.add(prop.name)
                placeholders.add(":${prop.name}")
                parameters.addValue(prop.name, value)
            }
        }

        val sql = "INSERT INTO klatre_groups (${tableColumns.joinToString(", ")}) VALUES (${placeholders.joinToString(", ")})"
        jdbcTemplate.update(sql, parameters, keyholder)
        val keys = keyholder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }


    open fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest): Long {
        val keyholder = GeneratedKeyHolder()

        val tableColumns = mutableListOf<String>()
        val placeHolders = mutableListOf<String>()
        val parameters = MapSqlParameterSource()
        placeRequest::class.memberProperties.forEach { prop ->
            val value = prop.getter.call(placeRequest)
            if(value != null) {
                parameters.addValue(prop.name, value)
                tableColumns.add(prop.name)
                placeHolders.add(":${prop.name}")
            }
        }
        val sql = "INSERT INTO places (${tableColumns.joinToString(", ")}) VALUES (${placeHolders.joinToString(", ")})"
        jdbcTemplate.update(
            sql, parameters, keyholder
        )
        val keys = keyholder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        return (keys["id"] as Number).toLong()
    }

    open fun addUserToGroup(userID: Long, groupID: Long, roleID: Int) {
        jdbcTemplate.update(
            "INSERT INTO user_groups (user_id, group_id, role) VALUES (:userID, :groupID, :role)",
            mapOf("userID" to userID, "groupID" to groupID, "role" to roleID)
        )
    }

    open fun getUserGroupRole(userID: Long, groupID: Long): Int? {
        var userRole: Int? = null
        jdbcTemplate.query("SELECT role FROM user_groups WHERE user_id = :userID AND group_id = :groupID",
            mapOf("userID" to userID, "groupID" to groupID)
        ) { rs -> userRole = rs.getInt("role") }
        return userRole
    }
    
    open fun deleteGroup(groupID: Long): ServiceResult<Unit> {
        try {
            val rowsAffected = jdbcTemplate.update(
                "DELETE FROM klatre_groups WHERE id = :groupId",
                mapOf("groupId" to groupID)
            )
            println("rowsAffected: $rowsAffected")
            return if (rowsAffected > 0) {
                ServiceResult(success = true, message = "Group deleted successfully")
            } else {
                ServiceResult(success = false, message = "Group not found")
            }
        } catch(e: Exception) {
            //logger.error("Failed to delete group with ID: $groupID", e)
            return ServiceResult(success = false, message = "Failed to delete group: ${e.message}")
        }
    }
    open fun getGradingSystems(groupID: Long): List<GradingSystem> {
        val gradingSystems: MutableList<GradingSystem> = mutableListOf()
        val sql = "SELECT * FROM grading_systems WHERE created_in_group = :groupID OR is_global = true"
        jdbcTemplate.query(sql,
            MapSqlParameterSource()
                .addValue("groupID", groupID)
        ) { rs ->
            gradingSystems.add(GradingSystem(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                climbType = rs.getString("climb_type"),
                grades = getGradesBySystemId(rs.getLong("id"))
            ))

        }
        return gradingSystems.toList()
    }

    private fun getGradesBySystemId(systemId: Long): List<Grade> {
        val grades: MutableList<Grade> = mutableListOf()
        val sql = "SELECT * FROM grades WHERE system_id = :systemId ORDER BY numerical_value"
        jdbcTemplate.query(sql,
            MapSqlParameterSource().addValue("systemId", systemId)
        ) { rs ->
            grades.add(
                Grade(
                    id = rs.getLong("id"),
                    gradeString = rs.getString("grade_string"),
                    numericalValue = rs.getInt("numerical_value")
                )
            )
        }
        return grades.toList()
    }

    

}