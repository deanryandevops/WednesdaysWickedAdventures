package com.atu.WednesdaysWickedAdventures.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    private static final Logger logger = LoggerFactory.getLogger(EventTest.class);
    private Validator validator;
    private Event event;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        event = new Event();
    }

    @Test
    void testValidEvent() {
        event.setName("Concert");
        event.setVenue("Venue A");
        event.setDateTime(LocalDateTime.now().plusDays(7));
        event.setAvailableTickets(100);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
        logger.info("Valid Event test passed.");
    }

    @Test
    void testInvalidEventNameTooShort() {
        event.setName("abcd");
        event.setVenue("Venue A");
        event.setDateTime(LocalDateTime.now().plusDays(7));
        event.setAvailableTickets(100);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Event name must be between 5 and 250 characters", violations.iterator().next().getMessage());
        logger.warn("Invalid Event Name Too Short test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidAvailableTicketsNegative() {
        event.setName("Concert");
        event.setVenue("Venue A");
        event.setDateTime(LocalDateTime.now().plusDays(7));
        event.setAvailableTickets(-1);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("availableTickets should not be less than 0", violations.iterator().next().getMessage());
        logger.warn("Invalid Available Tickets Negative test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidAvailableTicketsTooLarge() {
        event.setName("Concert");
        event.setVenue("Venue A");
        event.setDateTime(LocalDateTime.now().plusDays(7));
        event.setAvailableTickets(251);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("availableTickets should not be greater than 250", violations.iterator().next().getMessage());
        logger.warn("Invalid Available Tickets Too Large test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testIdNullOnCreation() {
        Event newEvent = new Event("Concert", "Venue A", LocalDateTime.now().plusDays(7), 100);
        Set<ConstraintViolation<Event>> violations = validator.validate(newEvent);
        assertTrue(violations.isEmpty());
        logger.info("Id Null On Creation test passed.");
    }

    @Test
    void testIdNotNullWhenSet() {
        event.setId("someId");
        event.setName("Concert");
        event.setVenue("Venue A");
        event.setDateTime(LocalDateTime.now().plusDays(7));
        event.setAvailableTickets(100);

        Set<ConstraintViolation<Event>> violations = validator.validate(event);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Id must be null for events.", violations.iterator().next().getMessage());
        logger.warn("testIdNotNullWhenSet passed, as expected, with violation: {}", violations.iterator().next().getMessage());
    }
}
