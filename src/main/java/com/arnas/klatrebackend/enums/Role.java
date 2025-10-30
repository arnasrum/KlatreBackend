package com.arnas.klatrebackend.enums;

public enum Role {
    owner(0),
    admin(1),
    member(2);

    public final int id;
    Role(int id) {
        this.id = id;
    }
}
