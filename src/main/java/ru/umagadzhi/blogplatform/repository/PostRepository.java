package ru.umagadzhi.blogplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.blogplatform.entities.Post;
import ru.umagadzhi.blogplatform.entities.User;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor_Username(String author);
    List<Post> findByCategory_Name(String categoryName);
    List<Post> findByCategory_NameAndAuthor_Username(String categoryName, String author);
}
