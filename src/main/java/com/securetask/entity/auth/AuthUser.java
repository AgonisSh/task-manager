package com.securetask.entity.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.securetask.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthUser implements UserDetails{


        private final User user;
    
    
        @Override
        public String getUsername() {
            return user.getUsername();
        }
    
        @Override
        public String getPassword() {
            return user.getPassword();
        }
    
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
    
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
    
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
    
        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
        }
    
}
