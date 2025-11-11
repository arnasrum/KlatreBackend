package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.dataclasses.UserGroupSessionStats;
import com.arnas.klatrebackend.interfaces.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatsRepositoryDefault implements StatsRepository {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StatsRepositoryDefault(@Autowired NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<UserGroupSessionStats> getUserAttemptActivity(long userId, long groupId) {
        var sql = """
        SELECT
            DATE(to_timestamp(cs.created_at)) AS session_date,
            COUNT(DISTINCT cs.id) AS sessions_that_day,
            COUNT(ra.id) AS route_attempts,
            SUM(ra.attempts) AS total_tries,
            SUM(CASE WHEN ra.completed THEN 1 ELSE 0 END) AS sends
        FROM
            climbing_sessions cs
        JOIN
            route_attempts ra ON ra.session = cs.id
        WHERE
            cs.user_id = :userId
            AND cs.group_id = :groupId
            AND cs.active = false
        GROUP BY
            session_date
        ORDER BY
            session_date;
       """.trim();
        var parameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("groupId", groupId);
        return jdbcTemplate.query(sql, parameters, (rs, index) ->
            new UserGroupSessionStats(
                    Integer.parseInt(rs.getString("session_date").split("-")[0]),
                    Integer.parseInt(rs.getString("session_date").split("-")[1]),
                    Integer.parseInt(rs.getString("session_date").split("-")[2]),
                    rs.getInt("route_attempts"),
                    rs.getInt("total_tries"),
                    rs.getInt("sends"),
                    groupId,
                    userId
            )
        );
    }
}
