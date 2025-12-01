package com.arnas.klatrebackend.features.invites;

import com.arnas.klatrebackend.features.auth.RequireGroupAccess;
import com.arnas.klatrebackend.features.auth.Role;
import com.arnas.klatrebackend.util.exceptions.UnauthorizedException;
import com.arnas.klatrebackend.util.exceptions.InviteAlreadyProcessedException;
import com.arnas.klatrebackend.util.exceptions.NotUpdatedException;
import com.arnas.klatrebackend.features.groups.GroupRepository;
import com.arnas.klatrebackend.features.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InviteServiceDefault implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public InviteServiceDefault(
            InviteRepository inviteRepository,
            UserRepository userRepository,
            GroupRepository groupRepository
    ) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this .groupRepository = groupRepository;
    }

    @Override
    @RequireGroupAccess(minRole = Role.ADMIN)
    public void sendInvite(long userId, long groupId, long invitedUserId) {
        var invite = inviteRepository.getGroupInviteById(invitedUserId);
        if(!invite.status().equals("pending"))  {
            throw new RuntimeException("Invite already has been " + invite.status() + " already.");
        }
        inviteRepository.inviteUserToGroup(userId, groupId, invitedUserId);
    }

    @Override
    public List<GroupInviteDisplay> getUserPendingInvites(long userId) {
        var pendingInvites = inviteRepository.getUserInvitesByStatus(userId, "pending");
        return pendingInvites.stream().map((invite) -> {
            var user = userRepository.getUserById(invite.senderId());
            if(user == null) throw new RuntimeException("User with Id: " + userId + " not found");
            var group = groupRepository.getGroupById(invite.groupId());
            if(group == null) throw new RuntimeException("Group with Id: " + invite.groupId() + " not found");
            return new GroupInviteDisplay(invite.id(), group, user, invite.status());
        }).toList();
    }

    @Override
    @Transactional
    public void acceptInvite(long inviteId, long userId) {
        var invite = inviteRepository.getGroupInviteById(inviteId);
        if(invite.userId() != userId) {
            throw new UnauthorizedException("Tried accepting an invite that is not for the user.");
        }
        if(!invite.status().equals("pending")) {
            throw new InviteAlreadyProcessedException(invite.status());
        }
        var userGroups = groupRepository.getGroups(invite.userId()).stream();
        if(userGroups.anyMatch(group -> group.getId() == invite.groupId())) {
            throw new InviteAlreadyProcessedException("User already has access to the group");
        }
        var rowsAffected = inviteRepository.acceptInvite(inviteId);
        if(rowsAffected != 1) throw new NotUpdatedException("Only one row should be affected");
        groupRepository.addUserToGroup(invite.userId(), invite.groupId(), Role.USER.getId());
    }

    @Override
    @Transactional
    public void rejectInvite(long inviteId, long userId) {
        var invite = inviteRepository.getGroupInviteById(inviteId);
        if(invite.userId() != userId)
            throw new UnauthorizedException("Tried declining an invite that is not for the user.");
        if(!invite.status().equals("pending"))
            throw new InviteAlreadyProcessedException(invite.status());
        var rowsAffected = inviteRepository.declineInvite(inviteId);
        if(rowsAffected != 1) throw new NotUpdatedException("Only one row should be affected");
    }
}
