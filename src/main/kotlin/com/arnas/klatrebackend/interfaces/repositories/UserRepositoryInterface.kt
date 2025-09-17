package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.User

interface UserRepositoryInterface {
    fun insertUser(email: String, name: String): Long
    fun getUserByEmail(email: String): User?
    fun getUserById(userId: Long): User?
}