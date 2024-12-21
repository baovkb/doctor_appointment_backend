package com.da.doctor_appointment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findByUsername(String username);
}
