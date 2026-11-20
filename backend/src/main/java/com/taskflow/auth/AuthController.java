package com.taskflow.auth;
import com.taskflow.user.UserController.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
  public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 8, max = 72) String password, @NotBlank @Size(max = 100) String fullName) {}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
  public record AuthResponse(String accessToken, UserDto user) {}
  private static final String COOKIE = "refresh_token";
  private final AuthService auth; private final boolean secure; private final String sameSite; private final long refreshDays;
  public AuthController(AuthService auth, @Value("${app.cookie.secure}") boolean secure, @Value("${app.cookie.same-site}") String sameSite, @Value("${app.refresh-days:14}") long refreshDays) {
    this.auth = auth; this.secure = secure; this.sameSite = sameSite; this.refreshDays = refreshDays;
  }
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public UserDto register(@Valid @RequestBody RegisterRequest r) { return UserDto.of(auth.register(r.email(), r.password(), r.fullName())); }
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest r) { return respond(auth.login(r.email(), r.password())); }
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@CookieValue(name = COOKIE, required = false) String raw) { return respond(auth.refresh(raw)); }
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue(name = COOKIE, required = false) String raw) {
    auth.logout(raw);
    return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString()).build();
  }
  private ResponseEntity<AuthResponse> respond(AuthService.Session s) {
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie(s.refreshToken(), Duration.ofDays(refreshDays)).toString())
      .body(new AuthResponse(s.accessToken(), UserDto.of(s.user())));
  }
  private ResponseCookie cookie(String v, Duration age) {
    return ResponseCookie.from(COOKIE, v).httpOnly(true).secure(secure).sameSite(sameSite).path("/api/v1/auth").maxAge(age).build();
  }
}
