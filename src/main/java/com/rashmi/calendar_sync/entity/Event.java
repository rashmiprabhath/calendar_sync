package com.rashmi.calendar_sync.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTS")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "TIMEZONE")
    private String timeZone;

    @Column(name = "calendar_id")
    private String calendarId;

    @Column(name = "SYNCED_WITH_CALENDAR")
    private Boolean syncedWithCalendar = false;

}
