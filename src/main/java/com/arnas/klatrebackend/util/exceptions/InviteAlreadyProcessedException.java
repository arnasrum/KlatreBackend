package com.arnas.klatrebackend.util.exceptions;

public class InviteAlreadyProcessedException extends RuntimeException{
    public InviteAlreadyProcessedException(String status) {
        super("Invite already " + status + ".");
    }
}
