package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("UserController")
class UserControllerValidationTest {

    @MockBean
    private InMemoryUserStorage inMemoryUserStorage;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    @DisplayName("Должен добавлять пользователя с пустым именем")
    void shouldAddUserWithNullName() {
        User user = new User("drakonhftfg@yandex.ru", "DraKon", null,
                LocalDate.of(2000, 7, 17));
        when(inMemoryUserStorage.createUser(user)).thenReturn(user);

        String response = mockMvc
                .perform(
                        post("/users")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(inMemoryUserStorage).createUser(user);
        assertEquals("Созданный пользователь не совпадает с возвращённым",
                objectMapper.writeValueAsString(user), response);
    }

    @SneakyThrows
    @Test
    @DisplayName("Не должен добавлять пользователя с неверным логином")
    void shouldNotAddUserWithBadLogin() {
        User user = new User("drakonhftfg@yandex.ru", " ", "Timur",
                LocalDate.of(2000, 7, 17));
        when(inMemoryUserStorage.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Должен бросать ValidationException при неккоректной дате рождения")
    void shouldThrow_ValidationException_For_BadDateOfBirthday() {
        User user = new User("drakon_1700@mail.ru", "DraKon", "Timur",
                LocalDate.of(3000, 7, 17));
        when(inMemoryUserStorage.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Не должен добавлять пользователя с пустыми полями")
    void shouldNotAdd_User_With_NullFields() {
        User user = new User(null, "DraKon", "Timur", null);
        when(inMemoryUserStorage.createUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")).andExpect(status().isBadRequest());
    }
}
