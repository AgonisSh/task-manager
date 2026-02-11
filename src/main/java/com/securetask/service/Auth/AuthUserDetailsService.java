package com.securetask.service.Auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.securetask.entity.auth.AuthUser;
import com.securetask.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService{
    
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(AuthUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

}
