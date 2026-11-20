package com.taskflow.auth;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name = "refresh_token")
public class RefreshToken {
  @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
  @Column(name = "user_id", nullable = false) private UUID userId;
  @Column(name = "token_hash", nullable = false, unique = true) private String tokenHash;
  @Column(name = "family_id", nullable = false) private UUID familyId;
  @Column(name = "expires_at", nullable = false) private Instant expiresAt;
  @Column(name = "revoked_at") private Instant revokedAt;
  @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
  protected RefreshToken() {}
  public RefreshToken(UUID userId, String tokenHash, UUID familyId, Instant expiresAt) { this.userId = userId; this.tokenHash = tokenHash; this.familyId = familyId; this.expiresAt = expiresAt; }
  public UUID getUserId() { return userId; } public UUID getFamilyId() { return familyId; }
  public Instant getExpiresAt() { return expiresAt; } public Instant getRevokedAt() { return revokedAt; }
  public void revoke() { revokedAt = Instant.now(); }
}
