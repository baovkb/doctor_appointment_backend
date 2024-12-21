package com.da.doctor_appointment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.PatientDTO;
import com.da.doctor_appointment.service.PatientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/profile")
    public ResponseEntity<?> getPatient() {
        PatientDTO patientDTO = patientService.getPatient();
        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/profile")
    public ResponseEntity<?> updatePatient(@RequestBody PatientDTO request) {
        PatientDTO patientDTO = patientService.updatePatient(request);
        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/patients")
    public ResponseEntity<?> getPatients() {
        List<PatientDTO> patientsDTOs = patientService.getPatients();
        return new ResponseEntity<>(patientsDTOs, HttpStatus.OK);
    }
}
