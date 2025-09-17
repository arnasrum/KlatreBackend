package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclass.BoulderWithSend
import com.arnas.klatrebackend.dataclass.RouteSend
import com.arnas.klatrebackend.dataclass.ServiceResult

interface RouteSendServiceInterface {
    fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>>
    fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>>
    fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String> = emptyMap())
}