package com.da.doctor_appointment.controller;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.AppointmentDTO;
import com.da.doctor_appointment.service.AppointmentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping()
    public ResponseEntity<?> createAppointment(@RequestBody @Valid AppointmentDTO request) {
        request.setRegistrationTime(LocalDateTime.now());
        request.setUpdateTime(request.getRegistrationTime());
        AppointmentDTO appointmentDTO = appointmentService.createAppointment(request);
        
        return new ResponseEntity<>(appointmentDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Integer id, @RequestBody @Valid AppointmentDTO request) {
        request.setUpdateTime(LocalDateTime.now());
        AppointmentDTO appointmentDTO = appointmentService.updateAppointment(id, request);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Integer id) {
        appointmentService.cancelAppointment(id);

        return new ResponseEntity<>("Cancel appointment successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Integer id) {
        appointmentService.confirmAppointment(id);
        return new ResponseEntity<>("Confirm appointment successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(@PathVariable Integer id) {
        appointmentService.completeAppointment(id);
        return new ResponseEntity<>("Complete appointment successfully", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping()
    public ResponseEntity<?> getMethodName(@RequestParam(defaultValue = "all") String status, @RequestParam(defaultValue = "desc") String sort) {
        if (!status.equals("all") && !status.equals("past") && !status.equals("upcoming")) {
            status = "all";
        }
        if (!sort.equals("desc") && !sort.equals("asc")){
            sort = "desc";
        }
        return new ResponseEntity<>(appointmentService.getAppointments(status, sort), HttpStatus.OK);
    }
    
}
