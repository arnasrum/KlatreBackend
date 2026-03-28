package com.arnas.klatrebackend.features.invites

import com.arnas.klatrebackend.features.auth.RequireGroupAccess
import com.arnas.klatrebackend.features.auth.Role
import com.arnas.klatrebackend.util.exceptions.UnauthorizedException
import com.arnas.klatrebackend.util.exceptions.InviteAlreadyProcessedException
import com.arnas.klatrebackend.util.exceptions.NotUpdatedException
import com.arnas.klatrebackend.features.groups.GroupRepository
import com.arnas.klatrebackend.features.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InviteServiceDefault(
    private val inviteRepository: InviteRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : InviteService {

    @RequireGroupAccess(minRole = Role.ADMIN)
    override fun sendInvite(userId: Long, groupId: Long, invitedUserId: Long) {
        val invite = inviteRepository.getGroupInviteById(invitedUserId)
        if (invite.status != "pending") {
            throw RuntimeException("Invite already has been ${invite.status} already.")
        }
        inviteRepository.inviteUserToGroup(userId, groupId, invitedUserId)
    }

    override fun getUserPendingInvites(userId: Long): List<GroupInviteDisplay> {
        val pendingInvites = inviteRepository.getUserInvitesByStatus(userId, "pending")
        return pendingInvites.map { invite ->
            val user = userRepository.getUserById(invite.senderId)
                ?: throw RuntimeException("User with Id: $userId not found")
            val group = groupRepository.getGroupById(invite.groupId)
                ?: throw RuntimeException("Group with Id: ${invite.groupId} not found")
            GroupInviteDisplay(
                id = invite.id,
                group = group,
                sender = user,
                status = invite.status
            )
        }
    }

    @Transactional
    override fun acceptInvite(inviteId: Long, userId: Long) {
        val invite = inviteRepository.getGroupInviteById(inviteId)
        if (invite.userId != userId) {
            throw UnauthorizedException("Tried accepting an invite that is not for the user.")
        }
        if (invite.status != "pending") {
            throw InviteAlreadyProcessedException(invite.status)
        }
        val userGroups = groupRepository.getGroups(invite.userId)
        if (userGroups.any { it.id == invite.groupId }) {
            throw InviteAlreadyProcessedException("User already has access to the group")
        }
        val rowsAffected = inviteRepository.acceptInvite(inviteId)
        if (rowsAffected != 1) throw NotUpdatedException("Only one row should be affected")
        groupRepository.addUserToGroup(invite.userId, invite.groupId, Role.USER.id)
    }

    @Transactional
    override fun rejectInvite(inviteId: Long, userId: Long) {
        val invite = inviteRepository.getGroupInviteById(inviteId)
        if (invite.userId != userId)
            throw UnauthorizedException("Tried declining an invite that is not for the user.")
        if (invite.status != "pending")
            throw InviteAlreadyProcessedException(invite.status)
        val rowsAffected = inviteRepository.declineInvite(inviteId)
        if (rowsAffected != 1) throw NotUpdatedException("Only one row should be affected")
    }
}

