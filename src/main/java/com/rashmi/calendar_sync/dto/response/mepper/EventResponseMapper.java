package com.rashmi.calendar_sync.dto.response.mepper;

import com.rashmi.calendar_sync.dto.response.EventResponseDto;
import com.rashmi.calendar_sync.entity.Event;
import org.springframework.stereotype.Component;

@Component
public class EventResponseMapper {

    public EventResponseDto toResponse(Event event) {
        EventResponseDto responseDto = new EventResponseDto();
        responseDto.setId(event.getId());
        responseDto.setTitle(event.getTitle());
        responseDto.setDescription(event.getDescription());
        responseDto.setStartTime(event.getStartTime());
        responseDto.setEndTime(event.getEndTime());
        responseDto.setSyncedWithCalendar(event.getSyncedWithCalendar());
        return responseDto;
    }
}
