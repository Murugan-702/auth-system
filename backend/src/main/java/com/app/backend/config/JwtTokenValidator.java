package com.app.backend.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {
    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            String jwt = null;
            if(request.getCookies()!=null){
                for(Cookie cookie:request.getCookies()){
                    if("token".equals(cookie.getName())){
                        jwt = cookie.getValue();
                    }
                }
            }

            if(jwt!=null){
            
                try {
                    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
                    String email = claims.get("email",String.class);
                    String authorities = claims.get("authorities",String.class);
                    List<GrantedAuthority>auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email,null,auth);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    throw new BadCredentialsException("Invalid Jwt");
                }
            }
            filterChain.doFilter(request,response);
    }
    
}
