package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.util.Date;

/**
 * Модель фильма.
 */
@Data
public final class Film {

    /**
     * Уникальный целочисленный идентификатор.
     */
    private final long id;

    /**
     * Название.
     */
    private final String name;

    /**
     * Описание.
     */
    private final String description;

    /**
     * Дата выпуска в прокат (дата релиза).
     */
    private final Date releaseDate;

    /**
     * Длительность.
     */
    private final Duration duration;
}
