package com.da.doctor_appointment.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.ReviewDTO;
import com.da.doctor_appointment.service.CustomUserDetails;
import com.da.doctor_appointment.service.ReviewService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping()
    public ResponseEntity<?> getReviews() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        List<ReviewDTO> reviewDTOs = reviewService.getReviews(userDetails.getId());
        return new ResponseEntity<>(reviewDTOs, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping()
    public ResponseEntity<?> addReview(@RequestBody @Valid ReviewDTO request) {
        request.setPostDate(LocalDateTime.now());
        ReviewDTO reviewDTO = reviewService.addReview(request);

        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }
    
}
