package com.example.proj.capstone.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtToken {
    public static final long JWT_TOKEN_VAliDITY=5*60*60;

        public String secret = "secret";
//using token so i can extract username
        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }
//retreve expirydate from token
        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }
      //retrieving info through token
        private Claims extractAllClaims(String token) {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }


  //to check token is expired or not
    private Boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

    //generating token so it will call createtoken
    public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            return createToken(claims, userDetails.getUsername());
        }
//creating token
        private String createToken(Map<String, Object> claims, String subject) {

            return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SignatureAlgorithm.HS256,secret).compact();
        }
//validating token
        public Boolean validateToken(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
}
