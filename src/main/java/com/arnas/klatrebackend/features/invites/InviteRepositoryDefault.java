package com.arnas.klatrebackend.features.invites;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class InviteRepositoryDefault implements InviteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InviteRepositoryDefault(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public GroupInvite getGroupInviteById(long inviteId) {
        var sql = "SELECT * FROM group_invites WHERE id = :inviteId";
        var parameters = new MapSqlParameterSource().addValue("inviteId", inviteId);
        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> new GroupInvite(
                    rs.getLong("id"),
                    rs.getLong("user_id"),
                    rs.getLong("sender_id"),
                    rs.getLong("group_id"),
                    rs.getString("status"),
                    rs.getLong("accepted_at"),
                    rs.getLong("declined_at"),
                    rs.getLong("revoked_at")
                )
        ).getFirst();
    }

    @Override
    public long inviteUserToGroup(long userId, long groupId, long senderId) {
        var keyholder = new GeneratedKeyHolder();
        String sql = "INSERT INTO group_invites(group_id, user_id, sender_id) VALUES (:groupId, :userId, :senderId)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("groupId", groupId)
                .addValue("userId", userId)
                .addValue("senderId", senderId);
        jdbcTemplate.update(sql, parameters, keyholder);
        var insertedInvite = keyholder.getKeys();
        if (insertedInvite == null) {
            throw new RuntimeException("Failed to insert invite");
        }
        return (long) insertedInvite.get("id");
    }

    @Override
    public List<GroupInvite> getUserInvitesByStatus(long userId, String status) {
        var sql = "SELECT * FROM group_invites WHERE user_id = :userId AND status = :status";
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", status);

        return jdbcTemplate.query(sql, parameters, (rs, rowNum) -> new GroupInvite(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("sender_id"),
                rs.getLong("group_id"),
                rs.getString("status"),
                rs.getLong("accepted_at"),
                rs.getLong("declined_at"),
                rs.getLong("revoked_at")
        ));
    }

    @Override
    public int acceptInvite(long inviteId) {
        var sql = "UPDATE group_invites SET status = 'accepted', accepted_at = :acceptedAt WHERE id = :inviteId";
        var parameters = new MapSqlParameterSource()
                .addValue("inviteId", inviteId)
                .addValue("acceptedAt", new Date().getTime());
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int declineInvite(long inviteId) {
        var sql = "UPDATE group_invites SET status = 'declined', declined_at = :declinedAt WHERE id = :inviteId";
        var parameters = new MapSqlParameterSource()
                .addValue("inviteId", inviteId)
                .addValue("declinedAt", new Date().getTime());
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int revokeInvite(long inviteId) {
        var sql = "UPDATE group_invites SET status = 'revoked', revoked_at = :revokedAt WHERE id = :inviteId";
        var parameters = new MapSqlParameterSource()
                .addValue("inviteId", inviteId)
                .addValue("revokedAt", new Date().getTime());
        return jdbcTemplate.update(sql, parameters);
    }
}
