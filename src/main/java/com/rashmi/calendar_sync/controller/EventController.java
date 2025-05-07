package com.rashmi.calendar_sync.controller;

import com.rashmi.calendar_sync.dto.request.EventDto;
import com.rashmi.calendar_sync.dto.request.mapper.EventMapper;
import com.rashmi.calendar_sync.dto.response.EventResponseDto;
import com.rashmi.calendar_sync.dto.response.mepper.EventResponseMapper;
import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.service.EventService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final EventMapper eventMapper;

    private final EventResponseMapper eventResponseMapper;

    @GetMapping
    public List<EventResponseDto> getEvents(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) {
        List<Event> events = eventService.fetchEvents(startDate, endDate);
        return events.stream().map(this.eventResponseMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public EventResponseDto getEvent(@PathParam(value = "id") Long id) {
        Event event = eventService.fetchEvent(id);
        return this.eventResponseMapper.toResponse(event);
    }

    @PostMapping
    public EventResponseDto createEvent(@RequestBody EventDto eventDto) {
        Event event = this.eventService.createEvent(this.eventMapper.toEntity(eventDto));
        return this.eventResponseMapper.toResponse(event);
    }

    @PatchMapping("/{id}")
    public EventResponseDto updateEvent(@PathParam(value = "id") Long id, @RequestBody EventDto eventDto) {
        Event event = this.eventService.updateEvent(id, eventDto);
        return this.eventResponseMapper.toResponse(event);
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathParam(value = "id") Long id) {
        return this.eventService.deleteEvent(id);
    }
}
