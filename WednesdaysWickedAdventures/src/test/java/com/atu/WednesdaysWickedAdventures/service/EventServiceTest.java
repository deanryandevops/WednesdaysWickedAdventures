package com.atu.WednesdaysWickedAdventures.service;

import com.atu.WednesdaysWickedAdventures.model.Event;
import com.atu.WednesdaysWickedAdventures.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EventServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceTest.class);

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = new ArrayList<>();
        events.add(new Event("Concert", "Venue A", LocalDateTime.now(), 100));
        events.add(new Event("Theater", "Venue B", LocalDateTime.now().plusDays(7), 50));

        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        assertEquals("Concert", result.get(0).getName());
        logger.info("testGetAllEvents passed.");
    }

    @Test
    void testGetEventById() {
        Event event = new Event("Concert", "Venue A", LocalDateTime.now(), 100);
        event.setId("event1");

        when(eventRepository.findById("event1")).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById("event1");

        assertTrue(result.isPresent());
        assertEquals("Concert", result.get().getName());
        logger.info("testGetEventById passed.");
    }

    @Test
    void testCreateEvent() {
        Event event = new Event("Concert", "Venue A", LocalDateTime.now(), 100);

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.createEvent(event);

        assertNotNull(result);
        assertEquals("Concert", result.getName());
        verify(eventRepository, times(1)).save(event);
        logger.info("testCreateEvent passed.");
    }

    @Test
    void testUpdateEventSuccess() {
        Event originalEvent = new Event("Concert", "Venue A", LocalDateTime.now(), 100);
        originalEvent.setId("event1");
        Event updatedEvent = new Event("Theater", "Venue B", LocalDateTime.now().plusDays(7), 50);

        when(eventRepository.existsById("event1")).thenReturn(true);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        Event result = eventService.updateEvent("event1", updatedEvent);

        assertNotNull(result);
        assertEquals("Theater", result.getName());
        verify(eventRepository, times(1)).save(updatedEvent);
        logger.info("testUpdateEventSuccess passed.");
    }

    @Test
    void testUpdateEventNotFound() {
        Event updatedEvent = new Event("Theater", "Venue B", LocalDateTime.now().plusDays(7), 50);

        when(eventRepository.existsById("nonexistentEvent")).thenReturn(false);

        Event result = eventService.updateEvent("nonexistentEvent", updatedEvent);

        assertNull(result);
        logger.warn("testUpdateEventNotFound passed, as expected, returned null.");
    }

    @Test
    void testDeleteEventSuccess() {
        when(eventRepository.existsById("event1")).thenReturn(true);

        boolean result = eventService.deleteEvent("event1");

        assertTrue(result);
        verify(eventRepository, times(1)).deleteById("event1");
        logger.info("testDeleteEventSuccess passed.");
    }

    @Test
    void testDeleteEventNotFound() {
        when(eventRepository.existsById("nonexistentEvent")).thenReturn(false);

        boolean result = eventService.deleteEvent("nonexistentEvent");

        assertFalse(result);
        logger.warn("testDeleteEventNotFound passed, as expected, returned false.");
    }
}