package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private InMemoryFilmStorage filmController;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void afterEach() {
        filmController.getFilms().clear();
    }

    @Test
    void tryToCreateFilmWithEarlyDateBadRequest() throws Exception {
        Film film = new Film("name", RandomString.make(200), DATE_RELEASE.minusDays(1), 1);
        String body = objectMapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertEquals("Указываемая дата релиза не должна быть ранее 28.12.1895 года", result.getResolvedException().getMessage()));
    }

    @Test
    void tryToCreateFilmWithoutNameBadRequest() throws Exception {
        Film film = new Film(" ", RandomString.make(200), DATE_RELEASE, 1);
        String body = objectMapper.writeValueAsString(film);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
