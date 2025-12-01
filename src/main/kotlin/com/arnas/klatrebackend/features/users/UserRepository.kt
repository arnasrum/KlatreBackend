package com.arnas.klatrebackend.features.users

interface UserRepository {
    fun insertUser(email: String, name: String): Long
    fun getUserByEmail(email: String): User?
    fun getUserById(userId: Long): User?
}