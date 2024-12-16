package com.arnas.klatrebackend.service

import com.nimbusds.jose.shaded.gson.JsonElement
import com.nimbusds.jose.shaded.gson.JsonObject
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Service
class LoginService(private val restTemplateBuilder: RestTemplateBuilder) {

    fun login(token: String) {
       val jwt = getJWTToken(token)
    }



    fun getJWTToken(token: String): String {
        val GOOGLE_CLIENT_ID = "733167968471-7runi5g0s0gahprbah0lj1460ua2jjv3.apps.googleusercontent.com"
        val GOOGLE_CLIENT_SECRET = "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"

        /*
        val requestBody = mapOf(
           "code" to token,
            "client_id" to GOOGLE_CLIENT_ID,
            "client_secret" to GOOGLE_CLIENT_SECRET,
            "redirect_uri" to "postmessage",
            "grant_type" to "authorization_code",
        )
        */
        val json = JsonObject()
        json.addProperty("client_id", GOOGLE_CLIENT_ID)
        json.addProperty("client_secret", GOOGLE_CLIENT_SECRET)
        json.addProperty("grant_type", "authorization_code")
        json.addProperty("code", token)
        json.addProperty("redirect_uri", "postmessage")

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth2.googleapis.com/token"))
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response)
        println(response.body())

        return ""
    }


}