package com.arnas.klatrebackend.service;

import com.arnas.klatrebackend.interfaces.repositories.GroupRepositoryInterface;
import com.arnas.klatrebackend.interfaces.repositories.InviteRepository;
import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface;
import com.arnas.klatrebackend.interfaces.services.InviteService;
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
    private UserRepositoryInterface userRepository;
    @Mock
    private GroupRepositoryInterface groupRepository;
    @InjectMocks
    private InviteService inviteService;





}
