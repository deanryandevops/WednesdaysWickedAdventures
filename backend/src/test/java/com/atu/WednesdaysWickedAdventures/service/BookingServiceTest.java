package com.atu.WednesdaysWickedAdventures.service;

import com.atu.WednesdaysWickedAdventures.model.Booking;
import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.repository.BookingRepository;
import com.atu.WednesdaysWickedAdventures.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceTest.class);

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("John Doe", "john.doe@example.com", "event1", 5));
        bookings.add(new Booking("Jane Doe", "jane.doe@example.com", "event2", 3));

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());
        logger.info("testGetAllBookings passed.");
    }

    @Test
    void testGetBookingById() {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        booking.setId("booking1");

        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.getBookingById("booking1");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getCustomerName());
        logger.info("testGetBookingById passed.");
    }

    @Test
    void testCreateBookingSuccess() {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        Event event = new Event("Concert", "Venue A", null, 10);
        event.setId("event1");

        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(eventRepository.save(event)).thenReturn(event);

        Booking result = bookingService.createBooking(booking);

        assertNotNull(result);
        assertEquals(5, event.getAvailableTickets());
        verify(eventRepository, times(1)).save(event);
        verify(bookingRepository, times(1)).save(booking);
        logger.info("testCreateBookingSuccess passed.");
    }

    @Test
    void testCreateBookingEventNotFound() {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "nonexistentEvent", 5);

        when(eventRepository.findById("nonexistentEvent")).thenReturn(Optional.empty());

        Booking result = bookingService.createBooking(booking);

        assertNull(result);
        logger.warn("testCreateBookingEventNotFound passed, as expected, returned null.");
    }

    @Test
    void testCreateBookingInsufficientTickets() {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 15);
        Event event = new Event("Concert", "Venue A", null, 10);
        event.setId("event1");

        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));

        Booking result = bookingService.createBooking(booking);

        assertNull(result);
        logger.warn("testCreateBookingInsufficientTickets passed, as expected, returned null.");
    }

    @Test
    void testUpdateBookingSuccess() {
        Booking originalBooking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        originalBooking.setId("booking1");
        Booking updatedBooking = new Booking("Jane Doe", "jane.doe@example.com", "event1", 8);
        Event event = new Event("Concert", "Venue A", null, 10);
        event.setId("event1");

        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(originalBooking));
        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));
        when(bookingRepository.save(updatedBooking)).thenReturn(updatedBooking);
        when(eventRepository.save(event)).thenReturn(event);

        Booking result = bookingService.updateBooking("booking1", updatedBooking);

        assertNotNull(result);
        assertEquals(7, event.getAvailableTickets());
        verify(eventRepository, times(1)).save(event);
        verify(bookingRepository, times(1)).save(updatedBooking);
        logger.info("testUpdateBookingSuccess passed.");
    }

    @Test
    void testUpdateBookingBookingNotFound() {
        Booking updatedBooking = new Booking("Jane Doe", "jane.doe@example.com", "event1", 8);

        when(bookingRepository.findById("nonexistentBooking")).thenReturn(Optional.empty());

        Booking result = bookingService.updateBooking("nonexistentBooking", updatedBooking);

        assertNull(result);
        logger.warn("testUpdateBookingBookingNotFound passed, as expected, returned null.");
    }

    @Test
    void testUpdateBookingInsufficientTickets() {
        Booking originalBooking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        originalBooking.setId("booking1");
        Booking updatedBooking = new Booking("Jane Doe", "jane.doe@example.com", "event1", 15);
        Event event = new Event("Concert", "Venue A", null, 10);
        event.setId("event1");

        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(originalBooking));
        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));

        Booking result = bookingService.updateBooking("booking1", updatedBooking);

        assertNull(result);
        logger.warn("testUpdateBookingInsufficientTickets passed, as expected, returned null.");
    }

    @Test
    void testDeleteBookingSuccess() {
        Booking booking = new Booking("John Doe", "john.doe@example.com", "event1", 5);
        booking.setId("booking1");
        Event event = new Event("Concert", "Venue A", null, 5);
        event.setId("event1");

        when(bookingRepository.findById("booking1")).thenReturn(Optional.of(booking));
        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));

        boolean result = bookingService.deleteBooking("booking1");

        assertTrue(result);
        assertEquals(10, event.getAvailableTickets());
        verify(bookingRepository, times(1)).deleteById("booking1");
        verify(eventRepository, times(1)).save(event);
        logger.info("testDeleteBookingSuccess passed.");
    }

    @Test
    void testDeleteBookingNotFound() {
        when(bookingRepository.findById("nonexistentBooking")).thenReturn(Optional.empty());

        boolean result = bookingService.deleteBooking("nonexistentBooking");

        assertFalse(result);
        logger.warn("testDeleteBookingNotFound passed, as expected, returned false.");
    }
}