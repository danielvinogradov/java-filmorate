package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Модель фильма.
 */
@Data
public final class Film {

    /**
     * Источник уникальных идентификаторов {@link #id} и счетчик добавленных фильмов.
     */
    private static long counter = 1;

    /**
     * Уникальный целочисленный идентификатор.
     */
    private final long id = counter++;

    /**
     * Название.
     */
    @NonNull
    @NotBlank
    private final String name;

    /**
     * Описание.
     */
    @Size(max = 200)
    private final String description;

    /**
     * Дата выпуска в прокат (дата релиза).
     */
    @PastOrPresent
    private final LocalDate releaseDate;

    /**
     * Длительность.
     */
    @Min(1)
    private final int duration;

}
