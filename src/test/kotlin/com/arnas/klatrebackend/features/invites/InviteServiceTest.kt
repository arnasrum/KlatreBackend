package com.arnas.klatrebackend.features.invites

import com.arnas.klatrebackend.features.auth.Role
import com.arnas.klatrebackend.features.groups.Group
import com.arnas.klatrebackend.features.groups.GroupRepository
import com.arnas.klatrebackend.features.users.User
import com.arnas.klatrebackend.features.users.UserRepository
import com.arnas.klatrebackend.util.exceptions.InviteAlreadyProcessedException
import com.arnas.klatrebackend.util.exceptions.NotUpdatedException
import com.arnas.klatrebackend.util.exceptions.UnauthorizedException
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
class InviteServiceTest {

    @Mock
    private lateinit var inviteRepository: InviteRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @InjectMocks
    private lateinit var inviteService: InviteServiceDefault

    // --- getUserPendingInvites ---

    @Test
    fun `getUserPendingInvites returns display list`() {
        val userId = 1L
        val senderId = 2L
        val groupId = 10L
        val invite = GroupInvite(1L, userId, senderId, groupId, "pending", null, null, null)
        val sender = User(senderId, "sender@test.com", "Sender")
        val group = Group(groupId, senderId, "Test Group", false, "uuid-1", null)

        `when`(inviteRepository.getUserInvitesByStatus(userId, "pending")).thenReturn(listOf(invite))
        `when`(userRepository.getUserById(senderId)).thenReturn(sender)
        `when`(groupRepository.getGroupById(groupId)).thenReturn(group)

        val result = inviteService.getUserPendingInvites(userId)

        assertEquals(1, result.size)
        assertEquals(group, result[0].group)
        assertEquals(sender, result[0].sender)
        assertEquals("pending", result[0].status)
    }

    @Test
    fun `getUserPendingInvites returns empty list when no pending invites`() {
        val userId = 1L
        `when`(inviteRepository.getUserInvitesByStatus(userId, "pending")).thenReturn(emptyList())

        val result = inviteService.getUserPendingInvites(userId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getUserPendingInvites throws when sender not found`() {
        val userId = 1L
        val invite = GroupInvite(1L, userId, 99L, 10L, "pending", null, null, null)

        `when`(inviteRepository.getUserInvitesByStatus(userId, "pending")).thenReturn(listOf(invite))
        `when`(userRepository.getUserById(99L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            inviteService.getUserPendingInvites(userId)
        }
    }

    @Test
    fun `getUserPendingInvites throws when group not found`() {
        val userId = 1L
        val senderId = 2L
        val invite = GroupInvite(1L, userId, senderId, 99L, "pending", null, null, null)
        val sender = User(senderId, "sender@test.com", "Sender")

        `when`(inviteRepository.getUserInvitesByStatus(userId, "pending")).thenReturn(listOf(invite))
        `when`(userRepository.getUserById(senderId)).thenReturn(sender)
        `when`(groupRepository.getGroupById(99L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            inviteService.getUserPendingInvites(userId)
        }
    }

    // --- acceptInvite ---

    @Test
    fun `acceptInvite succeeds for valid pending invite`() {
        val inviteId = 1L
        val userId = 1L
        val groupId = 10L
        val invite = GroupInvite(inviteId, userId, 2L, groupId, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)
        `when`(groupRepository.getGroups(userId)).thenReturn(emptyList())
        `when`(inviteRepository.acceptInvite(inviteId)).thenReturn(1)

        assertDoesNotThrow { inviteService.acceptInvite(inviteId, userId) }

        verify(inviteRepository).acceptInvite(inviteId)
        verify(groupRepository).addUserToGroup(userId, groupId, Role.USER.id)
    }

    @Test
    fun `acceptInvite throws UnauthorizedException when user does not match`() {
        val inviteId = 1L
        val invite = GroupInvite(inviteId, 2L, 3L, 10L, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)

        assertThrows(UnauthorizedException::class.java) {
            inviteService.acceptInvite(inviteId, 1L)
        }
    }

    @Test
    fun `acceptInvite throws InviteAlreadyProcessedException when not pending`() {
        val inviteId = 1L
        val userId = 1L
        val invite = GroupInvite(inviteId, userId, 2L, 10L, "accepted", 100L, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)

        assertThrows(InviteAlreadyProcessedException::class.java) {
            inviteService.acceptInvite(inviteId, userId)
        }
    }

    @Test
    fun `acceptInvite throws NotUpdatedException when no rows affected`() {
        val inviteId = 1L
        val userId = 1L
        val groupId = 10L
        val invite = GroupInvite(inviteId, userId, 2L, groupId, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)
        `when`(groupRepository.getGroups(userId)).thenReturn(emptyList())
        `when`(inviteRepository.acceptInvite(inviteId)).thenReturn(0)

        assertThrows(NotUpdatedException::class.java) {
            inviteService.acceptInvite(inviteId, userId)
        }
    }

    // --- rejectInvite ---

    @Test
    fun `rejectInvite succeeds for valid pending invite`() {
        val inviteId = 1L
        val userId = 1L
        val invite = GroupInvite(inviteId, userId, 2L, 10L, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)
        `when`(inviteRepository.declineInvite(inviteId)).thenReturn(1)

        assertDoesNotThrow { inviteService.rejectInvite(inviteId, userId) }

        verify(inviteRepository).declineInvite(inviteId)
    }

    @Test
    fun `rejectInvite throws UnauthorizedException when user does not match`() {
        val inviteId = 1L
        val invite = GroupInvite(inviteId, 2L, 3L, 10L, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)

        assertThrows(UnauthorizedException::class.java) {
            inviteService.rejectInvite(inviteId, 1L)
        }
    }

    @Test
    fun `rejectInvite throws InviteAlreadyProcessedException when not pending`() {
        val inviteId = 1L
        val userId = 1L
        val invite = GroupInvite(inviteId, userId, 2L, 10L, "declined", null, 100L, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)

        assertThrows(InviteAlreadyProcessedException::class.java) {
            inviteService.rejectInvite(inviteId, userId)
        }
    }

    @Test
    fun `rejectInvite throws NotUpdatedException when no rows affected`() {
        val inviteId = 1L
        val userId = 1L
        val invite = GroupInvite(inviteId, userId, 2L, 10L, "pending", null, null, null)

        `when`(inviteRepository.getGroupInviteById(inviteId)).thenReturn(invite)
        `when`(inviteRepository.declineInvite(inviteId)).thenReturn(0)

        assertThrows(NotUpdatedException::class.java) {
            inviteService.rejectInvite(inviteId, userId)
        }
    }
}
