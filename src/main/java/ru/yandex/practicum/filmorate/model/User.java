package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель пользователя.
 */
@Data
public final class User {

    private static long counter = 1;

    /**
     * Уникальный целочисленный идентификатор.
     */
    private final long id;

    /**
     * Электронная почта.
     */
    @Email
    private final String email;

    /**
     * Логин.
     */
    @NonNull
    @NotBlank
    @Pattern(regexp = "^\\w+$")
    private final String login;

    /**
     * Имя пользователя (для отображения).
     */
    @Nullable
    private final String name;

    /**
     * День рождения.
     */
    @NonNull
    @PastOrPresent
    private final LocalDate birthday;

    /**
     * No args constructor for jackson.
     */
    public User() {
        this(null, "", "", null, LocalDate.now());
    }

    public User(final @Nullable Long id,
                final @NonNull String email,
                final @NonNull String login,
                final @Nullable String name,
                final @NonNull LocalDate birthday) {

        this.id = Objects.requireNonNullElseGet(id, () -> counter++);

        this.email = email;
        this.login = login;

        if (name == null || name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }

        this.birthday = birthday;
    }
}
