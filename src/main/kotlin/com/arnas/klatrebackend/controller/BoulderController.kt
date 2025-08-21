package com.arnas.klatrebackend.controller

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult
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
import kotlin.reflect.full.memberProperties

@RestController
@Tag(name = "Boulder", description = "Boulder CRUD operations")
@RequestMapping("/boulders")
@CrossOrigin(origins = arrayOf("http://localhost:5173"))
class BoulderController(
    private val userService: UserService,
    private val boulderService: BoulderService,
    private val imageService: ImageService
) {


    @GetMapping("")
    open fun getBoulders(@RequestParam accessToken: String): ResponseEntity<List<Boulder>> {
        val userID: Long = userService.getUserByToken(accessToken).data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val boulders = boulderService.getBouldersByUser(userID)
        return ResponseEntity(boulders, HttpStatus.OK)
    }

    @GetMapping("/place")
    open fun getBouldersByPlace(@RequestParam accessToken: String, @RequestParam placeID: Long): ResponseEntity<out Any> {
        val userID: Long = userService.getUserByToken(accessToken).data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.getBouldersWithSendsByPlace(userID, placeID)
        if (!serviceResult.success) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(serviceResult.data, HttpStatus.OK)

    }

    @PostMapping("/place")
    open fun addBoulderToPlace(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<out Any> {
        val userID: Long = userService.getUserByToken(accessToken).data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val placeID: Long = requestBody["placeID"]?.toLong() ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        if(!userService.usersPlacePermissions(userID, placeID)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val serviceResult = boulderService.addBoulderToPlace(userID,  placeID, requestBody)
        requestBody["image"]?.let {
            if(!serviceResult.success) return ResponseEntity(HttpStatus.BAD_REQUEST)
            imageService.storeImage(serviceResult.data!!, it)
        }

        return ResponseEntity(HttpStatus.OK)
    }

    @PutMapping("")
    open fun putBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<Any> {
        val userID: Long = userService.getUserByToken(accessToken).data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        boulderService.updateBoulder(userID, requestBody)
        if(requestBody["image"] != null) {
            imageService.updateImage(requestBody["boulderID"]!!.toLong(), requestBody["image"]!!)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("")
    open fun deleteBoulder(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<Any> {
        val userID: Long = userService.getUserByToken(accessToken).data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        boulderService.deleteBoulder(userID, requestBody["id"]!!.toLong())

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/place/sends")
    open fun getRouteSends(@RequestParam accessToken: String, @RequestBody requestBody: Map<String, String>): ResponseEntity<Any> {
        val serviceResult: ServiceResult<User> = userService.getUserByToken(accessToken)
        if(!serviceResult.success) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val userID = serviceResult.data?.id ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        //val placeID = requestBody["placeID"]?.toLong() ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        val boulderID = requestBody["boulderID"]?.toLong() ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        val sendProps = RouteSend::class.memberProperties
            .filter { it.name != "boulderID" && it.name != "id" && it.name != "userID" }
            .associate { it.name to requestBody[it.name] }
            .filterValues { it != null } as Map<String, String>
        boulderService.addUserRouteSend(userID, boulderID, sendProps)



        return ResponseEntity(HttpStatus.OK)
    }

}