package com.arnas.klatrebackend.services;

import com.arnas.klatrebackend.annotation.RequireGroupAccess;
import com.arnas.klatrebackend.dataclasses.GroupInviteDisplay;
import com.arnas.klatrebackend.dataclasses.Role;
import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.InviteRepository;
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface;
import com.arnas.klatrebackend.interfaces.services.InviteService;
import org.springframework.stereotype.Service;

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
    public void acceptInvite(long inviteId, long userId) {
        inviteRepository.acceptInvite(inviteId);
    }
}
