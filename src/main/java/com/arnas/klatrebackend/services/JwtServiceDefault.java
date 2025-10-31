package com.arnas.klatrebackend.services;


import com.arnas.klatrebackend.exceptions.InvalidTokenException;
import com.arnas.klatrebackend.interfaces.services.JwtServiceInterface;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Date;
import java.util.Map;

@Service
public class JwtServiceDefault implements JwtServiceInterface {

    @Value("${JwtSecret:ThisIsATestingKeyPleaseDoNotStealThisKeepAwayFromTheCookieJar}")
    private String secretKey;
    private final Integer TOKEN_TTL_SECONDS = 60 * 60;

    @Override
    @NonNull
    public String createJwtToken(@NonNull String subject, @NonNull Map<String, String> claims) {
        var date = new Date();
        var jwtBuilder = Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(date.toInstant()))
                .expiration(Date.from(date.toInstant().plusSeconds(TOKEN_TTL_SECONDS)));
        return jwtBuilder.signWith(Keys.hmacShaKeyFor(secretKey.getBytes())).compact();
    }

    @Override
    @NonNull
    public Jws<Claims> decodeJwt(@NonNull String jwt) {
        //try {
        var jwtParser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();
        return jwtParser.parseSignedClaims(jwt);
        /*
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid JWT token");
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT claims string is empty.");
        }
        */
    }

    @Override
    @NonNull
    public Map<String, String> getJwtPayload(Jws<Claims> claims) {
        var payload = new HashMap<String, String>();
        claims.getPayload().forEach((key, value) -> {
            payload.put(key, value.toString());
        });
        return payload;
    }

    @Override
    public boolean validateJwtToken(@NonNull String token) {
        try {
            var parser = Jwts.parser();
            var claims = parser
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public String refreshToken(@NonNull String token) {
        try{
            var claims = decodeJwt(token).getPayload();
            var subject = claims.getSubject();
            var id = claims.get("id");
            var email = claims.get("email");
            var name = claims.get("name");

            if( subject == null || id == null || email == null || name == null)
                return null;

            return createJwtToken(
                subject,
                Map.ofEntries(
                    Map.entry("id", id.toString()),
                    Map.entry("email", email.toString()),
                    Map.entry("name", name.toString())
                )
            );
        } catch (Exception e) {
            return null;
        }
    }
}
