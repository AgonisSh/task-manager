package com.securetask.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.securetask.DAO.UserDAO;
import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.Exception.DuplicateResourceException;

@Component
public class UserValidator {

    @Autowired
    private UserDAO userDAO;

    public void validateUniqueFields(RegisterRequest request) 
    {
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        }
        else if (userDAO.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already in use: " + request.getUsername());
        }
    }

    
}
