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
    private static final File MAIN_DIR = new File("src/main/java/javax/time");
    private static final File MAIN_CALENDAR_DIR = new File("src/main/java/javax/time/calendar/field");
    private static final File MAIN_DURATION_DIR = new File("src/main/java/javax/time/duration/field");
    private static final File TEST_DIR = new File("src/test/java/javax/time");
    private static final File TEST_CALENDAR_DIR = new File("src/test/java/javax/time/calendar/field");
    private static final File TEST_DURATION_DIR = new File("src/test/java/javax/time/duration/field");

    public static void main(String[] args) {
        try {
            Velocity.init();
            
            CodeGen cg = new CodeGen();
//            cg.processDurationField();
//            cg.processTestDurationField();
            cg.processTimeField();
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
    private void processDurationField() throws Exception {
        Template template = Velocity.getTemplate(TEMPLATE_DIR + "DurationField.vm");
        
        processDurationField(template, "Years", true, "Months.months(12)");
        processDurationField(template, "Months", true, "null");
        processDurationField(template, "Weeks", true, "Days.days(7)");
        processDurationField(template, "Days", true, "Hours.hours(24)");
        processDurationField(template, "Hours", false, "Minutes.minutes(60)");
        processDurationField(template, "Minutes", false, "Seconds.seconds(60)");
        processDurationField(template, "Seconds", false, "null");
    }

    private void processDurationField(
            Template template,
            String classname, boolean date,
            String relativeField) throws Exception {
        File file = new File(MAIN_DURATION_DIR, classname + ".java");
        List<String> methodLines = findAdditionalMethods(file, "public String toString() {");
        int pos = indexOfLineContaining(methodLines, "private static class Rule", 0);
        if (pos >= 4) {
            methodLines = methodLines.subList(0, pos - 4);
        }
        VelocityContext vc = createPeriodContext(classname, date, methodLines, relativeField);
        generate(file, template, vc);
    }

    //-----------------------------------------------------------------------
    private void processTestDurationField() throws Exception {
        Template template = Velocity.getTemplate(TEMPLATE_DIR + "TestDurationField.vm");
        
        processTestDurationField(template, "Years", true);
        processTestDurationField(template, "Months", true);
        processTestDurationField(template, "Weeks", true);
        processTestDurationField(template, "Days", true);
        processTestDurationField(template, "Hours", false);
        processTestDurationField(template, "Minutes", false);
        processTestDurationField(template, "Seconds", false);
    }

    private void processTestDurationField(Template template, String classname, boolean date) throws Exception {
        File file = new File(TEST_DURATION_DIR, "Test" + classname + ".java");
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
        
        processTimeField(enumTemplate, "Era", "era", ERA, "0", "1");
        processTimeField(regularTemplate, "MilleniumOfEra", "millenium of era", null, "0", "Integer.MAX_VALUE / 1000");
        processTimeField(regularTemplate, "CenturyOfEra", "century of era", null, "0", "Integer.MAX_VALUE / 100");
        processTimeField(regularTemplate, "DecadeOfCentury", "decade of century", null, "0", "9");
        processTimeField(regularTemplate, "Year", "year", null, "Integer.MIN_VALUE", "Integer.MAX_VALUE");
        processTimeField(regularTemplate, "YearOfEra", "year of era", null, "1", "Integer.MAX_VALUE");
        processTimeField(regularTemplate, "Weekyear", "week-based year", null, "Integer.MIN_VALUE + 1", "Integer.MAX_VALUE -1");
        processTimeField(enumTemplate, "QuarterOfYear", "quarter of year", QUARTER_OF_YEAR, "1", "4");
        processTimeField(enumTemplate, "MonthOfYear", "month of year", MONTH_OF_YEARS, "1", "12");
        processTimeField(regularTemplate, "MonthOfQuarter", "month of quarter", null, "1", "3");
        processTimeField(regularTemplate, "WeekOfWeekyear", "week of week-based year", null, "1", "53");
        processTimeField(regularTemplate, "WeekOfMonth", "week of month", null, "1", "5");
        processTimeField(regularTemplate, "DayOfYear", "day of year", null, "1", "366");
        processTimeField(regularTemplate, "DayOfMonth", "day of month", null, "1", "31");
        processTimeField(enumTemplate, "DayOfWeek", "day of week", DAY_OF_WEEKS, "1", "7");
        processTimeField(enumTemplate, "MeridianOfDay", "meridian of day", MERIDIAN_OF_DAY, "0", "1");
        processTimeField(regularTemplate, "HourOfDay", "hour of day", null, "0", "23");
        processTimeField(regularTemplate, "HourOfMeridian", "hour of meridian", null, "0", "11");
        processTimeField(regularTemplate, "MinuteOfDay", "minute of day", null, "0", "1439");
        processTimeField(regularTemplate, "MinuteOfHour", "minute of hour", null, "0", "59");
        processTimeField(regularTemplate, "SecondOfDay", "second of day", null, "0", "86399");
        processTimeField(regularTemplate, "SecondOfMinute", "second of minute", null, "0", "59");
    }

    private void processTimeField(
            Template template,
            String classname, String desc, FieldSingleton[] singletons,
            String minValue, String maxValue) throws Exception {
        File file = new File(MAIN_CALENDAR_DIR, classname + ".java");
        List<String> methodLines = findAdditionalMethods(file,
              singletons == null ? "public int hashCode() {" : "public boolean isLessThan(");
        int pos = indexOfLineContaining(methodLines, "private static class Rule", 0);
        VelocityContext vc = new VelocityContext();
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
