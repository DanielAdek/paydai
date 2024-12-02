package com.paydai.api.infrastructure.security;

import com.paydai.api.domain.model.UserModel;
import com.paydai.api.infrastructure.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtAuthService {
  private final AppConfig appConfig;

  //******************* GENERATE TOKEN BY SETTING CLAIMS***************//
  @NotNull
  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(appConfig.getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String generateToken(Map<String, Object> extraClaims, @NotNull UserDetails userDetails) {
    String userId = ((UserModel) userDetails).getId().toString();

    return Jwts.builder()
      .setClaims(extraClaims)
      .setSubject(userId)
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 1 day
      .signWith(getSignKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  public String generateToken(@NotNull UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  //******************* EXTRACT THE CLAIM *******************************//
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public UUID extractUserId(String token) {
    String userId = extractClaim(token, Claims::getSubject);
    return UUID.fromString(userId);
  }

  //******************* CHECK TOKEN VALIDITY ****************************//
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    UserModel userModel = (UserModel) userDetails;
    UUID extractedUserId = extractUserId(token);
    UUID userId = userModel.getId();
    return userId.equals(extractedUserId) && !isTokenExpired(token);
  }
}
