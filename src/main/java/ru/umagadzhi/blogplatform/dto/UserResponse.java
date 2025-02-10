package ru.umagadzhi.blogplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//DTO для ответа при получении пользователя
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}
