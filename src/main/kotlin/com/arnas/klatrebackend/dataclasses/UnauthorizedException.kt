package com.arnas.klatrebackend.dataclasses

class UnauthorizedException(
    message: String? = null,
    cause: Throwable? = null
): RuntimeException(message, cause)