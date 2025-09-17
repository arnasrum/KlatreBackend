package com.arnas.klatrebackend.interfaces.services

interface UserServiceInterface {
    fun getGoogleUserProfile(token: String): Map<String, String>
    fun usersPlacePermissions(userID: Long, placeID: Long): Boolean
    fun createOrUpdateUser(userInfo: Map<String, String>): Long?
}
