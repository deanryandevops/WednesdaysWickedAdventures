package com.atu.WednesdaysWickedAdventures.service;

import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link Event} entities.
 * Provides methods for retrieving, creating, updating, and deleting events.
 *
 * <p>This class utilizes {@link EventRepository} to interact with the database.</p>
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@Service
public class EventService {

    /**
     * The repository for managing Event entities.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * Retrieves all events from the database.
     *
     * @return A list of all Event entities.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param id The ID of the event to retrieve.
     * @return An Optional containing the Event, or an empty Optional if not found.
     */
    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    /**
     * Creates a new event in the database.
     *
     * @param event The Event entity to create.
     * @return The created Event entity.
     */
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Updates an existing event in the database.
     *
     * @param id    The ID of the event to update.
     * @param event The updated Event entity.
     * @return The updated Event entity, or null if the event does not exist.
     */
    public Event updateEvent(String id, Event event) {

        if (eventRepository.existsById(id)) {
            event.setId(id);
            return eventRepository.save(event);
        }
        return null;
    }

    /**
     * Deletes an event from the database.
     *
     * @param id The ID of the event to delete.
     * @return true if the event was deleted, false otherwise.
     */
    public boolean deleteEvent(String id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}