package com.arnas.klatrebackend.features.places

import org.junit.jupiter.api.Assertions.*
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
@Import(PlaceRepositoryDefault::class)
class PlaceRepositoryTest {

    @Autowired
    private lateinit var placeRepository: PlaceRepositoryDefault

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
    fun `getPlacesByGroupId returns places for group`() {
        val places = placeRepository.getPlacesByGroupId(1)
        assertEquals(2, places.size)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `getPlacesByGroupId returns empty list for group with no places`() {
        val places = placeRepository.getPlacesByGroupId(999)
        assertTrue(places.isEmpty())
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `getPlaceById returns existing place`() {
        val place = placeRepository.getPlaceById(1)
        assertNotNull(place)
        assertEquals("LA", place?.name)
        assertEquals(1L, place?.groupId)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `getPlaceById returns null for non-existent place`() {
        val place = placeRepository.getPlaceById(999)
        assertNull(place)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `addPlaceToGroup creates new place`() {
        val placeId = placeRepository.addPlaceToGroup(1, "New Place", "A description")
        assertTrue(placeId > 0)

        val place = placeRepository.getPlaceById(placeId)
        assertNotNull(place)
        assertEquals("New Place", place?.name)
        assertEquals("A description", place?.description)
        assertEquals(1L, place?.groupId)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `addPlaceToGroup with null description`() {
        val placeId = placeRepository.addPlaceToGroup(1, "No Desc Place", null)
        assertTrue(placeId > 0)

        val place = placeRepository.getPlaceById(placeId)
        assertNotNull(place)
        assertNull(place?.description)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `updatePlace modifies existing place`() {
        val place = placeRepository.getPlaceById(1)
        assertNotNull(place)

        val updatedPlace = place!!.copy(name = "Updated LA", description = "New desc")
        val rowsAffected = placeRepository.updatePlace(updatedPlace)
        assertEquals(1, rowsAffected)

        val fetched = placeRepository.getPlaceById(1)
        assertEquals("Updated LA", fetched?.name)
        assertEquals("New desc", fetched?.description)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `deletePlace removes place`() {
        // Place 3 (Test, group 2) has no routes pointing to it
        val place = placeRepository.getPlaceById(3)
        assertNotNull(place)

        val rowsAffected = placeRepository.deletePlace(3)
        assertEquals(1, rowsAffected)

        val fetched = placeRepository.getPlaceById(3)
        assertNull(fetched)
    }

    @Test
    @Sql("/database/schema.sql")
    @Sql("/database/data.sql")
    fun `deletePlace returns 0 for non-existent place`() {
        val rowsAffected = placeRepository.deletePlace(999)
        assertEquals(0, rowsAffected)
    }
}
