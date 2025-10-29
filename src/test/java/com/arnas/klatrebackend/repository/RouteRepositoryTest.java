package com.arnas.klatrebackend.repository;

import com.arnas.klatrebackend.dataclasses.Route;
import com.arnas.klatrebackend.dataclasses.RouteDTO;
import com.arnas.klatrebackend.repositories.RouteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNull;

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(RouteRepository.class)
public class RouteRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Test
    @Sql("/database/schema.sql")
    public void testGetRouteByIdEmptyTable() {
        var route = routeRepository.getRouteById(0);
        assertNull(route, "Route should be null");
    }
    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    public void testGetRouteByIdBasicCase() {
        var fetchedRoute = routeRepository.getRouteById(1);
        var expectedRoute = new Route(1, "svaberg", 6, 1, null, true, null);
        Assertions.assertEquals(fetchedRoute, expectedRoute, "Route should be equal");
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    public void testGetRouteByIdNonExistingId() {
        var fetchedRoute = routeRepository.getRouteById(999);
        Assertions.assertNull(fetchedRoute, "The route should be null");
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    public void testInsertRouteBase() {
        var routeDTO = new RouteDTO("test", 1, 1, null, true, null);
        var routeId = routeRepository.addRoute(routeDTO, null, 1);
        var expectedRoute = new Route(routeId, "test", 1, 1, null, true, null);
        var fetchedRoute = routeRepository.getRouteById(routeId);
        Assertions.assertEquals(expectedRoute, fetchedRoute, "Route should be equal");
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    public void testDeleteRouteBase() {
        var routeId = Objects.requireNonNull(routeRepository.getRouteById(1)).getId();
        routeRepository.deleteRoute(routeId);
        var fetchedRoute = routeRepository.getRouteById(routeId);
        Assertions.assertNull(fetchedRoute, "Route should be null");
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    public void testUpdateRoute() {
        var route = Objects.requireNonNull(routeRepository.getRouteById(2));
        var newRoute = new Route(route.getId(), "newRoute", 2, 2, null, false, null);
        var rowsAffected = routeRepository.updateRoute(newRoute, null);
        var fetchedRoute = routeRepository.getRouteById(route.getId());
        Assertions.assertEquals(1, rowsAffected, "Rows affected should be 1");
        Assertions.assertEquals(newRoute, fetchedRoute, "Route should be equal");
    }

}
