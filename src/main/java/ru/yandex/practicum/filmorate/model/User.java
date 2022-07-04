package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Date;

/**
 * Модель пользователя.
 */
@Data
public final class User {

    /**
     * Уникальный целочисленный идентификатор.
     */
    private final long id;

    /**
     * Электронная почта.
     */
    private final String email;

    /**
     * Логин.
     */
    private final String login;

    /**
     * Имя пользователя (для отображения).
     */
    private final String name;

    /**
     * День рождения.
     */
    private final Date birthday;
}
