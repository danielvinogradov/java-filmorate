package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.lang.NonNull;

/**
 * Ошибка при валидации данных (переданные данные не валидны).
 */
public final class ValidationException extends Exception {

    public ValidationException() {
        this("Данные не валидны.");
    }

    public ValidationException(final @NonNull String message) {
        super(message);
    }
}
