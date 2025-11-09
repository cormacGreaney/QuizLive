package com.example.authservice.repo;

import com.example.authservice.domain.AuthProvider;
import com.example.authservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
  Optional<User> findByEmail(String email);
}
