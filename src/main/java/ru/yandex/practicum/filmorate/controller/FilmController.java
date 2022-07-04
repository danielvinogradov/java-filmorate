package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

        if (data.containsKey(id)) {
            throw new ValidationException("Фильм с таким идентификатором уже существует.");
        }

        if (id < 0) {
            throw new ValidationException("Идентификатор фильма не может быть меньше 0.");
        }

        data.put(id, film);

        final Film addedFilm = data.get(id);
        log.info(String.format("Добавлен новый фильм: %s.", addedFilm));

        return addedFilm;
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

        data.put(id, film);

        final Film updatedFilm = data.get(id);
        log.info(String.format("Обновлен фильм: %s.", updatedFilm));

        return updatedFilm;
    }

}
