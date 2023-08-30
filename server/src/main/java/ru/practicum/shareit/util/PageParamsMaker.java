package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageParamsMaker {
    private static final int SIZE_BY_DEFAULT = 1000;

    public static Pageable makePageable(Integer from, Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = SIZE_BY_DEFAULT;
        }
        int pageNumber = from / size;
        return PageRequest.of(pageNumber, size);
    }

    public static Pageable makePageableWithSort(Integer from, Integer size, Sort sort) {
        if (from == null && size == null) {
            from = 0;
            size = SIZE_BY_DEFAULT;
        }
        int pageNumber = from / size;
        return PageRequest.of(pageNumber, size, sort);
    }
}
