package com.taskflow.user;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
  @Column(nullable = false) private String email;
  @Column(name = "password_hash", nullable = false) private String passwordHash;
  @Column(name = "full_name", nullable = false) private String fullName;
  @Column(name = "email_verified", nullable = false) private boolean emailVerified;
  @Column(nullable = false) private boolean enabled = true;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;
  @PrePersist void onCreate() { createdAt = updatedAt = Instant.now(); }
  @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
  public UUID getId() { return id; } public String getEmail() { return email; } public void setEmail(String v) { email = v; }
  public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String v) { passwordHash = v; }
  public String getFullName() { return fullName; } public void setFullName(String v) { fullName = v; }
  public boolean isEmailVerified() { return emailVerified; } public boolean isEnabled() { return enabled; }
}
