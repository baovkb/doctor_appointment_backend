package com.da.doctor_appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.da.doctor_appointment.model.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    Optional<Role> findById(Integer id);
    Optional<Role> findByName(String name);
}
