package com.userservice.userservice.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.userservice.userservice.Entities.User;

public interface UserRepository extends JpaRepository<User,String> {
    
}
