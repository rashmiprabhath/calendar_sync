package com.rashmi.calendar_sync.service;

import com.rashmi.calendar_sync.dto.request.EventDto;
import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> fetchEvents() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(2);
        return this.eventRepository.findByStartTimeBetween(start, end);
    }

    public List<Event> fetchEvents(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return fetchEvents();
        }
        return this.eventRepository.findByStartTimeBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    public Event fetchEvent(Long id) {
        return eventRepository.findById(id).get();
    }

    public Event createEvent(Event event) {
        return this.eventRepository.save(event);
    }

    public Event updateEvent(Long id, EventDto dto) {
        Event event = fetchEvent(id);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getStartTime() != null) {
            event.setStartTime(dto.getStartTime());
        }

        if (dto.getEndTime() != null) {
            event.setEndTime(dto.getEndTime());
        }

        return this.eventRepository.save(event);
    }

    public String deleteEvent(Long id) {
        return "we do not support event delete!";
    }

}
