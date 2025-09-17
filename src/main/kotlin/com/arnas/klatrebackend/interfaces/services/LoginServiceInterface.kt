package com.arnas.klatrebackend.interfaces.services

interface LoginServiceInterface {
    fun getJWTToken(code: String): String?
}