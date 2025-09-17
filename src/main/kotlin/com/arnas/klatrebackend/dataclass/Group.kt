package com.arnas.klatrebackend.dataclass

data class Group(
    var id: Long,
    var owner: Long,
    var name: String,
    var personal: Boolean,
    var uuid: String,
    var description: String? = null,
)

data class AddGroupRequest(
    var name: String,
    var owner: Long,
    var personal: Boolean?,
    var description: String? = null,
    var invites: List<String>? = null,
)

data class GroupWithPlaces(
    var group: Group,
    var places: List<Place>
)