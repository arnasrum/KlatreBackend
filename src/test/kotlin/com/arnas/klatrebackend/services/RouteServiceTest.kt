package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.features.routes.Route
import com.arnas.klatrebackend.features.routes.RouteDTO
import com.arnas.klatrebackend.features.routes.RouteRepository
import com.arnas.klatrebackend.features.routes.RouteServiceDefault
import com.arnas.klatrebackend.features.images.ImageService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

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
    fun `getRoutesByPlace with zero limit disables paging`() {
        val placeId = 1L
        val page = 0
        val limit = 0

        `when`(routeRepository.getRoutesByPlace(placeId, page, 1, false))
            .thenReturn(emptyList())
        `when`(routeRepository.getNumRoutesInPlace(placeId, true)).thenReturn(0)
        `when`(routeRepository.getNumRoutesInPlace(placeId, false)).thenReturn(0)

        val result = routeService.getRoutesByPlace(placeId, page, limit)

        assertNotNull(result)
        assertEquals(0, result.limit)
        verify(routeRepository).getRoutesByPlace(placeId, page, 1, false)
    }

    @Test
    fun `deleteRoute without image does not call deleteImage`() {
        val routeId = 1L
        val route = Route(routeId, "Route", 1, 1, null, true, null)

        `when`(routeRepository.getRouteById(routeId)).thenReturn(Optional.of(route))

        routeService.deleteRoute(routeId)

        verify(imageService, never()).deleteImage(anyString())
        verify(routeRepository).deleteRoute(routeId)
    }

    @Test
    fun `addRoute with image stores image and passes id`() {
        val mockImage = mock(MultipartFile::class.java)
        val userId = 1L
        val routeDTO = RouteDTO("Route", 1, 1, null, true, mockImage)

        `when`(imageService.storeImageFile(mockImage, userId)).thenReturn("img-abc")
        doReturn(5L).`when`(routeRepository).addRoute(routeDTO, "img-abc", userId)

        val result = routeService.addRoute(routeDTO, userId)

        assertEquals(5L, result)
        verify(imageService).storeImageFile(mockImage, userId)
    }

    @Test
    fun `getRoutesByPlace hasMore is true when more results than limit`() {
        val placeId = 1L
        val page = 0
        val limit = 2
        val routes = listOf(
            Route(1L, "R1", 1, placeId, null, true, null),
            Route(2L, "R2", 1, placeId, null, true, null),
            Route(3L, "R3", 1, placeId, null, true, null)
        )

        `when`(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true)).thenReturn(routes)
        `when`(routeRepository.getNumRoutesInPlace(placeId, true)).thenReturn(5)
        `when`(routeRepository.getNumRoutesInPlace(placeId, false)).thenReturn(0)

        val result = routeService.getRoutesByPlace(placeId, page, limit)

        assertTrue(result.hasMore)
    }
}