package ru.yandex.practicum.filmorate.controller.filmcontroller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Модель фильма без id и вещественным `duration` для отправки запросов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class FilmModelNoIdWFloatPointDuration {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Double duration;
}
