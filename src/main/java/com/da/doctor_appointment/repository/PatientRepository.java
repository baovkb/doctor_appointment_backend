package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, String>{
    
}
