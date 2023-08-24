package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;

    private List<BookingDto> bookingDtoList;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ItemDto itemDto = new ItemDto(
                1L,
                "thing",
                "very important thing",
                true,
                1L
        );

        bookingDto = new BookingDto(
                1L,
                1L,
                "2024-10-10T12:00:00",
                "2024-10-11T14:00:00",
                BookingStatus.WAITING,
                new UserDto(1L, "booker", "booker@book.ru"),
                itemDto
        );

        bookingDtoList = Collections.singletonList(bookingDto);
    }

    @Test
    void bookItem() throws Exception {
        when(bookingService.bookItem(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName()), String.class));
    }

    @Test
    void bookItemWithoutHeaderShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.bookItem(bookingDto, null));

        assertEquals("Отсутствует обязательный заголовок запроса", exception.getMessage());
    }

    @Test
    void bookItemWithWrongBookingDataShouldThrowException() {
        bookingDto.setStart(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.bookItem(bookingDto, 1L));

        assertEquals("Ошибка входных данных", exception.getMessage());
    }

    @Test
    void approveBooking() throws Exception {
        BookingDto approvedBooking = bookingDto;
        approvedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(any(), anyBoolean(), anyLong()))
                .thenReturn(approvedBooking);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approvedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(approvedBooking.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(approvedBooking.getStatus().name()), String.class))
                .andExpect(jsonPath("$.booker.id", is(approvedBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(approvedBooking.getItem().getName()), String.class));
    }

    @Test
    void getBookingInfoByBookingId() throws Exception {
        when(bookingService.getBookingInfoByBookingId(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName()), String.class));
    }

    @Test
    void getBookingUserListByState() throws Exception {
        when(bookingService.getBookingUserListByState(any(), anyLong(), any(), any()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "")
                        .param("size", "")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDtoList.get(0).getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoList.get(0).getStatus().name()), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoList.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoList.get(0).getItem().getName()), String.class));
    }

    @Test
    void getBookingUserListByStateWithWrongParamsShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getBookingUserListByState("ALL", 1L, null, 1));

        assertEquals("Неверно указаны параметры пагинации", exception.getMessage());
    }

    @Test
    void getBookingItemsByOwner() throws Exception {
        when(bookingService.getBookingItemsByOwner(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDtoList.get(0).getItemId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoList.get(0).getStatus().name()), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoList.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtoList.get(0).getItem().getName()), String.class));
    }

    @Test
    void getBookingItemsByOwnerWithWrongParamsShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> controller.getBookingItemsByOwner("ALL", 1L, 0, null));

        assertEquals("Неверно указаны параметры пагинации", exception.getMessage());
    }
}
