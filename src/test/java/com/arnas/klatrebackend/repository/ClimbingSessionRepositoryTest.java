package com.arnas.klatrebackend.repository;


import com.arnas.klatrebackend.dataclasses.ClimbingSession;
import com.arnas.klatrebackend.dataclasses.RouteAttempt;
import com.arnas.klatrebackend.repositories.ClimbingSessionRepositoryDefault;
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

import java.util.List;

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(ClimbingSessionRepositoryDefault.class)
public class ClimbingSessionRepositoryTest {


    final private ClimbingSessionRepositoryDefault climbingSessionRepository;
    public ClimbingSessionRepositoryTest(@Autowired ClimbingSessionRepositoryDefault climbingSessionRepository) {
        this.climbingSessionRepository = climbingSessionRepository;
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
    public void testGetClimbingSessionByIdEmptyTable() {
        var fetchedResult = climbingSessionRepository.getClimbingSessionById(1L);
        Assertions.assertNull(fetchedResult);
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = {
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (1, true, 1, 1, 1, 987654321);",
        "INSERT INTO route_attempts(route_id, attempts, completed, session, last_updated) VALUES (1, 10, true, 1 , 123456789);"
    })
    public void testGetClimbingSessionByIdBasic() {

        var expectedResult = new ClimbingSession(1, 1, 1, 1, 987654321, true,
                List.of(new RouteAttempt(1, 10, true, 1, 123456789, 1)));
        var fetchedResult = climbingSessionRepository.getClimbingSessionById(1L);

        Assertions.assertEquals(expectedResult, fetchedResult);
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = {
        "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (1, true, 1, 1, 1, 987654321);",
        "INSERT INTO route_attempts(route_id, attempts, completed, session, last_updated) VALUES (1, 10, true, 1 , 123456789);"
    })
    public void testGetClimbingSessionByIdBasicWrongUser() {

        var fetchedResult = climbingSessionRepository.getClimbingSessionById(999L);

        Assertions.assertNull(fetchedResult);
    }


    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = {
            "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (1, true, 1, 1, 1, 987654321);",
    })
    public void testGetClimbingSessionByIdNoRouteAttempts() {

        var expectedResult = new ClimbingSession(1, 1, 1, 1, 987654321, true, List.of());
        var fetchedResult = climbingSessionRepository.getClimbingSessionById(1L);
        Assertions.assertEquals(expectedResult, fetchedResult);
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    @Sql(statements = {
            "INSERT INTO climbing_sessions(id, active, user_id, group_id, place_id, created_at) VALUES (1, true, 1, 1, 1, 987654321);",
    })
    public void testGetActiveSessionBasic() {}


}
