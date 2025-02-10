package ru.umagadzhi.blogplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.blogplatform.entities.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
