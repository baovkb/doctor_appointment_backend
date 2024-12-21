package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Integer>{
    
}
