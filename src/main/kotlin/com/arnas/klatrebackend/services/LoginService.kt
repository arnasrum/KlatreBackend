package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.interfaces.services.LoginServiceInterface
import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.JsonParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Service
class LoginService(
    private val userService: UserService,
    private val jwtService: JwtService,
    @Value("\${GOOGLE_CLIENT_SECRET}") private val googleClientSecret: String
): LoginServiceInterface {

    @Value("\${GOOGLE_CLIENT_ID}")
    private lateinit var GOOGLE_CLIENT_ID: String

    override fun getJWTToken(code: String): String? {

        try {
            val json = JsonObject()
            json.addProperty("client_id", GOOGLE_CLIENT_ID)
            json.addProperty("client_secret", googleClientSecret)
            json.addProperty("grant_type", "authorization_code")
            json.addProperty("code", code)
            json.addProperty("redirect_uri", "http://localhost:8080/api/oauth2/code")

            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                // Parse the response to get the access token
                val responseJson = JsonParser.parseString(response.body()).asJsonObject
                val accessToken = responseJson.get("access_token")?.asString
                if (accessToken != null) {
                    val userInfo = getUserInfoFromGoogle(accessToken)
                    userService.createOrUpdateUser(userInfo)
                    return createJWTFromUserInfo(userInfo)
                }
                return (accessToken)
            } else {
                throw Exception("Token exchange failed: ${response.statusCode()} - ${response.body()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun createJWTFromUserInfo(userInfo: Map<String, String>): String? {

        val email = userInfo["email"]
        val name = userInfo["name"]
        val googleID = userInfo["id"]

        if (email == null || googleID == null || name == null) return null

        return try {
            val userID = userService.createOrUpdateUser(userInfo) ?: return null
            val claims = mapOf("email" to email, "name" to name, "id" to userID.toString())
            jwtService.createJwtToken(userID.toString(), claims)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getUserInfoFromGoogle(accessToken: String): Map<String, String> {
        try {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                .header("Authorization", "Bearer $accessToken")
                .GET()
                .build()


            val userInfo = mutableMapOf<String, String>()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                val jsonResponse = JsonParser.parseString(response.body()).asJsonObject
                jsonResponse.get("email")?.asString?.let { userInfo["email"] = it }
                jsonResponse.get("name")?.asString?.let { userInfo["name"] = it }
                jsonResponse.get("id")?.asString?.let { userInfo["id"] = it }
                return userInfo.toMap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mapOf()
    }


}