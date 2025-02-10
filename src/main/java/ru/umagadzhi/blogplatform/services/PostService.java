package ru.umagadzhi.blogplatform.services;

import org.springframework.stereotype.Service;
import ru.umagadzhi.blogplatform.dto.CategoryResponse;
import ru.umagadzhi.blogplatform.dto.PostRequest;
import ru.umagadzhi.blogplatform.dto.PostResponse;
import ru.umagadzhi.blogplatform.dto.UserResponse;
import ru.umagadzhi.blogplatform.entities.Category;
import ru.umagadzhi.blogplatform.entities.Post;
import ru.umagadzhi.blogplatform.entities.User;
import ru.umagadzhi.blogplatform.repository.CategoryRepository;
import ru.umagadzhi.blogplatform.repository.PostRepository;
import ru.umagadzhi.blogplatform.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // Конструктор с зависимостью для postRepository,userRepository,categoryRepository
    public PostService(PostRepository postRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    //Добавление нового поста в БД
    public PostResponse createPost(PostRequest postRequest) {
        // Проверка, что id не передается для создания нового поста
        if (postRequest.getId() != null) {
            throw new IllegalArgumentException("Нельзя передавать id при создании поста.");
        }

        //Проверяем существует ли пользователь/автор
        User author = userRepository.findById(postRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + postRequest.getAuthorId() + " не найден."));

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория с id = " + postRequest.getCategoryId() + " не найдена."));

        //Создаем новый пост
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthor(author);
        post.setCategory(category);

        // Сохраняем пост в базе данных
        Post savedPost = postRepository.save(post);

        // Возвращаем ответ только что созданного поста
        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent(),
                //Используем UserResponse, чтобы в ответе скрыть важные данные, например пароль
                new UserResponse(author.getId(), author.getUsername(), author.getEmail()),
                //Здесь можно было бы с category_id, пока оставил так
                new CategoryResponse(category.getId(), category.getName())
        );
    }

    //Обновление поста
    public PostResponse updatePost(PostRequest postRequest) {
        if (postRequest.getId() == null) {
            throw new IllegalArgumentException("ID поста обязателен");
        }

        if (postRequest.getTitle() == null || postRequest.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Название поста обязательно");
        }

        if (postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Контент для поста обязателен");
        }

        if (postRequest.getAuthorId() == null) {
            throw new IllegalArgumentException("Автор обязателен для поста");
        }

        if (postRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("Категория обязательна для поста");
        }

        //Проверяем существует ли пользователь/автор
        User author = userRepository.findById(postRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с id = " + postRequest.getAuthorId() + " не найден."));

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Категория с id = " + postRequest.getCategoryId() + " не найдена."));

        //Ищем пост по id
        return postRepository.findById(postRequest.getId())
                .map(post -> {
                    //Обновляем данные поста
                    post.setTitle(postRequest.getTitle());
                    post.setContent(postRequest.getContent());
                    post.setAuthor(author);
                    post.setCategory(category);

                    // Сохраняем обновленный пост в базе данных
                    Post updatedPost = postRepository.save(post);

                    //Возвращаем обновленный пост
                    return new PostResponse(
                            updatedPost.getId(),
                            updatedPost.getTitle(),
                            updatedPost.getContent(),
                            new UserResponse(author.getId(), author.getUsername(), author.getEmail()),
                            new CategoryResponse(category.getId(), category.getName())
                            );
                }).orElse(null); //Если пост с таким id не найден
    }

    //Получить пост по его id
    public PostResponse getPostById(Long id) {
        // Ищем пост, если нет - выбрасываем исключение
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пост с id = " + id + " не найден."));

        //Возвращаем DTO объект поста
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                new UserResponse(
                        post.getAuthor().getId(),
                        post.getAuthor().getUsername(),
                        post.getAuthor().getEmail()
                ),
                new CategoryResponse(
                        post.getCategory().getId(),
                        post.getCategory().getName()
                )
        );
    }

    //Удаляем пост по его ID
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    //Получаем список постов по названию категории
    public List<PostResponse> getPostsByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории обязательно");
        }

        List<Post> postList = postRepository.findByCategory_Name(categoryName);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов в данной категории");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        new UserResponse(
                                post.getAuthor().getId(),
                                post.getAuthor().getUsername(),
                                post.getAuthor().getEmail()
                        ),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());

    }

    //Получаем список постов по автору
    public List<PostResponse> getPostsByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Автор обязателен");
        }

        List<Post> postList = postRepository.findByAuthor_Username(author);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов у этого автора");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        new UserResponse(
                                post.getAuthor().getId(),
                                post.getAuthor().getUsername(),
                                post.getAuthor().getEmail()
                        ),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());
    }

    //Получаем посты по категории и автору
    public List<PostResponse> getPostsByCategoryNameAndAuthor(String categoryName, String author) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория обязательна");
        }

        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Автор обязателен");
        }

        List<Post> postList = postRepository.findByCategory_NameAndAuthor_Username(categoryName, author);

        if (postList.isEmpty()) {
            throw new IllegalArgumentException("Нет постов по этой категории или автору");
        }

        //Преобразуем в PostResponse в возвращаем список
        return postList.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        new UserResponse(
                                post.getAuthor().getId(),
                                post.getAuthor().getUsername(),
                                post.getAuthor().getEmail()
                        ),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                )).collect(Collectors.toList());
    }

    //Получаем все посты
    public List<PostResponse> getAllPosts() {
        // Находим все категории в базе и преобразуем в CategoryResponse
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        new UserResponse(
                                post.getAuthor().getId(),
                                post.getAuthor().getUsername(),
                                post.getAuthor().getEmail()
                        ),
                        new CategoryResponse(
                                post.getCategory().getId(),
                                post.getCategory().getName()
                        )
                ))
                .toList();
    }

}
