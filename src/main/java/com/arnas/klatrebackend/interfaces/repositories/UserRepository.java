package com.arnas.klatrebackend.interfaces.repositories;

import com.arnas.klatrebackend.dataclasses.User;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository {

    @NonNull Long insertUser(@NonNull User user);
    Optional<User> getUserByEmail(@NonNull String email);
    Optional<User> getUserById(@NonNull Long userId);

}
