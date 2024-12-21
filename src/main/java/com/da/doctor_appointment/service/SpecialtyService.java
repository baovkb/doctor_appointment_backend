package com.da.doctor_appointment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.doctor_appointment.dto.SpecialtyDTO;
import com.da.doctor_appointment.model.Specialty;
import com.da.doctor_appointment.repository.SpecialtyRepository;

@Service
public class SpecialtyService {
    @Autowired
    private SpecialtyRepository specialtyRepository;
    
    @Autowired
    private ModelMapper mapper;

    public List<SpecialtyDTO> getAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        List<SpecialtyDTO> specialtyDTOs = specialties.stream()
            .map(specialty -> mapper.map(specialty, SpecialtyDTO.class))
            .collect(Collectors.toList());
            
        return specialtyDTOs;
    }
}
