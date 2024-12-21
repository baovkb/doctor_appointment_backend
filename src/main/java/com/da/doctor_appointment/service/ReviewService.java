package com.da.doctor_appointment.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.ReviewDTO;
import com.da.doctor_appointment.exception.ConflictException;
import com.da.doctor_appointment.model.Appointment;
import com.da.doctor_appointment.model.Review;
import com.da.doctor_appointment.repository.AppointmentRepository;
import com.da.doctor_appointment.repository.ReviewRepository;
import com.da.doctor_appointment.utils.constants.AppointmentStatusE;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ModelMapper mapper;

    public List<ReviewDTO> getReviews(String patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            return appointments.stream()
                .map(appointment -> {
                    Review review = appointment.getReview();
                    return review != null ? mapper.map(review, ReviewDTO.class) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(ReviewDTO request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
            .orElseThrow(() -> new EntityNotFoundException("Appointment does not exist"));

        if (appointment.getReview() != null)
            throw new ConflictException("This appointment has already had review");
        if (!appointment.getStatus().equals(AppointmentStatusE.COMPLETED.name()))
            throw new ConflictException("This appointment has not completed yet");
        
        Review review = new Review();
        review.setAppointment(appointment);
        review.setContent(request.getContent());
        review.setDoctor(appointment.getTimeSlot().getSchedule().getDoctor());
        review.setPostDate(request.getPostDate());
        review.setStar(request.getStar());

        return mapper.map(reviewRepository.save(review), ReviewDTO.class);
    }
}
