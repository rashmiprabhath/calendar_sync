package com.rashmi.calendar_sync.service.google.mapper;

import com.google.api.client.util.DateTime;
import com.rashmi.calendar_sync.entity.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class GoogleEventMapper {

    @Value("${google.calendar.timezone.default:America/New_York}")
    private String defaultTimeZone;

    public Event mapGoogleEventToMyEvent(com.google.api.services.calendar.model.Event googleEvent, String calendarId) {
        DateTime start = googleEvent.getStart().getDateTime();
        DateTime end = googleEvent.getEnd().getDateTime();

        Event event = new Event();
        event.setTitle(googleEvent.getSummary());
        event.setDescription(googleEvent.getDescription());
        event.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getValue()), ZoneId.systemDefault()));
        event.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getValue()), ZoneId.systemDefault()));
        event.setTimeZone(googleEvent.getStart().getTimeZone() != null ? googleEvent.getStart().getTimeZone() : defaultTimeZone);
        event.setCalendarId(calendarId);
        event.setSyncedWithCalendar(true);
        return event;
    }
}
