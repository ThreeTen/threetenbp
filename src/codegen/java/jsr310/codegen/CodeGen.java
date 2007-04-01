/*
 * Written by the members of JCP JSR-310 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package jsr310.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Code generates the repetitive parts of the JSR code.
 * 
 * @author Stephen Colebourne
 */
public class CodeGen {

    private static final File TEMPLATE_DIR = new File("src/codegen/java/jsr310/codegen");
    private static final File GENERATED_DIR = new File("src/main/java/javax/time");

    public static void main(String[] args) {
        try {
            CodeGen cg = new CodeGen();
            cg.processPeriod();
            cg.processField();
            System.out.println("Done");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------
    public void processPeriod() throws Exception {
        File templateFile = new File(TEMPLATE_DIR, "Period.template");
        List<String> templateLines = Collections.unmodifiableList(readFile(templateFile));
        processPeriod(templateLines, "Years", true);
        processPeriod(templateLines, "Months", true);
        processPeriod(templateLines, "Weeks", true);
        processPeriod(templateLines, "Days", true);
        processPeriod(templateLines, "Hours", false);
        processPeriod(templateLines, "Minutes", false);
        processPeriod(templateLines, "Seconds", false);
    }

    public void processPeriod(List<String> templateLines, String classname, boolean date) throws Exception {
        File file = new File(GENERATED_DIR, classname + ".java");
        
        // find the part of the file that has been customised
        List<String> origLines = Collections.unmodifiableList(readFile(file, templateLines));
        int startToStringPos = indexOfLineContaining(origLines, "public String toString() {", 0);
        int endToStringPos = indexOfEmptyLine(origLines, startToStringPos);
        List<String> methodLines = origLines.subList(endToStringPos + 1, origLines.size() - 1);
        
        // loop around template inserting data
        List<String> lines = new ArrayList<String>(templateLines);
        int insertPoint = -1;
        for (ListIterator<String> it = lines.listIterator(); it.hasNext(); ) {
            String line = it.next();
            line = line.replaceAll("[$][{]Type[}]", classname);
            line = line.replaceAll("[$][{]type[}]", classname.toLowerCase());
            line = line.replaceAll("[$][{]stringPrefix[}]", date ? "" : "T");
            line = line.replaceAll("[$][{]stringSuffix[}]", classname.substring(0, 1));
            if (line.equals("${Methods}")) {
                insertPoint = it.previousIndex();
                it.remove();
            } else {
                it.set(line);
            }
        }
        
        // add in custom methods
        lines.addAll(insertPoint, methodLines);
        
        // save
        writeFile(file, lines);
    }

    //-----------------------------------------------------------------------
    public void processField() throws Exception {
        File templateFile = new File(TEMPLATE_DIR, "Field.template");
        List<String> templateLines = Collections.unmodifiableList(readFile(templateFile));
        processField(templateLines, "Year", "year", null);
        processField(templateLines, "MonthOfYear", "month of year", MONTH_OF_YEARS);
        processField(templateLines, "DayOfYear", "day of year", null);
        processField(templateLines, "DayOfMonth", "day of month", null);
        processField(templateLines, "DayOfWeek", "day of week", DAY_OF_WEEKS);
    }

    public void processField(
            List<String> templateLines,
            String classname,
            String desc,
            String[][] singletons) throws Exception {
        File file = new File(GENERATED_DIR, classname + ".java");
        
        // find the part of the file that has been customised
        List<String> origLines = Collections.unmodifiableList(readFile(file, templateLines));
        int startToStringPos = indexOfLineContaining(origLines, "public int hashCode() {", 0);
        int endToStringPos = indexOfEmptyLine(origLines, startToStringPos);
        List<String> methodLines = origLines.subList(endToStringPos + 1, origLines.size() - 1);
        
        // loop around template inserting data
        String mixedType = classname.substring(0, 1).toLowerCase() + classname.substring(1);
        List<String> lines = new ArrayList<String>(templateLines);
        int insertPoint = -1;
        for (ListIterator<String> it = lines.listIterator(); it.hasNext(); ) {
            String line = it.next();
            line = line.replaceAll("[$][{]Type[}]", classname);
            line = line.replaceAll("[$][{]type[}]", mixedType);
            line = line.replaceAll("[$][{]desc[}]", desc);
            if (line.equals("${Constants}")) {
                it.remove();
                if (singletons != null) {
                    for (String[] singleton : singletons) {
                        it.add("    /**");
                        it.add("     * " + singleton[1]);
                        it.add("     */");
                        it.add("    public static final " + classname + " " + singleton[0] +
                                " = new " + classname + "(" + singleton[2] + ");");
                    }
                    it.add("");
                }
            } else if (line.startsWith("${Singletons}")) {
                if (singletons != null) {
                    it.remove();
                    it.add("        switch (" + mixedType + ") {");
                    for (String[] singleton : singletons) {
                        it.add("            case " + singleton[2] + ":");
                        it.add("                return " + singleton[0] + ";");
                    }
                    it.add("            default:");
                    it.add("                throw new IllegalArgumentException(\"" + classname + " cannot have the value \" + " + mixedType + ");");
                    it.add("        }");
                } else {
                    it.set(line.substring(13));
                }
            } else if (line.equals("${Methods}")) {
                insertPoint = it.previousIndex();
                it.remove();
            } else {
                it.set(line);
            }
        }
        
        // add in custom methods
        lines.addAll(insertPoint, methodLines);
        
        // save
        writeFile(file, lines);
    }

    //-----------------------------------------------------------------------
    public List<String> readFile(File file, List<String> defaultLines) throws Exception {
        if (file.exists() == false) {
            List<String> lines = new ArrayList<String>(defaultLines);
            int pos = indexOfLineContaining(lines, "${Methods}", 0);
            lines.remove(pos);
            return lines;
        }
        return readFile(file);
    }

    public List<String> readFile(File file) throws Exception {
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

    public void writeFile(File file, List<String> lines) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public int indexOfEmptyLine(List<String> lines, int start) throws Exception {
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() == 0) {
                return i;
            }
        }
        return -1;
    }

    public int indexOfLineContaining(List<String> lines, String part, int start) throws Exception {
        for (int i = start; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains(part)) {
                return i;
            }
        }
        return -1;
    }

    //-----------------------------------------------------------------------
    private static final String[][] MONTH_OF_YEARS = new String[][] {
        {"JANUARY", "The singleton instance for the month of January.", "1"},
        {"FEBRUARY", "The singleton instance for the month of February.", "2"},
        {"MARCH", "The singleton instance for the month of March.", "3"},
        {"APRIL", "The singleton instance for the month of April.", "4"},
        {"MAY", "The singleton instance for the month of May.", "5"},
        {"JUNE", "The singleton instance for the month of June.", "6"},
        {"JULY", "The singleton instance for the month of July.", "7"},
        {"AUGUST", "The singleton instance for the month of August.", "18"},
        {"SEPTEMBER", "The singleton instance for the month of September.", "9"},
        {"OCTOBER", "The singleton instance for the month of October.", "10"},
        {"NOVEMBER", "The singleton instance for the month of November.", "11"},
        {"DECEMBER", "The singleton instance for the month of December.", "12"},
    };

    private static final String[][] DAY_OF_WEEKS = new String[][] {
        {"MONDAY", "The singleton instance for the day of week of Monday.", "1"},
        {"TUESDAY", "The singleton instance for the day of week of Tuesday.", "2"},
        {"WEDNESDAY", "The singleton instance for the day of week of Wednesday.", "3"},
        {"THURSDAY", "The singleton instance for the day of week of Thursday.", "4"},
        {"FRIDAY", "The singleton instance for the day of week of Friday.", "5"},
        {"SATURDAY", "The singleton instance for the day of week of Saturday.", "6"},
        {"SUNDAY", "The singleton instance for the day of week of Sunday.", "7"},
    };

}
