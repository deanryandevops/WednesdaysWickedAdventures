package com.atu.WednesdaysWickedAdventures.controller;

import com.atu.WednesdaysWickedAdventures.model.Booking;
import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.service.BookingService;
import com.atu.WednesdaysWickedAdventures.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Booking} entities.
 * Provides endpoints for retrieving, creating, updating, and deleting bookings.
 *
 * <p>All endpoints are prefixed with "/v1/bookings".</p>
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@RestController
@RequestMapping("/v1/bookings")
public class BookingController {

    /**
     * Service for handling Booking-related operations.
     */
    @Autowired
    private BookingService bookingService;

    /**
     * Service for handling Event-related operations.
     */
    @Autowired
    private EventService eventService;

    /**
     * Retrieves all bookings.
     *
     * @return ResponseEntity containing a list of all Booking entities.
     */
    @Operation(summary = "Retrieves all bookings. ")
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Retrieves a booking by its ID, including the associated Event.
     *
     * @param id The ID of the booking to retrieve.
     * @return ResponseEntity containing a {@link BookingEventResponse} with the Booking and Event,
     * or a 404 Not Found if the booking or event does not exist.
     */
    @Operation(summary = "Retrieves a booking by its ID, including the associated Event. ")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable String id) {
        Optional<Booking> bookingOptional = bookingService.getBookingById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Optional<Event> eventOptional = eventService.getEventById(booking.getEventId());
            if (eventOptional.isPresent()) {
                return ResponseEntity.ok(new BookingEventResponse(booking, eventOptional.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found for booking.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
        }
    }

    /**
     * Creates a new booking.
     *
     * @param booking The Booking entity to create.
     * @param result  BindingResult for validation errors.
     * @return ResponseEntity containing the created Booking, a 400 Bad Request if validation fails,
     * or a 400 Bad Request if there are not enough tickets available or the event is not found.
     */
    @Operation(summary = "Creates a new booking. ")
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Booking createdBooking = bookingService.createBooking(booking);
        if (createdBooking != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } else {
            return ResponseEntity.badRequest().body("Not enough tickets available or Event not found.");
        }
    }

    /**
     * Updates an existing booking.
     *
     * @param id      The ID of the booking to update.
     * @param booking The updated Booking entity.
     * @param result  BindingResult for validation errors.
     * @return ResponseEntity containing the updated Booking, a 400 Bad Request if validation fails,
     * or a 400 Bad Request if there are not enough tickets available or the booking/event is not found.
     */
    @Operation(summary = "Updates an existing booking. ")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable String id, @Valid @RequestBody Booking booking, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        Booking updatedBooking = bookingService.updateBooking(id, booking);
        if (updatedBooking != null) {
            return ResponseEntity.ok(updatedBooking);
        } else {
            return ResponseEntity.badRequest().body("Not enough tickets available or Event/Booking not found.");
        }
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param id The ID of the booking to delete.
     * @return ResponseEntity with a 204 No Content if successful, or a 404 Not Found if the booking does not exist.
     */
    @Operation(summary = "Deletes a booking by its ID. ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable String id) {
        if (bookingService.deleteBooking(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Response Data Transfer Object (DTO) containing a Booking and its associated Event.
     */
    static class BookingEventResponse {
        private Booking booking;
        private Event event;

        /**
         * Constructs a new BookingEventResponse.
         *
         * @param booking The Booking entity.
         * @param event   The associated Event entity.
         */
        public BookingEventResponse(Booking booking, Event event) {
            this.booking = booking;
            this.event = event;
        }

        /**
         * Gets the Booking entity.
         *
         * @return The Booking entity.
         */
        public Booking getBooking() {
            return booking;
        }

        /**
         * Gets the Event entity.
         *
         * @return The Event entity.
         */
        public Event getEvent() {
            return event;
        }
    }
}