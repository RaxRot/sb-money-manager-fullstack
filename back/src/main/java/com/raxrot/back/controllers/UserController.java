package com.raxrot.back.controllers;

import com.raxrot.back.dtos.UserRequestDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public ResponseEntity<String>activateUser(@RequestParam("token")String token) {
        boolean activated = userService.activateUser(token);
        if (activated) {
            return new ResponseEntity<>("Activated", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Not activated", HttpStatus.BAD_REQUEST);
        }
    }
}
