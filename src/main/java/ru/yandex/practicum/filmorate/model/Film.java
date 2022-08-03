package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
public class Film {

    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private static long lastId = 0;

    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма 200 символов.")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private int duration;

    private int rate;

    private Set<Long> likesUserId;

    private Set<Genre> genres;

    @JsonProperty("mpa")
    private Rating ratingMPA;

    public void setId() {
        id = ++lastId;
    }
}
