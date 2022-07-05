package ru.yandex.practicum.filmorate.controller.filmcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelNoId;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelNoIdWFloatPointDuration;
import ru.yandex.practicum.filmorate.controller.filmcontroller.model.FilmModelWId;
import ru.yandex.practicum.filmorate.model.Film;
import util.ControllerTestHelper;

import java.io.IOException;

import org.springframework.http.HttpHeaders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты для {@link FilmController} и валидации данных в {@link Film}.
 *
 * @see Film
 * @see FilmController
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
final class FilmControllerTest {

    private static final ObjectMapper objectMapper = ControllerTestHelper.getObjectMapper();

    private static WebTestClient webClient;

    @BeforeEach
    void beforeEach() {
        webClient = WebTestClient.bindToController(FilmController.class)
                .configureClient()
                .baseUrl("/films")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @AfterEach
    void afterEach() {
        webClient = null;
    }

    /**
     * Корректно добавляется один фильм с валидными данными.
     *
     * @see FilmController#addFilm(Film)
     */
    @Test
    void shouldCorrectlyAddAndRetrieveFilms() throws IOException {
        final FilmModelNoId expectedFilm = new FilmModelNoId("Shakeita Begin",
                "Dose term games jazz factors cartridge prayer, entry notebook.",
                LocalDate.of(1995, 5, 4), 271);

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(expectedFilm))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FilmModelNoId.class).isEqualTo(expectedFilm);
    }

    /**
     * Корректно возвращаются несколько фильмов.
     *
     * @see FilmController#getAllFilms()
     */
    @Test
    void shouldCorrectlyRetrieveFilms() throws IOException {
        final FilmModelNoId film1 = new FilmModelNoId("Quentina Nyberg Dierdre",
                "Name realty teaches unexpected.",
                LocalDate.of(2001, 12, 4), 14);
        final FilmModelNoId film2 = new FilmModelNoId("Shaneaka Tisha Vangorder Chaise Spector",
                "Holiday bible causing sig oxford hunt kilometers.",
                LocalDate.of(1993, 4, 17), 121);

        final List<FilmModelNoId> filmsToAdd = List.of(film1, film2);

        for (final FilmModelNoId film : filmsToAdd) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(film))
                    .exchange()
                    .expectStatus().isOk();
        }

        webClient.get()
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<FilmModelNoId>>() {
                })
                .isEqualTo(filmsToAdd);
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
        // 200 знаков description можно
        final String description200 = "a".repeat(200);
        final FilmModelNoId film1 = new FilmModelNoId("Graylon Hilliard", description200,
                LocalDate.of(2001, 12, 4), 14);

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FilmModelNoId.class)
                .isEqualTo(film1);

        // 201 или больше уже нельзя
        final FilmModelNoId film2 = new FilmModelNoId("Dakota Claassen", "a".repeat(201),
                LocalDate.of(1901, 11, 19), 94);

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film2))
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
        // name is null (invalid)
        final FilmModelNoId film1 = new FilmModelNoId(null,
                "Autos mean cardiff camps buffalo management usd, enquiry amd monroe.",
                LocalDate.of(2001, 12, 4), 14);

        // name is blank (invalid)
        final FilmModelNoId film2 = new FilmModelNoId("  \n",
                "Occur merit toll europe customers funds the, memorabilia moss yourself vernon demonstrated.",
                LocalDate.of(1993, 4, 17), 121);

        for (final var film : List.of(film1, film2)) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(film))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody().isEmpty();
        }
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
        // null (invalid)
        final FilmModelNoId film1 = new FilmModelNoId("film1_description", "film1_description",
                LocalDate.of(2001, 12, 4), null);

        // отрицательное значение (invalid)
        final FilmModelNoId film2 = new FilmModelNoId("film2_description", "film2_description",
                LocalDate.of(2001, 12, 4), -10);

        // zero (invalid)
        final FilmModelNoId film3 = new FilmModelNoId("film3_description", "film3_description",
                LocalDate.of(2001, 12, 4), 0);

        // duration в виде вещественного числа (valid)
        final FilmModelNoIdWFloatPointDuration film4 = new FilmModelNoIdWFloatPointDuration("film4_description",
                "film4_description", LocalDate.of(2001, 12, 4), 60.7);

        // пограничный случай, duration == 1 (valid)
        final FilmModelNoId film5 = new FilmModelNoId("film5_description", "film5_description",
                LocalDate.of(2001, 12, 4), 1);

        // invalid data
        for (final var film : List.of(film1, film2, film3)) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(film))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody().isEmpty();
        }

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film4))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.duration").isEqualTo(film4.getDuration().intValue());

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film5))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.duration").isEqualTo(film5.getDuration());
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
    void shouldReturn400WhenReleaseDateIsBefore28_12_1895() throws IOException {
        // пограничный случай, 28 декабря 1895 года (valid)
        final FilmModelNoId film1 = new FilmModelNoId("Claudine Mclain"
                , "Dealt always mostly actress technical contacts relate.",
                LocalDate.of(1895, 12, 28), 120);

        // дата в будущем (valid)
        final FilmModelNoId film2 = new FilmModelNoId("Yentel Muhammad",
                "Fireplace gratis requesting necklace compact franklin entry, trips jonathan logan proteins.",
                LocalDate.now().plusDays(10), 220);

        // раньше 28 декабря 1895 (invalid)
        final FilmModelNoId film3 = new FilmModelNoId("Caryn Rogalski",
                "Cry suites humanitarian spouse televisions combinations thomson, core narrow compatibility.",
                LocalDate.of(1895, 12, 27), 320);

        // valid models
        for (final FilmModelNoId film : List.of(film1, film2)) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(film))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FilmModelNoId.class)
                    .isEqualTo(film);
        }

        // invalid value
        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film3))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    /**
     * Должен возвращать 500 status code при попытке обновить фильм, который не был добавлен.
     *
     * @see FilmController#updateFilm(Film)
     */
    @Test
    void shouldReturn400WhenUpdatingNonExistentFilm() throws IOException {
        // будет добавлен
        final FilmModelNoId film1 = new FilmModelNoId("Daphane Storey",
                "Navigator removing spider counted francis mistakes hi, leaf wifi cubic fioricet val haven.",
                LocalDate.of(1995, 12, 28), 120);

        // добавляем фильм
        final FilmModelWId createdFilm = webClient.post()
                .bodyValue(objectMapper.writeValueAsString(film1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(FilmModelWId.class)
                .returnResult().getResponseBody();

        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());

        // не будет добавлен, попробуем сразу обновить
        // генерируем несуществующий идентификатор (так как добавлен только 1 фильм, то операция существующего id
        // с любым числом, кроме нейтрального элемента генерирует не существующий id)
        final FilmModelWId film2 = new FilmModelWId(createdFilm.getId() + 121, "Willem Vogler Amon",
                "Trusts girls identifier door exactly traditional compute, correction fonts hunting person.",
                LocalDate.of(1965, 5, 1), 255);

        // обновление фильма с несуществующим идентификатором вызывает ошибку
        webClient.put()
                .bodyValue(objectMapper.writeValueAsString(film2))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().isEmpty();
    }

}
