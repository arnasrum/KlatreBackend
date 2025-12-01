package com.arnas.klatrebackend.features.places

interface PlaceRepositoryInterface {
    fun getPlacesByGroupId(groupId: Long): List<Place>
    fun addPlaceToGroup(groupID: Long, name: String, description: String?): Long
    fun getPlaceById(placeId: Long): Place?
    fun updatePlace(newPlace: Place): Int
    fun deletePlace(placeId: Long): Int
}