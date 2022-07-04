package ru.yandex.practicum.filmorate.controller;

import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public final class FilmController {

    /**
     * Данные о фильмах.
     */
    private static final Map<Long, Film> data = new HashMap<>();

    /**
     * Получить все фильмы.
     *
     * @return Список всех фильмов (порядок добавления не сохраняется).
     */
    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return data.values();
    }

    /**
     * Добавить новый фильм.
     *
     * @return Добавленный фильм.
     */
    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        final long id = film.getId();

        if (id < 0) {
            throw new ValidationException();
        }

        validateReleaseDate(film.getReleaseDate());

        data.put(id, film);

        return data.get(id);
    }

    /**
     * Обновить (полностью перезаписать) существующий фильм.
     *
     * @return Обновленный фильм.
     */
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        final long id = film.getId();

        if (!data.containsKey(id)) {
            throw new ValidationException("Фильма с переданным идентификатором не существует в базе.");
        }

        validateReleaseDate(film.getReleaseDate());

        data.put(id, film);

        return data.get(id);
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
}
