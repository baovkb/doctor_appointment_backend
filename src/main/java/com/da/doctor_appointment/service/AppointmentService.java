package com.da.doctor_appointment.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.AppointmentDTO;
import com.da.doctor_appointment.exception.AccessDeniedException;
import com.da.doctor_appointment.exception.ConflictException;
import com.da.doctor_appointment.exception.InvalidDayException;
import com.da.doctor_appointment.model.Appointment;
import com.da.doctor_appointment.model.Patient;
import com.da.doctor_appointment.model.Receptionist;
import com.da.doctor_appointment.model.TimeSlot;
import com.da.doctor_appointment.repository.AppointmentRepository;
import com.da.doctor_appointment.repository.PatientRepository;
import com.da.doctor_appointment.repository.ReceptionistRepository;
import com.da.doctor_appointment.repository.TimeSlotRepository;
import com.da.doctor_appointment.utils.constants.AppointmentStatusE;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Autowired
    private ModelMapper mapper;

    private static Logger logger = Logger.getLogger(AppointmentService.class.getName());

    public AppointmentDTO createAppointment(AppointmentDTO request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("This user does not exist"));

        TimeSlot timeSlot = timeSlotRepository.findByIdAndIsActive(request.getTimeSlotId(), true)
            .orElseThrow(() -> new EntityNotFoundException("Time slot does not exist"));

        checkRequestDate(request, timeSlot);

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setRegistrationTime(request.getRegistrationTime());
        appointment.setUpdateTime(request.getRegistrationTime());
        appointment.setStatus(AppointmentStatusE.PENDING.name());

        appointmentRepository.save(appointment);

        return mapper.map(appointment, AppointmentDTO.class);
    }

    public AppointmentDTO updateAppointment(Integer id, AppointmentDTO request) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Appointment appm = appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));
        
        if (!appm.getPatient().getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("This appointment does not belong to you");
        }

        TimeSlot newTimeSlot = timeSlotRepository.findByIdAndIsActive(request.getTimeSlotId(), true)
            .orElseThrow(() -> new EntityNotFoundException("Time slot does not exist"));

        LocalDateTime appDateTime = LocalDateTime.of(
            appm.getAppointmentDate().getYear(), 
            appm.getAppointmentDate().getMonth(), 
            appm.getAppointmentDate().getDayOfMonth(), 
            appm.getTimeSlot().getStartTime().getHour(), 
            appm.getTimeSlot().getStartTime().getMinute(), 
            appm.getTimeSlot().getStartTime().getSecond());
        
        long dis = Duration.between(LocalDateTime.now(), appDateTime).getSeconds();
        if (dis < 3600*24)
            throw new ConflictException("This appointment cannot reschedule");
        
        checkRequestDate(request, newTimeSlot);
        
        appm.setAppointmentDate(request.getAppointmentDate());
        appm.setTimeSlot(newTimeSlot);
        appm.setUpdateTime(request.getUpdateTime());

        appointmentRepository.save(appm);

        return mapper.map(appm, AppointmentDTO.class);
    }

    public void cancelAppointment(Integer id) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Appointment appm = appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));
        
        if (!appm.getPatient().getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("This appointment does not belong to you");
        }

        LocalDateTime appDateTime = LocalDateTime.of(
            appm.getAppointmentDate().getYear(), 
            appm.getAppointmentDate().getMonth(), 
            appm.getAppointmentDate().getDayOfMonth(), 
            appm.getTimeSlot().getStartTime().getHour(), 
            appm.getTimeSlot().getStartTime().getMinute(), 
            appm.getTimeSlot().getStartTime().getSecond());

        long dis = Duration.between(LocalDateTime.now(), appDateTime).toHours();
        if (dis < 24) {
            throw new InvalidDayException("Cannot cancel this appointment");
        }

        appm.setStatus(AppointmentStatusE.CANCELED.name());
        appointmentRepository.save(appm);
    }

    private void checkRequestDate(AppointmentDTO request, TimeSlot timeSlot) {
        //check date
        LocalDateTime appDateTime = LocalDateTime.of(
            request.getAppointmentDate().getYear(), 
            request.getAppointmentDate().getMonth(), 
            request.getAppointmentDate().getDayOfMonth(), 
            timeSlot.getStartTime().getHour(), 
            timeSlot.getStartTime().getMinute(), 
            timeSlot.getStartTime().getSecond());

        long dis = Duration.between(request.getUpdateTime(), appDateTime).toHours();
        if (dis < 24)
            throw new InvalidDayException("You must register before at least 1 day");

        if (dis > 30*24)
            throw new InvalidDayException("You must register before at most 30 days");

        if (request.getAppointmentDate().getDayOfWeek().getValue() != timeSlot.getSchedule().getDayOfWeek())
            throw new InvalidDayException("This day is invalid for this schedule");
        
        List<Appointment> appointments = appointmentRepository.findByTimeSlotIdAndAppointmentDateAndStatus(
            request.getTimeSlotId(), 
            request.getAppointmentDate(),
            AppointmentStatusE.PENDING.name());
        
        if (appointments.size() >= timeSlot.getCapacity()) 
            throw new ConflictException("This slot is currently full");
    }

    public void confirmAppointment(Integer appointmentId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Receptionist receptionist = receptionistRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Receptionist does not exist"));

        Integer recepHospitalId = receptionist.getHospital().getId();

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));

        Integer docHospitalId = appointment.getTimeSlot().getSchedule().getDoctor().getHospital().getId();

        if (recepHospitalId != docHospitalId) 
            throw new ConflictException("Receptionist does work in this place");

        LocalDateTime appmDateTime = LocalDateTime.of(
            appointment.getAppointmentDate().getYear(), 
            appointment.getAppointmentDate().getMonth(), 
            appointment.getAppointmentDate().getDayOfMonth(), 
            appointment.getTimeSlot().getStartTime().getHour(), 
            appointment.getTimeSlot().getStartTime().getMinute(), 
            appointment.getTimeSlot().getStartTime().getSecond());
        
        if (Duration.between(LocalDateTime.now(), appmDateTime).toMinutes() > 30)
            throw new ConflictException("You can only confirm this appointment before at most 30 minutes");
        
        if (LocalDateTime.now().isAfter(appmDateTime.plusMinutes(appointment.getTimeSlot().getSchedule().getDurationPerSlot())))
            throw new ConflictException("This appointment is expired");

        appointment.setStatus(AppointmentStatusE.CONFIRMED.name());

        appointmentRepository.save(appointment);
    }

    public void completeAppointment(Integer appointmentId) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Receptionist receptionist = receptionistRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("Receptionist does not exist"));

        Integer recepHospitalId = receptionist.getHospital().getId();

        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));

        Integer docHospitalId = appointment.getTimeSlot().getSchedule().getDoctor().getHospital().getId();

        if (recepHospitalId != docHospitalId) 
            throw new ConflictException("Receptionist does work in this place");

        LocalDateTime appmDateTimeStart = LocalDateTime.of(
            appointment.getAppointmentDate().getYear(), 
            appointment.getAppointmentDate().getMonth(), 
            appointment.getAppointmentDate().getDayOfMonth(), 
            appointment.getTimeSlot().getStartTime().getHour(), 
            appointment.getTimeSlot().getStartTime().getMinute(), 
            appointment.getTimeSlot().getStartTime().getSecond());
        
        LocalDateTime appmDateTimeEnd = LocalDateTime.of(
            appointment.getAppointmentDate().getYear(), 
            appointment.getAppointmentDate().getMonth(), 
            appointment.getAppointmentDate().getDayOfMonth(), 
            appointment.getTimeSlot().getEndTime().getHour(), 
            appointment.getTimeSlot().getEndTime().getMinute(), 
            appointment.getTimeSlot().getEndTime().getSecond());
        
        if (LocalDateTime.now().isBefore(appmDateTimeStart))
            throw new ConflictException("This appointment has not taken place yet");
        
        if (LocalDateTime.now().isAfter(appmDateTimeEnd))
            throw new ConflictException("This appointment is expired");

        appointment.setStatus(AppointmentStatusE.COMPLETED.name());

        appointmentRepository.save(appointment);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredAppointmentCronjob() {
        List<Appointment> appointments = appointmentRepository.findAll();
        appointments.forEach((appointment) -> {
            if (!appointment.getStatus().equals(AppointmentStatusE.PENDING.name()))
                return;
            
            LocalDateTime appmDateTimeEnd = LocalDateTime.of(
                appointment.getAppointmentDate().getYear(), 
                appointment.getAppointmentDate().getMonth(), 
                appointment.getAppointmentDate().getDayOfMonth(), 
                appointment.getTimeSlot().getEndTime().getHour(), 
                appointment.getTimeSlot().getEndTime().getMinute(), 
                appointment.getTimeSlot().getEndTime().getSecond());

            if (LocalDateTime.now().isBefore(appmDateTimeEnd))
                return;

            appointment.setStatus(AppointmentStatusE.EXPIRED.name());
            appointmentRepository.save(appointment);
        });
    }

    private LocalDateTime getAppointmentStartTime(Appointment appointment) {
        return LocalDateTime.of(
            appointment.getAppointmentDate().getYear(),
            appointment.getAppointmentDate().getMonth(),
            appointment.getAppointmentDate().getDayOfMonth(),
            appointment.getTimeSlot().getStartTime().getHour(),
            appointment.getTimeSlot().getStartTime().getMinute(),
            appointment.getTimeSlot().getStartTime().getSecond()
        );
    }

    private LocalDateTime getAppointmentEndTime(Appointment appointment) {
        return LocalDateTime.of(
            appointment.getAppointmentDate().getYear(), 
            appointment.getAppointmentDate().getMonth(), 
            appointment.getAppointmentDate().getDayOfMonth(), 
            appointment.getTimeSlot().getEndTime().getHour(), 
            appointment.getTimeSlot().getEndTime().getMinute(), 
            appointment.getTimeSlot().getEndTime().getSecond());
    }

    public List<AppointmentDTO> getAppointments(String status, String sort) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Patient patient = patientRepository.findById(userDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("This user does not exist"));
        
        switch (status) {
            case "upcoming":
                return patient.getAppointments().stream()
                    .map(appointment -> {
                        LocalDateTime appmDateTimeStart = getAppointmentStartTime(appointment);
                        if (LocalDateTime.now().isBefore(appmDateTimeStart) && appointment.getStatus().equals(AppointmentStatusE.PENDING.name())) {
                            return appointment;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((app1, app2) -> {
                        LocalDateTime app1StartTime = getAppointmentStartTime(app1);
                        LocalDateTime app2StartTime = getAppointmentStartTime(app2);

                        return sort.equals("desc") ? app2StartTime.compareTo(app1StartTime) : app1StartTime.compareTo(app2StartTime);
                    })
                    .map(appointment -> mapper.map(appointment, AppointmentDTO.class))
                    .collect(Collectors.toList());
            case "past":
                return patient.getAppointments().stream()
                    .map(appointment -> {
                        LocalDateTime appmDateTimeEnd = getAppointmentEndTime(appointment);
                        if (LocalDateTime.now().isAfter(appmDateTimeEnd))
                            return appointment;
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((app1, app2) -> {
                        LocalDateTime app1StartTime = getAppointmentStartTime(app1);
                        LocalDateTime app2StartTime = getAppointmentStartTime(app2);

                        return sort.equals("desc") ? app2StartTime.compareTo(app1StartTime) : app1StartTime.compareTo(app2StartTime);
                    })
                    .map(appointment -> mapper.map(appointment, AppointmentDTO.class))
                    .collect(Collectors.toList());
            case "all":
            default:
                return patient.getAppointments().stream()
                    .sorted((app1, app2) -> {
                        LocalDateTime app1StartTime = getAppointmentStartTime(app1);
                        LocalDateTime app2StartTime = getAppointmentStartTime(app2);

                        return sort.equals("desc") ? app2StartTime.compareTo(app1StartTime) : app1StartTime.compareTo(app2StartTime);
                    })
                    .map(appointment -> mapper.map(appointment, AppointmentDTO.class))
                    .collect(Collectors.toList());
        }
    }
}
