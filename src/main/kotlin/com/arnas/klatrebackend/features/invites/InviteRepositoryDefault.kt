package com.arnas.klatrebackend.features.invites

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.util.Date

@Repository
class InviteRepositoryDefault(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : InviteRepository {

    override fun getGroupInviteById(inviteId: Long): GroupInvite {
        val sql = "SELECT * FROM group_invites WHERE id = :inviteId"
        val parameters = MapSqlParameterSource().addValue("inviteId", inviteId)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            GroupInvite(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                senderId = rs.getLong("sender_id"),
                groupId = rs.getLong("group_id"),
                status = rs.getString("status"),
                acceptedAt = rs.getLong("accepted_at"),
                declinedAt = rs.getLong("declined_at"),
                revokedAt = rs.getLong("revoked_at")
            )
        }.first()
    }

    override fun inviteUserToGroup(userId: Long, groupId: Long, senderId: Long): Long {
        val keyHolder = GeneratedKeyHolder()
        val sql = "INSERT INTO group_invites(group_id, user_id, sender_id) VALUES (:groupId, :userId, :senderId)"
        val parameters = MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
            .addValue("senderId", senderId)
        jdbcTemplate.update(sql, parameters, keyHolder)
        val insertedInvite = keyHolder.keys ?: throw RuntimeException("Failed to insert invite")
        return insertedInvite["id"] as Long
    }

    override fun getUserInvitesByStatus(userId: Long, status: String): List<GroupInvite> {
        val sql = "SELECT * FROM group_invites WHERE user_id = :userId AND status = :status"
        val parameters = MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("status", status)
        return jdbcTemplate.query(sql, parameters) { rs, _ ->
            GroupInvite(
                id = rs.getLong("id"),
                userId = rs.getLong("user_id"),
                senderId = rs.getLong("sender_id"),
                groupId = rs.getLong("group_id"),
                status = rs.getString("status"),
                acceptedAt = rs.getLong("accepted_at"),
                declinedAt = rs.getLong("declined_at"),
                revokedAt = rs.getLong("revoked_at")
            )
        }
    }

    override fun acceptInvite(inviteId: Long): Int {
        val sql = "UPDATE group_invites SET status = 'accepted', accepted_at = :acceptedAt WHERE id = :inviteId"
        val parameters = MapSqlParameterSource()
            .addValue("inviteId", inviteId)
            .addValue("acceptedAt", Date().time)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun declineInvite(inviteId: Long): Int {
        val sql = "UPDATE group_invites SET status = 'declined', declined_at = :declinedAt WHERE id = :inviteId"
        val parameters = MapSqlParameterSource()
            .addValue("inviteId", inviteId)
            .addValue("declinedAt", Date().time)
        return jdbcTemplate.update(sql, parameters)
    }

    override fun revokeInvite(inviteId: Long): Int {
        val sql = "UPDATE group_invites SET status = 'revoked', revoked_at = :revokedAt WHERE id = :inviteId"
        val parameters = MapSqlParameterSource()
            .addValue("inviteId", inviteId)
            .addValue("revokedAt", Date().time)
        return jdbcTemplate.update(sql, parameters)
    }
}

