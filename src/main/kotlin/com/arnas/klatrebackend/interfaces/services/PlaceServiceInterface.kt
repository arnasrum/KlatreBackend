package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclass.Place
import com.arnas.klatrebackend.dataclass.ServiceResult

interface PlaceServiceInterface {
    fun getPlacesByGroupId(groupId: Long, userId: Long): ServiceResult<List<Place>>
}