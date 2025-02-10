package ru.umagadzhi.blogplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.blogplatform.entities.Comment;
import ru.umagadzhi.blogplatform.entities.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
