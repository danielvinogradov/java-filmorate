package ru.yandex.practicum.filmorate.controller.usercontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель пользователя без идентификатора для отправки запросов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserModelNoId {
    private String email;
    private String name;
    private String login;
    private LocalDate birthday;
}
