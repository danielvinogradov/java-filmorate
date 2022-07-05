package ru.yandex.practicum.filmorate.controller.usercontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.filmorate.controller.usercontroller.model.UserModelNoId;
import ru.yandex.practicum.filmorate.controller.usercontroller.model.UserModelWId;
import ru.yandex.practicum.filmorate.model.User;
import util.ControllerTestHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты для {@link UserController} и валидации данных в {@link User}.
 *
 * @see User
 * @see UserController
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
final class UserControllerTest {

    private static final ObjectMapper objectMapper = ControllerTestHelper.getObjectMapper();

    private static WebTestClient webClient;

    @BeforeEach
    void beforeEach() {
        webClient = WebTestClient.bindToController(UserController.class)
                .configureClient()
                .baseUrl("/users")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @AfterEach
    void afterEach() {
        webClient = null;
    }

    /**
     * Корректно добавляется новый пользователь с валидными данными.
     *
     * @see UserController#addUser(User)
     */
    @Test
    void shouldCorrectlyAddUser() throws IOException {
        final UserModelNoId user1 = new UserModelNoId("alejandrina_neagle9@pics.whu", "Karey",
                "whereas", LocalDate.of(1992, 7, 24));

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelNoId.class)
                .isEqualTo(user1);
    }

    /**
     * Корректно возвращается список пользователей.
     *
     * @see UserController#getAllUsers()
     */
    @Test
    void shouldCorrectlyAddAndRetrieveMultipleUsers() throws IOException {
        final UserModelNoId user1 = new UserModelNoId("katlyn_tiedemangzab@indiana.vzo", "Nidia Lansford",
                "ladarius7xm", LocalDate.of(1992, 10, 2));
        final UserModelNoId user2 = new UserModelNoId("tomoko_haddoxx@listen.ve", "Mattie Hussey",
                "manpre9etj", LocalDate.of(1987, 8, 10));

        final List<UserModelNoId> expectedUsers = List.of(user1, user2);

        // корректно добавились пользователи
        for (final UserModelNoId userToAdd : expectedUsers) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(userToAdd))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(UserModelNoId.class)
                    .isEqualTo(userToAdd);
        }

        // корректно возвращается список добавленных пользователей
        webClient.get()
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<UserModelNoId>>() {
                })
                .isEqualTo(expectedUsers);
    }

    /**
     * Проверка валидации поля `email`.
     *
     * @see UserController#addUser(User)
     */
    @Test
    void shouldReturn400WhenIncorrectEmail() throws JsonProcessingException {
        final UserModelNoId user1 = new UserModelNoId("wrong-email.com@", "Angeligue Blalock",
                "carilynpsc", LocalDate.of(1972, 1, 12));

        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    /**
     * Проверка валидации поля `login`.
     *
     * <ul>
     *     <li>Только alphanumerical.</li>
     *     <li>Буквы только латинского алфавита.</li>
     * </ul>
     *
     * @see UserController#addUser(User)
     */
    @Test
    void shouldReturn400WhenIncorrectLogin() throws JsonProcessingException {
        // содержит special char
        final UserModelNoId user1 = new UserModelNoId("kalinda_woodfinuevb@accurate.mf", "Wynn Hance",
                "herkc%w", LocalDate.of(1999, 10, 10));

        // содержит кириллические буквы
        final UserModelNoId user2 = new UserModelNoId("larrissa_millingtonq@xbox.ibh", "Latroy Kress",
                "aiтчrano1", LocalDate.of(2000, 8, 1));

        // содержит пробелы
        final UserModelNoId user3 = new UserModelNoId("sherman_gantzz@plastics.vqv", "Glendy Purnell",
                "shamer ahxb", LocalDate.of(1972, 1, 14));

        for (final var user : List.of(user1, user2, user3)) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(user))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody().isEmpty();
        }
    }

    /**
     * Проверка установки поля `name`: если name не передан (или is blank), то в этом поле
     * должно быть значение `login`.
     *
     * @see UserController#addUser(User)
     */
    @Test
    void shouldLoginBeUsedAsNameWhenNoNameIsPresent() throws IOException {
        // name is null
        final UserModelNoId user1 = new UserModelNoId("desteny_verreth@blogger.opu", null,
                "apoloniau", LocalDate.of(1999, 9, 9));

        // name is blank
        final UserModelNoId user2 = new UserModelNoId("joesph_nagyzu1@snow.yyj", "\n  ",
                "kaiserf", LocalDate.of(1999, 9, 9));

        for (final var user : List.of(user1, user2)) {
            webClient.post()
                    .bodyValue(objectMapper.writeValueAsString(user))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name").isEqualTo(user.getLogin());
        }
    }

    /**
     * Проверка валидации поля `birthday`.
     *
     * <ul>
     *     <li>День рождения не может быть в будущем.</li>
     * </ul>
     *
     * @see UserController#addUser(User)
     */
    @Test
    void shouldReturn400WhenBirthdayIsInFuture() throws JsonProcessingException {
        // пограничный случай – день рождения сегодня (формально валидный кейс, ограничений на возраст нет)
        final UserModelNoId user1 = new UserModelNoId("junaid_zackmydi@commons.aik", "Sharief Morrell",
                "leriny", LocalDate.now());

        // день рождения в будущем
        final UserModelNoId user2 = new UserModelNoId("xavion_townevdtv@destiny.ltn", "Chantele Tilford",
                "venessaya8r", LocalDate.now().plusDays(1L));

        // тестируем пограничный случай
        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelNoId.class)
                .isEqualTo(user1);

        // тестируем невалидный случай
        webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user2))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    /**
     * Проверка корректности обновления данных пользователя.
     *
     * @see UserController#updateUser(User)
     */
    @Test
    void shouldCorrectlyUpdateUser() throws IOException {
        final UserModelNoId user1 = new UserModelNoId("johnnylee_corbettc@valentine.zx", "Nitin Naccarato",
                "talliep", LocalDate.now().minusYears(20));

        // создаем нового пользователя и сохраняем данные (понадобится id для обновления)
        final UserModelWId responseBody = webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelWId.class)
                .returnResult().getResponseBody();

        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());

        // создаем model обновленного пользователя
        final UserModelWId expectedUser = new UserModelWId(responseBody.getId(), "geovany_zuehlkeqp@banana.gw", "Velinda Mccaslin",
                "shareai", LocalDate.of(2000, 2, 4));

        // обновляем данные о созданном пользователе
        webClient.put()
                .bodyValue(objectMapper.writeValueAsString(expectedUser))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelWId.class)
                .isEqualTo(expectedUser);
    }

    /**
     * При попытке обновить несуществующего пользователя
     * возвращается {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}.
     *
     * @see UserController#updateUser(User)
     */
    @Test
    void shouldReturn500WhenTryingToUpdateNonExistentUser() throws IOException {
        final UserModelNoId user1 = new UserModelNoId("dane_dorschva@debug.yaf", "Sharese Cameron",
                "tameaai0z", LocalDate.now().minusYears(10));

        // создаем нового пользователя
        final UserModelWId createdUser = webClient.post()
                .bodyValue(objectMapper.writeValueAsString(user1))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModelWId.class).returnResult().getResponseBody();

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());

        // создаем другого пользователя с несуществующим id: в списке пользователей есть только
        // 1 пользователь, значит любая операция с числом, не являющимся identity element создает несуществующий id
        final UserModelWId user2 = new UserModelWId(createdUser.getId() + 100, "destany_luedtkedju@no.qa", "Rasean Aldaco",
                "kamalidp", LocalDate.now().minusMonths(44));

        webClient.put()
                .bodyValue(objectMapper.writeValueAsString(user2))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().isEmpty();
    }

}
