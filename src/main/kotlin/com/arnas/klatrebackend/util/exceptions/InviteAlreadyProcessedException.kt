package com.arnas.klatrebackend.util.exceptions

class InviteAlreadyProcessedException(status: String) : RuntimeException("Invite already $status.")

