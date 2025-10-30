package com.arnas.klatrebackend.repositories;

import com.arnas.klatrebackend.interfaces.repositories.RouteRepository;
import com.arnas.klatrebackend.dataclasses.Route;
import com.arnas.klatrebackend.dataclasses.RouteDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Objects;
import java.util.Optional;

@Repository
public class RouteRepositoryDefault implements RouteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    RouteRepositoryDefault(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<Route> getRouteById(Long routeId) {

        var route = jdbcTemplate.query("SELECT * FROM routes WHERE id = :routeId",
                new MapSqlParameterSource().addValue("routeId", routeId),
                (rs, index) -> new Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("gradeId"),
                        rs.getLong("placeId"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getString("image")
                )
        ).getFirst();
        return Optional.of(route);
    }

    @Override
    public Route[] getRoutesByPlace(@NonNull Long placeId, int page, int limit, boolean pagingEnabled) {
        var sql = "SELECT b.id, b.name, b.description, g.id AS gId, b.place, b.active AS active, b.image_id AS image_id " +
                "FROM routes AS b INNER JOIN places AS p ON b.place = p.id " +
                "INNER JOIN grades AS g ON g.system_id = p.grading_system_id " +
                "WHERE b.place=:placeID AND g.id = b.grade " +
                "ORDER BY b.date_added DESC";
        if(pagingEnabled) {sql += " LIMIT " + limit + " OFFSET " + (page-1) * limit;}
        var parameters = new MapSqlParameterSource().addValue("placeID", placeId);

        var routes = jdbcTemplate.query(sql, parameters,
                (ResultSet rs, int index) ->
                new Route(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("gId"),
                        rs.getLong("place"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getString("image_id")
                )
        );
        return routes.toArray(Route[]::new);
    }
    @Override
    public Long insertRoute(@NonNull RouteDTO routeDTO) {
        var sql = "INSERT INTO routes(name, grade, place, active, description) VALUES (:name, :grade, :place, :active, :description)";
        var parameters = new MapSqlParameterSource()
                .addValue("name", routeDTO.getName())
                .addValue("grade", routeDTO.getGradeId())
                .addValue("place", routeDTO.getPlaceId())
                .addValue("active", routeDTO.getActive())
                .addValue("description", routeDTO.getDescription());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, parameters, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public int updateRoute(@NonNull Route route) {
        var sql = "UPDATE routes SET name = :name, grade = :grade, place = :place, active = :active, description = :description, image_id = :image WHERE id = :routeId";
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
    public int deleteRoute(@NonNull Long routeId) {
        var sql = "DELETE FROM routes WHERE id = :routeId";
        var parameters = new MapSqlParameterSource().addValue("routeId", routeId);
        return jdbcTemplate.update(sql, parameters);
    }

    @Override
    public int getNumRoutesInPlace(@NonNull Long placeId, boolean countActive) {
        var sql = "SELECT COUNT(*) FROM routes WHERE place = :placeId AND active = :active";
        var parameters = new MapSqlParameterSource()
                .addValue("placeId", placeId)
                .addValue("active", countActive);
        return jdbcTemplate.update(sql, parameters);
    }
}
