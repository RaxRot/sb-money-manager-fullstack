package com.raxrot.back.controllers;

import com.raxrot.back.dtos.LoginRequestDTO;
import com.raxrot.back.dtos.LoginResponseDTO;
import com.raxrot.back.dtos.UserRequestDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.security.jwt.JwtUtils;
import com.raxrot.back.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
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


    @PostMapping("/login")
    public ResponseEntity<?>login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        if (!userService.isUserActivated(loginRequestDTO.getEmail())) {
           return new ResponseEntity<>("Account not activated", HttpStatus.BAD_REQUEST);
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()));

        }catch (Exception e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken=jwtUtils.generateTokenFromUsername(userDetails);
        LoginResponseDTO loginResponseDTO=new LoginResponseDTO(jwtToken);
        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO>getCurrentUser() {
        UserResponseDTO userResponseDTO=userService.getUserCurrentUser(null);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
    }
}
