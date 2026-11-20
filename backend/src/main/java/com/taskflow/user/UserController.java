package com.taskflow.user;
import com.taskflow.common.ApiException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/users")
public class UserController {
  public record UserDto(UUID id, String email, String fullName, boolean emailVerified) {
    public static UserDto of(User u) { return new UserDto(u.getId(), u.getEmail(), u.getFullName(), u.isEmailVerified()); }
  }
  private final UserRepository users;
  public UserController(UserRepository users) { this.users = users; }
  @GetMapping("/me")
  public UserDto me(Authentication auth) {
    return users.findById(UUID.fromString(auth.getName())).map(UserDto::of)
      .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "User not found"));
  }
}
