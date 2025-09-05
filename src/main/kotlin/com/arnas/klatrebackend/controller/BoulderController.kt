package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.service.BoulderService
import com.arnas.klatrebackend.service.ImageService
import com.arnas.klatrebackend.service.UserService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "Boulder", description = "Boulder CRUD operations")
@RequestMapping("/boulders")
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class BoulderController(
    private val userService: UserService,
    private val boulderService: BoulderService,
    private val imageService: ImageService,
) {


    @GetMapping("")
    open fun getBoulders(user: User): ResponseEntity<List<Boulder>> {
        val boulders = boulderService.getBouldersByUser(user.id)
        return ResponseEntity(boulders, HttpStatus.OK)
    }

    @GetMapping("/place")
    open fun getBouldersByPlace(@RequestParam placeID: Long, user: User): ResponseEntity<out Any> {
        val userID: Long = user.id
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.getBouldersWithSendsByPlace(userID, placeID)
        if (!serviceResult.success) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(serviceResult.data, HttpStatus.OK)

    }

    @PostMapping("/place/add")
    open fun addBoulderToPlace(
        @RequestParam placeID: Long,
        @RequestParam name: String,
        @RequestParam grade: String,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<out Any> {
        val userID: Long = user.id
        
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val requestBody = mutableMapOf<String, String>().apply {
            put("name", name)
            put("grade", grade)
            description?.let { put("description", it) }
        }
        val serviceResult = boulderService.addBoulderToPlace(userID, placeID, requestBody)
        serviceResult.data?: return ResponseEntity.internalServerError().body(null)
        image?.let {
            if(!serviceResult.success) return ResponseEntity(HttpStatus.BAD_REQUEST)
            imageService.storeImageFile(image, serviceResult.data, "16/9", userID)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("/place/update")
    open fun putBoulder(
        @RequestParam boulderID: Long,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) grade: String?,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) image: MultipartFile?,
        user: User
    ): ResponseEntity<Any> {
        val userID: Long = user.id
        
        val requestBody = mutableMapOf<String, String>().apply {
            put("boulderID", boulderID.toString())
            name?.let { put("name", it) }
            grade?.let { put("grade", it) }
            description?.let { put("description", it) }
        }
        
        boulderService.updateBoulder(boulderID, userID, requestBody, image)
        return ResponseEntity.ok("Boulder updated successfully")
    }

    @DeleteMapping("")
    open fun deleteBoulder(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<Any> {
        val userID: Long = user.id
        boulderService.deleteBoulder(userID, requestBody["id"]!!.toLong())

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/place/sends")
    open fun updateRouteSend(
        @RequestParam boulderID: Long,
        @RequestParam (required = false) attempts: Int?,
        @RequestParam (required = false) perceivedGrade: String?,
        @RequestParam (required = false) completed: String?,
        user: User
    ): ResponseEntity<Any> {
        val userID = user.id

        if(!boulderService.getUserBoulderSends(userID, listOf(boulderID)).data.isNullOrEmpty()) {
            return ResponseEntity.badRequest().body("The user has already sent this route")
        }

        val additionalProps = mutableMapOf<String, String>()
        attempts?.let { additionalProps["attempts"] = it.toString() }
        perceivedGrade?.let { additionalProps["perceivedGrade"] = it }
        completed?.let { additionalProps["completed"] = it }

        boulderService.addUserRouteSend(userID, boulderID, additionalProps)

        return ResponseEntity(HttpStatus.OK)
    }

}