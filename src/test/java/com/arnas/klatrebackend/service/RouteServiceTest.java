package com.arnas.klatrebackend.service;


import com.arnas.klatrebackend.dataclasses.RouteDTO;
import com.arnas.klatrebackend.dataclasses.Route;
import com.arnas.klatrebackend.dataclasses.RouteResponse;
import com.arnas.klatrebackend.dataclasses.RouteUpdateDTO;
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface;
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface;
import com.arnas.klatrebackend.services.AccessControlService;
import com.arnas.klatrebackend.services.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    @Mock
    private RouteRepositoryInterface routeRepository;

    @Mock
    private ImageServiceInterface imageService;
    
    @Mock
    private GroupRepositoryInterface groupRepository;

    @Mock
    private PlaceRepositoryInterface placeRepository;

    @Mock
    private AccessControlService accessControlService;

    @InjectMocks
    private RouteService routeService;

    @Test
    void testAddRoute_Success() {
        RouteDTO routeDTO = new RouteDTO("Test Route", 1, 1, "A test route", true, null);
        Long userId = 1L;
        Long expectedRouteId = 123L;
        
        when(routeRepository.addRoute(eq(routeDTO), nullable(String.class), eq(userId)))
            .thenReturn(expectedRouteId);
        
        Long result = routeService.addRoute(userId, routeDTO);
        
        assertEquals(expectedRouteId, result);
        verify(routeRepository).addRoute(eq(routeDTO), nullable(String.class), eq(userId));
    }

    @Test
    void testAddRoute_WithImage() {
        MultipartFile mockImage = mock(MultipartFile.class);
        RouteDTO routeDTO = new RouteDTO("Test Route", 1, 1, "A test route", true, mockImage);
        Long userId = 1L;
        Long expectedRouteId = 123L;
        String imageId = "img-123";
        
        when(imageService.storeImageFile(mockImage, userId)).thenReturn(imageId);
        when(routeRepository.addRoute(any(RouteDTO.class), eq(imageId), eq(userId)))
            .thenReturn(expectedRouteId);
        
        Long result = routeService.addRoute(userId, routeDTO);
        
        assertEquals(expectedRouteId, result);
        verify(imageService).storeImageFile(mockImage, userId);
        verify(routeRepository).addRoute(any(RouteDTO.class), eq(imageId), eq(userId));
    }

    @Test
    void testUpdateRoute_FailsToUpdate_ThrowsException() {
        long routeId = 1L;
        long userId = 1L;
        RouteUpdateDTO routeUpdateDTO = new RouteUpdateDTO(routeId, null, null, null, null, null, null);

        assertThrows(Exception.class, () -> {
            routeService.updateRoute(routeUpdateDTO, userId);
        });
    }

    @Test
    void testDeleteRoute_Success() {
        long routeId = 1L;

        Route route = new Route(routeId, "Route", 1, 1, null, false, "test");

        when(routeRepository.deleteRoute(eq(routeId))).thenReturn(1);
        when(routeRepository.getRouteById(eq(routeId))).thenReturn(route);
        
        routeService.deleteRoute(routeId);

        verify(routeRepository).getRouteById(routeId);
        verify(routeRepository).deleteRoute(routeId);
        verify(imageService).deleteImage("test");
    }

    @Test
    void testDeleteRoute_Fail() {
        long routeId = 1L;

        assertThrows(Exception.class, () -> {
            routeService.deleteRoute(routeId);
        });

    }

    @Test
    void testGetRoutesByPlace_Success() {
        long placeId = 1L;
        int page = 0;
        int limit = 10;
        RouteResponse expectedResponse = new RouteResponse(
            List.of(
                new Route(1L, "Route 1", 1L, 1L, "Desc 1", true, "img-1"),
                new Route(2L, "Route 2", 1L, 2L, "Desc 2", true, "img-2")
            ),
                page,
                limit,
                2,
                0,
                false
        );
        
        when(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true))
            .thenReturn(expectedResponse.getBoulders());
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(true))).thenReturn(2);
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(false))).thenReturn(0);

        RouteResponse result = routeService.getRoutesByPlace(placeId, page, limit);

        assertNotNull(result);
        assertEquals(2, result.getBoulders().size());
        assertEquals(2, result.getActiveBouldersCount());
        assertEquals("Route 1", result.getBoulders().getFirst().getName());
        verify(routeRepository).getRoutesByPlace(placeId, page, limit + 1, true);
        verify(routeRepository).getNumRoutesInPlace(placeId, true);
    }

    @Test
    void testGetRoutesByPlace_EmptyResult() {
        long placeId = 1L;
        int page = 0;
        int limit = 10;
        
        when(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true))
            .thenReturn(List.of());
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(true)))
            .thenReturn(0);
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(false)))
                .thenReturn(0);
        
        RouteResponse result = routeService.getRoutesByPlace(placeId, page, limit);
        
        assertNotNull(result);
        assertTrue(result.getBoulders().isEmpty());
        assertEquals(0, result.getActiveBouldersCount());
        assertEquals(0, result.getRetiredBouldersCount());
    }

    @Test
    void testGetRoutesByPlace_WithPagination() {
        long placeId = 1L;
        int page = 2;
        int limit = 5;
        
        when(routeRepository.getRoutesByPlace(placeId, page, limit + 1, true))
            .thenReturn(List.of(
                new Route(11L, "Route 11", 1L, 1L, "Desc", true, null)
            ));
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(true)))
            .thenReturn(25);
        when(routeRepository.getNumRoutesInPlace(anyLong(), eq(false)))
                .thenReturn(0);
        
        RouteResponse result = routeService.getRoutesByPlace(placeId, page, limit);
        
        assertNotNull(result);
        assertEquals(1, result.getBoulders().size());
        assertEquals(25, result.getActiveBouldersCount());
        verify(routeRepository).getRoutesByPlace(placeId, page, limit + 1, true);
    }

    @Test
    void testUpdateRoute_WithPartialInfo() {
        long routeId = 1L;
        long userId = 1L;
        RouteUpdateDTO routeUpdateDTO = new RouteUpdateDTO(routeId, "New Name", null, null, "Desc", null, null);
        Route oldRoute = new Route(routeId, "oldName", 1, 1, null, false, null);
        Route newRoute = new Route(routeId, "New Name", 1, 1, "Desc", false, null);

        when(routeRepository.getRouteById(eq(routeId))).thenReturn(oldRoute);
        when(routeRepository.updateRoute(eq(newRoute))).thenReturn(1);

        routeService.updateRoute(routeUpdateDTO, userId);
        
        verify(routeRepository).updateRoute(newRoute);
    }

    @Test
    void testAddRoute_NullImageHandling() {
        RouteDTO routeDTO = new RouteDTO("Test Route", 1, 1, "Description", true, null);
        Long userId = 1L;
        Long expectedRouteId = 100L;
        
        when(routeRepository.addRoute(eq(routeDTO), isNull(), eq(userId)))
            .thenReturn(expectedRouteId);
        
        Long result = routeService.addRoute(userId, routeDTO);
        
        assertEquals(expectedRouteId, result);
        verify(imageService, never()).storeImageFile(any(), anyLong());
        verify(routeRepository).addRoute(eq(routeDTO), isNull(), eq(userId));
    }

    @Test
    void testUpdateRoute_AllFieldsUpdate() {
        long routeId = 1L;
        long userId = 1L;
        RouteUpdateDTO routeUpdateDTO = new RouteUpdateDTO(routeId, "New Name", (long)99, (long)99, "Desc", true, null);
        Route oldRoute = new Route(routeId, "oldName", 1, 1, null, false, null);
        Route newRoute = new Route(routeId, "New Name", 99, 99, "Desc", true, null);

        when(routeRepository.getRouteById(eq(routeId))).thenReturn(oldRoute);
        when(routeRepository.updateRoute(eq(newRoute))).thenReturn(1);

        routeService.updateRoute(routeUpdateDTO, userId);

        verify(routeRepository).updateRoute(newRoute);
    }
}
