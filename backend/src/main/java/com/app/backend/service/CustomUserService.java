package com.app.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.backend.model.User;
import com.app.backend.repo.UserRepo;

@Service
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepo.findByEmail(username);
       if(user == null){
         throw  new UsernameNotFoundException("User not found");
       }

       List<GrantedAuthority>authorities =new ArrayList<>();
       authorities.add(new SimpleGrantedAuthority(user.getRole()));
       return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),authorities);


    }
    
}
