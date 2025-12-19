package com.app.backend.config;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {
    private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public String generateToken(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roles = populateAuthorities(authorities);

        String jwt = Jwts.builder()
                .issuedAt(new Date())
                .claim("email", authentication.getName())
                .claim("authorities", roles)
                .expiration(new Date(System.currentTimeMillis() * EXPIRATION_TIME))
                .signWith(key)
                .compact();
         return jwt;

    }

    public Claims parseToken(String jwt) throws JwtException{
       return Jwts.parser()
       .verifyWith(key)
       .build()
       .parseSignedClaims(jwt)
       .getPayload();
    }

    public String getEmailFromJwsToken(String jwt){
        Claims claims = parseToken(jwt);
        return claims.get("email",String.class);
    }
    public String getAuthoritiesFromJwt(String jwt){
        Claims claims =  parseToken(jwt);
        return claims.get("authorities",String.class);
    }
    public boolean isTokenExpired(String jwt){
        try {
            Claims claims = parseToken(jwt);
            Date exp = claims.getExpiration();
            return exp.before(exp);
        } catch (Exception e) {
            return true;
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}
