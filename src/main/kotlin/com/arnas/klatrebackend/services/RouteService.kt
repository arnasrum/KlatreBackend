package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.dataclasses.GroupAccessSource
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.RouteDTO
import com.arnas.klatrebackend.dataclasses.RouteResponse
import com.arnas.klatrebackend.interfaces.repositories.RouteRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.RouteServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class RouteService(
    private val boulderRepository: RouteRepositoryInterface,
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
        val boulderID = boulderRepository.addRoute(
            name = routeDTO.name,
            grade = routeDTO.gradeId,
            place = routeDTO.placeId,
            description = routeDTO.description,
            active = true,
            imageId = imageId,
            userId = userId,
        )
        return boulderID
    }

    override fun updateRoute(routeId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?) {

        val oldRoute = boulderRepository.getRouteById(routeId)
            ?: throw Exception("Boulder with ID $routeId not found, cannot update it.")
        val group = placeRepository.getPlaceById(oldRoute.placeId)?.let {
            return@let groupRepository.getGroupById(it.groupID)
        }
        val role = accessControlService.getUserGroupRole(userId, group!!.id) ?: throw Exception("User has no access to this boulder")
        if(role > Role.ADMIN.id) throw Exception("User has no access to this boulder")
        val name = boulderInfo["name"]
        val place = boulderInfo["place"]?.toLong()
        val description = boulderInfo["description"]
        val grade = boulderInfo["grade"]?.toLong()
        val active = boulderInfo["active"]?.toBoolean()
        val newImageId = image?.let {
            oldRoute.image?.let {
                imageService.deleteImage(it)
            }
            return@let imageService.storeImageFile(image,  userId)
        }
        val rowAffected = boulderRepository.updateRoute(oldRoute.id, name, grade, place, description, active, newImageId)
        if(rowAffected <= 0) throw Exception("Failed to update boulder")
    }
    override fun deleteRoute(routeId: Long){
        val route = boulderRepository.getRouteById(routeId)?: throw Exception("Boulder not found")
        route.image?.let { imageService.deleteImage(it) }
        boulderRepository.deleteRoute(routeId)
   }

    override fun getRoutesByPlace(placeId: Long, page: Int, limit: Int): RouteResponse {
        val pagingEnabled = limit > 0
        boulderRepository.getRoutesByPlace(placeId, page, limit + 1, pagingEnabled).let {
            val hasMore = it.size > limit
            val numRetired = boulderRepository.getNumRoutesInPlace(placeId, false)
            val numActive = boulderRepository.getNumRoutesInPlace(placeId, true)
            val routeResponse = RouteResponse(it, page, limit, numActive, numRetired,  hasMore)
            return routeResponse
        }
    }
}