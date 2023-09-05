package ru.practicum.shareit.util;

public class PageParamsValidator {
    public static boolean validatePageableParams(Integer from, Integer size) {
        if (from == null && size == null)
            return true;
        if (from == null || size == null)
            return false;
        if (from < 0 || size < 1)
            return false;
        return true;
    }
}
