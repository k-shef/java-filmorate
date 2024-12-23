package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.group.UpdateGroup;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDTO> findAll() {
        log.info("Запрос на получение списка пользователей");
        if (userStorage.getUsers().isEmpty()) {
            return Collections.emptyList();
        }
        return userStorage.findAll();
    }

    public UserDTO create(@Valid @RequestBody User user) {
        validLogin(user.getLogin());
        UserDTO userDTO = userStorage.create(user);
        log.info("Пользователь успешно добавлен под id {}", user.getId());
        return userDTO;
    }

    public UserDTO update(@Validated(UpdateGroup.class) @RequestBody User user) {
        validId(user.getId());
        validLogin(user.getLogin());
        UserDTO userDTO = userStorage.update(user);
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return userDTO;
    }

    public UserDTO addNewFriend(Long id, Long friendId) {
        validUserEqualsFriend(id, friendId, "Нельзя добавить пользователя в друзья к самому себе");
        log.info("Пользователь с id {} успешно добавлен в друзья к пользователю с id {}", friendId, id);
        return userStorage.addNewFriend(id, friendId);
    }

    public UserDTO deleteFriend(Long id, Long friendId) {
        validUserEqualsFriend(id, friendId, "Нельзя удалить пользователя из друзей у самого себя");
        log.info("Пользователь с id {} успешно удален из друзей у пользователя с id {}", friendId, id);
        return userStorage.deleteFriend(id, friendId);
    }

    public List<UserDTO> getAllFriends(Long id) {
        validId(id);
        if (Objects.isNull(userStorage.getUsers().get(id).getFriends())) {
            return Collections.emptyList();
        }
        log.info("Запрос на получение всех друзей пользователя с id {}", id);
        return userStorage.getAllFriends(id);

    }

    public List<UserDTO> getMutualFriends(Long id, Long otherId) {
        validUserEqualsFriend(id, otherId, "Нельзя проверять соответствие друзей у себя и себя");
        List<UserDTO> friends = userStorage.getMutualFriends(id, otherId);
        log.info("Общие друзья пользователь с id {} и пользователя с id {}", otherId, id);
        return friends;
    }

    private void validLogin(String login) {
        if (login.contains(" ")) {
            log.error("Логин пользователя не должен содержать пробелы");
            throw new ValidationException("Логин пользователя не должен содержать пробелы");
        }
    }

    private void validUserEqualsFriend(Long id, Long friendId, String message) {
        validId(id);
        validId(friendId);
        if (Objects.equals(id, friendId)) {
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    private void validId(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.warn("Пользователя с id = {} нет.", id);
            throw new NotFoundException("Пользователя с id = {} нет." + id);
        }
    }
}
