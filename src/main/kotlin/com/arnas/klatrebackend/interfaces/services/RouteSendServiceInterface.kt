package com.arnas.klatrebackend.interfaces.services

import com.arnas.klatrebackend.dataclasses.RouteSend
import com.arnas.klatrebackend.dataclasses.RouteSendDTOUpdate
import com.arnas.klatrebackend.dataclasses.ServiceResult

interface RouteSendServiceInterface {
    fun getUserBoulderSends(userID: Long, boulderIDs: List<Long>): ServiceResult<List<RouteSend>>
    fun addUserRouteSend(userID: Long, boulderID: Long, additionalProps: Map<String, String> = emptyMap())

    fun getRouteSendByRoute(routeId: Long, userId: Long): ServiceResult<RouteSend?>
    fun updateRouteSend(routeSendDTO: RouteSendDTOUpdate): ServiceResult<RouteSend>


}