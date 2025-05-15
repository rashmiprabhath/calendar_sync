package com.rashmi.calendar_sync.scheduler;

import com.rashmi.calendar_sync.entity.Event;
import com.rashmi.calendar_sync.service.EventService;
import com.rashmi.calendar_sync.service.google.CalendarSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarFetchScheduler {

    private final CalendarSyncService calendarSyncService;
    private final EventService eventService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncFromCalendarToDatabase() {
        log.info("Starting calendar-to-DB sync...");

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        final Function<Event, String> keyGenerator = e -> e.getTitle() + "|" + e.getStartTime();

        try {
            List<Event> googleEvents = calendarSyncService.getEventsFromGoogleCalendar(start, end);
            List<Event> existingEvents = eventService.getEventsByDateRange(start, end);

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

            eventService.saveAll(newEvents);
            log.info("Calendar sync complete. {} new events added.", newEvents.size());

        } catch (Exception e) {
            log.error("Calendar sync failed", e);
        }
    }

}
