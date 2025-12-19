package com.app.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.backend.model.User;

public interface UserRepo extends JpaRepository<User,Long> {
    public User findByEmail(String email);
}
