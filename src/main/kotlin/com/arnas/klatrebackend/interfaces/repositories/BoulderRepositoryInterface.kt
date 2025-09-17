package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclass.Boulder
import com.arnas.klatrebackend.dataclass.BoulderRequest

interface BoulderRepositoryInterface {
    fun addBoulder(userId: Long, boulder: BoulderRequest): Long
    fun updateBoulder(boulderInfo: Map<String, String>): Int
    fun deleteBoulder(boulderId: Long): Int
    fun getBouldersByPlace(placeId: Long): List<Boulder>
}