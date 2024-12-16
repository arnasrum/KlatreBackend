package com.arnas.klatrebackend.service

import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class UserService {

    val client: HttpClient = HttpClient.newBuilder().build()

    fun getGoogleUserProfile(token: String) {

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v1/userinfo?access_token=${token}"))
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())
        //return response.body()
    }

}