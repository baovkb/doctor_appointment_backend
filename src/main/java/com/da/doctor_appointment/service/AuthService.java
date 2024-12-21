package com.da.doctor_appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.components.JwtUtil;
import com.da.doctor_appointment.dto.request.LoginRequest;
import com.da.doctor_appointment.dto.request.SignupRequest;
import com.da.doctor_appointment.model.Patient;

@Service
public class AuthService {    
    @Autowired 
    private PatientService patientService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public Patient createPatient(SignupRequest signupRequest) {
        return patientService.createPatient(signupRequest);
    }

    public String loginUser(LoginRequest loginRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            return jwtUtil.generateToken((CustomUserDetails)auth.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Username or password is incorrect");
        }  
        
    }
}
