package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer>{
    
}
