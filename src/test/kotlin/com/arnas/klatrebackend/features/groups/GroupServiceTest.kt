package com.arnas.klatrebackend.features.groups

import com.arnas.klatrebackend.features.gradesystems.GradeSystemRepository
import com.arnas.klatrebackend.features.gradesystems.GradingSystem
import com.arnas.klatrebackend.features.places.Place
import com.arnas.klatrebackend.features.places.PlaceRepository
import com.arnas.klatrebackend.features.places.PlaceRequest
import com.arnas.klatrebackend.features.users.GroupUser
import com.arnas.klatrebackend.features.users.User
import com.arnas.klatrebackend.features.users.UserRepository
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
class GroupServiceTest {

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var placeRepository: PlaceRepository

    @Mock
    private lateinit var gradingSystemRepository: GradeSystemRepository

    @InjectMocks
    private lateinit var groupService: GroupServiceDefault


    // --- getGroups ---

    @Test
    fun `getGroups returns groups with places`() {
        val userId = 1L
        val group = Group(1L, userId, "Group 1", false, "uuid-1", null)
        val places = listOf(Place(1L, "Place 1", null, 1L, 1L))

        `when`(groupRepository.getGroups(userId)).thenReturn(listOf(group))
        `when`(placeRepository.getPlacesByGroupId(1L)).thenReturn(places)

        val result = groupService.getGroups(userId)

        assertEquals(1, result.size)
        assertEquals("Group 1", result[0].name)
        assertEquals(1, result[0].places.size)
        assertEquals("Place 1", result[0].places[0].name)
    }

    @Test
    fun `getGroups returns empty list when user has no groups`() {
        `when`(groupRepository.getGroups(1L)).thenReturn(emptyList())

        val result = groupService.getGroups(1L)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getGroups returns group with empty places`() {
        val userId = 1L
        val group = Group(1L, userId, "Empty Group", false, "uuid-1", null)

        `when`(groupRepository.getGroups(userId)).thenReturn(listOf(group))
        `when`(placeRepository.getPlacesByGroupId(1L)).thenReturn(emptyList())

        val result = groupService.getGroups(userId)

        assertEquals(1, result.size)
        assertTrue(result[0].places.isEmpty())
    }

    // --- addGroup ---

    @Test
    fun `addGroup creates group and adds owner`() {
        val userId = 1L
        val request = AddGroupRequest("New Group", userId, false, "A group", null)
        val expectedGroupId = 10L
        // Service creates: AddGroupRequest(owner=userId, name=request.name, personal=false)
        // then sets name and description from request
        val expectedGroup = AddGroupRequest(name = "New Group", owner = userId, personal = false, description = "A group")

        `when`(groupRepository.addGroup(expectedGroup)).thenReturn(expectedGroupId)

        val result = groupService.addGroup(userId, request)

        assertEquals(expectedGroupId, result)
        verify(groupRepository).addUserToGroup(userId, expectedGroupId, 0)
    }

    @Test
    fun `addGroup with invites adds invited users`() {
        val userId = 1L
        val invitedUser = User(2L, "invited@test.com", "Invited")
        val request = AddGroupRequest("New Group", userId, false, null, listOf("invited@test.com"))
        val expectedGroupId = 10L
        val expectedGroup = AddGroupRequest(name = "New Group", owner = userId, personal = false)

        `when`(groupRepository.addGroup(expectedGroup)).thenReturn(expectedGroupId)
        `when`(userRepository.getUserByEmail("invited@test.com")).thenReturn(invitedUser)

        groupService.addGroup(userId, request)

        verify(groupRepository).addUserToGroup(userId, expectedGroupId, 0)
        verify(groupRepository).addUserToGroup(2L, expectedGroupId, 2)
    }

    @Test
    fun `addGroup with non-existent invite email skips that user`() {
        val userId = 1L
        val request = AddGroupRequest("New Group", userId, false, null, listOf("nonexistent@test.com"))
        val expectedGroupId = 10L
        val expectedGroup = AddGroupRequest(name = "New Group", owner = userId, personal = false)

        `when`(groupRepository.addGroup(expectedGroup)).thenReturn(expectedGroupId)
        `when`(userRepository.getUserByEmail("nonexistent@test.com")).thenReturn(null)

        groupService.addGroup(userId, request)

        // Only the owner should be added, not the nonexistent user
        verify(groupRepository).addUserToGroup(userId, expectedGroupId, 0)
        verify(groupRepository, times(1)).addUserToGroup(anyLong(), anyLong(), anyInt())
    }

    // --- addPlaceToGroup ---

    @Test
    fun `addPlaceToGroup creates a place`() {
        val userId = 1L
        val groupId = 1L
        val placeRequest = PlaceRequest(groupId, "New Place", "desc")

        `when`(placeRepository.addPlaceToGroup(groupId, "New Place", "desc")).thenReturn(100L)

        val result = groupService.addPlaceToGroup(userId, groupId, placeRequest)

        assertEquals(100L, result)
    }

    // --- deleteGroup ---

    @Test
    fun `deleteGroup succeeds when rows affected`() {
        `when`(groupRepository.deleteGroup(1L)).thenReturn(1)

        assertDoesNotThrow { groupService.deleteGroup(1L, 1L) }

        verify(groupRepository).deleteGroup(1L)
    }

    @Test
    fun `deleteGroup throws when no rows affected`() {
        `when`(groupRepository.deleteGroup(999L)).thenReturn(0)

        assertThrows(Exception::class.java) {
            groupService.deleteGroup(1L, 999L)
        }
    }

    // --- getGroupUserRole ---

    @Test
    fun `getGroupUserRole returns role when user is member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(0)

        val result = groupService.getGroupUserRole(1L, 1L)

        assertEquals(0, result)
    }

    @Test
    fun `getGroupUserRole returns null when user is not member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(null)

        val result = groupService.getGroupUserRole(1L, 1L)

        assertNull(result)
    }

    // --- changeGroupUserRole ---

    @Test
    fun `changeGroupUserRole succeeds when acting user has higher permissions`() {
        val actingUserId = 1L
        val targetUserId = 2L
        val groupId = 1L

        `when`(groupRepository.getUserGroupRole(actingUserId, groupId)).thenReturn(0) // OWNER
        `when`(groupRepository.getUserGroupRole(targetUserId, groupId)).thenReturn(2) // USER
        `when`(groupRepository.updateUserGroupRole(targetUserId, groupId, 1)).thenReturn(1)

        assertDoesNotThrow { groupService.changeGroupUserRole(actingUserId, targetUserId, 1, groupId) }

        verify(groupRepository).updateUserGroupRole(targetUserId, groupId, 1)
    }

    @Test
    fun `changeGroupUserRole throws UnauthorizedException when acting user is not member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(null)

        assertThrows(UnauthorizedException::class.java) {
            groupService.changeGroupUserRole(1L, 2L, 1, 1L)
        }
    }

    @Test
    fun `changeGroupUserRole throws when target user is not member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(0)
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            groupService.changeGroupUserRole(1L, 2L, 1, 1L)
        }
    }

    @Test
    fun `changeGroupUserRole throws when acting user has insufficient permissions`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(2) // USER
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(1) // ADMIN

        assertThrows(UnauthorizedException::class.java) {
            groupService.changeGroupUserRole(1L, 2L, 2, 1L)
        }
    }

    @Test
    fun `changeGroupUserRole does nothing when role is same`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(0)
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(2)

        groupService.changeGroupUserRole(1L, 2L, 2, 1L)

        // No updateUserGroupRole should be called
        verifyNoMoreInteractions(groupRepository.also {
            verify(it).getUserGroupRole(1L, 1L)
            verify(it).getUserGroupRole(2L, 1L)
        })
    }

    // --- kickUserFromGroup ---

    @Test
    fun `kickUserFromGroup succeeds when acting user has higher permissions`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(0) // OWNER
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(2) // USER

        assertDoesNotThrow { groupService.kickUserFromGroup(1L, 2L, 1L) }

        verify(groupRepository).deleteUserFromGroup(2L, 1L)
    }

    @Test
    fun `kickUserFromGroup throws UnauthorizedException when acting user is not member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(null)

        assertThrows(UnauthorizedException::class.java) {
            groupService.kickUserFromGroup(1L, 2L, 1L)
        }
    }

    @Test
    fun `kickUserFromGroup throws when target user is not member`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(0)
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(null)

        assertThrows(RuntimeException::class.java) {
            groupService.kickUserFromGroup(1L, 2L, 1L)
        }
    }

    @Test
    fun `kickUserFromGroup throws when acting user has insufficient permissions`() {
        `when`(groupRepository.getUserGroupRole(1L, 1L)).thenReturn(2)
        `when`(groupRepository.getUserGroupRole(2L, 1L)).thenReturn(1)

        assertThrows(UnauthorizedException::class.java) {
            groupService.kickUserFromGroup(1L, 2L, 1L)
        }
    }

    // --- getGradingSystemsInGroup ---

    @Test
    fun `getGradingSystemsInGroup returns grading systems`() {
        val groupId = 1L
        val systems = listOf(
            GradingSystem(1L, "V-Scale", "bouldering", true, emptyList()),
            GradingSystem(2L, "French", "sport", false, emptyList())
        )

        `when`(gradingSystemRepository.getGradingSystemsInGroup(groupId)).thenReturn(systems)

        val result = groupService.getGradingSystemsInGroup(groupId)

        assertEquals(2, result.size)
        assertEquals("V-Scale", result[0].name)
    }

    @Test
    fun `getGradingSystemsInGroup returns empty list`() {
        `when`(gradingSystemRepository.getGradingSystemsInGroup(999L)).thenReturn(emptyList())

        val result = groupService.getGradingSystemsInGroup(999L)

        assertTrue(result.isEmpty())
    }

    // --- getUsersInGroup ---

    @Test
    fun `getUsersInGroup returns group users`() {
        val groupId = 1L
        val users = listOf(
            GroupUser(1L, "User 1", "user1@test.com", true, true, groupId),
            GroupUser(2L, "User 2", "user2@test.com", false, false, groupId)
        )

        `when`(groupRepository.getGroupUsers(groupId)).thenReturn(users)

        val result = groupService.getUsersInGroup(1L, groupId)

        assertEquals(2, result.size)
        assertTrue(result[0].isOwner)
        assertFalse(result[1].isOwner)
    }

    // --- getGroupByUuid ---

    @Test
    fun `getGroupByUuid returns group when found`() {
        val uuid = "test-uuid"
        val group = Group(1L, 1L, "Test Group", false, uuid, null)

        `when`(groupRepository.getGroupByUuid(uuid)).thenReturn(group)

        val result = groupService.getGroupByUuid(uuid)

        assertEquals(group, result)
    }

    @Test
    fun `getGroupByUuid throws when group not found`() {
        `when`(groupRepository.getGroupByUuid("invalid-uuid")).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            groupService.getGroupByUuid("invalid-uuid")
        }
    }
}
