package com.da.doctor_appointment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.request.ReceptionistSignupRequest;
import com.da.doctor_appointment.dto.request.SignupRequest;
import com.da.doctor_appointment.service.ReceptionistService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/receptionists")
public class ReceptionistController {
    @Autowired
    private ReceptionistService receptionistService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createReceptionist(@Valid @RequestBody ReceptionistSignupRequest request) {
        receptionistService.createReceptionist(request);
        return new ResponseEntity<>("Signup successful", HttpStatus.OK);
    }
    
}
