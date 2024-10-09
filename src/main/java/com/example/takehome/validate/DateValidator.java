package com.example.takehome.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public void initialize(ValidDate validDate) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDate.parse(value); // Or use a specific format with DateTimeFormatter
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
