package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.ClimbingSessionDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, start_date) VALUES ('test', 1, 1, 1, '2020-01-01');",
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, start_date) VALUES ('test2', 1, 1, 1, '2020-01-01');"
    ])
    fun `test getClimbingSessionByGroupId returns sessions`() {
        val groupId = 1L
        val userId = 1L
        val result = climbingSessionRepository.getClimbingSessionByGroupId(groupId, userId)
        assert(result.size == 2)
    }

    @Test
    fun `test uploadClimbingSession stores session successfully`() {
        val userId = 1L
        val climbingSession = ClimbingSessionDTO(
            groupId = 1L,
            placeId = 1L,
            startDate = "2025-10-04",
            name = "Test session",
            userId = userId,
            routeSends = emptyList()
        )
        assertDoesNotThrow {
            val generatedID = climbingSessionRepository.uploadClimbingSession(climbingSession)
            assert(generatedID == 1L)
        }

    }


}