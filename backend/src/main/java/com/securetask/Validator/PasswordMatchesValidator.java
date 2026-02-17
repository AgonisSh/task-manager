package com.securetask.Validator;

import com.securetask.Annotation.PasswordMatches;
import com.securetask.DTO.requests.RegisterRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getPasswordConfirmation() == null) {
            return false;
        }
        return request.getPassword().equals(request.getPasswordConfirmation());
    }
    
}
