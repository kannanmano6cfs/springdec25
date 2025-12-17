package com.example.EmployeeService.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmployeeNameValidator implements ConstraintValidator<ValidEmpName, String> {

    @Override
    public boolean isValid(String empname, ConstraintValidatorContext context) {
        return empname.length()>0 && Character.isUpperCase(empname.charAt(0));
    }
}
