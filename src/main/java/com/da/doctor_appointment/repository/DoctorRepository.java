package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Doctor;
import java.util.List;


public interface DoctorRepository extends JpaRepository<Doctor, String>{
    public List<Doctor> findBySpecialtyId(Integer specialtyId);
}
