package com.rashmi.calendar_sync.scheduler;

import com.rashmi.calendar_sync.service.EventService;
import com.rashmi.calendar_sync.service.google.CalendarFetchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarFetchScheduler {

    private final CalendarFetchService calendarFetchService;
    private final EventService eventService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncFromCalendarToDatabase() {
        log.info("Starting calendar-to-DB sync...");

        try {
            int eventsCount = calendarFetchService.syncGoogleEventsToDatabase();
            log.info("Calendar sync complete. {} new events added.", eventsCount);

        } catch (Exception e) {
            log.error("Calendar sync failed", e);
        }
    }

}
