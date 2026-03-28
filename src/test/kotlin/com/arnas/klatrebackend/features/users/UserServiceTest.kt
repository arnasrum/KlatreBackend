package com.arnas.klatrebackend.features.users

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
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserServiceDefault

    // --- getUserById ---

    @Test
    fun `getUserById returns user when found`() {
        val user = User(1L, "test@test.com", "Test User")
        `when`(userRepository.getUserById(1L)).thenReturn(user)

        val result = userService.getUserById(1L)

        assertEquals(user, result)
        assertEquals("Test User", result.name)
        assertEquals("test@test.com", result.email)
    }

    @Test
    fun `getUserById throws when user not found`() {
        `when`(userRepository.getUserById(999L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            userService.getUserById(999L)
        }
    }

    // --- createOrUpdateUser ---

    @Test
    fun `createOrUpdateUser returns existing user id when user exists`() {
        val existingUser = User(1L, "existing@test.com", "Existing")
        `when`(userRepository.getUserByEmail("existing@test.com")).thenReturn(existingUser)

        val result = userService.createOrUpdateUser(mapOf("email" to "existing@test.com", "name" to "Existing"))

        assertEquals(1L, result)
        verify(userRepository, never()).insertUser(anyString(), anyString())
    }

    @Test
    fun `createOrUpdateUser creates new user when user does not exist`() {
        `when`(userRepository.getUserByEmail("new@test.com")).thenReturn(null)
        `when`(userRepository.insertUser("new@test.com", "New User")).thenReturn(2L)

        val result = userService.createOrUpdateUser(mapOf("email" to "new@test.com", "name" to "New User"))

        assertEquals(2L, result)
        verify(userRepository).insertUser("new@test.com", "New User")
    }

    @Test
    fun `createOrUpdateUser returns null when email is missing`() {
        val result = userService.createOrUpdateUser(mapOf("name" to "No Email"))

        assertNull(result)
    }

    @Test
    fun `createOrUpdateUser returns null when name is missing`() {
        val result = userService.createOrUpdateUser(mapOf("email" to "noemail@test.com"))

        assertNull(result)
    }

    @Test
    fun `createOrUpdateUser returns null when both fields are missing`() {
        val result = userService.createOrUpdateUser(emptyMap())

        assertNull(result)
    }
}

