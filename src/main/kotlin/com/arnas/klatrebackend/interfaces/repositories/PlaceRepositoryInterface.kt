package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest

interface PlaceRepositoryInterface {
    fun getPlacesByGroupId(groupId: Long): List<Place>
    fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest): Long
    fun getPlaceById(placeId: Long): Place?
    fun updatePlace(placeId: Long, name: String?, description: String?, groupId: Long?, gradingSystem: Long?): Int
    fun deletePlace(placeId: Long): Int
}