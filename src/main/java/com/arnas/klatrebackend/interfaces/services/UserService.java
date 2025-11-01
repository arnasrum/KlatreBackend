package com.arnas.klatrebackend.interfaces.services;

import com.arnas.klatrebackend.dataclasses.User;

import java.util.Map;

public interface UserService {
    Map<String, String> getGoogleUserProfile(String token);
    Long createOrUpdateUser(Map<String, String> userInfo);
    User getUserById(Long userId);
}
