package ru.yandex.practicum.filmorate.controller.usercontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель пользователя с идентификатором для отправки запросов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserModelWId {
    private Long id;
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
}

