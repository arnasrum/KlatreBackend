package com.arnas.klatrebackend.aspects

import com.arnas.klatrebackend.features.auth.AccessControlService
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import com.arnas.klatrebackend.annotation.RequireGroupAccess
import com.arnas.klatrebackend.features.auth.GroupAccessSource
import com.arnas.klatrebackend.features.auth.Role
import com.arnas.klatrebackend.features.climbingsessions.ClimbingSessionRepository
import com.arnas.klatrebackend.features.places.PlaceService
import com.arnas.klatrebackend.features.routes.RouteRepository
import kotlin.reflect.full.memberProperties
import kotlin.reflect.KProperty1

@Aspect
@Component
class AccessControlAspect(
    private val accessControlService: AccessControlService,
    private val placeService: PlaceService,
    private val routeRepository: RouteRepository,
    private val climbingSessionRepository: ClimbingSessionRepository
) {

    @Before("@annotation(requireGroupAccess)")
    fun checkGroupAccess(joinPoint: JoinPoint, requireGroupAccess:RequireGroupAccess) {
        val methodSignature = joinPoint.signature as MethodSignature
        val parameterNames = methodSignature.parameterNames
        val args = joinPoint.args

        val userIdIndex = parameterNames.indexOf(requireGroupAccess.userIdParam)
        if (userIdIndex == -1) {
            throw IllegalArgumentException("Parameter '${requireGroupAccess.userIdParam}' not found in method arguments")
        }
        val userId = args[userIdIndex] as Long

        val groupId = when (requireGroupAccess.resolveGroupFrom) {
            GroupAccessSource.DIRECT -> {
                val groupIdIndex = parameterNames.indexOf(requireGroupAccess.groupIdParam)
                if (groupIdIndex == -1) {
                    throw IllegalArgumentException("Parameter '${requireGroupAccess.groupIdParam}' not found in method arguments")
                }
                args[groupIdIndex] as Long
            }
            GroupAccessSource.FROM_PLACE -> {
                var placeId: Long
                if(requireGroupAccess.sourceObjectParam.isNotBlank()) {
                    val sourceObjectIndex = parameterNames.indexOf(requireGroupAccess.sourceObjectParam)
                    if (sourceObjectIndex == -1) {
                        throw IllegalArgumentException("Parameter '${requireGroupAccess.sourceObjectParam}' not found in method arguments")
                    }
                    val sourceObject = args[sourceObjectIndex]
                    placeId = getLongProperty("placeId", sourceObject)
                } else {
                    val placeIdIndex = parameterNames.indexOf("placeId")
                    if (placeIdIndex == -1) {
                        throw IllegalArgumentException("Parameter 'placeId' not found in method arguments")
                    }
                    placeId = args[placeIdIndex] as Long
                }
                placeService.getPlaceById(placeId)?.groupId
                    ?: throw IllegalArgumentException("Place with ID $placeId not found")
            }
            GroupAccessSource.FROM_ROUTE -> {
                var routeId: Long
                if(requireGroupAccess.sourceObjectParam.isNotBlank()) {
                    val sourceObjectIndex = parameterNames.indexOf(requireGroupAccess.sourceObjectParam)
                    if (sourceObjectIndex == -1) {
                        throw IllegalArgumentException("Parameter '${requireGroupAccess.sourceObjectParam}' not found in method arguments")
                    }
                    val sourceObject = args[sourceObjectIndex]
                    routeId = getLongProperty("routeId", sourceObject)
                } else {
                    val routeIdIndex = parameterNames.indexOf("routeId")
                    if (routeIdIndex == -1) {
                        throw IllegalArgumentException("Parameter 'routeId' not found in method arguments")
                    }
                    routeId = args[routeIdIndex] as Long
                }
                val routeOptional = routeRepository.getRouteById(routeId)
                if(routeOptional.isEmpty) throw IllegalArgumentException("Route with ID $routeId not found")

                val place = placeService.getPlaceById(routeOptional.get().placeId)
                    ?: throw IllegalArgumentException("Place not found")
                place.groupId
            }
            GroupAccessSource.FROM_SESSION -> {
                val sessionIdIndex = parameterNames.indexOf("sessionId")
                if (sessionIdIndex == -1) {
                    throw IllegalArgumentException("Parameter 'sessionId' not found in method arguments")
                }
                val sessionId = args[sessionIdIndex] as Long
                climbingSessionRepository.getClimbingSessionById(sessionId)?.groupId
                    ?: climbingSessionRepository.getClimbingSessionById(sessionId)?.groupId
                    ?: throw IllegalArgumentException("Session with ID $sessionId not found")
            }
        }

        val userRole = accessControlService.getUserGroupRole(userId, groupId)
            ?: throw RuntimeException("User has no access to this group")
        if (userRole > requireGroupAccess.minRole.id) {
            throw RuntimeException("User does not have sufficient permissions. Required: ${requireGroupAccess.minRole}, User has role: ${Role.fromId(userRole)}")
        }
    }

    fun getLongProperty(propertyName: String, obj: Any): Long {
        val kClass = obj::class

        val property = kClass.memberProperties.find { it.name == propertyName }

        if (property == null) {
            throw NoSuchElementException("Property '$propertyName' not found on object of type ${kClass.simpleName}.")
        }
        @Suppress("UNCHECKED_CAST")
        val kProperty = property as? KProperty1<Any, *>
            ?: throw IllegalStateException("Property '$propertyName' is not a valid instance property.")

        val value = kProperty.get(obj)
            ?: throw IllegalStateException("Property '$propertyName' returned null.")

        if (value is Long) {
            return value
        } else {
            throw TypeCastException(
                "Property '$propertyName' is of type ${value::class.simpleName}, " +
                        "but expected Long."
            )
        }
    }

}
