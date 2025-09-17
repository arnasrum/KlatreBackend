package com.arnas.klatrebackend.repository

import com.arnas.klatrebackend.dataclass.Grade
import com.arnas.klatrebackend.dataclass.GradeToCreate
import com.arnas.klatrebackend.dataclass.GradingSystem
import com.arnas.klatrebackend.interfaces.repositories.GradingSystemRepositoryInterface
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
class GradingSystemRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
): GradingSystemRepositoryInterface {
    override fun getGradesBySystemId(systemId: Long): List<Grade> {
        val grades: MutableList<Grade> = mutableListOf()
        val sql = "SELECT * FROM grades WHERE system_id = :systemId ORDER BY numerical_value"
        jdbcTemplate.query(sql,
            MapSqlParameterSource().addValue("systemId", systemId)
        ) { rs ->
            grades.add(
                Grade(
                    id = rs.getLong("id"),
                    gradeString = rs.getString("grade_string"),
                    numericalValue = rs.getInt("numerical_value")
                )
            )
        }
        return grades.toList()
    }

    override fun getGradingSystemsInGroup(groupID: Long): List<GradingSystem> {
        val gradingSystems: MutableList<GradingSystem> = mutableListOf()
        val sql = "SELECT * FROM grading_systems WHERE created_in_group = :groupID OR is_global = true"
        jdbcTemplate.query(sql,
            MapSqlParameterSource()
                .addValue("groupID", groupID)
        ) { rs ->
            gradingSystems.add(GradingSystem(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                climbType = rs.getString("climb_type"),
                grades = getGradesBySystemId(rs.getLong("id")),
                isGlobal = rs.getBoolean("is_global"),
            ))

        }
        return gradingSystems.toList()
    }

    override fun makeNewGradingSystem(
        groupId: Long,
        gradingSystemName: String,
        grades: List<GradeToCreate>)
            : Long {
        val keyholder = GeneratedKeyHolder()
        val sql = "INSERT INTO grading_systems (name, created_in_group, climb_type) VALUES (:name, :groupID, :climbType)"
        jdbcTemplate.update(
            sql,
            MapSqlParameterSource()
                .addValue("name", gradingSystemName)
                .addValue("groupID", groupId)
                .addValue("climbType", "boulder"),
            keyholder
        )
        val keys = keyholder.keys ?: throw RuntimeException("Failed to retrieve generated keys")
        val systemId = (keys["id"] as Number).toLong()

        val gradeSql = "INSERT INTO grades (grade_string, numerical_value, system_id) VALUES (:gradeString, :numericalValue, :systemId)"
        val gradeParameters = SqlParameterSourceUtils.createBatch(grades.map {
            mapOf(
                "gradeString" to it.gradeString,
                "numericalValue" to it.numericalValue,
                "systemId" to systemId
            ) }.toTypedArray()
        )
        jdbcTemplate.batchUpdate(gradeSql, gradeParameters, keyholder)
        return systemId
    }
}