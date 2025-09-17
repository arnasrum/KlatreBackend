package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.RouteSend

interface RouteSendRepositoryInterface {
    fun getBoulderSends(userId: Long, boulderIds: List<Long>): List<RouteSend>
    fun insertRouteSend(userId: Long, boulderId: Long, sendInfo: Map<String, String>): Long

}