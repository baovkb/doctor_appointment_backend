package com.da.doctor_appointment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.request.SignupRequest;
import com.da.doctor_appointment.exception.AppRoleNotFoundException;
import com.da.doctor_appointment.exception.UserAlreadyExistsException;
import com.da.doctor_appointment.model.Role;
import com.da.doctor_appointment.model.User;
import com.da.doctor_appointment.repository.RoleRepository;
import com.da.doctor_appointment.repository.UserRepository;
import com.da.doctor_appointment.utils.constants.RoleEnum;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(SignupRequest signupRequest, RoleEnum roleEnum) {
        Optional<User> userOptional = userRepository.findByUsername(signupRequest.getUsername());
        if (userOptional.isPresent()) 
            throw new UserAlreadyExistsException("User already exists");
            
        Role role = roleRepository.findByName(roleEnum.name())
            .orElseThrow(() -> new AppRoleNotFoundException("Role USER not found"));

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setName(signupRequest.getName());
        user.setCreateAt(LocalDateTime.now());
        user.setRole(role);

        return userRepository.save(user);
    }
}
