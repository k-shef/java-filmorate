package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {

    boolean existsById(Long id);

    Map<Long, Film> films = new HashMap<>();

    List<FilmDTO> findAll();

    FilmDTO create(Film film);

    FilmDTO update(Film film);

    FilmDTO putLike(Long id, Long userId);

    FilmDTO deleteLike(Long id, Long userId);

    Collection<FilmDTO> getBestFilms(Long count);

    Map<Long, Film> getFilms();
}
