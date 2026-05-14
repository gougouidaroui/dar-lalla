package com.darlalla.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.darlalla.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}