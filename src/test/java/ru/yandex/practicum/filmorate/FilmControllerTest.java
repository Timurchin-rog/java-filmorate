package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@DisplayName("FilmController")
class FilmControllerTest {

    @MockBean
    private InMemoryFilmStorage inMemoryFilmStorage;
    @MockBean
    private FilmService filmService;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @DisplayName("Должен бросать ValidationException при слишком ранней дате релиза")
    void shouldThrow_ValidationException_For_TooEarlyReleaseDate() {
        Film film = new Film("Призрак оперы", "Романтика/мюзикл",
                LocalDate.of(1000, 12, 9), 143);
        when(inMemoryFilmStorage.createFilm(film)).thenReturn(film);

        mockMvc.perform(post("/films")).andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Не должен добавлять фильм с отрицательной продолжительностью")
    void shouldNotAdd_Film_With_NegativeDuration() {
        Film film = new Film("Призрак оперы", "Романтика/мюзикл",
                LocalDate.of(2004, 12, 9), -143);
        when(inMemoryFilmStorage.createFilm(film)).thenReturn(film);

        mockMvc.perform(post("/films")).andExpect(status().isBadRequest());
    }
}
