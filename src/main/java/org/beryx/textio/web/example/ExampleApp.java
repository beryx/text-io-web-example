/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.textio.web.example;

import com.google.gson.Gson;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.web.RunnerData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The Text-IO runner of this application
 */
public class ExampleApp implements BiConsumer<TextIO, RunnerData> {
    private static Dictionary[] DICTIONARIES = {
            new Dictionary("English", Locale.US, "EEEE, MMMM dd, yyyy","On-Call Schedule", "Date", "Name"),
            new Dictionary("German", Locale.GERMANY, "EEEE, dd MMMM yyyy", "Bereitschaftsplan", "Datum", "Name"),
            new Dictionary("French", Locale.FRANCE, "EEEE, dd MMMM yyyy", "Horaires de garde", "Date", "Nom"),
    };

    private static class Dictionary {
        final String language;
        final Locale locale;
        final String datePattern;
        final String title;
        final String date;
        final String name;

        Dictionary(String language, Locale locale, String datePattern, String title, String date, String name) {
            this.language = language;
            this.locale = locale;
            this.datePattern = datePattern;
            this.title = title;
            this.date = date;
            this.name = name;
        }

        DateFormat getDateFormat() {
            return new SimpleDateFormat(datePattern, locale);
        }

        @Override
        public String toString() {
            return language;
        }
    }

    private static class ResultData {
        @SuppressWarnings("unused")
        final String title;
        final Map<String, String> tableHeaders = new LinkedHashMap<>();
        final Map<String, String>[] tableData;

        @SuppressWarnings("unchecked")
        ResultData(String title, int rowCount) {
            this.title = title;
            this.tableData = new Map[rowCount];
        }
    }

    private static class SimpleDate {
        int year;
        int month;
        int day;
    }
    private static Calendar getCalendar(String initData) {
        Calendar cal = Calendar.getInstance();
        if(initData != null && !initData.isEmpty()) {
            SimpleDate simpleDate = new Gson().fromJson(initData, SimpleDate.class);
            if(simpleDate != null) {
                cal.set(Calendar.YEAR, 1900 + simpleDate.year);
                cal.set(Calendar.MONTH, simpleDate.month);
                cal.set(Calendar.DAY_OF_MONTH, simpleDate.day);
            }
        }
        return cal;
    }

    @Override
    public void accept(TextIO textIO, RunnerData runnerData) {
        int dayCount = textIO.newIntInputReader()
                .withMinVal(3)
                .withMaxVal(7)
                .read("For how many days do you plan the schedule?");

        String[] names = new String[dayCount];
        String initData = (runnerData == null) ? null : runnerData.getInitData();
        Calendar cal = getCalendar(initData);
        DateFormat sdf = DICTIONARIES[0].getDateFormat();
        for(int i = 0; i < dayCount; i++) {
            names[i] = textIO.newStringInputReader()
                    .read("Who is on-call on " + sdf.format(cal.getTime()) + "?");
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Dictionary dict = textIO.<Dictionary>newGenericInputReader(null)
                .withNumberedPossibleValues(DICTIONARIES)
                .read("In which language should the on-call schedule be generated?");

        textIO.newStringInputReader().withMinLength(0).read("\nPress enter to generate the schedule...");

        sdf = dict.getDateFormat();
        ResultData res = new ResultData(dict.title, dayCount);
        res.tableHeaders.put("date", dict.date);
        res.tableHeaders.put("name", dict.name);

        cal = getCalendar(initData);
        for(int i = 0; i < dayCount; i++) {
            res.tableData[i] = new LinkedHashMap<>();
            res.tableData[i].put("date", sdf.format(cal.getTime()));
            res.tableData[i].put("name", names[i]);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        textIO.dispose(new Gson().toJson(res));
    }

    public static void main(String[] args) {
        TextIO textIO = TextIoFactory.getTextIO();
        new ExampleApp().accept(textIO, null);
    }
}
