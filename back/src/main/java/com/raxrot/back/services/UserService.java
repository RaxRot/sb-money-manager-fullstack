package com.raxrot.back.services;

import com.raxrot.back.dtos.UserRequestDTO;
import com.raxrot.back.dtos.UserResponseDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    boolean activateUser(String activationToken);
}
