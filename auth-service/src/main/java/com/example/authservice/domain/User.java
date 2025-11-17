package com.example.authservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name="users", indexes={
  @Index(name="idx_users_provider_providerId", columnList="provider,providerId", unique=true),
  @Index(name="idx_users_email", columnList="email", unique=true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private AuthProvider provider;
  @Column(nullable=false, length=128) private String providerId;
  @Column(nullable=false, length=320) private String email;
  @Column(nullable=false, length=120) private String name;
  @Column(length=512) private String pictureUrl;
  @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Role role;
  @CreationTimestamp @Column(nullable=false, updatable=false) private Instant createdAt;
  @UpdateTimestamp @Column(nullable=false) private Instant updatedAt;
}
