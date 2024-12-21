package com.da.doctor_appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO extends UserDTO {
    private String phone;

    public PatientDTO(UserDTO userDTO, String phone) {
        super.setAddress(userDTO.getAddress());
        super.setCreateAt(userDTO.getCreateAt());
        super.setDateOfBirth(userDTO.getDateOfBirth());
        super.setEmail(userDTO.getEmail());
        super.setGender(userDTO.getGender());
        super.setId(userDTO.getId());
        super.setName(userDTO.getName());
        super.setPhotoUrl(userDTO.getPhotoUrl());
        super.setUsername(userDTO.getUsername());
        this.phone = phone;
    }
}
