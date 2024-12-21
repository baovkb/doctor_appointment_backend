package com.da.doctor_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;

import com.da.doctor_appointment.utils.constants.RoleEnum;

public class UpdateRoleRequest {
    @NotBlank(message = "Role is required")
    private RoleEnum role;
}
