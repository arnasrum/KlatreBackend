package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclass.Place

interface PlaceRepositoryInterface {
    fun getPlacesByGroupId(groupId: Long): List<Place>
}