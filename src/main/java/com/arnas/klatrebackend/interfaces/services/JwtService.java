package com.arnas.klatrebackend.interfaces.services;

import java.util.Map;

import com.arnas.klatrebackend.exceptions.InvalidTokenException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String createJwtToken(String subject, Map<String, String> claims, Integer ttlSeconds);
    Jws<Claims> decodeJwt(String jwt) throws InvalidTokenException;
    Map<String, String> getJwtPayload(Jws<Claims> claims);
    boolean validateJwtToken(String token);
    String refreshToken(String token);
}
