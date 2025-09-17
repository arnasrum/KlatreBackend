package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest

interface BoulderRepositoryInterface {
    fun addBoulder(userId: Long, boulder: BoulderRequest): Long
    fun updateBoulder(boulderInfo: Map<String, String>): Int
    fun deleteBoulder(boulderId: Long): Int
    fun getBouldersByPlace(placeId: Long): List<Boulder>
}