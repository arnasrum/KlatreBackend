package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record Group(
        long id,
        long owner,
        String name,
        boolean personal,
        @NonNull String uuid,
        String description
) {}
