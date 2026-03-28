package com.arnas.klatrebackend.features.routes

import com.arnas.klatrebackend.features.images.ImageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile
import java.util.Optional
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class RouteServiceTest {

    @Mock
    private lateinit var routeRepository: RouteRepository

    @Mock
    private lateinit var imageService: ImageService

    @InjectMocks
    private lateinit var routeService: RouteServiceDefault

    @Test
    fun testAddRoute_Success() {
        val routeDTO = RouteDTO("Test Route", 1, 1, "A test route", true, null)
        val userId = 1L
        val expectedRouteId = 123L

        `when`(routeRepository.addRoute(routeDTO, null, userId)).thenReturn(expectedRouteId)

        val result = routeService.addRoute(routeDTO, userId)

        assertEquals(expectedRouteId, result)
        verify(routeRepository).addRoute(routeDTO, null, userId)
    }

    @Test
    fun testAddRoute_WithImage() {
        val mockImage = mock(MultipartFile::class.java)
        val routeDTO = RouteDTO("Test Route", 1, 1, "A test route", true, mockImage)
        val userId = 1L
        val expectedRouteId = 123L
        val imageId = "img-123"

        `when`(imageService.storeImageFile(mockImage, userId)).thenReturn(imageId)
        `when`(routeRepository.addRoute(routeDTO, imageId, userId)).thenReturn(expectedRouteId)

        val result = routeService.addRoute(routeDTO, userId)

        assertEquals(expectedRouteId, result)
        verify(imageService).storeImageFile(mockImage, userId)
        verify(routeRepository).addRoute(routeDTO, imageId, userId)
    }

    @Test
    fun testUpdateRoute_FailsToUpdate_ThrowsException() {
        val routeId = 1L
        val userId = 1L
        val routeUpdateDTO = RouteUpdateDTO(routeId, null, null, null, null, null, null)

        assertThrows(Exception::class.java) {
            routeService.updateRoute(routeUpdateDTO, userId)
        }
    }

    @Test
    fun testDeleteRoute_Success() {
        val routeId = 1L
        val route = Route(routeId, "Route", 1, 1, null, false, "test")

        `when`(routeRepository.deleteRoute(routeId)).thenReturn(1)
        `when`(routeRepository.getRouteById(routeId)).thenReturn(Optional.of(route))

        routeService.deleteRoute(routeId)

        verify(routeRepository).getRouteById(routeId)
        verify(routeRepository).deleteRoute(routeId)
        verify(imageService).deleteImage("test")
    }

    @Test
    fun testDeleteRoute_Fail() {
        val routeId = 1L

        `when`(routeRepository.getRouteById(routeId)).thenReturn(Optional.empty())

        assertThrows(Exception::class.java) {
            routeService.deleteRoute(routeId)
        }
    }

    @Test
    fun testGetRoutesByPlace_Success() {
        val placeId = 1L
        val page = 0
        val limit = 10
        val routes = listOf(
            Route(1L, "Route 1", 1L, 1L, "Desc 1", true, "img-1"),
            Route(2L, "Route 2", 1L, 2L, "Desc 2", true, "img-2")
        )

        `when`(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true)).thenReturn(routes)
        `when`(routeRepository.getNumRoutesInPlace(placeId, true)).thenReturn(2)
        `when`(routeRepository.getNumRoutesInPlace(placeId, false)).thenReturn(0)

        val result = routeService.getRoutesByPlace(placeId, page, limit)

        assertNotNull(result)
        assertEquals(2, result.boulders.size)
        assertEquals(2, result.activeBouldersCount)
        assertEquals("Route 1", result.boulders.first().name)
        verify(routeRepository).getRoutesByPlace(placeId, page, limit + 1, true)
        verify(routeRepository).getNumRoutesInPlace(placeId, true)
    }

    @Test
    fun testGetRoutesByPlace_EmptyResult() {
        val placeId = 1L
        val page = 0
        val limit = 10

        `when`(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true)).thenReturn(emptyList())
        `when`(routeRepository.getNumRoutesInPlace(placeId, true)).thenReturn(0)
        `when`(routeRepository.getNumRoutesInPlace(placeId, false)).thenReturn(0)

        val result = routeService.getRoutesByPlace(placeId, page, limit)

        assertNotNull(result)
        assertTrue(result.boulders.isEmpty())
        assertEquals(0, result.activeBouldersCount)
        assertEquals(0, result.retiredBouldersCount)
    }

    @Test
    fun testGetRoutesByPlace_WithPagination() {
        val placeId = 1L
        val page = 2
        val limit = 5

        `when`(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true))
            .thenReturn(listOf(Route(11L, "Route 11", 1L, 1L, "Desc", true, null)))
        `when`(routeRepository.getNumRoutesInPlace(placeId, true)).thenReturn(25)
        `when`(routeRepository.getNumRoutesInPlace(placeId, false)).thenReturn(0)

        val result = routeService.getRoutesByPlace(placeId, page, limit)

        assertNotNull(result)
        assertEquals(1, result.boulders.size)
        assertEquals(25, result.activeBouldersCount)
        verify(routeRepository).getRoutesByPlace(placeId, page, limit + 1, true)
    }

    @Test
    fun testUpdateRoute_WithPartialInfo() {
        val routeId = 1L
        val userId = 1L
        val routeUpdateDTO = RouteUpdateDTO(routeId, "New Name", null, null, "Desc", null, null)
        val oldRoute = Route(routeId, "oldName", 1, 1, null, false, null)
        val newRoute = Route(routeId, "New Name", 1, 1, "Desc", false, null)

        `when`(routeRepository.getRouteById(routeId)).thenReturn(Optional.of(oldRoute))
        `when`(routeRepository.updateRoute(newRoute)).thenReturn(1)

        routeService.updateRoute(routeUpdateDTO, userId)

        verify(routeRepository).updateRoute(newRoute)
    }

    @Test
    fun testAddRoute_NullImageHandling() {
        val routeDTO = RouteDTO("Test Route", 1, 1, "Description", true, null)
        val userId = 1L
        val expectedRouteId = 100L

        `when`(routeRepository.addRoute(routeDTO, null, userId)).thenReturn(expectedRouteId)

        val result = routeService.addRoute(routeDTO, userId)

        assertEquals(expectedRouteId, result)
        verify(routeRepository).addRoute(routeDTO, null, userId)
    }

    @Test
    fun testUpdateRoute_AllFieldsUpdate() {
        val routeId = 1L
        val userId = 1L
        val routeUpdateDTO = RouteUpdateDTO(routeId, "New Name", 99L, 99L, "Desc", true, null)
        val oldRoute = Route(routeId, "oldName", 1, 1, null, false, null)
        val newRoute = Route(routeId, "New Name", 99, 99, "Desc", true, null)

        `when`(routeRepository.getRouteById(routeId)).thenReturn(Optional.of(oldRoute))
        `when`(routeRepository.updateRoute(newRoute)).thenReturn(1)

        routeService.updateRoute(routeUpdateDTO, userId)

        verify(routeRepository).updateRoute(newRoute)
    }
}
