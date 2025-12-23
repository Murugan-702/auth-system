package com.app.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.dto.ApiResponse;
import com.app.backend.dto.LoginRequest;
import com.app.backend.dto.RegisterRequest;
import com.app.backend.service.user_service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody RegisterRequest req){
        try {
            ApiResponse response = userService.registerUser(req);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false , "Sonething went wrong!");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequest req , HttpServletResponse response){
        try {
            ApiResponse res = userService.loginUser(req, response);
            return new ResponseEntity<>(res,HttpStatus.OK);
        } catch (Exception e) {
           ApiResponse res = new ApiResponse(false , e.getMessage());
           return new ResponseEntity<>(res , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response){
        try {
            ApiResponse res = userService.logOut(response);
            return new ResponseEntity<>(res , HttpStatus.OK);
            
        } catch (Exception e) {
            ApiResponse res = new ApiResponse(false , "Something went wrong");
            return new ResponseEntity<>(res , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
