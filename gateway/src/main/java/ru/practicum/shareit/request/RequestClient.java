package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestDtoWithAnswers;

import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.Util.handleExceptionFromServer;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    private final ObjectMapper objectMapper;

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl,
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

    public RequestDto addItemRequest(long requestorId, RequestDto requestDto) {
        ResponseEntity<Object> responseEntity = post("", requestorId, requestDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), RequestDto.class);
    }

    public List<RequestDtoWithAnswers> getUserItemRequestList(long userId) {
        ResponseEntity<Object> responseEntity = get("", userId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
        });
    }

    public List<RequestDtoWithAnswers> getOtherUsersItemRequestList(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        ResponseEntity<Object> responseEntity = get("/all?from={from}&size={size}", userId, parameters);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
        });
    }

    public RequestDtoWithAnswers getItemRequest(long userId, long requestId) {
        ResponseEntity<Object> responseEntity = get("/" + requestId, userId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), RequestDtoWithAnswers.class);
    }
}
