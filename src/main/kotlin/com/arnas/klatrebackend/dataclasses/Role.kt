package com.arnas.klatrebackend.dataclasses

enum class Role(val id: Int ) {
    OWNER(0),
    ADMIN(1),
    USER(2);

    companion object {
        fun fromId(id: Int) = entries.firstOrNull { it.id == id }
    }
}