package com.arnas.klatrebackend.features.routes

import com.arnas.klatrebackend.features.auth.RequireGroupAccess
import com.arnas.klatrebackend.features.auth.GroupAccessSource
import com.arnas.klatrebackend.features.auth.Role
import com.arnas.klatrebackend.features.images.ImageService
import org.springframework.stereotype.Service

@Service
class RouteServiceDefault(
    private val routeRepository: RouteRepository,
    private val imageService: ImageService
) : RouteService {

    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE, sourceObjectParam = "routeDTO")
    override fun addRoute(routeDTO: RouteDTO, userId: Long): Long {
        val image = routeDTO.image
        val imageId: String? = if (image != null) {
            imageService.storeImageFile(image, userId)
        } else null
        return routeRepository.addRoute(routeDTO, imageId, userId)
    }

    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE, sourceObjectParam = "routeDTO")
    override fun updateRoute(routeDTO: RouteUpdateDTO, userId: Long) {
        val oldRoute = routeRepository.getRouteById(routeDTO.routeId).orElseThrow {
            RuntimeException("Route not found")
        }
        val image = routeDTO.image
        var newImageId: String? = null
        if (image != null) {
            val oldImageId = oldRoute.imageId
            if (oldImageId != null) {
                imageService.deleteImage(oldImageId)
            }
            newImageId = imageService.storeImageFile(image, userId)
        }

        val newRoute = Route(
            id = routeDTO.routeId,
            name = routeDTO.name ?: oldRoute.name,
            gradeId = routeDTO.gradeId ?: oldRoute.gradeId,
            placeId = routeDTO.placeId ?: oldRoute.placeId,
            description = routeDTO.description ?: oldRoute.description,
            active = routeDTO.active ?: oldRoute.active,
            imageId = if (image != null) newImageId else oldRoute.imageId
        )
        val rowAffected = routeRepository.updateRoute(newRoute)
        if (rowAffected <= 0) throw RuntimeException("Failed to update boulder")
    }

    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE)
    override fun deleteRoute(routeId: Long) {
        val route = routeRepository.getRouteById(routeId).orElseThrow {
            RuntimeException("Route not found")
        }
        val imageId = route.imageId
        if (imageId != null) {
            imageService.deleteImage(imageId)
        }
        routeRepository.deleteRoute(routeId)
    }

    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse {
        val pagingEnabled = limit > 0
        val routes = routeRepository.getRoutesByPlace(placeId, page, limit + 1, pagingEnabled)
        return RouteResponse(
            boulders = routes,
            page = page,
            limit = limit,
            activeBouldersCount = routeRepository.getNumRoutesInPlace(placeId, true),
            retiredBouldersCount = routeRepository.getNumRoutesInPlace(placeId, false),
            hasMore = routes.size > limit
        )
    }
}

