package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades
import com.arnas.klatrebackend.dataclasses.ServiceResult

interface PlaceServiceInterface {
    fun getPlacesByGroupId(groupId: Long, userId: Long): ServiceResult<List<PlaceWithGrades>>
    fun updatePlaceGradingSystem(userId: Long, placeId: Long, newGradingSystemId: Long): ServiceResult<Unit>
}