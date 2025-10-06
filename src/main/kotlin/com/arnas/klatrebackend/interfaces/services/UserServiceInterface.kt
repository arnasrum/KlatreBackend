package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.ServiceResult
import com.arnas.klatrebackend.dataclasses.User

interface UserServiceInterface {
    fun getGoogleUserProfile(token: String): Map<String, String>
    fun usersPlacePermissions(userID: Long, placeID: Long): Boolean
    fun createOrUpdateUser(userInfo: Map<String, String>): Long?
    fun getUserById(userId: Long): ServiceResult<User>
}
