package com.atu.WednesdaysWickedAdventures.controller;

import com.atu.WednesdaysWickedAdventures.model.Event;
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
 * REST controller for managing {@link Event} entities.
 * Provides endpoints for retrieving, creating, updating, and deleting events.
 *
 * <p>All endpoints are prefixed with "/v1/events".</p>
 * 
 * @author	Mathieu Bizumuremyi 
 * @version	1.0 
 * @since	07-03-2025
 * 
 */
@RestController
@RequestMapping("/v1/events")
public class EventController {

    /**
     * Service for handling Event-related operations.
     */
    @Autowired
    private EventService eventService;

    /**
     * Retrieves all events.
     *
     * @return ResponseEntity containing a list of all Event entities.
     */
    @Operation(summary = "Retrieves all events. ")
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param id The ID of the event to retrieve.
     * @return ResponseEntity containing the Event, or a 404 Not Found if not found.
     */
    @Operation(summary = "Retrieves an event by its ID. ")
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new event.
     *
     * @param event  The Event entity to create.
     * @param result BindingResult for validation errors.
     * @return ResponseEntity containing the created Event, or a 400 Bad Request if validation fails.
     */
    @Operation(summary = "Creates a new event. ")
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody Event event, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Event createdEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    /**
     * Updates an existing event.
     *
     * @param id     The ID of the event to update.
     * @param event  The updated Event entity.
     * @param result BindingResult for validation errors.
     * @return ResponseEntity containing the updated Event, a 400 Bad Request if validation fails,
     * or a 404 Not Found if the event does not exist.
     */
    @Operation(summary = "Updates an existing event. ")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @Valid @RequestBody Event event, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        Event updatedEvent = eventService.updateEvent(id, event);
        if (updatedEvent != null) {
            return ResponseEntity.ok(updatedEvent);
        } else {
            //return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found.");
        }
    }

    /**
     * Deletes an event by its ID.
     *
     * @param id The ID of the event to delete.
     * @return ResponseEntity with a 204 No Content if successful, or a 404 Not Found if the event does not exist.
     */
    @Operation(summary = "Deletes an event by its ID. ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}