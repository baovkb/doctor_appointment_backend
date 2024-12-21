package com.da.doctor_appointment.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.request.DoctorSignupRequest;
import com.da.doctor_appointment.model.Doctor;
import com.da.doctor_appointment.model.Hospital;
import com.da.doctor_appointment.model.Specialty;
import com.da.doctor_appointment.model.User;
import com.da.doctor_appointment.repository.DoctorRepository;
import com.da.doctor_appointment.repository.HospitalRepository;
import com.da.doctor_appointment.repository.SpecialtyRepository;
import com.da.doctor_appointment.utils.constants.RoleEnum;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    public Doctor createDoctor(DoctorSignupRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
            .orElseThrow(() -> new EntityNotFoundException("Hospital does not exist"));

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
            .orElseThrow(() -> new EntityNotFoundException("Specialty does not exist"));
        
        User user = userService.createUser(request, RoleEnum.DOCTOR);
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDescription(request.getDescription());
        doctor.setHospital(hospital);
        doctor.setSpecialty(specialty);

        return doctorRepository.save(doctor);
    }
}
