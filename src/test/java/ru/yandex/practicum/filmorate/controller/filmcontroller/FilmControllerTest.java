package ru.yandex.practicum.filmorate.controller.filmcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelNoId;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelNoIdWFloatPointDuration;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelWId;
import ru.yandex.practicum.filmorate.model.Film;
import util.ControllerTestHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для {@link FilmController} и валидации данных в {@link Film}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
final class FilmControllerTest {

    private static final String URI = "/films";

    private static final ObjectMapper objectMapper = ControllerTestHelper.getObjectMapper();

    private static final WebTestClient webClient = WebTestClient.bindToController(FilmController.class).build();

    /**
     * Корректно добавляется один фильм с валидными данными.
     *
     * @see FilmController#addFilm(Film)
     */
    @Test
    void shouldCorrectlyAddAndRetrieveFilms() throws IOException {
        final FilmModelNoId expectedFilm = new FilmModelNoId("NAME_" + Math.random(),
                "DESCRIPTION_" + Math.random(),
                LocalDate.of(1995, 5, 4),
                ((int) (Math.random() * 100)));

        final byte[] responseBody = webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(expectedFilm))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().returnResult().getResponseBody();

        final Film actualDeserializedFilm = objectMapper.readValue(responseBody, Film.class);

        assertEquals(expectedFilm.getName(), actualDeserializedFilm.getName());
        assertEquals(expectedFilm.getDescription(), actualDeserializedFilm.getDescription());
        assertEquals(expectedFilm.getReleaseDate(), actualDeserializedFilm.getReleaseDate());
        assertEquals(expectedFilm.getDuration(), actualDeserializedFilm.getDuration());
    }

    /**
     * Корректно возвращаются несколько фильмов.
     *
     * @see FilmController#getAllFilms()
     */
    @Test
    void shouldCorrectlyRetrieveFilms() throws IOException {
        final FilmModelNoId film1 = new FilmModelNoId("film1_name", "film2_description",
                LocalDate.of(2001, 12, 4), 14);
        final FilmModelNoId film2 = new FilmModelNoId("film2_name", "film2_description",
                LocalDate.of(1993, 4, 17), 121);

        final List<FilmModelNoId> filmsToAdd = List.of(film1, film2);

        for (FilmModelNoId film : filmsToAdd) {
            webClient.post()
                    .uri(URI)
                    .header("Content-Type", "application/json")
                    .bodyValue(objectMapper.writeValueAsString(film))
                    .exchange()
                    .expectStatus().isOk();
        }

        final byte[] responseBody = webClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult().getResponseBody();

        final List<FilmModelNoId> actualDeserializedFilmList = Arrays.asList(objectMapper.readValue(responseBody,
                FilmModelNoId[].class));

        assertEquals(filmsToAdd.size(), actualDeserializedFilmList.size());
        assertTrue(actualDeserializedFilmList.containsAll(filmsToAdd));
    }

    /**
     * Проверка валидации поля `description`.
     *
     * <ul>
     *     <li>Поле не может содержать больше 200 символов.</li>
     * </ul>
     *
     * @see FilmController#addFilm(Film)
     */
    @Test
    void shouldReturn400WhenDescriptionLengthIsMoreThan200() throws JsonProcessingException {
        final String description200 = "a".repeat(200);
        final FilmModelNoId film1 = new FilmModelNoId("film1_name", description200,
                LocalDate.of(2001, 12, 4), 14);

        // 200 знаков description можно
        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description").isNotEmpty();

        final FilmModelNoId film2 = new FilmModelNoId("film2_name", "a".repeat(201),
                LocalDate.of(2001, 12, 4), 14);

        // 201 или больше уже нельзя
        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film2))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    /**
     * Проверка валидации поля `name`.
     *
     * <ul>
     *     <li>Поле не может быть null</li>
     *     <li>Поле не может быть blank</li>
     * </ul>
     *
     * @see FilmController#addFilm(Film)
     */
    @Test
    void shouldReturn400WhenNameIsNullOrBlank() throws JsonProcessingException {
        // name is null
        final FilmModelNoId film1 = new FilmModelNoId(null, "film1_description",
                LocalDate.of(2001, 12, 4), 14);

        // name is blank
        final FilmModelNoId film2 = new FilmModelNoId("  \n", "film2_description",
                LocalDate.of(1993, 4, 17), 121);

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film2))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    /**
     * Проверка валидации поля `duration`.
     * <p>
     * Должно быть целым положительным числом.
     * <ul>
     *     <li>Поле не может быть null.</li>
     *     <li>Поле не может быть меньше 0.</li>
     *     <li>Полен не может быть 0.</li>
     *     <li>В случае отправки вещественного числа останется его целая часть.</li>
     * </ul>
     */
    @Test
    void shouldReturn400WhenDurationIsNotPositive() throws JsonProcessingException {
        // null
        final FilmModelNoId film1 = new FilmModelNoId("film1_description", "film1_description",
                LocalDate.of(2001, 12, 4), null);

        // отрицательное значение
        final FilmModelNoId film2 = new FilmModelNoId("film2_description", "film2_description",
                LocalDate.of(2001, 12, 4), -10);

        // zero
        final FilmModelNoId film3 = new FilmModelNoId("film3_description", "film3_description",
                LocalDate.of(2001, 12, 4), 0);

        // duration в виде вещественного числа
        final FilmModelNoIdWFloatPointDuration film4 = new FilmModelNoIdWFloatPointDuration("film4_description",
                "film4_description", LocalDate.of(2001, 12, 4), 60.7);

        // пограничный случай, duration == 1
        final FilmModelNoId film5 = new FilmModelNoId("film5_description", "film5_description",
                LocalDate.of(2001, 12, 4), 1);

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film2))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film3))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film4))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.duration").isNumber();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film5))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.duration").isNumber();
    }

    /**
     * Проверка валидации поля `releaseDate`.
     *
     * <ul>
     *     <li>Не может быть раньше 28 декабря 1895 года.</li>
     *     <li>Может быть в будущем.</li>
     * </ul>
     *
     * @see FilmController#addFilm(Film)
     */
    @Test
    void shouldReturn400WhenReleaseDateIsBefore28_12_1895() throws JsonProcessingException {
        // пограничный случай, 28 декабря 1895 года (валидное значение)
        final FilmModelNoId film1 = new FilmModelNoId("film1_description", "film1_description",
                LocalDate.of(1895, 12, 28), 120);

        // раньше 28 декабря 1895
        final FilmModelNoId film2 = new FilmModelNoId("film2_description", "film2_description",
                LocalDate.of(1895, 12, 27), 120);

        // дата в будущем
        final FilmModelNoId film3 = new FilmModelNoId("film3_description", "film3_description",
                LocalDate.now().plusDays(10), 120);

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.releaseDate").exists();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film2))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();

        webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film3))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.releaseDate").exists();
    }

    /**
     * Должен возвращать 500 status code при попытке обновить фильм, который не был добавлен.
     *
     * @see FilmController#updateFilm(Film)
     */
    @Test
    void shouldReturn400WhenUpdatingNonExistentFilm() throws IOException {
        // будет добавлен
        final FilmModelNoId film1 = new FilmModelNoId("film1_description", "film1_description",
                LocalDate.of(1995, 12, 28), 120);

        final byte[] film1ResponseBody = webClient.post()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .returnResult().getResponseBody();

        final long film1Id = objectMapper.readValue(film1ResponseBody, FilmModelWId.class).getId();
        final FilmModelWId film1Updated = new FilmModelWId(film1Id, "film1_description_updated",
                "film1_description", LocalDate.of(1995, 12, 28), 120);

        webClient.put()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film1Updated))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isNotEmpty();

        // не будет добавлен
        final FilmModelWId film2 = new FilmModelWId(film1Id + 121, "film2_description", "film2_description",
                LocalDate.of(1995, 12, 28), 120);

        webClient.put()
                .uri(URI)
                .bodyValue(objectMapper.writeValueAsString(film2))
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().isEmpty();
    }

}
