package com.atu.WednesdaysWickedAdventures.controller;

import com.atu.WednesdaysWickedAdventures.model.Booking;
import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.service.BookingService;
import com.atu.WednesdaysWickedAdventures.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingControllerTest {
	
    private static final Logger logger = LoggerFactory.getLogger(BookingControllerTest.class);


    @Mock
    private BookingService bookingService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllBookings_ReturnsOkWithBookings() throws Exception {
        List<Booking> bookings = List.of(
                new Booking("John Doe", "john.doe@example.com", "event1", 5),
                new Booking("Jane Doe", "jane.doe@example.com", "event2", 3)
        );
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[1].customerName").value("Jane Doe"));
        logger.info("getAllBookings_ReturnsOkWithBookings passed.");
    }

    @Test
    void getBookingById_ReturnsOkWithBookingAndEvent() throws Exception {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        booking.setId("booking1");
        Event event = new Event("Concert", "Venue A", LocalDateTime.now(), 100);
        event.setId("event1");
        when(bookingService.getBookingById("booking1")).thenReturn(Optional.of(booking));
        when(eventService.getEventById("event1")).thenReturn(Optional.of(event));

        mockMvc.perform(get("/v1/bookings/booking1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking.customerName").value("John Doe"))
                .andExpect(jsonPath("$.event.name").value("Concert"));
        logger.info("getBookingById_ReturnsOkWithBookingAndEvent passed.");
    }

    @Test
    void getBookingById_ReturnsNotFound_WhenBookingNotFound() throws Exception {
        when(bookingService.getBookingById("nonexistentBooking")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/bookings/nonexistentBooking"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found."));
        logger.warn("getBookingById_ReturnsNotFound_WhenBookingNotFound passed.");
    }

    @Test
    void getBookingById_ReturnsNotFound_WhenEventNotFound() throws Exception {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "nonexistentEvent", 5);
        booking.setId("booking1");
        when(bookingService.getBookingById("booking1")).thenReturn(Optional.of(booking));
        when(eventService.getEventById("nonexistentEvent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/bookings/booking1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found for booking."));
        logger.warn("getBookingById_ReturnsNotFound_WhenEventNotFound passed.");
    }

    @Test
    void createBooking_ReturnsCreated_WhenValidBooking() throws Exception {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
        logger.warn("createBooking_ReturnsCreated_WhenValidBooking passed.");
    }

    @Test
    void createBooking_ReturnsBadRequest_WhenInvalidBooking() throws Exception {
        mockMvc.perform(post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Booking("", "", "", 0))))
                .andExpect(status().isBadRequest());
        logger.warn("createBooking_ReturnsBadRequest_WhenInvalidBooking passed.");
    }

    @Test
    void createBooking_ReturnsBadRequest_WhenServiceFails() throws Exception {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        when(bookingService.createBooking(any(Booking.class))).thenReturn(null);

        mockMvc.perform(post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough tickets available or Event not found."));
        logger.warn("createBooking_ReturnsBadRequest_WhenServiceFails passed.");
    }

    @Test
    void updateBooking_ReturnsOk_WhenValidBooking() throws Exception {
        Booking booking = new Booking("Jane Doe", "jane.doe@example.com", "event1", 8);
        when(bookingService.updateBooking(eq("booking1"), any(Booking.class))).thenReturn(booking);

        mockMvc.perform(put("/v1/bookings/booking1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Doe"));
        logger.warn("pdateBooking_ReturnsOk_WhenValidBooking passed.");
    }

    @Test
    void updateBooking_ReturnsBadRequest_WhenInvalidBooking() throws Exception {
        mockMvc.perform(put("/v1/bookings/booking1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Booking("", "", "", 0))))
                .andExpect(status().isBadRequest());
        logger.warn("updateBooking_ReturnsBadRequest_WhenInvalidBooking passed.");
    }

    @Test
    void updateBooking_ReturnsBadRequest_WhenServiceFails() throws Exception {
        Booking booking = new Booking("Jane Doe", "jane.doe@example.com", "event1", 8);
        when(bookingService.updateBooking(eq("booking1"), any(Booking.class))).thenReturn(null);

        mockMvc.perform(put("/v1/bookings/booking1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough tickets available or Event/Booking not found."));
        logger.warn("updateBooking_ReturnsBadRequest_WhenServiceFails passed.");
    }

    @Test
    void deleteBooking_ReturnsNoContent_WhenBookingDeleted() throws Exception {
        when(bookingService.deleteBooking("booking1")).thenReturn(true);

        mockMvc.perform(delete("/v1/bookings/booking1"))
                .andExpect(status().isNoContent());
        logger.warn("deleteBooking_ReturnsNoContent_WhenBookingDeleted passed.");
    }

    @Test
    void deleteBooking_ReturnsNotFound_WhenBookingNotFound() throws Exception {
        when(bookingService.deleteBooking("nonexistentBooking")).thenReturn(false);

        mockMvc.perform(delete("/v1/bookings/nonexistentBooking"))
                .andExpect(status().isNotFound());
        logger.warn("deleteBooking_ReturnsNotFound_WhenBookingNotFound passed.");
    }
}