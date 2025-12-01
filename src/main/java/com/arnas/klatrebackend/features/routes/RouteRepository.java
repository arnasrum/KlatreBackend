package com.arnas.klatrebackend.features.routes;

import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.List;

public interface RouteRepository {

    Optional<Route> getRouteById(long routeId);
    List<Route> getRoutesByPlace(long placeId, int page, int limit, boolean pagingEnabled);
    List<Route> getRoutesByPlace(long placeId);

    long addRoute(@NonNull RouteDTO routeDTO, String imageId, long userId);
    int updateRoute(@NonNull Route route);
    int deleteRoute(long routeId);
    int getNumRoutesInPlace(long placeId, boolean countActive);

}
