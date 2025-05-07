package com.rashmi.calendar_sync.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeZone;
    private Boolean syncedWithCalendar;
}
