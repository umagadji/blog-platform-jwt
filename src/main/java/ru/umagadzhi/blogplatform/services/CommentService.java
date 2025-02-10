package ru.umagadzhi.blogplatform.services;

import org.springframework.stereotype.Service;
import ru.umagadzhi.blogplatform.dto.CommentRequest;
import ru.umagadzhi.blogplatform.dto.CommentResponse;
import ru.umagadzhi.blogplatform.dto.PostResponse;
import ru.umagadzhi.blogplatform.dto.UserResponse;
import ru.umagadzhi.blogplatform.entities.Comment;
import ru.umagadzhi.blogplatform.entities.Post;
import ru.umagadzhi.blogplatform.entities.User;
import ru.umagadzhi.blogplatform.repository.CommentRepository;
import ru.umagadzhi.blogplatform.repository.PostRepository;
import ru.umagadzhi.blogplatform.repository.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    //Добавление комментария к посту
    public CommentResponse createComment(CommentRequest commentRequest) {
        // Проверка, что id не передается для создания нового комментария
        if (commentRequest.getId() != null) {
            throw new IllegalArgumentException("Нельзя передавать id при создании комментария.");
        }

        // Проверяем, передано ли содержимое комментария
        if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым.");
        }

        //Проверяем существует ли пользователь/автор
        User author = userRepository.findById(commentRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + commentRequest.getAuthorId() + " не найден."));

        //Проверяем существует ли пост, куда добавляется комментарий
        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Пост с id = " + commentRequest.getPostId() + " не найден."));

        //Создаем комментарий
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        //Сохраняем комментарий
        Comment savedComment = commentRepository.save(comment);

        //Возвращаем DTO комментария
        return new CommentResponse(
                savedComment.getId(),
                savedComment.getContent(),
                new UserResponse(author.getId(), author.getUsername(), author.getEmail())
        );
    }

    //Обновление комментария
    public CommentResponse updateComment(CommentRequest commentRequest) {
        if (commentRequest.getId() == null) {
            throw new IllegalArgumentException("ID комментария обязателен");
        }

        // Проверяем, передано ли содержимое комментария
        if (commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Комментарий не может быть пустым.");
        }

        if (commentRequest.getAuthorId() == null) {
            throw new IllegalArgumentException("Автор обязателен для комментария");
        }

        if (commentRequest.getPostId() == null) {
            throw new IllegalArgumentException("Пост обязателен для комментария");
        }

        //Проверяем существует ли пользователь/автор
        User author = userRepository.findById(commentRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + commentRequest.getAuthorId() + " не найден."));

        //Проверяем существует ли пост, куда добавляется комментарий
        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Пост с id = " + commentRequest.getPostId() + " не найден."));

        //Ищем комментарий по его ID
        return commentRepository.findById(commentRequest.getId())
                .map(comment -> {
                    //Обновляем комментарий
                    comment.setContent(commentRequest.getContent());
                    comment.setPost(post);
                    comment.setAuthor(author);

                    //Сохраняем в БД обновленный комментарий
                    Comment updatedComment = commentRepository.save(comment);

                    return new CommentResponse(
                            updatedComment.getId(),
                            updatedComment.getContent(),
                            new UserResponse(author.getId(), author.getUsername(), author.getEmail())
                    );
                }).orElse(null); //Если комментарий не найден
    }

    //Получаем комментарий по ID
    public CommentResponse getCommentByID(Long id) {
        //Ищем комментарий
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий и id = " + id + " не найден"));

        //Возвращаем DTO объект
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                new UserResponse(comment.getAuthor().getId(), comment.getAuthor().getUsername(), comment.getAuthor().getEmail())
        );
    }

    //Удаляем комментарий по его ID
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
