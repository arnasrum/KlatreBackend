package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.repositories.BoulderRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface
import com.arnas.klatrebackend.interfaces.repositories.PlaceRepositoryInterface
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.interfaces.services.ImageServiceInterface
import com.arnas.klatrebackend.repositories.BoulderRepository
import com.arnas.klatrebackend.repositories.GroupRepository
import com.arnas.klatrebackend.repositories.PlaceRepository
import com.arnas.klatrebackend.repositories.RouteSendRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class BoulderService(
    private val boulderRepository: BoulderRepositoryInterface,
    private val imageService: ImageServiceInterface,
    private val groupRepository: GroupRepositoryInterface,
    private val placeRepository: PlaceRepositoryInterface,
): BoulderServiceInterface {


    fun getBoulder(userId: Long, boulderId: Long): ServiceResult<Boulder> {
        try {
            val boulder = boulderRepository.getRouteById(routeId = boulderId)?: throw Exception("Boulder not found")
            placeRepository.getPlaceById(boulder.place)?.let {
                groupRepository.getGroupUsers(it.groupID)
                    .any { user -> user.id == userId }
                    .let { hasAccess -> if (!hasAccess) throw Exception("User has no access to this boulder") }
            }
            return ServiceResult(success = true, message = "Boulder retrieved successfully", data = boulder)
        } catch (e: Exception) {
            return ServiceResult(success = false, message = e.message, data = null)
        }
    }


    override fun addBoulder(userId: Long, placeId: Long, name: String, grade: Long, description: String?): ServiceResult<Long> {
        val boulderRequest = BoulderRequest(
            name = name,
            grade = grade,
            place = placeId,
            description = description,
        )
        val boulderID = boulderRepository.addBoulder(userId, boulderRequest)

        return ServiceResult(success = true, message = "Boulder added successfully", data = boulderID)
    }

   override fun updateBoulder(boulderId: Long, userId: Long, boulderInfo: Map<String, String>, image: MultipartFile?): ServiceResult<String> {
       // Check if user has permission to update boulder
       val name = boulderInfo["name"]
       val place = boulderInfo["place"]?.toLong()
       val description = boulderInfo["description"]
       val groupId = boulderInfo["groupID"]
       val grade = boulderInfo["grade"]?.toLong()
       boulderRepository.updateBoulder(boulderId, name, grade, place, description)
       image?.let {
           imageService.storeImageFile(image, boulderId, userId)
       }
       return ServiceResult(success = true, message = "Boulder updated successfully")
   }

   override fun deleteBoulder(boulderId: Long): ServiceResult<Unit> {
        try {
            imageService.getImage(boulderId)?.let { imageService.deleteImage(it) }
            boulderRepository.deleteBoulder(boulderId)
            return ServiceResult(success = true, message = "Boulder deleted successfully")
        } catch (e: Exception) {
            return ServiceResult(success = false, message = "Error deleting boulder", data = null)
        }
   }

    override fun getBouldersByPlace(placeId: Long): ServiceResult<List<Boulder>> {
        try {
            boulderRepository.getBouldersByPlace(placeId).let { return ServiceResult(success = true, message = "Boulders retrieved successfully", data = it) }
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult(success = false, message = "Error getting boulders by place", data = null)
        }
    }



}