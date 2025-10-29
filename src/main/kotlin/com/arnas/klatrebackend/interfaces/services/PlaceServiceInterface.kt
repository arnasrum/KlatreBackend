package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.Place
import com.arnas.klatrebackend.dataclasses.PlaceRequest
import com.arnas.klatrebackend.dataclasses.PlaceUpdateDTO
import com.arnas.klatrebackend.dataclasses.PlaceWithGrades
import com.arnas.klatrebackend.dataclasses.ServiceResult

interface PlaceServiceInterface {
    fun getPlaceById(placeId: Long): Place?
    fun getPlacesByGroupId(groupId: Long, userId: Long): List<PlaceWithGrades>
    fun updatePlace(userId: Long, placeUpdateDTO: PlaceUpdateDTO)
    fun updatePlaceGradingSystem(placeId: Long, oldGradingSystemId: Long, newGradingSystemId: Long?): Long

}