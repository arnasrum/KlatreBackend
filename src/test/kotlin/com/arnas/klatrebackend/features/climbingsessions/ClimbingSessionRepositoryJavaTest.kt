package com.arnas.klatrebackend.features.climbingsessions

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
@Import(ClimbingSessionRepositoryDefault::class)
class ClimbingSessionRepositoryJavaTest(
    @Autowired private val climbingSessionRepository: ClimbingSessionRepositoryDefault
) {

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
    @Sql("/database/data.sql")
    fun testGetClimbingSessionByIdEmptyTable() {
        // data.sql inserts sessions with auto-generated IDs, so use an ID that doesn't exist
        val fetchedResult = climbingSessionRepository.getClimbingSessionById(999L)
        Assertions.assertNull(fetchedResult)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = [
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (100, true, 1, 1, 1, 987654321);",
        "INSERT INTO route_attempts(id, route_id, attempts, completed, session, last_updated) VALUES (100, 1, 10, true, 100 , 123456789);"
    ])
    fun testGetClimbingSessionByIdBasic() {
        val expectedResult = ClimbingSession(100, 1, 1, 1, 987654321, true,
            listOf(RouteAttempt(100, 10, true, 1, 123456789, 100)))
        val fetchedResult = climbingSessionRepository.getClimbingSessionById(100L)
        Assertions.assertEquals(expectedResult, fetchedResult)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = [
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (100, true, 1, 1, 1, 987654321);",
        "INSERT INTO route_attempts(id, route_id, attempts, completed, session, last_updated) VALUES (100, 1, 10, true, 100 , 123456789);"
    ])
    fun testGetClimbingSessionByIdBasicWrongUser() {
        val fetchedResult = climbingSessionRepository.getClimbingSessionById(999L)
        Assertions.assertNull(fetchedResult)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = [
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (100, true, 1, 1, 1, 987654321);"
    ])
    fun testGetClimbingSessionByIdNoRouteAttempts() {
        val expectedResult = ClimbingSession(100, 1, 1, 1, 987654321, true, emptyList())
        val fetchedResult = climbingSessionRepository.getClimbingSessionById(100L)
        Assertions.assertEquals(expectedResult, fetchedResult)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = [
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (100, true, 1, 1, 1, 987654321);"
    ])
    fun testGetActiveSessionBasic() {
    }
}
