package com.da.doctor_appointment.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.request.LoginRequest;
import com.da.doctor_appointment.dto.request.SignupRequest;
import com.da.doctor_appointment.dto.response.ApiResponse;
import com.da.doctor_appointment.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest signupRequest) {
        authService.createPatient(signupRequest);
        return new ResponseEntity<>("Signup successful", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authService.loginUser(loginRequest);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
    
}
