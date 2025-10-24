package com.arnas.klatrebackend.repositories

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
@Import(ClimbingSessionRepository::class)
class ClimbingSessionRepositoryTest {

    @Autowired
    private lateinit var climbingSessionRepository: ClimbingSessionRepository


    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
    }


    @Test
    @Sql(statements = [
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, timestamp) VALUES ('test', 1, 1, 1, 1761252478);",
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, timestamp) VALUES ('test2', 1, 1, 1, 1761253478);"
    ])
    fun `test getClimbingSessionByGroupId returns sessions`() {
        val groupId = 1L
        val userId = 1L
        //val result = climbingSessionRepository.getClimbingSessionByGroupId(groupId, userId)
        //assert(result.size == 2)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/pastSessionsTest.sql")
    fun `test get past sessions size`() {
        val pastSessions = climbingSessionRepository.getPastSessions(201, 101)
        assert(pastSessions.size == 2) {"Past sessions size is not 2. Actual size: ${pastSessions.size}"}


    }
}