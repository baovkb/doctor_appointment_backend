package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
    
}
