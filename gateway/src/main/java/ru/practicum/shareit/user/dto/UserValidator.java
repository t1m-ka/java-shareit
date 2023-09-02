package ru.practicum.shareit.user.dto;

import org.apache.commons.lang3.StringUtils;

public class UserValidator {
    public static boolean validateUserCreation(UserDto userDto) {
        return !StringUtils.isBlank(userDto.getName()) && !StringUtils.isBlank(userDto.getEmail());
    }

    public static boolean validateUserUpdate(UserDto userDto) {
        return !StringUtils.isBlank(userDto.getName()) || !StringUtils.isBlank(userDto.getEmail());
    }
}
