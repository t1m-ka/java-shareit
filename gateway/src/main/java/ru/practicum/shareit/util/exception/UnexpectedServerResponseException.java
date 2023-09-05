package ru.practicum.shareit.util.exception;

public class UnexpectedServerResponseException extends RuntimeException {
    public UnexpectedServerResponseException(String message) {
        super(message);
    }
}
