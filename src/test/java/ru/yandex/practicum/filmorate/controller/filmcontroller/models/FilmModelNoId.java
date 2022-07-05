package ru.yandex.practicum.filmorate.controller.filmcontroller.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель фильма без id для отправки запросов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class FilmModelNoId {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}
