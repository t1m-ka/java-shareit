package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.util.Util.handleExceptionFromServer;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    private final ObjectMapper objectMapper;

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
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

    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        ResponseEntity<Object> responseEntity = post("", ownerId, itemDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), ItemDto.class);
    }

    public ItemDto updateItem(long itemId, long ownerId, ItemDto itemDto) {
        ResponseEntity<Object> responseEntity = patch("/" + itemId, ownerId, itemDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), ItemDto.class);
    }

    public ItemDtoWithBookingAndComments getItemById(long itemId, long userId) {
        ResponseEntity<Object> responseEntity = get("/" + itemId, userId);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), ItemDtoWithBookingAndComments.class);
    }

    public List<ItemDtoWithBookingAndComments> getOwnerItems(long ownerId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        ResponseEntity<Object> responseEntity = get("?from={from}&size={size}", ownerId, parameters);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
        });
    }

    public List<ItemDto> searchItemsByName(String text, long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        ResponseEntity<Object> responseEntity = get("/search?text={text}&from={from}&size={size}", userId, parameters);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
        });
    }

    public CommentDto addCommentToItem(long itemId, long authorId, CommentDto commentDto) {
        ResponseEntity<Object> responseEntity = post("/" + itemId + "/comment", authorId, commentDto);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            handleExceptionFromServer(responseEntity, objectMapper);
        return objectMapper.convertValue(responseEntity.getBody(), CommentDto.class);
    }
}
