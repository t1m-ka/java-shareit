package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.util.Util.handleExceptionFromServer;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    private final ObjectMapper objectMapper;

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl,
            RestTemplateBuilder builder,
            ObjectMapper objectMapper) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper = objectMapper;
    }

    public UserDto addUser(UserDto userDto) {
        ResponseEntity<Object> responseEntity = post("", userDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), UserDto.class);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        ResponseEntity<Object> responseEntity = patch("/" + userId, userDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), UserDto.class);
    }

    public UserDto getUserById(long userId) {
        ResponseEntity<Object> responseEntity =  get("/" + userId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        ResponseEntity<Object> responseEntity = get("");
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
        });
    }

    public ResponseEntity<Object> deleteUserById(long userId) {
        ResponseEntity<Object> responseEntity = delete("/" + userId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return responseEntity;
    }
}
