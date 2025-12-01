package com.arnas.klatrebackend.features.invites;

import com.arnas.klatrebackend.features.groups.Group;
import com.arnas.klatrebackend.features.users.User;
import org.springframework.lang.NonNull;

public record GroupInviteDisplay(
    long id,
    @NonNull Group group,
    @NonNull User sender,
    @NonNull String status
) { }
