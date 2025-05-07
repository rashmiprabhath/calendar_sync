package com.rashmi.calendar_sync.dto.request.mapper;

import com.rashmi.calendar_sync.dto.request.EventDto;
import com.rashmi.calendar_sync.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventDto dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setTimeZone(dto.getTimeZone());
        return event;
    }
}
