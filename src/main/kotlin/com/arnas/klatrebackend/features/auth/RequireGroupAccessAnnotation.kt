package com.arnas.klatrebackend.features.auth

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireGroupAccess(
    val groupIdParam: String = "groupId",
    val userIdParam: String = "userId",
    val sourceObjectParam: String = "",
    val minRole: Role = Role.USER,
    val resolveGroupFrom: GroupAccessSource = GroupAccessSource.DIRECT
)
