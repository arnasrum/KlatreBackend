package com.arnas.klatrebackend.services;

import com.arnas.klatrebackend.interfaces.repositories.UserRepositoryInterface;
import com.arnas.klatrebackend.dataclasses.User;
import com.arnas.klatrebackend.interfaces.services.UserServiceInterface;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceDefault implements UserServiceInterface {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final UserRepositoryInterface userRepository;

    public UserServiceDefault(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NonNull
    public User getUserById(long userId) {
        var user = userRepository.getUserById(userId);
        if(user == null) throw new RuntimeException("User not found");
        return user;
    }

    @Override
    @NonNull
    public Map<String, String> getGoogleUserProfile(@NonNull String token) {
        var request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + token))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) return new HashMap<>();
            //var json = new .parse(response.body()).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching user profile from Google");
        }
        return new HashMap<>();
    }

    @Override
    public Long createOrUpdateUser(@NonNull Map<String, String> userInfo) {
        var email = userInfo.get("email");
        var name = userInfo.get("name");
        if(email == null || name == null) return null;
        var user = userRepository.getUserByEmail(email);
        if(user != null)
            return user.getId();
        return userRepository.insertUser(email, name);
    }
}
