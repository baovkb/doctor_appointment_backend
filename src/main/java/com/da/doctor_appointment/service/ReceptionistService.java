package com.da.doctor_appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.request.ReceptionistSignupRequest;
import com.da.doctor_appointment.model.Hospital;
import com.da.doctor_appointment.model.Receptionist;
import com.da.doctor_appointment.model.User;
import com.da.doctor_appointment.repository.HospitalRepository;
import com.da.doctor_appointment.repository.ReceptionistRepository;
import com.da.doctor_appointment.utils.constants.RoleEnum;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReceptionistService {
    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private UserService userService;

    public Receptionist createReceptionist(ReceptionistSignupRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
            .orElseThrow(() -> new EntityNotFoundException("Hospital does not exist"));

        User user = userService.createUser(request, RoleEnum.RECEPTIONIST);
        Receptionist receptionist = new Receptionist();
        receptionist.setUser(user);
        receptionist.setHospital(hospital);

        return receptionistRepository.save(receptionist);
    }
}
