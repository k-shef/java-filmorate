package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.group.UpdateGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<FilmDTO> findAll() {
        log.info("Запрос на получение списка фильмов");
        return filmService.findAll().stream()
                .map(this::getDTO)
                .toList();
    }

    @PostMapping
    public FilmDTO create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма: {}", film.getName());
        return getDTO(filmService.create(film));
    }

    @PutMapping
    public FilmDTO update(@Validated(UpdateGroup.class) @RequestBody Film film) {
        log.info("Обновление фильма с id {}", film.getId());
        return getDTO(filmService.update(film));
    }

    private FilmDTO getDTO(Film film) {
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setId(film.getId());
        filmDTO.setName(film.getName());
        filmDTO.setDescription(film.getDescription());
        filmDTO.setReleaseDate(film.getReleaseDate());
        filmDTO.setDuration(film.getDuration());
        return filmDTO;
    }
}
