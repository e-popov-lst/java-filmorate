package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @Order(1)
    void findAll() throws Exception {

        String json = "{\"id\":101,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\":102,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        MvcResult result = mvc.perform(
                get("/films"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(102))
                .andReturn();
    }

    @Test
    void create() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";

        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test film"))
                .andReturn();
    }

    @Test
    void createFailByName() throws Exception {
        String json = "{\"id\":10,\"name\":\"\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void createFailByDescription() throws Exception {
        String json = "{\"id\":10,\"name\":\"Test film\",\"description\":\"Описание" + "1234567890".repeat(20) + "\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void createFailByReleaseDate() throws Exception {
        final String json = "{\"id\":10,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"1894-05-22\",\"duration\":90}";

        final NestedServletException exception = assertThrows(
                // класс ошибки
                NestedServletException.class,

                // создание и переопределение экземпляра класса Executable
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        mvc.perform(
                                post("/films")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                                .andExpect(status().is(400))
                                .andReturn();
                    }
                });

        assertTrue(exception.getMessage().contains("Дата релиза - не раньше 28 декабря 1895 г."));
    }

    @Test
    void createFailByDuration() throws Exception {
        String json = "{\"id\":10,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2005-05-22\",\"duration\":-90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void update() throws Exception {
        String json = "{\"id\":201,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\":201,\"name\":\"Test film777\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test film777"))
                .andReturn();
    }

    @Test
    void updateFailByName() throws Exception {
        String json = "{\"id\":301,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\":301,\"name\":\"\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void updateFailByDescription() throws Exception {
        String json = "{\"id\":401,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\":10,\"name\":\"Test film\",\"description\":\"Описание" + "1234567890".repeat(20) + "\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void updateFailByReleaseDate() throws Exception {
        String json = "{\"id\":501,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        final NestedServletException exception = assertThrows(
                // класс ошибки
                NestedServletException.class,

                // создание и переопределение экземпляра класса Executable
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        String json = "{\"id\":501,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"1894-05-22\",\"duration\":90}";
                        mvc.perform(
                                put("/films")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json))
                                .andExpect(status().is(400))
                                .andReturn();
                    }
                });

        assertTrue(exception.getMessage().contains("Дата релиза - не раньше 28 декабря 1895 г."));
    }

    @Test
    void updateFailByDuration() throws Exception {
        String json = "{\"id\":601,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":90}";
        mvc.perform(
                post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\":601,\"name\":\"Test film\",\"description\":\"Описание\",\"releaseDate\":\"2004-05-22\",\"duration\":-90}";
        mvc.perform(
                put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

}