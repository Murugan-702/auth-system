package com.app.backend.service.user_service;

import com.app.backend.dto.ApiResponse;
import com.app.backend.dto.LoginRequest;
import com.app.backend.dto.RegisterRequest;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
       public ApiResponse registerUser(RegisterRequest req);
       public ApiResponse loginUser(LoginRequest req , HttpServletResponse response);
       public ApiResponse logOut(HttpServletResponse response);
} 