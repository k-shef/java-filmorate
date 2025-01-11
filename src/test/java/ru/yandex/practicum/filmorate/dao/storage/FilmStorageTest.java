package ru.yandex.practicum.filmorate.dao.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dal.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dal.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Import({UserDbStorage.class})
class FilmStorageTest {

    @Autowired
    FilmStorage filmStorage;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should find all films")
    void findAllTest() {
        Collection<Film> films = filmStorage.findAll();

        assertThat(films)
                .isNotEmpty()
                .hasSize(7)
                .satisfies(filmList -> {
                    Film firstFilm = filmList.iterator().next();
                    assertThat(firstFilm)
                            .hasFieldOrPropertyWithValue("id", 1)
                            .hasFieldOrPropertyWithValue("name", "филосовский камень");
                });
    }

    @Test
    @DisplayName("Should create new film")
    void createTest() {
        Film newFilm = new Film();
        newFilm.setName("проклятое дитя");
        newFilm.setDescription("description8");
        newFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        newFilm.setDuration(128);
        newFilm.setMpa(new Mpa(1, null));

        Film createFilm = filmStorage.create(newFilm);

        assertThat(createFilm).hasFieldOrPropertyWithValue("id", 8);
        assertThat(createFilm).hasFieldOrPropertyWithValue("name", "проклятое дитя");
        assertThat(createFilm).hasFieldOrPropertyWithValue("description", "description8");
        assertThat(createFilm).hasFieldOrPropertyWithValue("duration", 128);
        assertThat(createFilm).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(2022, 1, 1));
        assertThat(createFilm.getMpa()).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @DisplayName("FilmRepository_should_not_update")
    void updateTest() {
        Film newFilm = new Film();
        newFilm.setId(8);
        newFilm.setName("фантастические твари");
        newFilm.setDescription("description8");
        newFilm.setReleaseDate(LocalDate.of(2001, 11, 22));
        newFilm.setDuration(121);
        newFilm.setMpa(new Mpa(1, null));

        assertThatThrownBy(() -> filmStorage.update(newFilm))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Should get all films")
    void getAllIdTest() {
        Collection<Film> films = filmStorage.findAll();
        Collection<Integer> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        assertThat(ids)
                .isNotEmpty()
                .hasSize(7)
                .contains(1, 2, 3, 4, 5, 6, 7);
    }

    @Nested
    @DisplayName("Get Film Tests")
    class GetFilmTests {
        @Test
        @DisplayName("Should get film by ID")
        void getByIdTest() {
            Film film = filmStorage.getById(1);

            assertThat(film).hasFieldOrPropertyWithValue("id", 1);
            assertThat(film).hasFieldOrPropertyWithValue("name", "филосовский камень");
            assertThat(film).hasFieldOrPropertyWithValue("description", "description1");
            assertThat(film).hasFieldOrPropertyWithValue("duration", 121);
            assertThat(film).hasFieldOrPropertyWithValue("releaseDate",
                    LocalDate.of(2001, 11, 22));
            assertThat(film.getMpa()).hasFieldOrPropertyWithValue("id", 1);
            assertThat(film.getGenres()).hasSize(1);
            assertThat(film.getGenres()).element(0)
                    .hasFieldOrPropertyWithValue("id", 1);
        }

        @Test
        @DisplayName("Should throw exception when film not found")
        void shouldThrowExceptionWhenFilmNotFound() {
            assertThatThrownBy(() -> filmStorage.getById(999))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Like Operations Tests")
    class LikeOperationsTests {
        @Test
        @DisplayName("Should put like to film")
        void putLikeTest() {
            filmStorage.putLike(1, 1);

            Collection<Film> bestFilms = filmStorage.getBestFilm(1);
            assertThat(bestFilms)
                    .isNotEmpty()
                    .anySatisfy(film ->
                            assertThat(film).hasFieldOrPropertyWithValue("id", 1));
        }

        @Test
        @DisplayName("Should delete like from film")
        void deleteLikeTest() {
            // Сначала ставим лайк
            filmStorage.putLike(1, 1);

            // Затем удаляем
            filmStorage.deleteLike(1, 1);

            Collection<Film> bestFilms = filmStorage.getBestFilm(1);
            assertThat(bestFilms).isEmpty();
        }
    }
}