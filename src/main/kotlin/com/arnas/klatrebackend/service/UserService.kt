package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.User
import com.arnas.klatrebackend.repository.UserRepository
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class UserService(
    var userRepository: UserRepository
) {

    open val client: HttpClient = HttpClient.newBuilder().build()


    open fun registerUser(accessToken: String) {

        val userInfo = getGoogleUserProfile(accessToken)
        val email = userInfo["email"] ?: return
        val name = userInfo["name"] ?: return
        userRepository.createUser(User(email, name))
    }

    open fun loginUser(token: String): User? {
        return getUserByToken(token)
    }

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

    open fun getUserByToken(token: String): User? {
        val userInfo = getGoogleUserProfile(token)
        if (userInfo.isEmpty()) {return null}
        val email = userInfo["email"] ?: return null
        val name = userInfo["name"] ?: return null
        val user: User = User(email, name)
        userRepository.createUser(user)
        return user
    }

    open fun getUserID(accessToken: String): Int? {
        val user: User = getUserByToken(accessToken) ?: return null
        val userID: Int = userRepository.getUserIDByObject(user)
        return userID
    }

}