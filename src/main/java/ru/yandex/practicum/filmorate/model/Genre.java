package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Genre {
    @EqualsAndHashCode.Include
    private final Long id;

    @NotBlank(message = "Название жанра не может быть пустым.")
    private final String name;

    public Genre(Long id, @NotBlank(message = "Название жанра не может быть пустым.") String name) {
        this.id = id;
        this.name = name;
    }
}
