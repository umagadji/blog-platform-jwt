package ru.umagadzhi.blogplatform.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.umagadzhi.blogplatform.dto.LikeRequest;
import ru.umagadzhi.blogplatform.dto.LikeResponse;
import ru.umagadzhi.blogplatform.dto.UserResponse;
import ru.umagadzhi.blogplatform.entities.Like;
import ru.umagadzhi.blogplatform.entities.Post;
import ru.umagadzhi.blogplatform.entities.User;
import ru.umagadzhi.blogplatform.repository.LikeRepository;
import ru.umagadzhi.blogplatform.repository.PostRepository;
import ru.umagadzhi.blogplatform.repository.UserRepository;

import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    //Метод для добавления лайка
    public LikeResponse addLike(LikeRequest likeRequest) {
        //Проверяем существует ли пользователь/автор
        User user = userRepository.findById(likeRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + likeRequest.userId() + " не найден."));

        //Проверяем существует ли пост, куда добавляется комментарий
        Post post = postRepository.findById(likeRequest.postId())
                .orElseThrow(() -> new IllegalArgumentException("Пост с id = " + likeRequest.postId() + " не найден."));

        // Проверяем, не поставил ли пользователь лайк ранее
        if (likeRepository.existsLikeByPostAndUser(post, user)) {
            throw new IllegalStateException("Вы уже лайкнули этот пост");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        Like savedLike = likeRepository.save(like);

        return new LikeResponse(savedLike.getId(), new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    //Удаление лайка у поста
    @Transactional
    public void removeLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        // Проверяем, есть ли лайк перед удалением
        if (!likeRepository.existsLikeByPostAndUser(post, user)) {
            throw new IllegalStateException("Лайка не существует");
        }

        likeRepository.deleteByPostAndUser(post, user);
    }

    //Получить количество лайков у поста
    public long getLikesCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        return likeRepository.countByPost(post);
    }

    //Получить список пользователей лайкнувших пост
    public List<UserResponse> getUsersWhoLikedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        List<Like> likes = likeRepository.findByPost(post);

        return likes.stream()
                .map(like -> new UserResponse(like.getUser().getId(), like.getUser().getUsername(), like.getUser().getEmail()))
                .toList();
    }
}
