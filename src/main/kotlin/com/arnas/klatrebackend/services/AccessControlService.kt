package com.arnas.klatrebackend.services

import com.arnas.klatrebackend.repositories.GroupRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class AccessControlService(
    private val groupRepository: GroupRepository,
) {

    @Cacheable(value = ["userGroupAccess"], key = "#userId + ':' + #groupId")
    fun hasGroupAccess(userId: Long, groupId: Long): Boolean {
        return groupRepository.getUserGroupRole(userId, groupId) != null
    }

    @Cacheable(value = ["userGroupRole"], key = "#userId + ':' + #groupId")
    fun getUserGroupRole(userId: Long, groupId: Long): Int? {
        return groupRepository.getUserGroupRole(userId, groupId)
    }

    @CacheEvict(
        value = ["userGroupAccess", "userGroupRole"],
        key = "#userId + ':' + #groupId"
    )
    fun evictUserGroupCache(userId: Long, groupId: Long) {}

    @CacheEvict(
        value = ["userGroupAccess", "userGroupRole"],
        allEntries = true
    )
    fun evictAllGroupCache() {}
}