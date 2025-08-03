package com.arnas.klatrebackend.dataclass

data class Group(
    var owner: Long,
    var id: Long? = null,
    var name: String? = null,
    var description: String? = null
)