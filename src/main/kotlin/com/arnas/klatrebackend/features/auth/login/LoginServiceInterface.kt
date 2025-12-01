package com.arnas.klatrebackend.features.auth.login

interface LoginServiceInterface {
    fun getJWTToken(code: String, codeVerifier: String?): String?
}