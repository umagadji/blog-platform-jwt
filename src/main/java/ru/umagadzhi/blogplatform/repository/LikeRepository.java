package ru.umagadzhi.blogplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.umagadzhi.blogplatform.entities.Like;
import ru.umagadzhi.blogplatform.entities.Post;
import ru.umagadzhi.blogplatform.entities.User;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // Проверяет, существует ли лайк от конкретного пользователя к посту
    boolean existsLikeByPostAndUser(Post post, User user);

    // Удаляет лайк пользователя у поста
    void deleteByPostAndUser(Post post, User user);

    // Получает все лайки определённого поста
    List<Like> findByPost(Post post);

    // Подсчитывает количество лайков у поста
    long countByPost(Post post);
}
