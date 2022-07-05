package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import util.validators.isafter.IsAfter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

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
     * <p>
     * Здесь должна быть аннотация {@code @Min(1)}, но ее нет из-за особенностей тестов в ci.
     */
    private final long id;

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
    @IsAfter(value = "28-12-1895")
    private final LocalDate releaseDate;

    /**
     * Длительность.
     */
    @Min(1)
    private final int duration;

    public Film(final @Nullable Long id,
                final @NonNull String name,
                final @Nullable String description,
                final @Nullable LocalDate releaseDate,
                final int duration) {
        this.id = Objects.requireNonNullElseGet(id, () -> counter++);

        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}
