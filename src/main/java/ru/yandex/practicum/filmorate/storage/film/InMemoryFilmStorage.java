package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ConvertFilms;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final ConvertFilms convertFilms;
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    public InMemoryFilmStorage(ConvertFilms convertFilms) {
        this.convertFilms = convertFilms;
    }

    @Override
    public List<FilmDTO> findAll() {
        return films.values().stream()
                .map(convertFilms::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDTO create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return convertFilms.getDTO(film);
    }

    @Override
    public FilmDTO update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
        return convertFilms.getDTO(film);
    }

    @Override
    public FilmDTO putLike(Long id, Long userId) {
        Film film = Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден."));
        film.getLikes().add(userId);
        return convertFilms.getDTO(film);
    }

    @Override
    public FilmDTO deleteLike(Long id, Long userId) {
        Film film = Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден."));
        film.getLikes().remove(userId);
        return convertFilms.getDTO(film);
    }

    @Override
    public Collection<FilmDTO> getBestFilms(Long count) {
        return films.values().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .map(convertFilms::getDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    private Long getNextId() {
        return ++currentId;
    }

    @Override
    public boolean existsById(Long id) {
        return films.containsKey(id);
    }
}

