package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.Route;
import com.arnas.klatrebackend.dataclasses.RouteDTO;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RouteRepository {

    Optional<Route> getRouteById(Long routeId);
    Route[] getRoutesByPlace(@NonNull Long placeId, int page, int limit, boolean pagingEnabled);
    Long insertRoute(@NonNull RouteDTO routeDTO);
    int updateRoute(@NonNull Route route);
    int deleteRoute(@NonNull Long routeId);
    int getNumRoutesInPlace(@NonNull Long placeId, boolean countActive);

}
