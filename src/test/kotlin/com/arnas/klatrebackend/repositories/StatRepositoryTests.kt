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
@Import(StatRepository::class)
class StatRepositoryTests {


    @Autowired
    lateinit var statRepository: StatRepository

    companion object {
        @Container
        @ServiceConnection
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/routeAttempts.sql")
    fun testGroupActivityOverTime() {
        statRepository.groupActivityOverTime(201, "month")
        assert(true)
    }




}