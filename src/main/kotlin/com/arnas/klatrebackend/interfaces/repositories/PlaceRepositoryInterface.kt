package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest

interface PlaceRepositoryInterface {
    fun getPlacesByGroupId(groupId: Long): List<Place>
    fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest): Long
}