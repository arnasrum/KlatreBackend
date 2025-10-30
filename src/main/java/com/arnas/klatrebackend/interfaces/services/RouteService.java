package com.arnas.klatrebackend.interfaces.services;

import com.arnas.klatrebackend.dataclasses.RouteResponse;
import com.arnas.klatrebackend.dataclasses.RouteDTO;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface RouteService {

    Long addRoute(Long userId, RouteDTO routeDTO);
    void updateRoute(Long routeId, Long userId, RouteDTO routeDTO, @Nullable MultipartFile image);

    void deleteRoute(@NonNull Long routeId);

    RouteResponse getRoutesByPlace(Long placeId, Integer page, Integer limit);


}
