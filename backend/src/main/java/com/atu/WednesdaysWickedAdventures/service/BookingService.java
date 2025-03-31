package com.atu.WednesdaysWickedAdventures.service;

import com.atu.WednesdaysWickedAdventures.model.Booking;
import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.repository.BookingRepository;
import com.atu.WednesdaysWickedAdventures.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link Booking} entities and associated {@link Event} updates.
 * Provides methods for retrieving, creating, updating, and deleting bookings, while also
 * managing event ticket availability.
 *
 * <p>This class utilizes {@link BookingRepository} and {@link EventRepository} to interact
 * with the database.</p>
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@Service
public class BookingService {

    /**
     * The repository for managing Booking entities.
     */
    @Autowired
    private BookingRepository bookingRepository;

    /**
     * The repository for managing Event entities.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * Retrieves all bookings from the database.
     *
     * @return A list of all Booking entities.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param id The ID of the booking to retrieve.
     * @return An Optional containing the Booking, or an empty Optional if not found.
     */
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    /**
     * Creates a new booking and updates the associated event's ticket availability.
     *
     * @param booking The Booking entity to create.
     * @return The created Booking entity, or null if the event does not exist or has insufficient tickets.
     */
    public Booking createBooking(Booking booking) {
        Optional<Event> eventOptional = eventRepository.findById(booking.getEventId());
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getAvailableTickets() >= booking.getNumberOfTickets()) {
                event.setAvailableTickets(event.getAvailableTickets() - booking.getNumberOfTickets());
                eventRepository.save(event);
                return bookingRepository.save(booking);
            } else {
                return null; // Or throw an exception
            }
        }
        return null; // or throw an exception
    }

    /**
     * Updates an existing booking and adjusts the associated event's ticket availability.
     *
     * @param id      The ID of the booking to update.
     * @param booking The updated Booking entity.
     * @return The updated Booking entity, or null if the booking or event does not exist or has insufficient tickets.
     */
    public Booking updateBooking(String id, Booking booking) {
        Optional<Booking> originalBookingOptional = bookingRepository.findById(id);
        if (originalBookingOptional.isPresent()) {
            Booking originalBooking = originalBookingOptional.get();
            Optional<Event> eventOptional = eventRepository.findById(booking.getEventId());
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                int ticketDifference = booking.getNumberOfTickets() - originalBooking.getNumberOfTickets();
                if (event.getAvailableTickets() + originalBooking.getNumberOfTickets() >= booking.getNumberOfTickets()) {
                    event.setAvailableTickets(event.getAvailableTickets() - ticketDifference);
                    eventRepository.save(event);
                    booking.setId(id);
                    return bookingRepository.save(booking);
                } else {
                    return null; // or throw an exception
                }
            }
        }
        return null; // or throw an exception
    }

    /**
     * Deletes a booking and restores the associated event's ticket availability.
     *
     * @param id The ID of the booking to delete.
     * @return true if the booking was deleted, false otherwise.
     */
    public boolean deleteBooking(String id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Optional<Event> eventOptional = eventRepository.findById(booking.getEventId());
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                event.setAvailableTickets(event.getAvailableTickets() + booking.getNumberOfTickets());
                eventRepository.save(event);
            }
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
}