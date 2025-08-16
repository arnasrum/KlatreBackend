package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.Place
import com.arnas.klatrebackend.dataclass.PlaceRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import kotlin.reflect.full.memberProperties

@Repository
 class GroupRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    open fun getGroups(userID: Long): Array<GroupWithPlaces> {
        val groups: ArrayList<Group> = arrayListOf()

        jdbcTemplate.query("SELECT kg.id AS klatreID, kg.name AS klatreName, kg.personal AS personal, kg.description AS description, " +
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
                    description = rs.getString("description")
                ).let { groups.add(it) }
        }

        val groupsWithPlaces: ArrayList<GroupWithPlaces> = arrayListOf()
        groups.forEach { group ->
            val places = arrayListOf<Place>()
            jdbcTemplate.query("SELECT * FROM places WHERE group_id = :groupID",
                mapOf("groupID" to group.id)
            ) { rs, _ ->
                places.add(Place(rs.getLong("id"), rs.getString("name")))
            }
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

    open fun addUserToGroup(userID: Long, groupID: Long) {
        jdbcTemplate.update(
            "INSERT INTO user_groups (user_id, group_id) VALUES (:userID, :groupID)",
            mapOf("userID" to userID, "groupID" to groupID)
        )
    }




}