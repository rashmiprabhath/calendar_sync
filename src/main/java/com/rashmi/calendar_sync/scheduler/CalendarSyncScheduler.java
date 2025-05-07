package com.rashmi.calendar_sync.scheduler;

import com.rashmi.calendar_sync.service.google.CalendarSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalendarSyncScheduler {

    private final CalendarSyncService calendarSyncService;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void runMidnightSync() {
        log.info("Starting calendar sync job at midnight...");
        try {
            calendarSyncService.syncUnSyncedEvents();
            log.info("Calendar sync job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during calendar sync job", e);
        }
    }
}
