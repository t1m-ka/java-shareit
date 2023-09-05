package ru.practicum.shareit.util.exception;

import lombok.Generated;

@Generated
public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
