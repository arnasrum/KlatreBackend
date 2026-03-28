package com.arnas.klatrebackend.features.stats

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
class StatsServiceTest {

    @Mock
    private lateinit var statsRepository: StatsRepository

    @InjectMocks
    private lateinit var statsService: StatsServiceDefault

    @Test
    fun `getUserAttemptActivity returns stats list`() {
        val userId = 1L
        val groupId = 1L
        val stats = listOf(
            UserGroupSessionStats(2024, 1, 1, 5, 15, 3, groupId, userId),
            UserGroupSessionStats(2024, 1, 3, 3, 8, 2, groupId, userId),
            UserGroupSessionStats(2024, 2, 3, 2, 17, 1, groupId, userId)
        )

        `when`(statsRepository.getUserAttemptActivity(userId, groupId)).thenReturn(stats)

        val result = statsService.getUserAttemptActivity(userId, groupId)

        assertEquals(3, result.size)
        assertEquals(2024, result[0].year)
        assertEquals(1, result[0].month)
        assertEquals(1, result[0].day)
        assertEquals(5, result[0].routesTried)
        assertEquals(15, result[0].totalTries)
        assertEquals(3, result[0].totalCompleted)
    }

    @Test
    fun `getUserAttemptActivity returns empty list when no sessions`() {
        `when`(statsRepository.getUserAttemptActivity(1L, 1L)).thenReturn(emptyList())

        val result = statsService.getUserAttemptActivity(1L, 1L)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getUserAttemptActivity passes correct userId and groupId`() {
        val userId = 42L
        val groupId = 99L

        `when`(statsRepository.getUserAttemptActivity(userId, groupId)).thenReturn(emptyList())

        statsService.getUserAttemptActivity(userId, groupId)

        verify(statsRepository).getUserAttemptActivity(userId, groupId)
    }
}

