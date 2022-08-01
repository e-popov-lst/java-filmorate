package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Rating {
    @EqualsAndHashCode.Include
    private final Long id;

    @NotBlank(message = "Значение рейтинга не может быть пустым.")
    private final String name;

    public Rating(Long id, @NotBlank(message = "Значение рейтинга не может быть пустым.") String name) {
        this.id = id;
        this.name = name;
    }
}
