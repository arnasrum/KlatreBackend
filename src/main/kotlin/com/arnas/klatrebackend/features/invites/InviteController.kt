package com.arnas.klatrebackend.features.invites

import com.arnas.klatrebackend.features.users.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/invite")
class InviteController(
    private val inviteService: InviteService
) {

    @GetMapping("/pending")
    fun getUserPendingInvites(user: User): ResponseEntity<List<GroupInviteDisplay>> {
        return ResponseEntity.ok().body(inviteService.getUserPendingInvites(user.id))
    }

    @PutMapping("/accept")
    fun acceptInvite(@RequestParam inviteId: Long, user: User): ResponseEntity<String> {
        inviteService.acceptInvite(inviteId, user.id)
        return ResponseEntity.ok().body("Invite accepted successfully")
    }

    @PutMapping("/reject")
    fun rejectInvite(@RequestParam inviteId: Long, user: User): ResponseEntity<String> {
        inviteService.rejectInvite(inviteId, user.id)
        return ResponseEntity.ok().body("Invite rejected successfully")
    }
}

