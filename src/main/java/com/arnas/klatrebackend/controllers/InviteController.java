package com.arnas.klatrebackend.controllers;

import com.arnas.klatrebackend.dataclasses.GroupInviteDisplay;
import com.arnas.klatrebackend.dataclasses.User;
import com.arnas.klatrebackend.interfaces.services.InviteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invite")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @GetMapping("/pending")
    ResponseEntity<List<GroupInviteDisplay>> getUserPendingInvites(User user) {
        return ResponseEntity.ok().body(inviteService.getUserPendingInvites(user.getId()));
    }

    @PutMapping("/accept")
    ResponseEntity<String> acceptInvite(@RequestParam long inviteId, User user) {
        inviteService.acceptInvite(inviteId, user.getId());
        return ResponseEntity.ok().body("Invite accepted successfully");
    }

    @PutMapping("/reject")
    ResponseEntity<String> rejectInvite(@RequestParam long inviteId, User user) {
        inviteService.rejectInvite(inviteId, user.getId());
        return ResponseEntity.ok().body("Invite rejected successfully");
    }
}
