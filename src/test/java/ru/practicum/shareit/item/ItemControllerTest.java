package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;

    private ItemDtoWithBookingAndComments itemDtoWithBookingAndComments;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        itemDto = new ItemDto(
                1L,
                "thing",
                "very important thing",
                true,
                1L
        );

        BookingDto lastBooking = new BookingDto(
                1L,
                1L,
                "2023-08-08T12:00:00",
                "2023-08-08T14:00:00",
                BookingStatus.APPROVED,
                new UserDto(1L, "booker", "booker@book.ru"),
                itemDto
        );

        BookingDto nextBooking = new BookingDto(
                2L,
                1L,
                "2023-08-28T12:00:00",
                "2023-08-28T14:00:00",
                BookingStatus.APPROVED,
                new UserDto(1L, "booker", "booker@book.ru"),
                itemDto
        );

        List<CommentDto> commentDtoList = Collections.singletonList(
                new CommentDto(1L, "", "user", "2023-08-18T14:00:00"));

        itemDtoWithBookingAndComments = new ItemDtoWithBookingAndComments(
                2L,
                "thing2",
                "very important thing2",
                true,
                2L,
                lastBooking,
                nextBooking,
                commentDtoList
        );
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void addItemWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.addItem(itemDto, null));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void addItemWithWrongItemDtoShouldThrowException() {
        itemDto.setName("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.addItem(itemDto, 1L));

        assertEquals("Отсутствуют обязательные поля", exception.getMessage());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void updateItemWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.updateItem(itemDto, 1L, null));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

    @Test
    void updateItemWithWrongItemDtoShouldThrowException() {
        itemDto.setName(null);
        itemDto.setDescription(null);
        itemDto.setAvailable(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.updateItem(itemDto, 1L, 1L));

        assertEquals("Отсутствуют обязательные поля", exception.getMessage());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBookingAndComments);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookingAndComments.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookingAndComments.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoWithBookingAndComments.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookingAndComments.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoWithBookingAndComments.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.id", is(1L), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(2L), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)));
    }

    @Test
    void getItemByIdWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getItemById(1L, null));

        assertEquals("Отсутствует параметр запроса", exception.getMessage());
    }

}
