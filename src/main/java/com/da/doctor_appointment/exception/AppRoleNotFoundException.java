package com.da.doctor_appointment.exception;

public class AppRoleNotFoundException extends RuntimeException {
    public AppRoleNotFoundException(String message) {
        super(message);
    }
}
