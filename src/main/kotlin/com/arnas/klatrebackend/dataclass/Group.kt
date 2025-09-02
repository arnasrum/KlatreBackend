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
    var places: Array<Place>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupWithPlaces

        if (group != other.group) return false
        if (!places.contentEquals(other.places)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + places.contentHashCode()
        return result
    }
}