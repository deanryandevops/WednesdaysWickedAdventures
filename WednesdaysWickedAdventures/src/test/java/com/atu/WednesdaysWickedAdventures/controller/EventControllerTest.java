package com.atu.WednesdaysWickedAdventures.controller;

import com.atu.WednesdaysWickedAdventures.model.Event;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(EventControllerTest.class);

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllEvents_ReturnsOkWithEvents() throws Exception {
        List<Event> events = List.of(
                new Event("Concert", "Venue A", LocalDateTime.now(), 100),
                new Event("Workshop", "Venue B", LocalDateTime.now().plusDays(1), 50)
        );
        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Concert"))
                .andExpect(jsonPath("$[1].name").value("Workshop"));
        logger.info("getAllEvents_ReturnsOkWithEvents passed.");
    }

    @Test
    void getEventById_ReturnsOkWithEvent() throws Exception {
        Event event = new Event("Concert", "Venue A", LocalDateTime.now(), 100);
        event.setId("event1");
        when(eventService.getEventById("event1")).thenReturn(Optional.of(event));

        mockMvc.perform(get("/v1/events/event1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Concert"));
        logger.info("getEventById_ReturnsOkWithEvent passed.");
    }

    @Test
    void getEventById_ReturnsNotFound_WhenEventNotFound() throws Exception {
        when(eventService.getEventById("nonexistentEvent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/events/nonexistentEvent"))
                .andExpect(status().isNotFound());
        logger.warn("getEventById_ReturnsNotFound_WhenEventNotFound passed.");
    }

    @Test
    void createEvent_ReturnsCreated_WhenValidEvent() throws Exception {
        Event event = new Event("Concert", "Venue A", null, 100);
        when(eventService.createEvent(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Concert"));
        logger.info("createEvent_ReturnsCreated_WhenValidEvent passed.");
    }

    @Test
    void createEvent_ReturnsBadRequest_WhenInvalidEvent() throws Exception {
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new FieldError("event", "name", "Name cannot be empty")));
        mockMvc.perform(post("/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Event("", "", null, 0))))
                .andExpect(status().isBadRequest());
        logger.warn("createEvent_ReturnsBadRequest_WhenInvalidEvent passed.");
    }

    @Test
    void updateEvent_ReturnsOk_WhenValidEvent() throws Exception {
        Event event = new Event("Updated Concert", "Updated Venue A", null, 150);
        when(eventService.updateEvent(eq("event1"), any(Event.class))).thenReturn(event);

        mockMvc.perform(put("/v1/events/event1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Concert"));
        logger.info("updateEvent_ReturnsOk_WhenValidEvent passed.");
    }

    @Test
    void updateEvent_ReturnsBadRequest_WhenInvalidEvent() throws Exception {
        BindingResult bindingResult = org.mockito.Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new FieldError("event", "name", "Name cannot be empty")));
        mockMvc.perform(put("/v1/events/event1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Event("", "", null, 0))))
                .andExpect(status().isBadRequest());
        logger.warn("updateEvent_ReturnsBadRequest_WhenInvalidEvent passed.");
    }

    @Test
    void updateEvent_ReturnsNotFound_WhenEventNotFound() throws Exception {
        Event event = new Event("Updated Concert", "Updated Venue A", null, 150);
        when(eventService.updateEvent(eq("event1"), any(Event.class))).thenReturn(null);

        mockMvc.perform(put("/v1/events/event1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Event not found."));
        logger.warn("updateEvent_ReturnsNotFound_WhenEventNotFound passed.");
    }

    @Test
    void deleteEvent_ReturnsNoContent_WhenEventDeleted() throws Exception {
        when(eventService.deleteEvent("event1")).thenReturn(true);

        mockMvc.perform(delete("/v1/events/event1"))
                .andExpect(status().isNoContent());
        logger.info("deleteEvent_ReturnsNoContent_WhenEventDeleted passed.");
    }

    @Test
    void deleteEvent_ReturnsNotFound_WhenEventNotFound() throws Exception {
        when(eventService.deleteEvent("nonexistentEvent")).thenReturn(false);

        mockMvc.perform(delete("/v1/events/nonexistentEvent"))
                .andExpect(status().isNotFound());
        logger.warn("deleteEvent_ReturnsNotFound_WhenEventNotFound passed.");
    }
}