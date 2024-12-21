package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Schedule;
import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{
    public List<Schedule> findByDoctorIdAndDayOfWeek(String doctorId, Integer dateOfWeek);
    public List<Schedule> findByDoctorId(String doctorId);
}
