package com.arnas.klatrebackend.components;

import com.arnas.klatrebackend.interfaces.services.JwtServiceInterface;
import com.arnas.klatrebackend.interfaces.services.UserServiceInterface;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Primary
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_COOKIE_NAME = "authToken";
    private final JwtServiceInterface jwtService;
    private final UserServiceInterface userService;

    public JwtAuthenticationFilter(
            @Autowired JwtServiceInterface jwtService,
            @Autowired UserServiceInterface userService
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            Cookie authCookieObject = Arrays.stream(request.getCookies())
                    .filter((Cookie cookie) -> cookie.getName().equals(AUTH_COOKIE_NAME))
                    .findFirst()
                    .orElse(null);
            if (authCookieObject == null || authCookieObject.getValue() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("No auth cookie found.");
                return;
            }
            String authCookie = authCookieObject.getValue();
            if (!jwtService.validateJwtToken(authCookie)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid auth cookie.");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var userId = Long.parseLong(jwtService.decodeJwt(authCookie).getPayload().getSubject());
                var user = userService.getUserById(userId);
                var authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                authentication.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            var newToken = jwtService.refreshToken(authCookie);
            String cookieValue = String.format("authToken=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", newToken, 60 * 60);
            response.setHeader("Set-Cookie", cookieValue);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token.");
        }
        filterChain.doFilter(request,response);
    }
}
