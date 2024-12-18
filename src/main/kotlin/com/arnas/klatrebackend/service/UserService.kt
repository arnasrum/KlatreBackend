package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.UserRepository
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class UserService {

    val client: HttpClient = HttpClient.newBuilder().build()
    @Autowired
    lateinit var userRepository: UserRepository

    fun getGoogleUserProfile(token: String): Map<String, String>? {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v1/userinfo?access_token=${token}"))
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {return null}
        val json = JSONObject(response.body())
        return mapOf(
            "name" to json.getString("name"),
            "email" to json.getString("email")
        )
    }

    fun getUserByToken(token: String): User? {
        val userInfo = getGoogleUserProfile(token)
        if (userInfo.isNullOrEmpty()) {return null}
        val email = userInfo["email"] ?: return null
        val name = userInfo["name"] ?: return null
        val user: User = User(email, name)
        userRepository.createUser(user)
        return user
    }

    fun getUserID(accessToken: String): Int? {
        val user: User = getUserByToken(accessToken) ?: return null
        val userID: Int = userRepository.getUserIDByObject(user)
        return userID
    }

}