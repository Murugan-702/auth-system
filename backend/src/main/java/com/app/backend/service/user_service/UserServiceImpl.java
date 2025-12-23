package com.app.backend.service.user_service;

import org.springframework.http.HttpHeaders;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.backend.config.JwtProvider;
import com.app.backend.dto.ApiResponse;
import com.app.backend.dto.LoginRequest;
import com.app.backend.dto.RegisterRequest;
import com.app.backend.model.User;
import com.app.backend.repo.UserRepo;
import com.app.backend.service.CustomUserService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired

    private  CustomUserService customUserService;

    @Override
    public ApiResponse registerUser(RegisterRequest req) {
           
        // check user if already exists 
        if(userRepo.findByEmail(req.getEmail()) != null){
                   return new ApiResponse(false , "User already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setRole("USER");
        
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepo.save(user);
        return new ApiResponse(true , "User registered successfully");
    
    }

    @Override
    public ApiResponse loginUser(LoginRequest req, HttpServletResponse response) {
        String email = req.getEmail();
        String password = req.getPassword();
        if(userRepo.findByEmail(email)  ==  null){
             return new ApiResponse(false , "User not exists");
        }

        Authentication authentication = authenticate(email, password);

        String jwt = jwtProvider.generateToken(authentication);

          
          ResponseCookie cookie = ResponseCookie.from("token",jwt)
          .httpOnly(true)
          .secure(false)
          .sameSite("Strict")
          .path("/")
          .maxAge(Duration.ofDays(1))
          .build();

          response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
          return new ApiResponse(true , "Login Successful");
        
    }

    @Override
    public ApiResponse logOut(HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from("token","")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return new ApiResponse(true , "Logout Successful");
    }

    private Authentication authenticate(String email , String password)throws BadCredentialsException{
       UserDetails userDetails = customUserService.loadUserByUsername(email);
       if(userDetails == null){
          throw new BadCredentialsException("Invalid Credentails");
       }
       if(!passwordEncoder.matches(password, userDetails.getPassword())){
         throw new BadCredentialsException("Invalid Password");
       }
       return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
    
}
