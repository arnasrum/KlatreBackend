package com.arnas.klatrebackend.features.routes;

import com.arnas.klatrebackend.features.auth.RequireGroupAccess;
import com.arnas.klatrebackend.features.auth.GroupAccessSource;
import com.arnas.klatrebackend.features.auth.Role;
import com.arnas.klatrebackend.features.images.ImageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceDefault implements RouteService {

    private final RouteRepository routeRepository;
    private final ImageService imageService;

    public RouteServiceDefault(
            RouteRepository routeRepository,
            ImageService imageService
    ) {
        this.routeRepository = routeRepository;
        this.imageService = imageService;
    }

    @Override
    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE, sourceObjectParam = "routeDTO")
    public long addRoute(@NotNull RouteDTO routeDTO, long userId) {
        String imageId = null;
        if(routeDTO.getImage() != null)
            imageId = imageService.storeImageFile(routeDTO.getImage(), userId);
        return routeRepository.addRoute(routeDTO, imageId, userId);
    }

    @Override
    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE, sourceObjectParam = "routeDTO")
    public void updateRoute(@NotNull RouteUpdateDTO routeDTO, long userId) {
        var oldRouteOptional = routeRepository.getRouteById(routeDTO.getRouteId());
        if(oldRouteOptional.isEmpty())
            throw new RuntimeException("Route not found");
        var oldRoute = oldRouteOptional.get();
        String newImageId = null;
        if(routeDTO.getImage() != null) {
            if(oldRoute.getImageId() != null)
                imageService.deleteImage(oldRoute.getImageId());
            newImageId = imageService.storeImageFile(routeDTO.getImage(), userId);
        }

        var newRoute = new Route(
                routeDTO.getRouteId(),
                routeDTO.getName() != null ? routeDTO.getName() : oldRoute.getName(),
                routeDTO.getGradeId() != null ? routeDTO.getGradeId() : oldRoute.getGradeId(),
                routeDTO.getPlaceId() != null ? routeDTO.getPlaceId() : oldRoute.getPlaceId(),
                routeDTO.getDescription() != null ? routeDTO.getDescription() : oldRoute.getDescription(),
                routeDTO.getActive() != null ? routeDTO.getActive() : oldRoute.getActive(),
                routeDTO.getImage() != null ? newImageId : oldRoute.getImageId()
        );
        var rowAffected = routeRepository.updateRoute(newRoute);
        if(rowAffected <= 0) throw new RuntimeException("Failed to update boulder");

    }

    @Override
    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE)
    public void deleteRoute(long routeId) {
        var routeOptional= routeRepository.getRouteById(routeId);
        if(routeOptional.isEmpty()) throw new RuntimeException("Route not found");
        var route = routeOptional.get();
        if(route.getImageId() != null)
            imageService.deleteImage(route.getImageId());
        routeRepository.deleteRoute(routeId);
    }

    @Override
    @NotNull
    public RouteResponse getRoutesByPlace(long placeId, int page, int limit) {
        var pagingEnabled = limit > 0;
        var routes = routeRepository.getRoutesByPlace(placeId, page, limit + 1, pagingEnabled);
        return new RouteResponse(
                routes,
                page,
                limit,
                routeRepository.getNumRoutesInPlace(placeId, true),
                routeRepository.getNumRoutesInPlace(placeId, false),
                routes.size() > limit
        );
    }
}
