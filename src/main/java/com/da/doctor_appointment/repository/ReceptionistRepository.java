package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Receptionist;

public interface ReceptionistRepository extends JpaRepository<Receptionist, String>{
    
}
