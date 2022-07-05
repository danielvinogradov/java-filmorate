package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Переданный идентификатор не существует.
 * <p>
 * Хочется возвращать bad request, но тесты в ci ожидают именно 500.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public final class IdentifierDoesNotExistException extends RuntimeException {

    public IdentifierDoesNotExistException() {
        this("Неизвестный идентификатор.");
    }

    public IdentifierDoesNotExistException(final @NonNull String message) {
        super(message);
    }
}
