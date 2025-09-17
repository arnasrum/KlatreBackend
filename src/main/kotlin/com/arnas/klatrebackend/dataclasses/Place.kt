package com.arnas.klatrebackend.dataclasses

data class Place(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupID: Long,
    val gradingSystem: Long
)

data class Test(
    val id: Long,
    val grades: List<Grade>
)

data class PlaceWithGrades(
    val id: Long,
    val name: String,
    val description: String? = null,
    val groupID: Long,
    val gradingSystem: Test
)

data class PlaceWithBoulders(
    val place: Place,
    val boulders: Array<Boulder>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaceWithBoulders

        if (place != other.place) return false
        if (!boulders.contentEquals(other.boulders)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = place.hashCode()
        result = 31 * result + boulders.contentHashCode()
        return result
    }
}


data class PlaceRequest(
    val group_id: Long,
    val name: String,
    val description: String? = null
)