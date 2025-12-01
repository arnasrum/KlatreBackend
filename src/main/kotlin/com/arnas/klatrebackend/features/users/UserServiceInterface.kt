package com.arnas.klatrebackend.features.users

interface UserServiceInterface {
    fun getGoogleUserProfile(token: String): Map<String, String>
    fun createOrUpdateUser(userInfo: Map<String, String>): Long?
    fun getUserById(userId: Long): User
}