package com.taskflow.user;
import java.util.*;
import org.springframework.data.jpa.repository.*;
public interface UserRepository extends JpaRepository<User, UUID> {
  @Query("select u from User u where lower(u.email) = lower(:email)") Optional<User> findByEmailIgnoreCase(String email);
  @Query("select count(u) > 0 from User u where lower(u.email) = lower(:email)") boolean existsByEmailIgnoreCase(String email);
}
