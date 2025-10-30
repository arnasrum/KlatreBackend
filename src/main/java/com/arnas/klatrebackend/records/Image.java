package com.arnas.KlatreBackend.records;

import org.springframework.lang.NonNull;

public record Image(
        long id,
        @NonNull String contentType,
        long fileSize
) { }
