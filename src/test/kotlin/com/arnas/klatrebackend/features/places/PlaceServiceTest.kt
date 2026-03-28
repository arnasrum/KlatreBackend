package com.arnas.klatrebackend.features.places

import com.arnas.klatrebackend.features.gradesystems.Grade
import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepository
import com.arnas.klatrebackend.features.routes.Route
import com.arnas.klatrebackend.features.routes.RouteRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class PlaceServiceTest {

    @Mock
    private lateinit var placeRepository: PlaceRepository

    @Mock
    private lateinit var gradingSystemRepository: GradeSystemRepository

    @Mock
    private lateinit var routeService: RouteRepository

    @InjectMocks
    private lateinit var placeService: PlaceServiceDefault

    // --- getPlaceById ---

    @Test
    fun `getPlaceById returns place when found`() {
        val place = Place(1L, "Test Place", "desc", 1L, 1L)
        `when`(placeRepository.getPlaceById(1L)).thenReturn(place)

        val result = placeService.getPlaceById(1L)

        assertNotNull(result)
        assertEquals("Test Place", result?.name)
    }

    @Test
    fun `getPlaceById returns null when not found`() {
        `when`(placeRepository.getPlaceById(999L)).thenReturn(null)

        val result = placeService.getPlaceById(999L)

        assertNull(result)
    }

    // --- getPlacesByGroupId ---

    @Test
    fun `getPlacesByGroupId returns places with grades`() {
        val groupId = 1L
        val userId = 1L
        val gradingSystemId = 10L
        val places = listOf(
            Place(1L, "Place 1", null, groupId, gradingSystemId),
            Place(2L, "Place 2", "desc", groupId, gradingSystemId)
        )
        val grades = listOf(Grade(1L, "V0", 1), Grade(2L, "V1", 2))

        `when`(placeRepository.getPlacesByGroupId(groupId)).thenReturn(places)
        `when`(gradingSystemRepository.getGradesBySystemId(gradingSystemId)).thenReturn(grades)

        val result = placeService.getPlacesByGroupId(groupId, userId)

        assertEquals(2, result.size)
        assertEquals("Place 1", result[0].name)
        assertEquals(2, result[0].gradingSystem.grades.size)
        assertEquals(gradingSystemId, result[0].gradingSystem.id)
    }

    @Test
    fun `getPlacesByGroupId returns empty list for group with no places`() {
        `when`(placeRepository.getPlacesByGroupId(999L)).thenReturn(emptyList())

        val result = placeService.getPlacesByGroupId(999L, 1L)

        assertTrue(result.isEmpty())
    }

    // --- updatePlace ---

    @Test
    fun `updatePlace succeeds with partial update`() {
        val placeId = 1L
        val existingPlace = Place(placeId, "Old Name", "Old Desc", 1L, 1L)
        val updateDTO = PlaceUpdateDTO(placeId, "New Name", null, null, null)
        // The service builds: name from DTO ("New Name"), description from existing ("Old Desc"),
        // groupId from existing (1L), gradingSystemId unchanged (1L)
        val expectedUpdatedPlace = Place(placeId, "New Name", "Old Desc", 1L, 1L)

        `when`(placeRepository.getPlaceById(placeId)).thenReturn(existingPlace)
        `when`(placeRepository.updatePlace(expectedUpdatedPlace)).thenReturn(1)

        assertDoesNotThrow { placeService.updatePlace(1L, updateDTO) }

        verify(placeRepository).updatePlace(expectedUpdatedPlace)
    }

    @Test
    fun `updatePlace throws when place not found`() {
        val updateDTO = PlaceUpdateDTO(999L, "Name", null, null, null)

        `when`(placeRepository.getPlaceById(999L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            placeService.updatePlace(1L, updateDTO)
        }
    }

    @Test
    fun `updatePlace throws when update fails`() {
        val placeId = 1L
        val existingPlace = Place(placeId, "Old Name", "Old Desc", 1L, 1L)
        val updateDTO = PlaceUpdateDTO(placeId, "New Name", null, null, null)
        val expectedUpdatedPlace = Place(placeId, "New Name", "Old Desc", 1L, 1L)

        `when`(placeRepository.getPlaceById(placeId)).thenReturn(existingPlace)
        `when`(placeRepository.updatePlace(expectedUpdatedPlace)).thenReturn(0)

        assertThrows(RuntimeException::class.java) {
            placeService.updatePlace(1L, updateDTO)
        }
    }

    // --- updatePlaceGradingSystem ---

    @Test
    fun `updatePlaceGradingSystem returns old system when new is null`() {
        val result = placeService.updatePlaceGradingSystem(1L, 1L, null)
        assertEquals(1L, result)
    }

    @Test
    fun `updatePlaceGradingSystem returns old system when same id`() {
        val result = placeService.updatePlaceGradingSystem(1L, 5L, 5L)
        assertEquals(5L, result)
    }

    @Test
    fun `updatePlaceGradingSystem migrates routes to new grading system`() {
        val placeId = 1L
        val oldSystemId = 1L
        val newSystemId = 2L

        val oldGrades = listOf(Grade(10L, "V0", 1), Grade(11L, "V1", 2))
        val newGrades = listOf(Grade(20L, "5.4", 1), Grade(21L, "5.5", 2))
        val route = Route(1L, "Route 1", 10L, placeId, null, true, null)
        val routes = listOf(route)
        // Route has gradeId=10L which matches oldGrades[0] with numericalValue=1
        // Closest in newGrades is 20L (numericalValue=1)
        val expectedUpdatedRoute = route.copy(gradeId = 20L)

        `when`(routeService.getRoutesByPlace(placeId)).thenReturn(routes)
        `when`(gradingSystemRepository.getGradesBySystemId(oldSystemId)).thenReturn(oldGrades)
        `when`(gradingSystemRepository.getGradesBySystemId(newSystemId)).thenReturn(newGrades)
        `when`(routeService.updateRoute(expectedUpdatedRoute)).thenReturn(1)

        val result = placeService.updatePlaceGradingSystem(placeId, oldSystemId, newSystemId)

        assertEquals(newSystemId, result)
        verify(routeService).updateRoute(expectedUpdatedRoute)
    }

    @Test
    fun `updatePlaceGradingSystem with no routes returns new system id`() {
        val placeId = 1L
        val oldSystemId = 1L
        val newSystemId = 2L

        `when`(routeService.getRoutesByPlace(placeId)).thenReturn(emptyList())
        `when`(gradingSystemRepository.getGradesBySystemId(oldSystemId)).thenReturn(listOf(Grade(10L, "V0", 1)))
        `when`(gradingSystemRepository.getGradesBySystemId(newSystemId)).thenReturn(listOf(Grade(20L, "5.4", 1)))

        val result = placeService.updatePlaceGradingSystem(placeId, oldSystemId, newSystemId)

        assertEquals(newSystemId, result)
    }

    @Test
    fun `updatePlaceGradingSystem throws when route update fails`() {
        val placeId = 1L
        val oldSystemId = 1L
        val newSystemId = 2L

        val oldGrades = listOf(Grade(10L, "V0", 1))
        val newGrades = listOf(Grade(20L, "5.4", 1))
        val route = Route(1L, "Route 1", 10L, placeId, null, true, null)
        val expectedUpdatedRoute = route.copy(gradeId = 20L)

        `when`(routeService.getRoutesByPlace(placeId)).thenReturn(listOf(route))
        `when`(gradingSystemRepository.getGradesBySystemId(oldSystemId)).thenReturn(oldGrades)
        `when`(gradingSystemRepository.getGradesBySystemId(newSystemId)).thenReturn(newGrades)
        `when`(routeService.updateRoute(expectedUpdatedRoute)).thenReturn(0)

        assertThrows(RuntimeException::class.java) {
            placeService.updatePlaceGradingSystem(placeId, oldSystemId, newSystemId)
        }
    }
}
