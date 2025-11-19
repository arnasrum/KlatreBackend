package com.arnas.klatrebackend.services;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.dataclasses.GroupInviteDisplay;
import com.arnas.klatrebackend.dataclasses.Role;
import com.arnas.klatrebackend.dataclasses.UnauthorizedException;
import com.arnas.klatrebackend.exceptions.InviteAlreadyProcessedException;
import com.arnas.klatrebackend.exceptions.NotUpdatedException;
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.InviteRepository;
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface;
import com.arnas.klatrebackend.interfaces.services.InviteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InviteServiceDefault implements InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepositoryInterface userRepository;
    private final GroupRepositoryInterface groupRepository;

    public InviteServiceDefault(
            InviteRepository inviteRepository,
            UserRepositoryInterface userRepository,
            GroupRepositoryInterface groupRepository
    ) {
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this .groupRepository = groupRepository;
    }

    @Override
    @RequireGroupAccess(minRole = Role.ADMIN)
    public void sendInvite(long userId, long groupId, long invitedUserId) {
        var invite = inviteRepository.getGroupInviteById(invitedUserId);
        if(!invite.getStatus().equals("pending"))  {
            throw new RuntimeException("Invite already has been " + invite.getStatus() + " already.");
        }
        inviteRepository.inviteUserToGroup(userId, groupId, invitedUserId);
    }

    @Override
    public List<GroupInviteDisplay> getUserPendingInvites(long userId) {
        var pendingInvites = inviteRepository.getUserInvitesByStatus(userId, "pending");
        return pendingInvites.stream().map((invite) -> {
            var user = userRepository.getUserById(invite.getSenderId());
            if(user == null) throw new RuntimeException("User with Id: " + userId + " not found");
            var group = groupRepository.getGroupById(invite.getGroupId());
            if(group == null) throw new RuntimeException("Group with Id: " + invite.getGroupId() + " not found");
            return new GroupInviteDisplay(invite.getId(), group, user, invite.getStatus());
        }).toList();
    }

    @Override
    @Transactional
    public void acceptInvite(long inviteId, long userId) {
        var invite = inviteRepository.getGroupInviteById(inviteId);
        if(invite.getUserId() != userId) {
            throw new UnauthorizedException("Tried accepting an invite that is not for the user.", null);
        }
        if(!invite.getStatus().equals("pending")) {
            throw new InviteAlreadyProcessedException(invite.getStatus());
        }
        var userGroups = groupRepository.getGroups(invite.getUserId()).stream();
        if(userGroups.anyMatch(group -> group.getId() == invite.getGroupId())) {
            throw new InviteAlreadyProcessedException("User already has access to the group");
        }
        var rowsAffected = inviteRepository.acceptInvite(inviteId);
        if(rowsAffected != 1) throw new NotUpdatedException("Only one row should be affected");
        groupRepository.addUserToGroup(invite.getUserId(), invite.getGroupId(), Role.USER.getId());
    }

    @Override
    @Transactional
    public void rejectInvite(long inviteId, long userId) {
        var invite = inviteRepository.getGroupInviteById(inviteId);
        if(invite.getUserId() != userId)
            throw new UnauthorizedException("Tried declining an invite that is not for the user.", null);
        if(!invite.getStatus().equals("pending"))
            throw new InviteAlreadyProcessedException(invite.getStatus());
        var rowsAffected = inviteRepository.declineInvite(inviteId);
        if(rowsAffected != 1) throw new NotUpdatedException("Only one row should be affected");
    }
}
