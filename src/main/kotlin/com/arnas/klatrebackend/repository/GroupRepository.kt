package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Group
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
 class GroupRepository(
    private val dataSource: DataSource,
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    open fun getGroups(userId: Int): Array<Long> {
        val results: ArrayList<Long> = arrayListOf()
        jdbcTemplate.query("SELECT * FROM user_groups WHERE user_id=:userId",
            mapOf("userId" to userId),
            ) { rs, _ ->
                while(rs.next()) {
                    results.add(rs.getLong("group_id"))
                }
            }
        return results.toTypedArray()
    }

    open fun addGroup(group: Group): Long {
        val keyholder = GeneratedKeyHolder()

        val parameters = mutableMapOf<String, Any>("owner" to group.owner)
        val tableColumns = mutableListOf("owner")
        val placeholders = mutableListOf(":owner")

        group.name?.let {
            parameters["name"] = it
            tableColumns.add("name")
            placeholders.add(":name")
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