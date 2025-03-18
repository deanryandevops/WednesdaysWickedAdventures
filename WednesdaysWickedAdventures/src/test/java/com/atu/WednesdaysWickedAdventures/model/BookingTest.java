package com.atu.WednesdaysWickedAdventures.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingTest.class);
    private Validator validator;
    private Booking booking;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        booking = new Booking();
    }

    @Test
    void testValidBooking() {
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
        logger.info("Valid Booking test passed.");
    }

    @Test
    void testInvalidCustomerNameTooShort() {
        booking.setCustomerName("abcd");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Customer name must be between 5 and 250 characters", violations.iterator().next().getMessage());
        logger.warn("Invalid Customer Name Too Short test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidCustomerNameBlank() {
        booking.setCustomerName("");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        //assertEquals("Customer name is required", violations.iterator().next().getMessage());
        logger.warn("Invalid Customer Name Blank test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailBlank() {
        booking.setCustomerName("John Doe");
        booking.setEmail("");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        assertEquals("Customer email is required", violations.iterator().next().getMessage());
        logger.warn("Invalid Email Blank test passed. Violation: {}", violations.iterator().next().getMessage());
    }
    
    @Test
    void testInvalidEmailFormat() {
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
        logger.warn("Invalid Email Format test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEventIdNull() {
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example.com");
        booking.setNumberOfTickets(5);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Event ID is required", violations.iterator().next().getMessage());
        logger.warn("Invalid Event ID Null test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidNumberOfTicketsNegative() {
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(-1);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("numberOfTickets should not be less than 0", violations.iterator().next().getMessage());
        logger.warn("Invalid Number Of Tickets Negative test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidNumberOfTicketsTooLarge() {
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(26);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("numberOfTickets should not be greater than 25", violations.iterator().next().getMessage());
        logger.warn("Invalid Number Of Tickets Too Large test passed. Violation: {}", violations.iterator().next().getMessage());
    }

    @Test
    void testIdNullOnCreation() {
        Booking newBooking = new Booking("John Doe", "john.doe@example.com", "event123", 5);
        Set<ConstraintViolation<Booking>> violations = validator.validate(newBooking);
        assertTrue(violations.isEmpty());
        logger.info("Id Null On Creation test passed.");
    }

    @Test
    void testIdNotNullWhenSet() {
        
        booking.setCustomerName("John Doe");
        booking.setEmail("john.doe@example.com");
        booking.setEventId("event123");
        booking.setNumberOfTickets(5);
        booking.setId("someId");
        
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Id must be null for booking.", violations.iterator().next().getMessage());
        logger.warn("Id Not Null When Set test passed, as expected, with violation: {}", violations.iterator().next().getMessage());
    }
}