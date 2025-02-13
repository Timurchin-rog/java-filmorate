package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@DisplayName("FilmController")
class FilmControllerValidationTest {

    @MockBean
    private FilmController filmController;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @DisplayName("Должен бросать ValidationException при слишком ранней дате релиза")
    void shouldNotAddFilmWithTooEarlyReleaseDate() {
        try {
            Film film = new Film(1L, "Призрак оперы", "Романтика/мюзикл",
                    LocalDate.of(1000, 12, 9), 143);
            when(filmController.create(film)).thenReturn(film);

            mockMvc.perform(post("/films"));
        } catch (ValidationException exception) {
            assertNotNull(exception.getMessage(), "Сообщение ValidationException - пустое");
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Не должен добавлять фильм с отрицательной продолжительностью")
    void shouldNotAddFilmWithNegativeDuration() {
        Film film = new Film(1L, "Призрак оперы", "Романтика/мюзикл",
                LocalDate.of(2004, 12, 9), -143);
        when(filmController.create(film)).thenReturn(film);

        mockMvc.perform(post("/films")).andExpect(status().isBadRequest());
    }
}
