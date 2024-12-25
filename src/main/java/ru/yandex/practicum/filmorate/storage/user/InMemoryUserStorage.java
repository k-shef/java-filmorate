package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.mapper.ConvertUsers;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final ConvertUsers convertUsers;

    public InMemoryUserStorage(ConvertUsers convertUsers) {
        this.convertUsers = convertUsers;
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserDTO> allUserDTO = new ArrayList<>();
        users.values().forEach(user -> allUserDTO.add(convertUsers.getDTO(user)));
        return allUserDTO;
    }

    @Override
    public UserDTO create(User user) {
        if (Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return convertUsers.getDTO(user);
    }

    @Override
    public UserDTO update(User user) {
        users.put(user.getId(), user);
        return convertUsers.getDTO(user);
    }

    @Override
    public UserDTO addNewFriend(Long id, Long friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        return convertUsers.getDTO(users.get(id));
    }

    @Override
    public UserDTO deleteFriend(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
        return convertUsers.getDTO(users.get(id));
    }

    @Override
    public List<UserDTO> getAllFriends(Long id) {
        return users.get(id).getFriends().stream().map(users::get).map(convertUsers::getDTO).toList();
    }

    @Override
    public List<UserDTO> getMutualFriends(Long id, Long otherId) {
        Set<Long> user = users.get(id).getFriends();
        Set<Long> other = users.get(otherId).getFriends();
        Set<Long> mutualFriendTds = user.stream().filter(other::contains).collect(Collectors.toSet());
        if (mutualFriendTds.isEmpty()) {
            return Collections.emptyList();
        }
        return mutualFriendTds.stream().map(users::get).map(convertUsers::getDTO).toList();
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

}