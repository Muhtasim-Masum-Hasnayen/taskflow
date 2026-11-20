package com.taskflow.auth;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.taskflow.common.ApiException;
import com.taskflow.security.JwtService;
import com.taskflow.user.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
class AuthServiceTest {
  UserRepository users = mock(UserRepository.class); RefreshTokenRepository tokens = mock(RefreshTokenRepository.class);
  BCryptPasswordEncoder enc = new BCryptPasswordEncoder(4);
  AuthService svc;
  @BeforeEach void setup() {
    svc = new AuthService(users, tokens, enc, new JwtService("unit-test-secret-unit-test-secret-1234", 15), 14);
    when(users.save(any(User.class))).thenAnswer(i -> i.getArgument(0)); when(tokens.save(any())).thenAnswer(i -> i.getArgument(0));
  }
  @Test void registerRejectsDuplicateEmail() {
    when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);
    assertEquals("EMAIL_TAKEN", assertThrows(ApiException.class, () -> svc.register("a@b.com", "password1", "A")).code());
  }
  @Test void registerHashesPassword() {
    User u = svc.register("a@b.com", "password1", "A");
    assertNotEquals("password1", u.getPasswordHash()); assertTrue(enc.matches("password1", u.getPasswordHash()));
  }
  @Test void loginFailsWithWrongPassword() {
    User u = new User(); u.setEmail("a@b.com"); u.setPasswordHash(enc.encode("right-pass"));
    when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(u));
    assertEquals("BAD_CREDENTIALS", assertThrows(ApiException.class, () -> svc.login("a@b.com", "wrong")).code());
  }
  @Test void reusedRefreshTokenRevokesFamily() {
    RefreshToken t = new RefreshToken(UUID.randomUUID(), AuthService.hash("raw"), UUID.randomUUID(), Instant.now().plusSeconds(60)); t.revoke();
    when(tokens.findByTokenHash(AuthService.hash("raw"))).thenReturn(Optional.of(t));
    assertEquals("REFRESH_REUSE", assertThrows(ApiException.class, () -> svc.refresh("raw")).code());
    verify(tokens).revokeFamily(eq(t.getFamilyId()), any(Instant.class));
  }
}
