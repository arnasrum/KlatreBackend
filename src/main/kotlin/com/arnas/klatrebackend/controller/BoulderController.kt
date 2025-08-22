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
import kotlin.reflect.full.memberProperties

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

    @PostMapping("/place")
    open fun addBoulderToPlace(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<out Any> {
        val userID: Long = user.id
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
    open fun putBoulder(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<Any> {
        val userID: Long = user.id
        boulderService.updateBoulder(userID, requestBody)
        if(requestBody["image"] != null) {
            imageService.updateImage(requestBody["boulderID"]!!.toLong(), requestBody["image"]!!)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("")
    open fun deleteBoulder(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<Any> {
        val userID: Long = user.id
        boulderService.deleteBoulder(userID, requestBody["id"]!!.toLong())

        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/place/sends")
    open fun getRouteSends(@RequestBody requestBody: Map<String, String>, user: User): ResponseEntity<Any> {
        val userID = user.id
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