package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO

interface PlaceRepositoryInterface {
    fun getPlacesByGroupId(groupId: Long): List<Place>
    fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest): Long
    fun getPlaceById(placeId: Long): Place?
    fun updatePlace(placeId: Long, name: String?, description: String?, groupId: Long?, gradingSystem: Long?): Int
    fun updatePlace(placeUpdateDTO: PlaceUpdateDTO): Int
    fun deletePlace(placeId: Long): Int
}