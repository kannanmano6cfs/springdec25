package com.example.EmployeeService.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployeeNameValidator.class)
public @interface ValidEmpName {
    String message() default "Invalid Employee Name!! Must start with an upper case letter!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
