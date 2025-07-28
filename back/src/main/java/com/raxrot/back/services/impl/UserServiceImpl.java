package com.raxrot.back.services.impl;

import com.raxrot.back.dtos.UserRequestDTO;
import com.raxrot.back.dtos.UserResponseDTO;
import com.raxrot.back.entities.User;
import com.raxrot.back.exceptions.ApiException;
import com.raxrot.back.repositories.UserRepository;
import com.raxrot.back.services.EmailService;
import com.raxrot.back.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ApiException("Email address already in use");
        }
        User user = modelMapper.map(userRequestDTO, User.class);
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);

        //send activation email
        String activationLink="http://localhost:8080/api/v1.0/activate?token="+token;
        String subject = "Activate Money Manager Account";
        String body="Click here to activate your account. " +activationLink;
        emailService.sendEmail(user.getEmail(), subject, body);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public boolean activateUser(String activationToken) {
        return userRepository.findByActivationToken(activationToken).map(user->{
            user.setIsActive(true);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean isUserActivated(String email) {
       User user = userRepository.findByEmail(email)
               .orElseThrow(()->new ApiException("User not found with"));
       return user.getIsActive();
    }

    @Override
    public UserResponseDTO getUserCurrentUser(String email) {
        User user=null;
        if (email==null) {
            Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();
            user = userRepository.findByEmail(currentEmail)
                    .orElseThrow(()->new ApiException("User not found with"));
        }else{
            user = userRepository.findByEmail(email)
                    .orElseThrow(()->new ApiException("User not found with"));
        }
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
