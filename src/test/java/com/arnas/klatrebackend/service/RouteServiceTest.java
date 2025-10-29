package com.arnas.klatrebackend.service;


import com.arnas.klatrebackend.dataclasses.RouteDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    void testTheTest() {
        assert true;
    }

    @Test
    void testAddRoute() {
        RouteDTO routeDTO = new RouteDTO("test", 1, 1, "this is an description", true, null);
        Long expectedRouteId = 123L;
        when(routeRepository.addRoute(eq(routeDTO), nullable(String.class), eq(1L))).thenReturn(expectedRouteId);
        Long result = routeService.addRoute(1L, routeDTO);
        assertEquals(expectedRouteId, result);
    }

    @Test
    void testUpdateRoute() {


    }

}
