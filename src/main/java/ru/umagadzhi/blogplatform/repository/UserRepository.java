package ru.umagadzhi.blogplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.blogplatform.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String text);
    Optional<User> findByEmail(String text);
}
