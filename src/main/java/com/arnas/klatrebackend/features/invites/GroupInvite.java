package com.arnas.klatrebackend.features.invites;

import org.springframework.lang.NonNull;

public record GroupInvite(
    long id,
    long userId,
    long senderId,
    long groupId,
    @NonNull String status,
    Long acceptedAt,
    Long declinedAt,
    Long revokedAt

) { }
