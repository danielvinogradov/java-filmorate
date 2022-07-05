package ru.yandex.practicum.filmorate.exceptions;

/**
 * Ошибка при валидации данных (переданные данные не валидны).
 */
public final class ValidationException extends RuntimeException {

    public ValidationException() {
        this("Данные не валидны.");
    }

    public ValidationException(String message) {
        super(message);
    }
}
