package com.taskflow.auth;
import com.taskflow.common.ApiException;
import com.taskflow.security.JwtService;
import com.taskflow.user.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
  public record Session(String accessToken, String refreshToken, User user) {}
  private static final SecureRandom RANDOM = new SecureRandom();
  private final UserRepository users; private final RefreshTokenRepository tokens;
  private final PasswordEncoder encoder; private final JwtService jwt; private final long refreshDays;
  public AuthService(UserRepository users, RefreshTokenRepository tokens, PasswordEncoder encoder, JwtService jwt, @Value("${app.refresh-days:14}") long refreshDays) {
    this.users = users; this.tokens = tokens; this.encoder = encoder; this.jwt = jwt; this.refreshDays = refreshDays;
  }
  @Transactional
  public User register(String email, String password, String fullName) {
    if (users.existsByEmailIgnoreCase(email)) throw new ApiException(HttpStatus.CONFLICT, "EMAIL_TAKEN", "Email already registered");
    User u = new User(); u.setEmail(email.trim()); u.setFullName(fullName.trim()); u.setPasswordHash(encoder.encode(password));
    return users.save(u);
  }
  @Transactional
  public Session login(String email, String password) {
    User u = users.findByEmailIgnoreCase(email).orElse(null);
    if (u == null || !u.isEnabled() || !encoder.matches(password, u.getPasswordHash()))
      throw new ApiException(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Invalid email or password");
    return issue(u, UUID.randomUUID());
  }
  /** Rotates the refresh token. Reuse of a revoked token revokes the whole family (theft detection). */
  @Transactional(noRollbackFor = ApiException.class)
  public Session refresh(String raw) {
    if (raw == null) throw new ApiException(HttpStatus.UNAUTHORIZED, "NO_REFRESH_TOKEN", "Missing refresh token");
    RefreshToken t = tokens.findByTokenHash(hash(raw)).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Invalid refresh token"));
    if (t.getRevokedAt() != null) { tokens.revokeFamily(t.getFamilyId(), Instant.now()); throw new ApiException(HttpStatus.UNAUTHORIZED, "REFRESH_REUSE", "Refresh token reuse detected"); }
    if (t.getExpiresAt().isBefore(Instant.now())) throw new ApiException(HttpStatus.UNAUTHORIZED, "REFRESH_EXPIRED", "Refresh token expired");
    t.revoke();
    User u = users.findById(t.getUserId()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "Invalid refresh token"));
    return issue(u, t.getFamilyId());
  }
  @Transactional
  public void logout(String raw) {
    if (raw != null) tokens.findByTokenHash(hash(raw)).ifPresent(t -> tokens.revokeFamily(t.getFamilyId(), Instant.now()));
  }
  private Session issue(User u, UUID family) {
    byte[] b = new byte[48]; RANDOM.nextBytes(b);
    String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    tokens.save(new RefreshToken(u.getId(), hash(raw), family, Instant.now().plus(refreshDays, ChronoUnit.DAYS)));
    return new Session(jwt.createAccessToken(u.getId(), u.getEmail()), raw, u);
  }
  static String hash(String raw) {
    try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8))); }
    catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
  }
}
