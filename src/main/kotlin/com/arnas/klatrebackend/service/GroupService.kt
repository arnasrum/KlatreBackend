package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.AddGroupRequest
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.PlaceRequest
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.GroupRepository
import com.arnas.klatrebackend.repository.UserRepository
import org.springframework.stereotype.Service

@Service
open class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) {

    open fun getGroups(userID: Long): ServiceResult<Array<GroupWithPlaces>> {
        val groups: Array<GroupWithPlaces> = groupRepository.getGroups(userID)
        return ServiceResult(data = groups, message = "Groups retrieved successfully", success = true)
    }

    open fun addGroup(userID: Long, request: AddGroupRequest): ServiceResult<Long> {
        val name = request.name
        val personal: Boolean = (request.personal ?: ServiceResult(data = null, message = "Personal status is required", success = false)) as Boolean
        val group = AddGroupRequest(owner = userID, name = name, personal = personal)
        request.name.let { group.name = it }
        request.description?.let { group.description = it }
        val groupID = groupRepository.addGroup(group)
        val userIDs = request.invites?.mapNotNull { email ->
            userRepository.getUserByEmail(email)?.id
        } ?: emptyList()

        groupRepository.addUserToGroup(userID, groupID, 0)
        userIDs.forEach {
            groupRepository.addUserToGroup(it, groupID, 2)
        }
        return ServiceResult(data = groupID, message = "Group added successfully", success = true)
    }


    open fun addPlaceToGroup(groupID: Long, placeRequest: PlaceRequest) {

        groupRepository.addPlaceToGroup(groupID, placeRequest)

    }

    open fun deleteGroup(userID: Long, groupID: Long): ServiceResult<Unit> {
        val userRole = groupRepository.getUserGroupRole(userID, groupID)
            ?: return ServiceResult(success = false, message = "User is not a member of group")

        if (userRole != 0) {
            return ServiceResult(success = false, message = "Only group owners can delete groups")
        }
        val result = groupRepository.deleteGroup(groupID)
        return if (result.success) {
            ServiceResult(success = true, message = "Group deleted successfully")
        } else {
            ServiceResult(success = false, message = result.message, errorCode = "401")
        }
    }
}