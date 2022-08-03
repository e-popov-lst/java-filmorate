package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;


    @Test
    @Order(1)
    void findAll() throws Exception {
        String json = "{\"id\": 101," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\": 102," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        MvcResult result = mvc.perform(
                get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(102))
                .andReturn();
    }

    @Test
    void create() throws Exception {
        String json = "{\"id\": 1," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("123@test.com"))
                .andReturn();
    }

    @Test
    void createFailByEmail() throws Exception {
        String json = "{\"id\": 10," +
                "\"email\": \"123test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();

        json = "{\"id\": 10," +
                "\"email\": \"\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void createFailByLogin() throws Exception {
        String json = "{\"id\": 10," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t 123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        MvcResult result = mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();

        json = "{\"id\": 10," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        result = mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void createFailByBirthday() throws Exception {
        String json = "{\"id\": 10," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t 123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2055-05-25\"}";

        MvcResult result = mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void update() throws Exception {
        String json = "{\"id\": 201," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\": 201," +
                "\"email\": \"321@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        MvcResult result = mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("321@test.com"))
                .andReturn();
    }

    @Test
    void updateFailByEmail() throws Exception {
        String json = "{\"id\": 301," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\": 301," +
                "\"email\": \"321test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();

        json = "{\"id\": 301," +
                "\"email\": \"\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void updateFailByLogin() throws Exception {
        String json = "{\"id\": 401," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\": 401," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();

        json = "{\"id\": 401," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t 123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void updateFailByBirthday() throws Exception {
        String json = "{\"id\": 501," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2004-05-22\"}";

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        json = "{\"id\": 501," +
                "\"email\": \"123@test.com\"," +
                "\"login\": \"t123\"," +
                "\"name\": \"John\"," +
                "\"birthday\": \"2055-05-25\"}";

        mvc.perform(
                put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andReturn();
    }
}
