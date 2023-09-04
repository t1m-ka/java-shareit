package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    private final ObjectMapper objectMapper;

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder,
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

    public BookingDto bookItem(long userId, BookingDto requestDto) {
        ResponseEntity<Object> responseEntity = post("", userId, requestDto);
        return objectMapper.convertValue(responseEntity.getBody(), BookingDto.class);
    }

    public BookingDto approveBooking(long bookingId, boolean approved, long ownerId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);
        ResponseEntity<Object> responseEntity = patch("/" + bookingId + "?approved={approved}",
                ownerId,
                parameters,
                null);
        return objectMapper.convertValue(responseEntity.getBody(), BookingDto.class);
    }

    public BookingDto getBooking(long userId, Long bookingId) {
        ResponseEntity<Object> responseEntity = get("/" + bookingId, userId);
        return objectMapper.convertValue(responseEntity.getBody(), BookingDto.class);
    }

    public List<BookingDto> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        ResponseEntity<Object> responseEntity = get("?state={state}&from={from}&size={size}",
                userId,
                parameters);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<List<BookingDto>>() {
        });
    }

    public List<BookingDto> getBookingItemsByOwner(long ownerId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        ResponseEntity<Object> responseEntity = get("/owner?state={state}&from={from}&size={size}",
                ownerId,
                parameters);
        return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<List<BookingDto>>() {
        });
    }
}
