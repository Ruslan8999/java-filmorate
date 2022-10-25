package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;
    @Min(1)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private int duration;
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonProperty("mpa")
    private Mpa mpa;
    private Integer rate;
    private Set<Integer> likes = new HashSet<>();
    private List<Genre> genres;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    @Builder
    public Film(int id, @NonNull String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.mpa = mpa;
    }

    public void addLike(int filmId){
        likes.add(filmId);
    }

    public void removeLike(int userId){
        likes.remove(userId);
    }
}
