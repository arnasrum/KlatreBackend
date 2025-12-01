package com.arnas.klatrebackend.features.routes;

import org.springframework.lang.NonNull;

public interface RouteService {

    long addRoute(@NonNull RouteDTO routeDTO, long userId);
    void updateRoute(@NonNull RouteUpdateDTO routeDTO, long userId);
    void deleteRoute(long routeId);
    @NonNull
    RouteResponse getRoutesByPlace(long placeId, int page, int limit);

}
