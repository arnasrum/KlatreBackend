package com.arnas.klatrebackend.features.gradesystems

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class GradeSystemServiceTest {

    @Mock
    private lateinit var gradeSystemRepository: GradeSystemRepository

    @InjectMocks
    private lateinit var gradeSystemService: GradeSystemServiceDefault

    // --- makeGradingSystem ---

    @Test
    fun `makeGradingSystem creates system with correctly averaged grades`() {
        val groupId = 1L
        val userId = 1L
        val referenceSystemId = 10L
        val referenceGrades = listOf(
            Grade(1L, "V0", 10),
            Grade(2L, "V1", 20),
            Grade(3L, "V2", 30),
            Grade(4L, "V3", 40)
        )
        val grades = listOf(
            mapOf("name" to "Easy", "from" to "0", "to" to "1"),
            mapOf("name" to "Hard", "from" to "2", "to" to "3")
        )
        // Easy: avg of V0(10), V1(20) = 15
        // Hard: avg of V2(30), V3(40) = 35
        val expectedNewGrades = listOf(
            GradeToCreate("Easy", 15),
            GradeToCreate("Hard", 35)
        )

        `when`(gradeSystemRepository.getGradesBySystemId(referenceSystemId)).thenReturn(referenceGrades)
        `when`(gradeSystemRepository.makeNewGradingSystem(groupId, "Custom System", expectedNewGrades)).thenReturn(100L)

        val result = gradeSystemService.makeGradingSystem(groupId, userId, referenceSystemId, "Custom System", grades)

        assertEquals(100L, result)
        verify(gradeSystemRepository).makeNewGradingSystem(groupId, "Custom System", expectedNewGrades)
    }

    @Test
    fun `makeGradingSystem with single grade range`() {
        val referenceGrades = listOf(
            Grade(1L, "V0", 10),
            Grade(2L, "V1", 20)
        )
        val grades = listOf(
            mapOf("name" to "Only", "from" to "0", "to" to "0")
        )
        // Only: avg of V0(10) = 10
        val expectedNewGrades = listOf(GradeToCreate("Only", 10))

        `when`(gradeSystemRepository.getGradesBySystemId(1L)).thenReturn(referenceGrades)
        `when`(gradeSystemRepository.makeNewGradingSystem(1L, "Single", expectedNewGrades)).thenReturn(50L)

        val result = gradeSystemService.makeGradingSystem(1L, 1L, 1L, "Single", grades)

        assertEquals(50L, result)
        verify(gradeSystemRepository).makeNewGradingSystem(1L, "Single", expectedNewGrades)
    }

    @Test
    fun `makeGradingSystem throws when grade range missing from field`() {
        val referenceGrades = listOf(Grade(1L, "V0", 10))
        val grades = listOf(
            mapOf("name" to "Bad", "to" to "0") // missing "from"
        )

        `when`(gradeSystemRepository.getGradesBySystemId(1L)).thenReturn(referenceGrades)

        assertThrows(Exception::class.java) {
            gradeSystemService.makeGradingSystem(1L, 1L, 1L, "Bad System", grades)
        }
    }

    @Test
    fun `makeGradingSystem throws when grade range missing to field`() {
        val referenceGrades = listOf(Grade(1L, "V0", 10))
        val grades = listOf(
            mapOf("name" to "Bad", "from" to "0") // missing "to"
        )

        `when`(gradeSystemRepository.getGradesBySystemId(1L)).thenReturn(referenceGrades)

        assertThrows(Exception::class.java) {
            gradeSystemService.makeGradingSystem(1L, 1L, 1L, "Bad System", grades)
        }
    }

    @Test
    fun `makeGradingSystem throws when grade range missing name field`() {
        val referenceGrades = listOf(Grade(1L, "V0", 10))
        val grades = listOf(
            mapOf("from" to "0", "to" to "0") // missing "name"
        )

        `when`(gradeSystemRepository.getGradesBySystemId(1L)).thenReturn(referenceGrades)

        assertThrows(Exception::class.java) {
            gradeSystemService.makeGradingSystem(1L, 1L, 1L, "Bad System", grades)
        }
    }

    // --- deleteGradeSystem ---

    @Test
    fun `deleteGradeSystem succeeds when rows affected`() {
        `when`(gradeSystemRepository.deleteGradingSystem(1L)).thenReturn(1)

        assertDoesNotThrow { gradeSystemService.deleteGradeSystem(1L, 1L, 1L) }

        verify(gradeSystemRepository).deleteGradingSystem(1L)
    }

    @Test
    fun `deleteGradeSystem throws when no rows affected`() {
        `when`(gradeSystemRepository.deleteGradingSystem(999L)).thenReturn(0)

        assertThrows(Exception::class.java) {
            gradeSystemService.deleteGradeSystem(999L, 1L, 1L)
        }
    }
}
