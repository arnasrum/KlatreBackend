package com.arnas.klatrebackend.features.routes

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(RouteRepositoryDefault::class)
class RouteRepositoryTest {

    @Autowired
    private lateinit var routeRepository: RouteRepository

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
    }

    @Test
    @Sql("/database/schema.sql")
    fun testGetRouteByIdEmptyTable() {
        val route = routeRepository.getRouteById(0)
        Assertions.assertTrue(route.isEmpty, "Route should be empty")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testGetRouteByIdBasicCase() {
        val fetchedRoute = routeRepository.getRouteById(1)
        assert(fetchedRoute.isPresent) { "Expected route to be present" }
        val expectedRoute = Route(1, "svaberg", 6, 1, null, true, null)
        Assertions.assertEquals(fetchedRoute.get(), expectedRoute, "Route should be equal")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testGetRouteByIdNonExistingId() {
        val fetchedRoute = routeRepository.getRouteById(999)
        Assertions.assertTrue(fetchedRoute.isEmpty, "The route should be empty")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testInsertRouteBase() {
        val routeDTO = RouteDTO("test", 1, 1, null, true, null)
        val routeId = routeRepository.addRoute(routeDTO, null, 1)
        val expectedRoute = Route(routeId, "test", 1, 1, null, true, null)
        val fetchedRoute = routeRepository.getRouteById(routeId)
        assert(fetchedRoute.isPresent) { "Expected route to be present" }
        Assertions.assertEquals(expectedRoute, fetchedRoute.get(), "Route should be equal")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testDeleteRouteBase() {
        val routeOpt = routeRepository.getRouteById(1)
        assert(routeOpt.isPresent) { "Expected route to be present" }
        val routeId = routeOpt.get().id
        routeRepository.deleteRoute(routeId)
        val fetchedRoute = routeRepository.getRouteById(routeId)
        Assertions.assertFalse(fetchedRoute.isPresent, "Route should be null")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testUpdateRoute() {
        val routeOptional = routeRepository.getRouteById(2)
        assert(routeOptional.isPresent) { "Expected route to be present" }
        val route = routeOptional.get()

        val newRoute = Route(route.id, "newRoute", 2, 2, null, false, null)
        val rowsAffected = routeRepository.updateRoute(newRoute)
        val fetchedRoute = routeRepository.getRouteById(route.id)
        assert(fetchedRoute.isPresent) { "Expected route to be present" }
        Assertions.assertEquals(1, rowsAffected, "Rows affected should be 1")
        Assertions.assertEquals(newRoute, fetchedRoute.get(), "Route should be equal")
    }
}

