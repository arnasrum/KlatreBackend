package com.arnas.klatrebackend.interfaces.services;

import com.arnas.KlatreBackend.records.User;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    Map<String, String> getGoogleUserProfile(String token);
    Long createOrUpdateUser(Map<String, String> userInfo);
    User getUserById(Long userId);
}
