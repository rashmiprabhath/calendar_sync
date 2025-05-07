package com.rashmi.calendar_sync.repository;

import com.rashmi.calendar_sync.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findBySyncedWithCalendar(boolean syncedWithCalendar);

    List<Event> findByStartTimeBetweenAndSyncedWithCalendar(LocalDateTime start, LocalDateTime end, Boolean synced);
}
