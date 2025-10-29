package com.arnas.klatrebackend.repository;

import com.arnas.klatrebackend.repositories.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@Import(PlaceRepository.class)
public class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");






}
