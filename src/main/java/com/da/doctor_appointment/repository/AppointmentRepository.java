package com.da.doctor_appointment.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{
    public List<Appointment> findByTimeSlotIdAndAppointmentDateAndStatus(Integer timeSlotId, LocalDate date, String status);
    public List<Appointment> findByPatientId(String patientId);
}
