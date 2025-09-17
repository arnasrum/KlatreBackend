package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclasses.Grade
import com.arnas.klatrebackend.dataclasses.GradeToCreate
import com.arnas.klatrebackend.dataclasses.GradingSystem

interface GradingSystemRepositoryInterface {
    fun getGradesBySystemId(systemId: Long): List<Grade>
    fun makeNewGradingSystem(groupId: Long, gradingSystemName: String, grades: List<GradeToCreate>): Long
    fun getGradingSystemsInGroup(groupID: Long): List<GradingSystem>
    fun deleteGradingSystem(gradingSystemId: Long): Int
}