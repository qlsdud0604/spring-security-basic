package com.example.springsecuritybasic.repository;

import com.example.springsecuritybasic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
