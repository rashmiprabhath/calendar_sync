package com.rashmi.calendar_sync.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.EventDateTime;
import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarSyncService {

    private final EventRepository eventRepository;
    private final GoogleCalendarService googleCalendarService;

    public void syncUnSyncedEvents() throws Exception {
        List<Event> events = eventRepository.findBySyncedWithCalendar(false);

        Calendar calendarService = googleCalendarService.getCalendarService();

        for (Event event : events) {
            com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
                    .setSummary(event.getTitle())
                    .setDescription(event.getDescription());

            DateTime startDateTime = new DateTime(event.getStartTime().atZone(ZoneId.of(event.getTimeZone())).toInstant().toEpochMilli());
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone(event.getTimeZone());
            googleEvent.setStart(start);

            DateTime endDateTime = new DateTime(event.getEndTime().atZone(ZoneId.of(event.getTimeZone())).toInstant().toEpochMilli());
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
}
