package com.arnas.klatrebackend.features.invites

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(InviteRepositoryDefault::class)
class InviteRepositoryTest(
    @Autowired private val inviteRepository: InviteRepositoryDefault
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
    fun testInviteUserToGroupInserts() {
        val inviteId = inviteRepository.inviteUserToGroup(1, 1, 1)
        Assertions.assertTrue(inviteId > 0, "Invite ID should be positive")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testInviteUserToGroupNonExistentSender() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            inviteRepository.inviteUserToGroup(101, 1, 1)
        }
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun testRevokeInvite() {
        val inviteId = inviteRepository.inviteUserToGroup(2, 1, 1)
        inviteRepository.revokeInvite(inviteId)
        val revokedInvites = inviteRepository.getUserInvitesByStatus(2, "revoked")
        Assertions.assertEquals(1, revokedInvites.size)
        Assertions.assertEquals(inviteId, revokedInvites.first().id)
    }
}

