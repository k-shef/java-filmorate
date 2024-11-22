package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}