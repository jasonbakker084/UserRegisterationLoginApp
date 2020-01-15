package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserRegistration;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistration registration);
}