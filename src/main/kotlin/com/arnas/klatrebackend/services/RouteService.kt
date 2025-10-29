package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.GroupAccessSource
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.Route
import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.dataclasses.RouteResponse
import com.arnas.klatrebackend.dataclasses.RouteUpdateDTO
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.RouteServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import org.springframework.stereotype.Service

@Service
class RouteService(
    private val routeRepository: RouteRepositoryInterface,
    private val imageService: ImageServiceInterface,
    private val groupRepository: GroupRepositoryInterface,
    private val placeRepository: PlaceRepositoryInterface,
    private val accessControlService: AccessControlService,
): RouteServiceInterface {


    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_PLACE, sourceObjectParam = "routeDTO")
    override fun addRoute(userId: Long, routeDTO: RouteDTO): Long {
        val imageId = routeDTO.image?.let {
            return@let imageService.storeImageFile(it, userId)
        }
        val boulderID = routeRepository.addRoute(
            routeDTO = routeDTO,
            imageId = imageId,
            userId = userId,
        )
        return boulderID
    }

    @RequireGroupAccess(minRole = Role.ADMIN, resolveGroupFrom = GroupAccessSource.FROM_ROUTE, sourceObjectParam = "routeDTO")
    override fun updateRoute(routeDTO: RouteUpdateDTO, userId: Long) {

        val oldRoute = routeRepository.getRouteById(routeDTO.routeId)
            ?: throw Exception("Boulder with ID ${routeDTO.routeId} not found, cannot update it.")
        val group = placeRepository.getPlaceById(oldRoute.placeId)?.let {
            return@let groupRepository.getGroupById(it.groupId)
        }
        val newImageId = routeDTO.image?.let { newImage ->
            oldRoute.imageId?.let { imageService.deleteImage(it) }
            return@let imageService.storeImageFile(newImage, userId)
        }

        val newRoute = Route(
            id = routeDTO.routeId,
            name = routeDTO.name?: oldRoute.name,
            gradeId = routeDTO.gradeId?: oldRoute.gradeId,
            placeId = routeDTO.placeId?: oldRoute.placeId,
            description = routeDTO.description?: oldRoute.description,
            active = routeDTO.active?: oldRoute.active,
            imageId = newImageId ?: oldRoute.imageId,
        )
        val rowAffected = routeRepository.updateRoute(newRoute)
        if(rowAffected <= 0) throw Exception("Failed to update boulder")
    }

    override fun deleteRoute(routeId: Long){
        val route = routeRepository.getRouteById(routeId)?: throw Exception("Boulder not found")
        route.imageId?.let { imageService.deleteImage(it) }
        routeRepository.deleteRoute(routeId)
   }

    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse {
        val pagingEnabled = limit > 0
        routeRepository.getRoutesByPlace(placeId, page, limit + 1, pagingEnabled).let {
            val hasMore = it.size > limit
            val numRetired = routeRepository.getNumRoutesInPlace(placeId, false)
            val numActive = routeRepository.getNumRoutesInPlace(placeId, true)
            val routeResponse = RouteResponse(it, page, limit, numActive, numRetired,  hasMore)
            return routeResponse
        }
    }
}