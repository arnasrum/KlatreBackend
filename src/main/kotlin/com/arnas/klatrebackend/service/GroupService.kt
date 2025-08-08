package com.arnas.klatrebackend.service

import com.arnas.klatrebackend.dataclass.Group
import com.arnas.klatrebackend.dataclass.GroupWithPlaces
import com.arnas.klatrebackend.dataclass.ServiceResult
import com.arnas.klatrebackend.repository.GroupRepository
import org.springframework.stereotype.Service

@Service
open class GroupService(
    private val groupRepository: GroupRepository,
) {

    open fun getGroups(userID: Long): ServiceResult<Array<GroupWithPlaces>> {
        val groups: Array<GroupWithPlaces> = groupRepository.getGroups(userID)
        return ServiceResult(data = groups, message = "Groups retrieved successfully", success = true)
    }

    open fun addGroup(userID: Long, body: Map<String, String>): ServiceResult<Long> {
        val name = body["name"] ?: return ServiceResult(data = null, message = "Name is required", success = false)
        val personal: Boolean = (body["personal"]?.toBoolean() ?: ServiceResult(data = null, message = "Personal status is required", success = false)) as Boolean
        val group = Group(owner = userID, name = name, personal = personal)
        body["name"]?.let { group.name = it }
        body["description"]?.let { group.description = it }
        val groupID = groupRepository.addGroup(group)
        return ServiceResult(data = groupID, message = "Group added successfully", success = true)
    }




}