package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.dataclasses.BoulderRequest
import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.interfaces.services.BoulderServiceInterface
import com.arnas.klatrebackend.repositories.BoulderRepository
import com.arnas.klatrebackend.repositories.RouteSendRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class BoulderService(
    private val boulderRepository: BoulderRepository,
    private val imageService: ImageService,
    private val routeSendRepository: RouteSendRepository,
): BoulderServiceInterface {


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


}