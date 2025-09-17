package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.interfaces.services.UserServiceInterface
import com.arnas.klatrebackend.repositories.UserRepository
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class UserService(
    private var userRepository: UserRepository,
    private val groupService: GroupService,
): UserServiceInterface {

    private val client: HttpClient = HttpClient.newBuilder().build()

     override fun getGoogleUserProfile(token: String): Map<String, String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v1/userinfo?access_token=${token}"))
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {return mapOf()}
        val json = JSONObject(response.body())
        return mapOf(
            "name" to json.getString("name"),
            "email" to json.getString("email"),
            "id" to json.getLong("id").toString()
        )
    }

    override fun usersPlacePermissions(userID: Long, placeID: Long): Boolean {
        val groups = groupService.getGroups(userID).data ?: return false
        val hasAccess = groups.any { group ->
            group.places.any{ it.id == placeID }
        }
        return hasAccess
    }

    override fun createOrUpdateUser(userInfo: Map<String, String>): Long? {
        val email = userInfo["email"] ?: return null
        val name = userInfo["name"] ?: return null
        userRepository.getUserByEmail(email)?.let {
            return it.id
        }
        val userId = userRepository.insertUser(email, name)
        return userId
    }
}