package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmTest {

    @Test
    void shouldThrowValidationExceptionIfReleaseDateIsIncorrect() throws ValidationException {
        new Film(null, "mock name", "mock description", LocalDate.of(1895, 12, 28), 10);
        new Film(null, "mock name", "mock description", LocalDate.of(1995, 1, 14), 10);

        assertThrows(
                ValidationException.class,
                () -> new Film(null, "mock name", "mock description", LocalDate.of(1800, 1, 1), 10)
        );

        assertThrows(
                ValidationException.class,
                () -> new Film(null, "mock name", "mock description", LocalDate.of(1895, 12, 27), 10)
        );
    }

}
