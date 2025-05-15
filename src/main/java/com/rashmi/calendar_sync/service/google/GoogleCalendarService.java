package com.rashmi.calendar_sync.service.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarService {

    public static final String PRIMARY_CALENDAR_ID = "primary";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.credentials.path}")
    private String credentialsPath;

    @Value("${google.project.name}")
    private String applicationName;

    @Value("${google.tokens.path:tokens}")
    private String tokensDirectoryPath;

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        try (Reader reader = new FileReader(credentialsPath)) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new com.google.api.client.util.store.FileDataStoreFactory(
                            Paths.get(tokensDirectoryPath).toFile()))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8800).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }
    }

    /**
     * Builds and returns an authorized Calendar client service.
     */
    public Calendar getCalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);

        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    /**
     * get all calendars from the google calendar account. this includes
     * primary calendar
     * secondary calendars
     * and custom calendars
     * @return
     * @throws Exception
     */
    public List<CalendarListEntry> getAllCalendars() throws Exception {
        Calendar.CalendarList.List request = this.getCalendarService().calendarList().list();
        List<CalendarListEntry> allCalendars = new ArrayList<>();

        do {
            CalendarList calendarList = request.execute();
            allCalendars.addAll(calendarList.getItems());
            request.setPageToken(calendarList.getNextPageToken());
        } while (request.getPageToken() != null);

        return allCalendars;
    }

}
