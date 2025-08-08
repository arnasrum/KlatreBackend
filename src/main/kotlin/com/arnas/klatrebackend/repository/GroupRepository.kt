package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.Place
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import javax.sql.DataSource

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

    open fun addGroup(group: Group): Long {
        val keyholder = GeneratedKeyHolder()

        val parameters = mutableMapOf<String, Any>("owner" to group.owner)
        val tableColumns = mutableListOf("owner")
        val placeholders = mutableListOf(":owner")

        group.name.let {
            parameters["name"] = it
            tableColumns.add("name")
            placeholders.add(":name")
        }

        group.personal.let {
            parameters["personal"] = it
            tableColumns.add("personal")
            placeholders.add(":personal")
        }

        group.description?.let {
            parameters["description"] = it
            tableColumns.add("description")
            placeholders.add(":description")
        }

        val sql = "INSERT INTO team_groups (${tableColumns.joinToString(", ")}) VALUES (${placeholders.joinToString(", ")})"
        jdbcTemplate.update(sql, parameters as SqlParameterSource, keyholder)
        return keyholder.key?.toLong() ?: -1L
    }

}