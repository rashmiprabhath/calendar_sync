package com.rashmi.calendar_sync.service.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Events;
import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.repository.EventRepository;
import com.rashmi.calendar_sync.service.google.mapper.GoogleEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarFetchService {

    private final EventRepository eventRepository;
    private final GoogleCalendarService googleCalendarService;
    private final GoogleEventMapper googleEventMapper;

    public int syncGoogleEventsToDatabase() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        final Function<Event, String> keyGenerator = e -> e.getTitle() + "|" + e.getStartTime();

        List<Event> googleEvents = this.getEventsFromAllCalendars(start, end);
        List<Event> existingEvents = eventRepository.findByStartTimeBetween(start, end);

        Map<String, LocalDateTime> existingMap = existingEvents.stream()
                .collect(Collectors.toMap(
                        keyGenerator,
                        Event::getStartTime
                ));

        List<Event> newEvents = new ArrayList<>();

        for (Event gEvent : googleEvents) {
            String key = keyGenerator.apply(gEvent);

            if (!existingMap.containsKey(key)) {
                newEvents.add(gEvent);
            }
        }

        eventRepository.saveAll(newEvents);
        return newEvents.size();
    }

    private List<Event> getEventsFromAllCalendars(LocalDateTime start, LocalDateTime end) throws Exception {
        List<CalendarListEntry> calendars = googleCalendarService.getAllCalendars();
        List<Event> events = new ArrayList<>();

        for (CalendarListEntry calendar : calendars) {
            String calendarId = calendar.getId();
            List<Event> googleEvents = this.getEventsFromGoogleCalendar(calendarId, start, end);
            events.addAll(googleEvents);
        }
        return events;
    }

    private List<Event> getEventsFromGoogleCalendar(final String calendarId, LocalDateTime start, LocalDateTime end) throws Exception {
        DateTime timeMin = new DateTime(Timestamp.valueOf(start));
        DateTime timeMax = new DateTime(Timestamp.valueOf(end));

        Events events = googleCalendarService.getCalendarService().events().list(calendarId)
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems().stream().map(ge -> googleEventMapper.mapGoogleEventToMyEvent(ge, calendarId)).toList();
    }

    private List<Event> getEventsFromPrimaryGoogleCalendar(LocalDateTime start, LocalDateTime end) throws Exception {
        return getEventsFromGoogleCalendar(GoogleCalendarService.PRIMARY_CALENDAR_ID, start, end);
    }

}
