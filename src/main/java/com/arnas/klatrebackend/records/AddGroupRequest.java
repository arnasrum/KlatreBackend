package com.arnas.klatrebackend.records;

public record AddGroupRequest(
        String name,
        long owner,
        Boolean personal,
        String description,
        String[] invites
) {
    public AddGroupRequest {
        if (personal == null) personal = false;
        if (description == null) description = "";
        if (invites == null) invites = new String[] {};
    }
}
