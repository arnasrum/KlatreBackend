package com.arnas.klatrebackend.features.invites;

import com.arnas.klatrebackend.features.groups.GroupRepository;
import com.arnas.klatrebackend.features.users.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InviteServiceTest {

    @Mock
    private InviteRepository inviteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private InviteService inviteService;





}
