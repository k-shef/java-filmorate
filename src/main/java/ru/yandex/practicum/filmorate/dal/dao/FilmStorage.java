package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void putLike(int id, int userId);

    void deleteLike(int id, int userId);

    Film getById(int id);

    List<Film> getBestFilm(int count);
}
