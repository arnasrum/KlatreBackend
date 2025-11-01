package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.dataclasses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.arnas.klatrebackend.interfaces.repositories.ClimbingSessionRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public class ClimbingSessionRepositoryDefault implements ClimbingSessionRepository {

    final private NamedParameterJdbcTemplate jdbcTemplate;

    public ClimbingSessionRepositoryDefault(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Nullable
    public ClimbingSession getClimbingSessionById(long sessionId) {
        var sql = "SELECT * FROM climbing_sessions WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", sessionId);
        return jdbcTemplate.query(sql, parameters, (rs, index) -> new ClimbingSession(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("group_id"),
                rs.getLong("place_id"),
                rs.getLong("created_at"),
                rs.getBoolean("active"),
                rs.getString("name"),
                getRouteAttemptsBySessionId(sessionId)
        )).stream().findFirst().orElse(null);
    }

    @Override
    @Nullable
    public ClimbingSession getActiveSession(long groupId, long userId) {
        var sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = :active";
        var parameters = new MapSqlParameterSource()
            .addValue("groupId", groupId)
            .addValue("userId", userId)
            .addValue("active", true);
        var sessions = jdbcTemplate.query(sql, parameters, (rs, index) -> new ClimbingSession(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("group_id"),
                rs.getLong("place_id"),
                rs.getLong("created_at"),
                rs.getBoolean("active"),
                rs.getString("name"),
                getRouteAttemptsBySessionId(rs.getLong("id"))
        ));
        if(sessions.isEmpty()) return null;
        return sessions.getFirst();
    }

    @Override
    public List<ClimbingSession> getPastSessions(long groupId, long userId) {
        var sql = "SELECT * FROM climbing_sessions WHERE group_id = :groupId AND user_id = :userId AND active = :active";
        var parameters = new MapSqlParameterSource()
                .addValue("groupId", groupId)
                .addValue("userId", userId)
                .addValue("active", false);
        return jdbcTemplate.query(sql, parameters, (rs, index) -> new ClimbingSession(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("group_id"),
                rs.getLong("place_id"),
                rs.getLong("created_at"),
                rs.getBoolean("active"),
                rs.getString("name"),
                getRouteAttemptsBySessionId(rs.getLong("id"))
        ));
    }

    @Override
    public long openActiveSession(long userId, long groupId, long placeId) {
        var sql = "INSERT INTO climbing_sessions(user_id, group_id, place_id, active, name) VALUES (:userId, :groupId, :placeId, :active, :name)";
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("groupId", groupId)
                .addValue("placeId", placeId)
                .addValue("active", true)
                .addValue("name", "");
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameters, keyHolder);
        Object id = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return Long.parseLong(id.toString());
    }

    @Override
    public int setSessionAsInactive(long activeSessionId) {
        var sql = "UPDATE climbing_sessions SET active = :active WHERE id = :activeSessionId";
        var parameters = new MapSqlParameterSource()
                .addValue("active", false)
                .addValue("activeSessionId", activeSessionId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public long uploadClimbingSession(ClimbingSessionDTO climbingSession) {
        var sql = "INSERT INTO climbing_sessions(user_id, group_id, place_id, active) VALUES (:userId, :groupId, :placeId, :active)";
        var parameters = new MapSqlParameterSource()
                .addValue("user_id", climbingSession.getUserId())
                .addValue("group_id", climbingSession.getGroupId())
                .addValue("place_id", climbingSession.getPlaceId())
                .addValue("active", false);
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameters, keyHolder);
        Object id = Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return Long.parseLong(id.toString());
    }

    @Override
    public int deleteClimbingSession(long climbingSessionId) {
        var sql = "DELETE FROM climbing_sessions WHERE id = :climbingSessionId";
        var parameters = new MapSqlParameterSource().addValue("climbingSessionId", climbingSessionId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    @NonNull
    public List<RouteAttempt> getRouteAttemptsBySessionId(long sessionId) {
        var sql = "SELECT * FROM route_attempts WHERE session = :sessionId";
        var parameters = new MapSqlParameterSource().addValue("sessionId", sessionId);
        return jdbcTemplate.query(sql, parameters, (rs, index) ->
            new RouteAttempt(
                rs.getLong("id"),
                rs.getInt("attempts"),
                rs.getBoolean("completed"),
                rs.getLong("route_id"),
                rs.getLong("last_updated"),
                rs.getLong("session")
            )
        );
    }
    @Override
    public int updateRouteAttempt(RouteAttempt routeAttempt) {
        var sql = "UPDATE route_attempts SET attempts = :attempts, completed = :completed, last_updated = :lastUpdated WHERE id = :id";
        var parameters = new MapSqlParameterSource()
                .addValue("attempts", routeAttempt.getAttempts())
                .addValue("completed", routeAttempt.getCompleted())
                .addValue("lastUpdated", new Date().getTime())
                .addValue("id", routeAttempt.getId());
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int deleteRouteAttempt(long routeAttemptId) {
        var sql = "DELETE FROM route_attempts WHERE id = :routeAttemptId";
        var parameters = new MapSqlParameterSource().addValue("routeAttemptId", routeAttemptId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public RouteAttempt addRouteAttemptToActiveSession(long activeSessionId, RouteAttemptDTO routeAttempt) {
        var sql = "INSERT INTO route_attempts(route_id, session, attempts, completed, last_updated) VALUES (:routeId, :activeSessionId, :attempts, :completed, :timestamp)";
        var keyHolder = new GeneratedKeyHolder();
        var parameters = new MapSqlParameterSource()
                .addValue("routeId", routeAttempt.getRouteId())
                .addValue("activeSessionId", activeSessionId)
                .addValue("attempts", routeAttempt.getAttempts())
                .addValue("completed", routeAttempt.getCompleted())
                .addValue("timestamp", routeAttempt.getTimestamp());
        jdbcTemplate.update(sql, parameters, keyHolder);
        Long id = (Long) Objects.requireNonNull(keyHolder.getKeys()).get("id");
        return getRouteAttemptById(id);
    }

    @Override
    @NonNull
    public RouteAttempt getRouteAttemptById(long id) {
        var sql = "SELECT * FROM route_attempts WHERE id = :id";
        var parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.query(sql, parameters, (rs, index) ->
            new RouteAttempt(
                    rs.getLong("id"),
                    rs.getInt("attempts"),
                    rs.getBoolean("completed"),
                    rs.getLong("route_id"),
                    rs.getLong("last_updated"),
                    rs.getLong("session")
            )
        ).getFirst();
    }
}
