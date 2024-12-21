package com.da.doctor_appointment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.PatientDTO;
import com.da.doctor_appointment.dto.UserDTO;
import com.da.doctor_appointment.dto.request.SignupRequest;
import com.da.doctor_appointment.model.Patient;
import com.da.doctor_appointment.model.User;
import com.da.doctor_appointment.repository.PatientRepository;
import com.da.doctor_appointment.utils.constants.RoleEnum;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    public Patient createPatient(SignupRequest request) {
        User user = userService.createUser(request, RoleEnum.PATIENT);
        Patient patient = new Patient();
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    public PatientDTO getPatient() {
        CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient does not exist"));
        PatientDTO patientDTO = new PatientDTO(mapper.map(patient.getUser(), UserDTO.class), patient.getPhone());

        return patientDTO;
    } 

    public PatientDTO updatePatient(PatientDTO request) {
        CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Patient does not exist"));
        
        patient.getUser().setAddress(request.getAddress());
        patient.getUser().setDateOfBirth(request.getDateOfBirth());
        patient.getUser().setEmail(request.getEmail());
        patient.getUser().setGender(request.getGender());
        patient.getUser().setName(request.getName());

        patient = patientRepository.save(patient);
        PatientDTO patientDTO = new PatientDTO(mapper.map(patient.getUser(), UserDTO.class), patient.getPhone());

        return patientDTO;
    }

    public List<PatientDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
            .map(patient -> mapper.map(patient, PatientDTO.class))
            .collect(Collectors.toList());
    }
}
