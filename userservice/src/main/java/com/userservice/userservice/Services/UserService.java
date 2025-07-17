package com.userservice.userservice.Services;

import java.util.List;


import com.userservice.userservice.Entities.User;

public interface UserService {
    
    // Save User
    User saveUser(User user);

    // Get All Users
    List<User> getAllUsers();

    // Get Single User
    User getUser(String userId);
}
