package com.da.doctor_appointment.exception;

public class InvalidDayException extends RuntimeException{
    public InvalidDayException(String message) {
        super(message);
    }
}
