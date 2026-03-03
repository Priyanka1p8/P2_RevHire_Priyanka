package com.rev.app.service;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
