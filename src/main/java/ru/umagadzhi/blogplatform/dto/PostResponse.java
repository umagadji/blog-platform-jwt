package ru.umagadzhi.blogplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//Класс описывающий ответы для CRUD операций постов
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private UserResponse author; //Используем DTO классы
    private CategoryResponse category; //Используем DTO классы
}
