package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequestDto requestDto;

    private ItemRequestDto returnedDto;

    private ItemRequestDtoWithAnswers requestDtoWithAnswers;

    private List<ItemRequestDtoWithAnswers> requestDtoWithAnswersList;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        requestDto = new ItemRequestDto("Needed thing");

        returnedDto = new ItemRequestDto(
                1L,
                "Needed thing",
                "2023-08-23T14:00:00"
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "thing",
                "very important thing",
                true,
                1L
        );

        List<ItemDto> itemList = Collections.singletonList(itemDto);
        requestDtoWithAnswers = new ItemRequestDtoWithAnswers(
                1L,
                "Needed thing",
                "2023-08-23T14:00:00",
                itemList
        );

        requestDtoWithAnswersList = Collections.singletonList(requestDtoWithAnswers);
    }

    @Test
    void testCorrectAddItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any()))
                .thenReturn(returnedDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(returnedDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(returnedDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(returnedDto.getCreated()), String.class));
    }

    @Test
    void testAddItemRequestWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.addItemRequest(null, requestDto));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void testAddItemRequestWithoutBodyShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.addItemRequest(1L, null));

        assertEquals("Отсутствуют обязательные поля", exception.getMessage());
    }

    @Test
    void testCorrectGetUserItemRequestList() throws Exception {
        when(itemRequestService.getUserItemRequestList(anyLong()))
                .thenReturn(requestDtoWithAnswersList);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoWithAnswers.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created", is(requestDtoWithAnswers.getCreated()), String.class))
                .andExpect(jsonPath("$[0].items[0].name", is(requestDtoWithAnswers.getItems().get(0).getName()),
                        String.class));
    }

    @Test
    void testGetUserItemRequestListWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getUserItemRequestList(null));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void testCorrectGetOtherUsersItemRequestList() throws Exception {
        when(itemRequestService.getOtherUsersItemRequestList(anyLong(), any(), any()))
                .thenReturn(requestDtoWithAnswersList);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "")
                        .param("size", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDtoWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoWithAnswers.getDescription()), String.class))
                .andExpect(jsonPath("$[0].created", is(requestDtoWithAnswers.getCreated()), String.class))
                .andExpect(jsonPath("$[0].items[0].name", is(requestDtoWithAnswers.getItems().get(0).getName()),
                        String.class));
    }

    @Test
    void testGetOtherUsersItemRequestListWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getOtherUsersItemRequestList(null, 0, 1));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void testGetOtherUsersItemRequestListWithoutParamsShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getOtherUsersItemRequestList(1L, 0, 0));

        assertEquals("Неверно указаны параметры пагинации", exception.getMessage());
    }

    @Test
    void testCorrectGetItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(requestDtoWithAnswers);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoWithAnswers.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(requestDtoWithAnswers.getCreated()), String.class))
                .andExpect(jsonPath("$.items[0].id", is(requestDtoWithAnswers.getItems().get(0).getId()), Long.class));
    }

    @Test
    void testGetItemRequestWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getItemRequest(null, 1L));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }
}
