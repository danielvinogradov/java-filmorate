package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public final class UserController {

    private static final Map<Long, User> data = new HashMap<>();

    /**
     * Получить всех пользователей.
     *
     * @return Список всех пользователей (порядок добавления не сохраняется).
     */
    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return data.values();
    }

    /**
     * Создать нового пользователя.
     *
     * @param user Новый пользователь.
     * @return Объект нового пользователя.
     * @throws ValidationException Исключение в случае невалидных данных.
     */
    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        final long id = user.getId();

        if (id < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть меньше нуля.");
        }

        if (data.containsKey(id)) {
            throw new ValidationException("Пользователь с указанным идентификатором уже существует.");
        }

        data.put(id, user);

        return data.get(id);
    }

    /**
     * Обновить (полностью перезаписать) данные о существующем пользователе.
     *
     * @param user Новый объект пользователя.
     * @return Записанный объект нового пользователя.
     * @throws ValidationException Исключение в случае невалидных данных.
     */
    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        final long id = user.getId();

        if (id < 0) {
            throw new ValidationException("Идентификатор пользователя не может быть меньше нуля.");
        }

        if (!data.containsKey(id)) {
            throw new ValidationException("Пользователя с указанным идентификатором не существует.");
        }

        data.put(id, user);

        return data.get(id);
    }

}
