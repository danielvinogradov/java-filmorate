package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    private final LocalDate releaseDate;

    /**
     * Длительность.
     */
    @Min(1)
    private final int duration;

    public Film(final @Nullable Long id,
                final @NonNull String name,
                final String description,
                final LocalDate releaseDate,
                final int duration)
            throws ValidationException {
        validateReleaseDate(releaseDate);

        this.id = Objects.requireNonNullElseGet(id, () -> counter++);

        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    /**
     * Проверяет, что дата не раньше "дня рождения кино", т.е. 28 декабря 1895 года.
     *
     * @param date Проверяемая дата.
     * @throws ValidationException Исключение в случае невалидной даты.
     */
    private void validateReleaseDate(final @NonNull LocalDate date) throws ValidationException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (date.isBefore(minReleaseDate)) {
            throw new ValidationException("Дата выхода в прокат не может быть раньше 28 декабря 1985 года.");
        }
    }

    /**
     * Проверяет, что идентификатор больше 0.
     *
     * @param id Проверяемый идентификатор.
     * @throws ValidationException Исключение в случае невалидного идентификатора.
     * @deprecated
     */
    private void validateId(final long id) throws ValidationException {
        if (id < 0) throw new ValidationException("Идентификатор должен быть положительным.");
    }
}
