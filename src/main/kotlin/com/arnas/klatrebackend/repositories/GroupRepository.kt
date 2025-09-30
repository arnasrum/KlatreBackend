package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.AddGroupRequest
import com.arnas.klatrebackend.dataclasses.Group
import com.arnas.klatrebackend.dataclasses.GroupUser
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import kotlin.reflect.full.memberProperties

@Repository
 class GroupRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
): GroupRepositoryInterface {

    override fun getGroups(userID: Long): List<Group> {
        val groups = jdbcTemplate.query("SELECT kg.id AS klatreID, kg.name AS klatreName, kg.personal AS personal, kg.description AS description, kg.uuid AS uuid, " +
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
                )
        }
        return groups.toList()
    }

    override fun addGroup(group: AddGroupRequest): Long {
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



    override fun addUserToGroup(userId: Long, groupId: Long, role: Int) {
        jdbcTemplate.update(
            "INSERT INTO user_groups (user_id, group_id, role) VALUES (:userID, :groupID, :role)",
            mapOf("userID" to userId, "groupID" to groupId, "role" to role)
        )
    }

    override fun getUserGroupRole(userId: Long, groupId: Long): Int? {
        var userRole: Int? = null
        jdbcTemplate.query("SELECT role FROM user_groups WHERE user_id = :userID AND group_id = :groupID",
            mapOf("userID" to userId, "groupID" to groupId)
        ) { rs -> userRole = rs.getInt("role") }
        return userRole
    }
    
    override fun deleteGroup(groupId: Long): Int {
        val rowsAffected = jdbcTemplate.update(
            "DELETE FROM klatre_groups WHERE id = :groupId",
            mapOf("groupId" to groupId)
        )
        return rowsAffected
    }

    override fun getGroupUsers(groupId: Long): List<GroupUser> {
        val users: MutableList<GroupUser> = mutableListOf()
        jdbcTemplate.query("SELECT * FROM user_groups AS ug INNER JOIN users AS u ON ug.user_id = u.id WHERE group_id = :groupID",
            MapSqlParameterSource()
                .addValue("groupID", groupId)
        ) { rs -> users.add(
            GroupUser(
                id = rs.getLong("user_id"),
                isAdmin = rs.getString("role") == "1" || rs.getString("role") == "0",
                isOwner = rs.getString("role") == "0",
                groupID = groupId,
                email = rs.getString("email"),
                name = rs.getString("name")
            )) }
        return users.toList()
    }

    override fun updateUserGroupRole(userId: Long, groupId: Long, newRoleId: Int): Int {
        val rowsAffected = jdbcTemplate.update(
            "UPDATE user_groups SET role = :role WHERE user_id = :userID AND group_id = :groupID",
            mapOf("userID" to userId, "groupID" to groupId, "role" to newRoleId)
        )
        return rowsAffected
    }

    override fun deleteUserFromGroup(userId: Long, groupId: Long): Int {
        val rowAffected = jdbcTemplate.update(
            "DELETE FROM user_groups WHERE user_id = :userID AND group_id = :groupID",
            mapOf("userID" to userId, "groupID" to groupId)
        )
        return rowAffected
    }

    fun getGroupByUuid(groupUuid: String): Group? {
        val group = jdbcTemplate.query("SELECT * FROM klatre_groups WHERE uuid = :groupUuid",
            mapOf("groupUuid" to groupUuid),
        ) { rs, _ ->
            Group(
                id = rs.getLong("id"),
                owner = rs.getLong("owner"),
                name = rs.getString("name"),
                personal = rs.getBoolean("personal"),
                description = rs.getString("description"),
                uuid = rs.getString("uuid")
            )
        }
        return group.firstOrNull()
    }


}