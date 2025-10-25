package com.arnas.klatrebackend.annotation

import com.arnas.klatrebackend.dataclasses.Role
import com.arnas.klatrebackend.dataclasses.GroupAccessSource

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequireGroupAccess(
    val groupIdParam: String = "groupId",
    val userIdParam: String = "userId",
    val sourceObjectParam: String = "",
    val minRole: Role = Role.USER,
    val resolveGroupFrom: GroupAccessSource = GroupAccessSource.DIRECT
)
