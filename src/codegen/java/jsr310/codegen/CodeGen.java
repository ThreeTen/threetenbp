/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jsr310.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Code generates the repetitive parts of the JSR code.
 *
 * @author Stephen Colebourne
 */
public class CodeGen {

    private static final String TEMPLATE_DIR = "src/codegen/java/jsr310/codegen/";
    private static final File BASE_DIR = new File("src/main/java");
    private static final String MAIN_CALENDAR_PKG = "javax.time.calendar";
    private static final String MAIN_CALENDAR_FIELD_PKG = "javax.time.calendar.field";
    private static final String I18N_CALENDAR_FIELD_PKG = "javax.time.i18n";
    private static final File MAIN_PERIOD_DIR = new File("src/main/java/javax/time/period/field");
    private static final File TEST_DIR = new File("src/test/java/javax/time");
    private static final File TEST_CALENDAR_FIELD_DIR = new File("src/test/java/javax/time/calendar/field");
    private static final File TEST_PERIOD_FIELD_DIR = new File("src/test/java/javax/time/period/field");

    public static void main(String[] args) {
        try {
            Velocity.init();
            
            CodeGen cg = new CodeGen();
//            cg.processCalendarClassField();
//            cg.processDurationField();
//            cg.processTestDurationField();
//            cg.processTimeField();
            System.out.println("Done");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    /**
     * Code generate single field period classes.
     */
    private void processCalendarClassField() throws Exception {
        Template template = Velocity.getTemplate(TEMPLATE_DIR + "Calendar.vm");
        
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "Year",
                "year", "Era + YearOfEra",
                "Year", "Periods.YEARS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "YearMonth",
                "year-month", "Year + MonthOfYear",
                "YearMonth", "Periods.MONTHS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "MonthDay",
                "month-day", "MonthOfYear + DayOfMonth",
                "MonthDate", "Periods.DAYS", "Periods.MONTHS");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "DateYMD",
                "date", "Year + MonthOfYear + DayOfMonth",
                "YearMonthDate", "Periods.DAYS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "TimeHM",
                "time", "HourOfDay + MinuteOfHour + SecondOfMinute",
                "HourMinute", "Periods.HOURS", "Periods.DAYS");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "TimeHMS",
                "time", "HourOfDay + MinuteOfHour + SecondOfMinute",
                "HourMinuteSecondNano", "Periods.NANOS", "Periods.DAYS");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "DateTimeHM",
                "date-time", "Year + DayOfYear + SecondOfDay",
                "YearMonthDateHourMinute", "Periods.HOURS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "DateTimeHMS",
                "date-time", "Year + DayOfYear + SecondOfDay",
                "YearMonthDateHourMinuteSecondNano", "Periods.NANOS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "ZonedDateTimeHM",
                "date-time", "Year + DayOfYear + SecondOfDay",
                "YearMonthDateHourMinute", "Periods.HOURS", "Periods.FOREVER");
        processCalendarClassField(MAIN_CALENDAR_PKG, template, "ZonedDateTimeHMS",
                "date-time", "Year + DayOfYear + SecondOfDay",
                "YearMonthDateHourMinuteSecondNano", "Periods.NANOS", "Periods.FOREVER");
    }

    private void processCalendarClassField(
            String pkg,
            Template template,
            String classname,
            String desc,
            String calendricalExample,
            String includeCode,
            String unitStr,
            String rangeStr) throws Exception {
        
        File file = new File(BASE_DIR, pkg.replace('.', '/') + '/' + classname + ".java");
        List<String> methodLines = findAdditionalMethods(file, "public String toString() {");
        List<String> importLines = findImports(file);
        List<String> commentLines = findClassComment(file, "<p>");
        List<String> topMethodLines = findCalendarToplMethods(file);
        
        VelocityContext vc = new VelocityContext();
        vc.put("package", pkg);
        vc.put("Type", classname);
        vc.put("hashCode", Integer.toString(classname.hashCode()));
        String mixedType = classname.substring(0, 1).toLowerCase() + classname.substring(1);
        vc.put("type", mixedType);
        vc.put("desc", desc);
        vc.put("calendricalExample", calendricalExample);
        vc.put("zoned", classname.contains("Zoned"));
        vc.put("year", includeCode.contains("Year"));
        vc.put("month", includeCode.contains("Month"));
        vc.put("date", includeCode.contains("Date"));
        vc.put("hour", includeCode.contains("Hour"));
        vc.put("minute", includeCode.contains("Minute"));
        vc.put("second", includeCode.contains("Second"));
        vc.put("nano", includeCode.contains("Nano"));
        vc.put("unit", unitStr);
        vc.put("range", rangeStr);
        vc.put("imports", importLines);
        vc.put("comments", commentLines);
        vc.put("topMethods", topMethodLines);
        vc.put("methods", methodLines);
        generate(file, template, vc);
    }

    private List<String> findCalendarToplMethods(File file) throws Exception {
        List<String> methodLines = Collections.emptyList();
        if (file.exists()) {
            List<String> origLines = readFile(file);
            int topStart = indexOfLineContaining(origLines, "private static final long serialVersionUID", 0);
            int topEnd = indexOfLineContaining(origLines, "public CalendricalState getCalendricalState() {", 0);
            methodLines = origLines.subList(topStart + 2, topEnd - 8);
        }
        return methodLines;
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    /**
     * Code generate single field period classes.
     */
    private void processPeriodField() throws Exception {
        Template template = Velocity.getTemplate(TEMPLATE_DIR + "PeriodField.vm");
        
        processPeriodField(template, "Years", true, "Months.months(12)");
        processPeriodField(template, "Months", true, "null");
        processPeriodField(template, "Weeks", true, "Days.days(7)");
        processPeriodField(template, "Days", true, "Hours.hours(24)");
        processPeriodField(template, "Hours", false, "Minutes.minutes(60)");
        processPeriodField(template, "Minutes", false, "Seconds.seconds(60)");
        processPeriodField(template, "Seconds", false, "null");
    }

    private void processPeriodField(
            Template template,
            String classname, boolean date,
            String relativeField) throws Exception {
        File file = new File(MAIN_PERIOD_DIR, classname + ".java");
        List<String> methodLines = findAdditionalMethods(file, "public String toString() {");
        int pos = indexOfLineContaining(methodLines, "private static class Unit", 0);
        if (pos >= 4) {
            methodLines = methodLines.subList(0, pos - 4);
        }
        VelocityContext vc = createPeriodContext(classname, date, methodLines, relativeField);
        generate(file, template, vc);
    }

    //-----------------------------------------------------------------------
    private void processTestPeriodField() throws Exception {
        Template template = Velocity.getTemplate(TEMPLATE_DIR + "TestPeriodField.vm");
        
        processTestPeriodField(template, "Years", true);
        processTestPeriodField(template, "Months", true);
        processTestPeriodField(template, "Weeks", true);
        processTestPeriodField(template, "Days", true);
        processTestPeriodField(template, "Hours", false);
        processTestPeriodField(template, "Minutes", false);
        processTestPeriodField(template, "Seconds", false);
    }

    private void processTestPeriodField(Template template, String classname, boolean date) throws Exception {
        File file = new File(TEST_PERIOD_FIELD_DIR, "Test" + classname + ".java");
        List<String> methodLines = findAdditionalMethods(file, "public void test_toString() {");
        VelocityContext vc = createPeriodContext(classname, date, methodLines, "");
        generate(file, template, vc);
    }

    //-----------------------------------------------------------------------
    private VelocityContext createPeriodContext(
            String classname, boolean date, List<String> methodLines,
            String relativeField) {
        VelocityContext vc = new VelocityContext();
        vc.put("Type", classname);
        vc.put("type", classname.toLowerCase());
        vc.put("stringPrefix", date ? "" : "T");
        vc.put("stringSuffix", classname.substring(0, 1));
        vc.put("relativeField", relativeField);
        vc.put("methods", methodLines);
        return vc;
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private void processTimeField() throws Exception {
        Template regularTemplate = Velocity.getTemplate(TEMPLATE_DIR + "Field.vm");
        Template enumTemplate = Velocity.getTemplate(TEMPLATE_DIR + "EnumField.vm");
        
        processTimeField(MAIN_CALENDAR_FIELD_PKG, enumTemplate, "Era", "era", ERA, "0", "1");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "MilleniumOfEra", "millenium of era", null, "0", "Integer.MAX_VALUE / 1000");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "CenturyOfEra", "century of era", null, "0", "Integer.MAX_VALUE / 100");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "DecadeOfCentury", "decade of century", null, "0", "9");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "Year", "year", null, "Integer.MIN_VALUE", "Integer.MAX_VALUE");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "YearOfEra", "year of era", null, "1", "Integer.MAX_VALUE");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "Weekyear", "week-based year", null, "Integer.MIN_VALUE + 1", "Integer.MAX_VALUE -1");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, enumTemplate, "QuarterOfYear", "quarter of year", QUARTER_OF_YEAR, "1", "4");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, enumTemplate, "MonthOfYear", "month of year", MONTH_OF_YEARS, "1", "12");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "MonthOfQuarter", "month of quarter", null, "1", "3");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "WeekOfWeekyear", "week of week-based year", null, "1", "53");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "WeekOfMonth", "week of month", null, "1", "5");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "DayOfYear", "day of year", null, "1", "366");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "DayOfMonth", "day of month", null, "1", "31");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, enumTemplate, "DayOfWeek", "day of week", DAY_OF_WEEKS, "1", "7");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, enumTemplate, "MeridianOfDay", "meridian of day", MERIDIAN_OF_DAY, "0", "1");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "HourOfDay", "hour of day", null, "0", "23");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "HourOfMeridian", "hour of meridian", null, "0", "11");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "MinuteOfDay", "minute of day", null, "0", "1439");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "MinuteOfHour", "minute of hour", null, "0", "59");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "SecondOfDay", "second of day", null, "0", "86399");
        processTimeField(MAIN_CALENDAR_FIELD_PKG, regularTemplate, "SecondOfMinute", "second of minute", null, "0", "59");

        processTimeField(I18N_CALENDAR_FIELD_PKG, enumTemplate, "CopticMonthOfYear", "Coptic month of year", COPTIC_MONTH_OF_YEARS, "1", "13");
        processTimeField(I18N_CALENDAR_FIELD_PKG, enumTemplate, "CopticSeasonOfYear", "Coptic season of year", COPTIC_SEASON, "1", "3");
    }

    private void processTimeField(
            String pkg, Template template,
            String classname, String desc, FieldSingleton[] singletons,
            String minValue, String maxValue) throws Exception {
        File file = new File(BASE_DIR, pkg.replace('.', '/') + '/' + classname + ".java");
        List<String> methodLines = findAdditionalMethods(file,
              singletons == null ? "public int hashCode() {" : "public boolean isLessThan(");
        int pos = indexOfLineContaining(methodLines, "private static class Rule", 0);
        VelocityContext vc = new VelocityContext();
        vc.put("package", pkg);
        vc.put("Type", classname);
        String mixedType = classname.substring(0, 1).toLowerCase() + classname.substring(1);
        vc.put("type", mixedType);
        vc.put("desc", desc);
        vc.put("singletons", singletons == null ? Collections.EMPTY_LIST : Arrays.asList(singletons));
        vc.put("minValue", minValue);
        vc.put("maxValue", maxValue);
        vc.put("bound", classname.contains("Of"));
        vc.put("methods", methodLines);
        vc.put("ruleMethods", new ArrayList<String>());
        if (pos >= 4) {
            vc.put("methods", methodLines.subList(0, pos - 4));
            vc.put("ruleMethods", methodLines.subList(pos + 6, methodLines.size() - 2));
        }
        generate(file, template, vc);
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    private List<String> findAdditionalMethods(File file, String lastMethodSig) throws Exception {
        List<String> methodLines = Collections.emptyList();
        if (file.exists()) {
            List<String> origLines = readFile(file);
            int startToStringPos = indexOfLineContaining(origLines, lastMethodSig, 0);
            int endToStringPos = indexOfEmptyLine(origLines, startToStringPos);
            methodLines = origLines.subList(endToStringPos + 1, origLines.size() - 1);
        }
        return methodLines;
    }

    private List<String> findImports(File file) throws Exception {
        List<String> methodLines = Collections.emptyList();
        if (file.exists()) {
            List<String> origLines = readFile(file);
            int start = indexOfLineContaining(origLines, "import ", 0);
            int end = indexOfLineContaining(origLines, "/**", start);
            methodLines = origLines.subList(start, end);
        }
        return methodLines;
    }

    private List<String> findClassComment(File file, String search) throws Exception {
        List<String> methodLines = Collections.emptyList();
        if (file.exists()) {
            List<String> origLines = readFile(file);
            int start = indexOfLineContaining(origLines, "/**", 0);
            int end = indexOfLineContaining(origLines, "*/", start);
            end = lastIndexOfLineContaining(origLines, search, end);
            methodLines = origLines.subList(start + 1, end);
        }
        return methodLines;
    }

    //-----------------------------------------------------------------------
    /**
     * Generate the output file using the template and context.
     * 
     * @param file  the file to output to
     * @param template  the velocity template
     * @param vc  the velocity context
     */
    private void generate(File file, Template template, VelocityContext vc) throws Exception {
        FileWriter out = new FileWriter(file);
        template.merge(vc, out);
        out.close();
    }

    //-----------------------------------------------------------------------
    private List<String> readFile(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();
        return lines;
    }

//    private void writeFile(File file, List<String> lines) throws Exception {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//        for (String line : lines) {
//            writer.write(line);
//            writer.newLine();
//        }
//        writer.close();
//    }

    private int indexOfEmptyLine(List<String> lines, int start) throws Exception {
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() == 0) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfLineContaining(List<String> lines, String part, int start) throws Exception {
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains(part)) {
                return i;
            }
        }
        return -1;
    }

    private int lastIndexOfLineContaining(List<String> lines, String part, int end) throws Exception {
        for (int i = end; i >= 0; i--) {
            String line = lines.get(i);
            if (line.contains(part)) {
                return i;
            }
        }
        return -1;
    }

    //-----------------------------------------------------------------------
    private static final FieldSingleton[] MONTH_OF_YEARS = new FieldSingleton[] {
        new FieldSingleton("JANUARY", "The singleton instance for the month of January.", "1"),
        new FieldSingleton("FEBRUARY", "The singleton instance for the month of February.", "2"),
        new FieldSingleton("MARCH", "The singleton instance for the month of March.", "3"),
        new FieldSingleton("APRIL", "The singleton instance for the month of April.", "4"),
        new FieldSingleton("MAY", "The singleton instance for the month of May.", "5"),
        new FieldSingleton("JUNE", "The singleton instance for the month of June.", "6"),
        new FieldSingleton("JULY", "The singleton instance for the month of July.", "7"),
        new FieldSingleton("AUGUST", "The singleton instance for the month of August.", "8"),
        new FieldSingleton("SEPTEMBER", "The singleton instance for the month of September.", "9"),
        new FieldSingleton("OCTOBER", "The singleton instance for the month of October.", "10"),
        new FieldSingleton("NOVEMBER", "The singleton instance for the month of November.", "11"),
        new FieldSingleton("DECEMBER", "The singleton instance for the month of December.", "12"),
    };

    private static final FieldSingleton[] DAY_OF_WEEKS = new FieldSingleton[] {
        new FieldSingleton("MONDAY", "The singleton instance for the day of week of Monday.", "1"),
        new FieldSingleton("TUESDAY", "The singleton instance for the day of week of Tuesday.", "2"),
        new FieldSingleton("WEDNESDAY", "The singleton instance for the day of week of Wednesday.", "3"),
        new FieldSingleton("THURSDAY", "The singleton instance for the day of week of Thursday.", "4"),
        new FieldSingleton("FRIDAY", "The singleton instance for the day of week of Friday.", "5"),
        new FieldSingleton("SATURDAY", "The singleton instance for the day of week of Saturday.", "6"),
        new FieldSingleton("SUNDAY", "The singleton instance for the day of week of Sunday.", "7"),
    };

    private static final FieldSingleton[] MERIDIAN_OF_DAY = new FieldSingleton[] {
        new FieldSingleton("AM", "The singleton instance for the morning (ante meridian).", "0"),
        new FieldSingleton("PM", "The singleton instance for the afternoon (post meridian).", "1"),
    };

    private static final FieldSingleton[] QUARTER_OF_YEAR = new FieldSingleton[] {
        new FieldSingleton("Q1", "The singleton instance for the first quarter of year, from January to March.", "1"),
        new FieldSingleton("Q2", "The singleton instance for the second quarter of year, from April to June.", "2"),
        new FieldSingleton("Q3", "The singleton instance for the third quarter of year, from July to September.", "3"),
        new FieldSingleton("Q4", "The singleton instance for the fourth quarter of year, from October to December.", "4"),
    };

    private static final FieldSingleton[] ERA = new FieldSingleton[] {
        new FieldSingleton("BC", "The singleton instance for the last Era, BC/BCE.", "0"),
        new FieldSingleton("AD", "The singleton instance for the current Era, AD/CE.", "1"),
    };

    private static final FieldSingleton[] COPTIC_MONTH_OF_YEARS = new FieldSingleton[] {
        new FieldSingleton("THOUT", "The singleton instance for the month of Thout.", "1"),
        new FieldSingleton("PAOPI", "The singleton instance for the month of Paopi.", "2"),
        new FieldSingleton("HATHOR", "The singleton instance for the month of Hathor.", "3"),
        new FieldSingleton("KOIAK", "The singleton instance for the month of Koiak.", "4"),
        new FieldSingleton("TOBI", "The singleton instance for the month of Tobi.", "5"),
        new FieldSingleton("MESHIR", "The singleton instance for the month of Meshir.", "6"),
        new FieldSingleton("PAREMHAT", "The singleton instance for the month of Paremhat.", "7"),
        new FieldSingleton("PAREMOUDE", "The singleton instance for the month of Paremoude.", "8"),
        new FieldSingleton("PASHONS", "The singleton instance for the month of Pashons.", "9"),
        new FieldSingleton("PAONI", "The singleton instance for the month of Paoni.", "10"),
        new FieldSingleton("EPIP", "The singleton instance for the month of Epip.", "11"),
        new FieldSingleton("MESORI", "The singleton instance for the month of Mesori.", "12"),
        new FieldSingleton("PI_KOGI_ENAVOT", "The singleton instance for the month of Pi Kogi Enavot.", "13"),
    };

    private static final FieldSingleton[] COPTIC_SEASON = new FieldSingleton[] {
        new FieldSingleton("AKHET", "The singleton instance for the first season Akhet, the season of innundation (floods of the River Nile).", "1"),
        new FieldSingleton("PROYET", "The singleton instance for the second season Proyet, the season of growth.", "2"),
        new FieldSingleton("SHOMU", "The singleton instance for the third season Shomu, the season of harvest.", "3"),
    };

    public static class FieldSingleton {
        private String type;
        private String desc;
        private String value;
        public FieldSingleton(String type, String desc, String value) {
            super();
            this.type = type;
            this.desc = desc;
            this.value = value;
        }
        public String getDesc() {
            return desc;
        }
        public String getType() {
            return type;
        }
        public String getValue() {
            return value;
        }
    }

}
