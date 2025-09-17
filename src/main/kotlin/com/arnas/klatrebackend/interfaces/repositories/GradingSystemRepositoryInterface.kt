package com.arnas.klatrebackend.interfaces.repositories

import com.arnas.klatrebackend.dataclass.Grade
import com.arnas.klatrebackend.dataclass.GradeToCreate
import com.arnas.klatrebackend.dataclass.GradingSystem

interface GradingSystemRepositoryInterface {
    fun getGradesBySystemId(systemId: Long): List<Grade>
    fun makeNewGradingSystem(groupId: Long, gradingSystemName: String, grades: List<GradeToCreate>): Long
    fun getGradingSystemsInGroup(groupID: Long): List<GradingSystem>
}