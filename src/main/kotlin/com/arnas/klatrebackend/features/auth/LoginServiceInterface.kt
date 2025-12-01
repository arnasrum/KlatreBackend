package com.arnas.klatrebackend.features.auth

interface LoginServiceInterface {
    fun getJWTToken(code: String, codeVerifier: String?): String?
}