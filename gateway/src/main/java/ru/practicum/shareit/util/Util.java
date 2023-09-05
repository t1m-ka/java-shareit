package ru.practicum.shareit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.util.exception.EntityNotFoundException;
import ru.practicum.shareit.util.exception.UnexpectedServerResponseException;
import ru.practicum.shareit.util.exception.UserAlreadyExistsException;

public class Util {
    public static void handleExceptionFromServer(ResponseEntity<Object> responseEntity, ObjectMapper objectMapper) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree((String) responseEntity.getBody());
        } catch (JsonProcessingException e) {
            throw new UnexpectedServerResponseException("Request processing error");
        }
        String errorText = jsonNode.get("error").asText();

        switch (responseEntity.getStatusCode().value()) {
            case 400:
                throw new IllegalArgumentException(errorText);
            case 403:
                throw new UserAlreadyExistsException(errorText);
            case 404:
                throw new EntityNotFoundException(errorText);
            default:
                throw new UnexpectedServerResponseException("Request processing error");
        }
    }
}