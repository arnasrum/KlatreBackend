package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.RouteSend
import com.arnas.klatrebackend.dataclasses.RouteSendDTOUpdate

interface RouteSendRepositoryInterface {
    fun getRouteSendById(routeId: Long, userId: Long): RouteSend?
    fun insertRouteSend(userId: Long, boulderId: Long, sendInfo: Map<String, String>): Long
    fun initializeRouteSend(routeId: Long, userId: Long): RouteSend?
    fun getRouteSends(userId: Long, boulderIds: List<Long>): List<RouteSend>
    fun updateRouteSend(routeSendDTO: RouteSendDTOUpdate): Int
}