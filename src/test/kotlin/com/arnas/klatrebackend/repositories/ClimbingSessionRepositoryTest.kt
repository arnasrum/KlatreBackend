package com.arnas.klatrebackend.repositories

import com.arnas.klatrebackend.dataclasses.RouteAttemptDTO
import com.arnas.klatrebackend.dataclasses.UpdateAttemptRequest
import com.arnas.klatrebackend.interfaces.repositories.ClimbingSessionRepositoryInterface
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
    private lateinit var climbingSessionRepository: ClimbingSessionRepositoryInterface

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
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, active) VALUES ('test', 1, 1, 1, false);",
        "INSERT INTO climbing_sessions(name, group_id, user_id, place_id, active) VALUES ('test2', 1, 1, 1, false);"
    ])
    fun `test getPastSessions returns 2`() {
        val groupId = 1L
        val userId = 1L
        val result = climbingSessionRepository.getPastSessions(groupId, userId)
        assert(result.size == 2)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/pastSessionsTest.sql")
    fun `test get past sessions size`() {
        val pastSessions = climbingSessionRepository.getPastSessions(201, 101)
        assert(pastSessions.size == 2) {"Past sessions size is not 2. Actual size: ${pastSessions.size}"}
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO places(id, name, group_id) VALUES (1, 'Test Place', 1);"
    ])
    fun `test openActiveSession creates new session`() {
        val userId = 1L
        val groupId = 1L
        val placeId = 1L
        
        val sessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
        
        assert(sessionId > 0) { "Session ID should be positive" }
        
        val activeSession = climbingSessionRepository.getActiveSession(sessionId)
        assert(activeSession != null) { "Active session should exist" }
        assert(activeSession?.userId == userId) { "User ID mismatch" }
        assert(activeSession?.groupId == groupId) { "Group ID mismatch" }
        assert(activeSession?.placeId == placeId) { "Place ID mismatch" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');"
    ])
    fun `test getActiveSession returns null when no active session exists`() {
        val activeSession = climbingSessionRepository.getActiveSession(1L, 1L)
        assert(activeSession == null) { "Active session should be null" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO places(id, name, group_id) VALUES (1, 'Test Place', 1);"
    ])
    fun `test setSessionAsInactive updates session status`() {
        val userId = 1L
        val groupId = 1L
        val placeId = 1L
        
        val sessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
        val rowsAffected = climbingSessionRepository.setSessionAsInactive(sessionId)
        
        assert(rowsAffected == 1) { "One row should be affected" }
        
        val activeSession = climbingSessionRepository.getActiveSession(sessionId)
        assert(activeSession == null) { "Session should no longer be active" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO grades(id, system_id, grade_string, numerical_value) VALUES (1, 1, 'V5', 5);",
        "INSERT INTO places(id, name, group_id, grading_system_id) VALUES (1, 'Test Place', 1, 1);",
        "INSERT INTO routes(id, name, grade, place, active) VALUES (1, 'Test Route', 1, 1, true);"
    ])
    fun `test addRouteAttemptToActiveSession creates attempt`() {
        val userId = 1L
        val groupId = 1L
        val placeId = 1L
        
        val sessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
        val routeAttemptDTO = RouteAttemptDTO(
            routeId = 1L,
            attempts = 3,
            completed = true,
            timestamp = System.currentTimeMillis(),
            session = sessionId
        )
        
        val createdAttempt = climbingSessionRepository.addRouteAttemptToActiveSession(sessionId, routeAttemptDTO)
        
        assert(createdAttempt.id > 0) { "Attempt ID should be positive" }
        assert(createdAttempt.routeId == routeAttemptDTO.routeId) { "Route ID mismatch" }
        assert(createdAttempt.attempts == routeAttemptDTO.attempts) { "Attempts count mismatch" }
        assert(createdAttempt.completed == routeAttemptDTO.completed) { "Completed status mismatch" }
        assert(createdAttempt.session == sessionId) { "Session ID mismatch" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO grades(id, system_id, grade_string, numerical_value) VALUES (1, 1, 'V5', 5);",
        "INSERT INTO places(id, name, group_id, grading_system_id) VALUES (1, 'Test Place', 1, 1);",
        "INSERT INTO routes(id, name, grade, place, active) VALUES (1, 'Test Route', 1, 1, true);",
        "INSERT INTO climbing_sessions(id, user_id, group_id, place_id, active) VALUES (100, 1, 1, 1, true);",
        "INSERT INTO route_attempts(id, route_id, attempts, completed, session, last_updated) VALUES (1, 1, 2, false, 100, 1000000);"
    ])
    fun `test getRouteAttemptsBySessionId returns attempts`() {
        val sessionId = 100L
        
        val attempts = climbingSessionRepository.getRouteAttemptsBySessionId(sessionId)
        
        assert(attempts.size == 1) { "Should have 1 attempt" }
        assert(attempts[0].routeId == 1L) { "Route ID should be 1" }
        assert(attempts[0].attempts == 2) { "Attempts count should be 2" }
        assert(!attempts[0].completed) { "Should not be completed" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO grades(id, system_id, grade_string, numerical_value) VALUES (1, 1, 'V5', 5);",
        "INSERT INTO places(id, name, group_id, grading_system_id) VALUES (1, 'Test Place', 1, 1);",
        "INSERT INTO routes(id, name, grade, place, active) VALUES (1, 'Test Route', 1, 1, true);",
        "INSERT INTO climbing_sessions(id, user_id, group_id, place_id, active) VALUES (100, 1, 1, 1, true);",
        "INSERT INTO route_attempts(id, route_id, attempts, completed, session, last_updated) VALUES (1, 1, 2, false, 100, 1000000);"
    ])
    fun `test updateRouteAttempt modifies attempt`() {
        val updateRequest = UpdateAttemptRequest(
            id = 1L,
            attempts = 5,
            completed = true,
            timestamp = System.currentTimeMillis()
        )
        
        val rowsAffected = climbingSessionRepository.updateRouteAttempt(updateRequest)
        
        assert(rowsAffected == 1) { "One row should be affected" }
        
        val attempts = climbingSessionRepository.getRouteAttemptsBySessionId(100L)
        assert(attempts[0].attempts == 5) { "Attempts should be updated to 5" }
        assert(attempts[0].completed) { "Should be marked as completed" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO grades(id, system_id, grade_string, numerical_value) VALUES (1, 1, 'V5', 5);",
        "INSERT INTO places(id, name, group_id, grading_system_id) VALUES (1, 'Test Place', 1, 1);",
        "INSERT INTO routes(id, name, grade, place, active) VALUES (1, 'Test Route', 1, 1, true);",
        "INSERT INTO climbing_sessions(id, user_id, group_id, place_id, active) VALUES (100, 1, 1, 1, true);",
        "INSERT INTO route_attempts(id, route_id, attempts, completed, session, last_updated) VALUES (1, 1, 2, false, 100, 1000000);"
    ])
    fun `test deleteRouteAttempt removes attempt`() {
        val routeAttemptId = 1L
        
        val rowsAffected = climbingSessionRepository.deleteRouteAttempt(routeAttemptId)
        
        assert(rowsAffected == 1) { "One row should be affected" }
        
        val attempts = climbingSessionRepository.getRouteAttemptsBySessionId(100L)
        assert(attempts.isEmpty()) { "No attempts should remain" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO places(id, name, group_id) VALUES (1, 'Test Place', 1);",
        "INSERT INTO climbing_sessions(id, user_id, group_id, place_id, active) VALUES (100, 1, 1, 1, false);"
    ])
    fun `test deleteClimbingSession removes session`() {
        val sessionId = 100L
        
        val rowsAffected = climbingSessionRepository.deleteClimbingSession(sessionId)
        
        assert(rowsAffected == 1) { "One row should be affected" }
        
        val session = climbingSessionRepository.getSessionById(sessionId)
        assert(session == null) { "Session should be deleted" }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql(statements = [
        "INSERT INTO users(id, email, name) VALUES (1, 'test@test.com', 'Test User');",
        "INSERT INTO klatre_groups(id, owner, name, personal) VALUES (1, 1, 'Test Group', false);",
        "INSERT INTO grading_systems(id, name, climb_type, is_global) VALUES (1, 'V-Scale', 'boulder', true);",
        "INSERT INTO places(id, name, group_id) VALUES (1, 'Test Place', 1);"
    ])
    fun `test getSessionById returns correct session`() {
        val userId = 1L
        val groupId = 1L
        val placeId = 1L
        
        val sessionId = climbingSessionRepository.openActiveSession(userId, groupId, placeId)
        
        val session = climbingSessionRepository.getSessionById(sessionId)
        
        assert(session != null) { "Session should exist" }
        assert(session?.id == sessionId) { "Session ID mismatch" }
        assert(session?.userId == userId) { "User ID mismatch" }
        assert(session?.groupId == groupId) { "Group ID mismatch" }
        assert(session?.placeId == placeId) { "Place ID mismatch" }
    }

    @Test
    @Sql("/database/schema.sql")
    fun `test getSessionById returns null for non-existent session`() {
        val session = climbingSessionRepository.getSessionById(999L)
        assert(session == null) { "Session should not exist" }
    }
}