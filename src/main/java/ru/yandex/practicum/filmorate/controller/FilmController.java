package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dao.FilmStorage;
import ru.yandex.practicum.filmorate.group.UpdateGroup;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {

    FilmStorage filmStorage;

    @GetMapping("/films/{id}")
    public Film getById(@PathVariable @Positive final int id) {
        return filmStorage.getById(id);
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody final Film film) {
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Validated(UpdateGroup.class) @Valid @RequestBody final Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable @Positive final int id, @PathVariable @Positive final int userId) {
        filmStorage.putLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable @Positive final int id, @PathVariable @Positive final int userId) {
        filmStorage.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getBestFilm(@RequestParam(defaultValue = "10") @Positive final int count) {
        return filmStorage.getBestFilm(count);
    }
}