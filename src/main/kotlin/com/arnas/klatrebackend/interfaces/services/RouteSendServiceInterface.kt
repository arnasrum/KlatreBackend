package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.BoulderWithSend
import com.arnas.klatrebackend.dataclasses.RouteSend
import com.arnas.klatrebackend.dataclasses.ServiceResult

interface RouteSendServiceInterface {
    fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>>
    fun getBouldersWithSendsByPlace(userID: Long, placeID: Long): ServiceResult<List<BoulderWithSend>>
    fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String> = emptyMap())
}