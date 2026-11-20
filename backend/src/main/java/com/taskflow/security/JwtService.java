package com.taskflow.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
  private final SecretKey key; private final long accessMinutes;
  public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.access-minutes:15}") long accessMinutes) {
    if (secret.length() < 32) throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessMinutes = accessMinutes;
  }
  public String createAccessToken(UUID userId, String email) {
    Instant now = Instant.now();
    return Jwts.builder().subject(userId.toString()).claim("email", email).issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(accessMinutes * 60))).signWith(key).compact();
  }
  /** Returns the user id, or throws JwtException if invalid/expired. */
  public UUID parseUserId(String token) {
    return UUID.fromString(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
  }
}
