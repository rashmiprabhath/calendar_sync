package com.rashmi.calendar_sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CalendarSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalendarSyncApplication.class, args);
	}

}
