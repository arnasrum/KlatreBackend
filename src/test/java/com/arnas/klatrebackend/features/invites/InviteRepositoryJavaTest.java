package com.arnas.klatrebackend.features.invites;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(InviteRepositoryDefault.class)
public class InviteRepositoryJavaTest {

    final private InviteRepositoryDefault inviteRepository;
    public InviteRepositoryJavaTest(@Autowired InviteRepositoryDefault inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    void testInviteUserToGroupInserts() {
        var inviteId = inviteRepository.inviteUserToGroup(1, 1, 1);
        Assertions.assertEquals(1L, inviteId);
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    void testInviteUserToGroupNonExistentSender() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            inviteRepository.inviteUserToGroup(101, 1, 1);
        });
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    void testRevokeInvite() {
        var inviteId = inviteRepository.inviteUserToGroup(2, 1, 1);
        inviteRepository.revokeInvite(inviteId);
        var revokedInvites = inviteRepository.getUserInvitesByStatus(2, "revoked");
        Assertions.assertEquals(1, revokedInvites.size());
        Assertions.assertEquals(inviteId, revokedInvites.getFirst().id());

    }



}
