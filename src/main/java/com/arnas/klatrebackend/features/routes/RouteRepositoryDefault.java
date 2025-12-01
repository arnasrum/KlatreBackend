package com.arnas.klatrebackend.features.routes;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;
import java.util.List;

@Repository
public class RouteRepositoryDefault implements RouteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    RouteRepositoryDefault(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<Route> getRouteById(long routeId) {

        var route = jdbcTemplate.query("SELECT * FROM routes WHERE id = :routeId",
                new MapSqlParameterSource().addValue("routeId", routeId),
                (rs, index) -> new Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("grade_id"),
                        rs.getLong("place_id"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getString("image_id")
                )
        ).getFirst();
        return Optional.of(route);
    }

    @Override
    public List<Route> getRoutesByPlace(long placeId, int page, int limit, boolean pagingEnabled) {
        var sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place_id, b.active AS active, b.image_id AS image_id " +
                "FROM routes AS b INNER JOIN places AS p ON b.place_id = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place_id=:placeId AND g.id = b.grade_id " +
                "ORDER BY b.date_added DESC";
        if(pagingEnabled) {sql += " LIMIT " + limit + " OFFSET " + (page) * limit;}
        var parameters = new MapSqlParameterSource().addValue("placeId", placeId);

        return jdbcTemplate.query(sql, parameters,
                (ResultSet rs, int index) ->
                new Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("gId"),
                        rs.getLong("place_id"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getString("image_id")
                )
        );
    }

    @Override
    public List<Route> getRoutesByPlace(long placeId) {
        return getRoutesByPlace(placeId, 0,0, false);
    }

    @Override
    public long addRoute(@NonNull RouteDTO routeDTO, String imageId, long userId) {
        var sql = "INSERT INTO routes(name, grade_id, place_id, active, description, image_id, user_id) VALUES (:name, :grade, :place, :active, :description, :imageId, :userId)";
        var parameters = new MapSqlParameterSource()
                .addValue("name", routeDTO.getName())
                .addValue("grade", routeDTO.getGradeId())
                .addValue("place", routeDTO.getPlaceId())
                .addValue("active", routeDTO.getActive())
                .addValue("description", routeDTO.getDescription())
                .addValue("imageId", imageId)
                .addValue("userId", userId);
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameters, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public int updateRoute(@NonNull Route route) {
        var sql = "UPDATE routes SET name = :name, grade_id = :grade, place_id = :place, active = :active, description = :description, image_id = :image WHERE id = :routeId";
        var parameters = new MapSqlParameterSource()
                .addValue("name", route.getName())
                .addValue("grade", route.getGradeId())
                .addValue("place", route.getPlaceId())
                .addValue("active", route.getActive())
                .addValue("description", route.getDescription())
                .addValue("image", route.getImageId())
                .addValue("routeId", route.getId());
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int deleteRoute(long routeId) {
        var sql = "DELETE FROM routes WHERE id = :routeId";
        var parameters = new MapSqlParameterSource().addValue("routeId", routeId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int getNumRoutesInPlace(long placeId, boolean countActive) {
        var sql = "SELECT COUNT(*) AS count FROM routes WHERE place_id = :placeId AND active = :active";
        var parameters = new MapSqlParameterSource()
                .addValue("placeId", placeId)
                .addValue("active", countActive);
        return jdbcTemplate.query(sql, parameters, (rs, index) -> rs.getInt("count")).getFirst();
    }
}
