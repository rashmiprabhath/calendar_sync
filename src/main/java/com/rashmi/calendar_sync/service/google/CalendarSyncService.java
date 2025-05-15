package com.rashmi.calendar_sync.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarSyncService {

    @Value("${google.calendar.timezone.default:America/New_York}")
    private String defaultTimeZone;

    private final EventRepository eventRepository;
    private final GoogleCalendarService googleCalendarService;

    public void syncUnSyncedEvents() throws Exception {
        List<Event> events = eventRepository.findBySyncedWithCalendar(false);

        Calendar calendarService = googleCalendarService.getCalendarService();

        for (Event event : events) {
            com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
                    .setSummary(event.getTitle())
                    .setDescription(event.getDescription());

            ZoneId zoneId;
            if (event.getTimeZone() == null) {
                event.setTimeZone(defaultTimeZone);
                zoneId = ZoneId.of(defaultTimeZone);
            } else {
                zoneId = ZoneId.of(event.getTimeZone());
            }


            DateTime startDateTime = new DateTime(event.getStartTime().atZone(zoneId).toInstant().toEpochMilli());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(event.getTimeZone());
            googleEvent.setStart(start);

            DateTime endDateTime = new DateTime(event.getEndTime().atZone(zoneId).toInstant().toEpochMilli());
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone(event.getTimeZone());
            googleEvent.setEnd(end);

            googleEvent = calendarService.events().insert("primary", googleEvent).execute();

            // Mark as synced
            event.setSyncedWithCalendar(true);
            eventRepository.save(event);
        }
    }

    public List<Event> getEventsFromGoogleCalendar(LocalDateTime start, LocalDateTime end) throws Exception {
        DateTime timeMin = new DateTime(Timestamp.valueOf(start));
        DateTime timeMax = new DateTime(Timestamp.valueOf(end));

        Events events = googleCalendarService.getCalendarService().events().list("primary")
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems().stream().map(this::mapGoogleEventToMyEvent).toList();
    }

    private Event mapGoogleEventToMyEvent(com.google.api.services.calendar.model.Event googleEvent) {
        DateTime start = googleEvent.getStart().getDateTime();
        DateTime end = googleEvent.getEnd().getDateTime();

        Event event = new Event();
        event.setTitle(googleEvent.getSummary());
        event.setDescription(googleEvent.getDescription());
        event.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getValue()), ZoneId.systemDefault()));
        event.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getValue()), ZoneId.systemDefault()));
        event.setTimeZone(googleEvent.getStart().getTimeZone() != null ? googleEvent.getStart().getTimeZone() : defaultTimeZone);
        event.setSyncedWithCalendar(true);
        return event;
    }

}
