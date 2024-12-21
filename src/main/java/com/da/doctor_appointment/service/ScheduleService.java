package com.da.doctor_appointment.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.logging.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.ScheduleDTO;
import com.da.doctor_appointment.exception.ConflictException;
import com.da.doctor_appointment.model.Doctor;
import com.da.doctor_appointment.model.Schedule;
import com.da.doctor_appointment.model.TimeSlot;
import com.da.doctor_appointment.repository.DoctorRepository;
import com.da.doctor_appointment.repository.ScheduleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private ModelMapper mapper;

    private static final Logger logger = Logger.getLogger(ScheduleService.class.getName());

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();

        List<ScheduleDTO> scheduleDTOs = schedules.stream()
            .map((schedule) -> {
                ScheduleDTO dto = mapper.map(schedule, ScheduleDTO.class);
                dto.setDoctorId(schedule.getDoctor().getId());
                return dto;
            })
            .collect(Collectors.toList());
        return scheduleDTOs;
    }

    public ScheduleDTO createSchedule(ScheduleDTO request) {
        CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Doctor doctor = doctorRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Doctor does not exist"));

        Schedule schedule = new Schedule();
        schedule.setCapacity(request.getCapacity());
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setDoctor(doctor);
        schedule.setDurationPerSlot(request.getDurationPerSlot());
        schedule.setEndTime(request.getEndTime());
        schedule.setStartTime(request.getStartTime());

        if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeek(
            schedule.getDoctor().getId(), 
            schedule.getDayOfWeek()
        );

        schedules.forEach((sche) -> {
            if (schedule.getStartTime().isBefore(sche.getEndTime()) && schedule.getEndTime().isAfter(sche.getStartTime())) {
                throw new IllegalArgumentException("The schedule conflicts with an existing schedule");
            }
        });

        scheduleRepository.save(schedule);

        List<Map<String, Object>> slots = timeSlotService.generateTimeSlot(
            schedule.getStartTime(), 
            schedule.getEndTime(), 
            schedule.getDurationPerSlot().longValue(),
            schedule.getCapacity());

        for (int i = 0; i < slots.size(); ++i) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setIsActive(true);
            timeSlot.setStartTime((LocalTime) slots.get(i).get("startTime"));
            timeSlot.setEndTime((LocalTime) slots.get(i).get("endTime"));
            timeSlot.setSchedule(schedule);
            timeSlot.setCapacity((Integer) slots.get(i).get("capacity"));
            timeSlotService.creatTimeSlot(timeSlot);
        }
        return mapper.map(schedule, ScheduleDTO.class);
    }

    public ScheduleDTO updateSchedule(Integer id, ScheduleDTO request) {
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Schedule does not exist"));
        schedule.setCapacity(request.getCapacity());
        schedule.setDayOfWeek(request.getDayOfWeek());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        
        if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

       List<Schedule> schedules = scheduleRepository.findByDoctorIdAndDayOfWeek(
            schedule.getDoctor().getId(), 
            schedule.getDayOfWeek()
        );

        schedules.forEach((sche) -> {
            if (sche.getId() == id) return;

            if (schedule.getStartTime().compareTo(sche.getEndTime()) < 0 || schedule.getEndTime().compareTo(sche.getStartTime()) > 0) {
                throw new ConflictException("The schedule conflicts with an existing schedule");
            }
        });

        scheduleRepository.save(schedule);
        
        //deactive old time slots
        timeSlotService.deactiveTimeSlotsByScheduleId(schedule.getId());
        //generate new time slots
        List<Map<String, Object>> slots = timeSlotService.generateTimeSlot(
            schedule.getStartTime(), 
            schedule.getEndTime(), 
            schedule.getDurationPerSlot().longValue(),
            schedule.getCapacity());

        for (int i = 0; i < slots.size(); ++i) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setIsActive(true);
            timeSlot.setStartTime((LocalTime) slots.get(i).get("startTime"));
            timeSlot.setEndTime((LocalTime) slots.get(i).get("endTime"));
            timeSlot.setSchedule(schedule);
            timeSlot.setCapacity((Integer) slots.get(i).get("capacity"));
            timeSlotService.creatTimeSlot(timeSlot);
        }
        return mapper.map(schedule, ScheduleDTO.class);
    }

    public void deleteSchedule(Integer id) {
        CustomUserDetails userDetails = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        doctorRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Doctor does not exist"));

        scheduleRepository.deleteById(id);
    }

    public List<ScheduleDTO> getSchedulesBySpecialtyId(Integer specialtyId) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyId(specialtyId);

        List<Schedule> schedules = new ArrayList<>();
        doctors.forEach(doctor -> {
            List<Schedule> docSchedules = scheduleRepository.findByDoctorId(doctor.getId());
            schedules.addAll(docSchedules);
        });

        return schedules.stream()
            .map(schedule -> mapper.map(schedule, ScheduleDTO.class))
            .collect(Collectors.toList());
    }
}
