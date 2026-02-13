package com.securetask.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.securetask.Entitity.User;


public interface UserRepository extends JpaRepository<User, Long> 
{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsById(@NonNull Long id);
    boolean existsByUsername(String username);
}
