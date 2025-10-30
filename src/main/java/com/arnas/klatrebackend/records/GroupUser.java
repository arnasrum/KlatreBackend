package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record GroupUser(
        long id,
        long groupId,
        @NonNull String name,
        @NonNull String email,
        boolean isOwner,
        boolean isAdmin

) { }
