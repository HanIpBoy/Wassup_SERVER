package com.example.demo.security;

import com.example.demo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenProvider {
	private static final String SECRET_KEY = "2B738E9FDA91B5366619C5D21156F";

	public String create(UserEntity userEntity) {
		Date expiryDate = Date.from(
				Instant.now().plus(1, ChronoUnit.DAYS));

		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userEntity.getUserId());
		claims.put("password", userEntity.getPassword());

		return Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.setClaims(claims)
				.setIssuer("Wassup")
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	public String validateAndGetUserId(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.parseClaimsJws(token)
				.getBody();
		
		return claims.get("userId", String.class);
	}


}
