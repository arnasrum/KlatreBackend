package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest
import com.arnas.klatrebackend.dataclasses.BoulderResponse
import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.repositories.BoulderRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class BoulderService(
    private val boulderRepository: BoulderRepositoryInterface,
    private val imageService: ImageServiceInterface,
    private val groupRepository: GroupRepositoryInterface,
    private val placeRepository: PlaceRepositoryInterface,
    private val accessControlService: AccessControlService,
): BoulderServiceInterface {


    fun getBoulder(userId: Long, boulderId: Long): ServiceResult<Boulder> {
        val boulder = boulderRepository.getRouteById(routeId = boulderId)?: throw Exception("Boulder not found")
        placeRepository.getPlaceById(boulder.place)?.let {
            groupRepository.getGroupUsers(it.groupID)
                .any { user -> user.id == userId }
                .let { hasAccess -> if (!hasAccess) throw Exception("User has no access to this boulder") }
        }
        return ServiceResult(success = true, message = "Boulder retrieved successfully", data = boulder)
    }


    override fun addBoulder(userId: Long, placeId: Long, name: String, grade: Long, description: String?, image: MultipartFile?): Long {
        val imageId = image?.let {
            return@let imageService.storeImageFile(it, userId)
        }
        val boulderID = boulderRepository.addBoulder(
            name = name,
            grade = grade,
            place = placeId,
            description = description,
            active = true,
            imageId = imageId,
            userId = userId,
        )
        return boulderID
    }

    override fun updateBoulder(routeId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?) {

        val oldRoute = boulderRepository.getRouteById(routeId)
            ?: throw Exception("Boulder with ID $routeId not found, cannot update it.")
        val group = placeRepository.getPlaceById(oldRoute.place)?.let {
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
        val rowAffected = boulderRepository.updateBoulder(oldRoute.id, name, grade, place, description, active, newImageId)
        if(rowAffected <= 0) throw Exception("Failed to update boulder")
    }
    override fun deleteBoulder(routeId: Long){
        val route = boulderRepository.getRouteById(routeId)?: throw Exception("Boulder not found")
        route.image?.let { imageService.deleteImage(it) }
        boulderRepository.deleteBoulder(routeId)
   }

    override fun getBouldersByPlace(placeId: Long, page: Int, limit: Int): BoulderResponse {
        val pagingEnabled = limit > 0
        boulderRepository.getBouldersByPlace(placeId, page, limit + 1, pagingEnabled).let {
            val hasMore = it.size > limit
            val numRetired = boulderRepository.getNumBouldersInPlace(placeId, false)
            val numActive = boulderRepository.getNumBouldersInPlace(placeId, true)
            val boulderResponse = BoulderResponse(it, page, limit, numActive, numRetired,  hasMore)
            return boulderResponse
        }
    }
}