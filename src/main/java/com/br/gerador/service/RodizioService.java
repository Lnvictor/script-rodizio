package com.br.gerador.service;

import com.br.gerador.domain.Day;
import com.br.gerador.domain.Organista;
import com.br.gerador.domain.Rodizio;
import com.br.gerador.integration.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Generates next rodizios automatically
 */
public class RodizioService {
    private static final String SPREADSHEETS_ID = "1RJNAQH_4zr6grMhh4CR0h-KTfItPFL8tPW-WVx75VQk";

    private final GoogleSheetsClient client;

    public RodizioService(GoogleSheetsClient client) {
        this.client = client;
    }

    public Rodizio generateNextRodizios(String firstAdult, String firstYoung, int initialMonth, int finalMonth) {
        String[] adultOrder = {"Alice", "Thalita", "Aparecida", "Elisiana"};
        String[] youngOrder = {"Keren", "Esther"};

        int currentAdultIndex = 0;
        int currentYoungIndex = 0;

        List<Day> days = new ArrayList<Day>();

        for (; currentAdultIndex < adultOrder.length; currentAdultIndex++) {
            if (adultOrder[currentAdultIndex].equals(firstAdult)) {
                break;
            }
        }

        for (; currentYoungIndex < youngOrder.length; currentYoungIndex++) {
            if (youngOrder[currentYoungIndex].equals(firstYoung)) {
                break;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, initialMonth - 1, 01);

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
        ) {
            calendar.add(Calendar.DATE, 1);
        }

        while (calendar.get(Calendar.MONTH) < finalMonth) {
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            if (day != Calendar.SATURDAY && day != Calendar.SUNDAY && day != Calendar.WEDNESDAY) {
                calendar.add(Calendar.DATE, 1);
                continue;
            }

            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            String weekDay = "";
            String date = String.format("%d/%d/%d", dayOfMonth, month, year);
            Organista adult = new Organista(adultOrder[currentAdultIndex++]);
            Organista young = null;

            if (day == 1) weekDay = "Domingo";
            if (day == 7) weekDay = "Sabado";
            if (day == 4) weekDay = "Quarta-Feira";

            if (currentAdultIndex >= adultOrder.length) {
                currentAdultIndex = 0;
            }

            if (weekDay.equals("Domingo")) {
                young = new Organista(youngOrder[currentYoungIndex++]);

                if (currentYoungIndex >= youngOrder.length) {
                    currentYoungIndex = 0;
                }
            }

            days.add(new Day(weekDay, day == 1, adult, young, date));
            calendar.add(Calendar.DATE, 1);
        }

        return new Rodizio(Month.of(calendar.get(Calendar.MONTH)).toString(), days);
    }

    public void generateSheetsFormat(Rodizio rodizio) throws IOException {
        String title = String.format("%s/%d", rodizio.getMonth(), Calendar.getInstance().get(Calendar.YEAR));
        Sheets service = this.client.newSheetsService();

        List<Object> monthHeader = new ArrayList<>() {
            {
                add(rodizio.getMonth());
            }
        };

        List<Object> headers = new ArrayList<>() {
            {
                add("");
                add("Dia da semana");
                add("Meia Hora");
                add("Culto Oficial");
                add("Reunião de jovens");
            }
        };
        List<List<Object>> sheets = new ArrayList<>(){{
            add(monthHeader); add(headers);
        }};

        for (Day day : rodizio.getDays()) {
            List<Object> row = new ArrayList<>();
            row.add(day.getOrdinalDay());
            row.add(day.getWeekDay());
            row.add(""); // Inserting empty here because we dont have rules for "meia hora" yet
            row.add(day.getCultOraganista().getName());

            if (day.getYoungOraganista() != null) {
                row.add(day.getYoungOraganista().getName());
            } else {
                row.add("");
            }
            sheets.add(row);
        }

        SheetProperties properties = new SheetProperties();
        List<Request> requests = new ArrayList<>();

        requests.add(new Request().setAddSheet(new AddSheetRequest()
                .setProperties(properties.setTitle(title))
        ));

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(SPREADSHEETS_ID, body).execute();
        System.out.println(response.getReplies());

        ValueRange valueRange = new ValueRange().setValues(sheets);
        service.spreadsheets().values().update(SPREADSHEETS_ID, title + "!1:31", valueRange)
                .setValueInputOption("RAW")
                .execute();
    }
}
