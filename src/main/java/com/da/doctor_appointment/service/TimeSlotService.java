package com.da.doctor_appointment.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.TimeSlotDTO;
import com.da.doctor_appointment.model.TimeSlot;
import com.da.doctor_appointment.repository.ScheduleRepository;
import com.da.doctor_appointment.repository.TimeSlotRepository;

@Service
public class TimeSlotService {
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ModelMapper mapper;

    private static final Logger logger = Logger.getLogger(TimeSlotService.class.getName());

    List<Map<String, Object>> generateTimeSlot(LocalTime startTime, LocalTime endTime, long slotMinutes, Integer capacity) {
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        long numSlot = durationMinutes / slotMinutes;
        Integer capacityPerSlot = capacity / Long.valueOf(numSlot).intValue();

        List<Map<String, Object>> slotList = new ArrayList<>();
        LocalTime currentSlot = startTime;

        for (int i = 0; i < numSlot; ++i) {
            Map<String, Object> slotMap = new HashMap<>();
            slotMap.put("startTime", currentSlot);
            currentSlot = currentSlot.plus(Duration.ofMinutes(slotMinutes));
            slotMap.put("endTime", currentSlot);
            slotMap.put("capacity", capacityPerSlot);

            slotList.add(slotMap);
        }

        return slotList;
    }

    public TimeSlot creatTimeSlot(TimeSlot slot) {
        scheduleRepository.findById(slot.getSchedule().getId())
            .orElseThrow(() -> new IllegalArgumentException("The schedule does not exist"));
        timeSlotRepository.save(slot);
        return slot;
    }

    public List<TimeSlot> getTimeSlotsByScheduleId(Integer scheduleId) {
        return timeSlotRepository.findByScheduleId(scheduleId);
    }

    public List<TimeSlotDTO> getAllAvailableTimeSlots(Integer scheduleId) {
        List<TimeSlot> timeSlots = timeSlotRepository.findByScheduleIdAndIsActive(scheduleId, true);
        List<TimeSlotDTO> timeSlotDTOs = timeSlots.stream()
            .map(timeSlot -> mapper.map(timeSlot, TimeSlotDTO.class))
            .collect(Collectors.toList());

        return timeSlotDTOs;
    }

    public List<TimeSlot> deactiveTimeSlotsByScheduleId(Integer scheduleId) {
        List<TimeSlot> timeslots = timeSlotRepository.findByScheduleId(scheduleId);
        timeslots.forEach(timeslot -> {
            timeslot.setIsActive(false);
            timeSlotRepository.save(timeslot);
        });

        return timeslots;
    }
}
