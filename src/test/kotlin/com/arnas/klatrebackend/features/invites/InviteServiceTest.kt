package com.arnas.klatrebackend.features.invites

import com.arnas.klatrebackend.features.groups.GroupRepository
import com.arnas.klatrebackend.features.users.UserRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class InviteServiceTest {

    @Mock
    private lateinit var inviteRepository: InviteRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @InjectMocks
    private lateinit var inviteService: InviteServiceDefault
}

