package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Boulder
import com.arnas.klatrebackend.dataclasses.BoulderRequest

interface BoulderRepositoryInterface {
    fun getRouteById(routeId: Long): Boulder?
    fun addBoulder(userId: Long, boulder: BoulderRequest): Long
    fun updateBoulder(boulderId: Long, name: String?, grade: Long?, place: Long?, description: String?, active: Boolean?): Int
    fun deleteBoulder(boulderId: Long): Int

    fun getBouldersByPlace(placeId: Long, page: Int, limit: Int, pagingEnabled: Boolean): List<Boulder>

    fun getNumBouldersInPlace(placeId: Long, countActive: Boolean): Int
}