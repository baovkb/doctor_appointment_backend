package com.da.doctor_appointment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer>{
    public List<TimeSlot> findByScheduleId(Integer scheduleId);
    public void deleteByScheduleId(Integer scheduleId);
    public List<TimeSlot> findByScheduleIdAndIsActive(Integer scheduleId, Boolean isActive);
    public Optional<TimeSlot> findByIdAndIsActive(Integer id, Boolean isActive);
}
