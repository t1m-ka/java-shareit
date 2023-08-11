package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @PostMapping
    public void bookItem() {

    }

    @PatchMapping("/{bookingId}")
    public void approveBooking() {

    }

    @GetMapping("/{bookingId}")
    public void getBookingInfoByItem() {

    }

    @GetMapping
    public void getBookingInfoByState(@RequestParam String state) {
        //sort by date

    }

    @GetMapping("/owner")
    public void  getBookingInfoByOwner(@RequestParam String state) {

    }
}
