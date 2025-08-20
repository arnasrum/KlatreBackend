package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.GroupRepository
import com.arnas.klatrebackend.repository.UserRepository
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class UserService(
    var userRepository: UserRepository,
    private val groupRepository: GroupRepository
) {

    open val client: HttpClient = HttpClient.newBuilder().build()

    open fun getGoogleUserProfile(token: String): Map<String, String> {
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
            "email" to json.getString("email")
        )
    }

    open fun getUserByToken(token: String): ServiceResult<User> {
        val userInfo = getGoogleUserProfile(token)
        if (userInfo.isEmpty()) {return ServiceResult(data = null, success = false, message = "Invalid token")}
        val email = userInfo["email"] ?: return ServiceResult(data = null, success = false, message = "Email is required")
        val name = userInfo["name"] ?: return ServiceResult(data = null, success = false, message = "Name is required")
        val userID = userRepository.createUser(email, name)
        val user = User(userID, email, name)
        return ServiceResult(data = user, success = true)
    }

    open fun usersPlacePermissions(userID: Long, placeID: Long): Boolean {

        val groups = groupRepository.getGroups(userID)
        val hasAccess = groups.any { group ->
            group.places.any{ it.id == placeID }
        }
        return hasAccess
    }

    open fun getUserIDByMail(email: String): Long {
        userRepository.getUserByEmail(email)?.let {
            return it.id
        }
        return -1L
    }

}