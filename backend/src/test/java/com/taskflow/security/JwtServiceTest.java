package com.taskflow.security;
import static org.junit.jupiter.api.Assertions.*;
import io.jsonwebtoken.JwtException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
class JwtServiceTest {
  private final JwtService jwt = new JwtService("unit-test-secret-unit-test-secret-1234", 15);
  @Test void roundTripsUserId() { UUID id = UUID.randomUUID(); assertEquals(id, jwt.parseUserId(jwt.createAccessToken(id, "a@b.com"))); }
  @Test void rejectsTamperedToken() { String t = jwt.createAccessToken(UUID.randomUUID(), "a@b.com"); assertThrows(JwtException.class, () -> jwt.parseUserId(t + "x")); }
  @Test void rejectsShortSecret() { assertThrows(IllegalStateException.class, () -> new JwtService("short", 15)); }
}
