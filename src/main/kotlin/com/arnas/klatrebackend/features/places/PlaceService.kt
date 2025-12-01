package com.arnas.klatrebackend.features.places

interface PlaceService {
    fun getPlaceById(placeId: Long): Place?
    fun getPlacesByGroupId(groupId: Long, userId: Long): List<PlaceWithGrades>
    fun updatePlace(userId: Long, placeUpdateDTO: PlaceUpdateDTO)
    fun updatePlaceGradingSystem(placeId: Long, oldGradingSystemId: Long, newGradingSystemId: Long?): Long

}