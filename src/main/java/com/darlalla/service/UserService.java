package com.darlalla.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darlalla.dto.UserRegistrationDTO;
import com.darlalla.entity.User;
import com.darlalla.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDTO dto) {
        logger.info("Registering new user with email: {}", dto.getEmail());

        User user = new User();
        user.setNom(dto.getNom());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with id: {}", savedUser.getId());

        return savedUser;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}