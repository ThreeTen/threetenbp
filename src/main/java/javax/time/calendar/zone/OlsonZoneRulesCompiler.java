/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.time.calendar.Calendrical;
import javax.time.calendar.DateAdjusters;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;
import javax.time.period.Period;

/**
 * A builder that can read the Olson TimeZone files and build ZoneRules instances.
 * <p>
 * OlsonZoneRulesCompiler is thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class OlsonZoneRulesCompiler {

    /**
     * A map to deduplicate object instances.
     */
    private final Map<Object, Object> deduplicateMap = new HashMap<Object, Object>();

    /**
     * The olson rules.
     */
    private final Map<String, List<OlsonRule>> rules = new HashMap<String, List<OlsonRule>>();
    /**
     * The olson zones.
     */
    private final Map<String, List<OlsonZone>> zones = new HashMap<String, List<OlsonZone>>();
    /**
     * The olson links.
     */
    private final Map<String, String> links = new HashMap<String, String>();
    /**
     * The built zones.
     */
    private final Map<String, TimeZone> builtZones = new HashMap<String, TimeZone>();

    /**
     * Reads a set of Olson files and builds a single combined data file.
     *
     * @param args  the arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            outputHelp();
            return;
        }
        
        // parse args
        String version = null;
        File srcDir = null;
        File dstDir = null;
        boolean verbose = false;
        
        // parse options
        File baseDir = new File(System.getProperty("user.dir"));
        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-") == false) {
                break;
            }
            if ("-srcdir".equals(arg)) {
                if (srcDir == null && ++i < args.length) {
                    srcDir = new File(baseDir, args[i]);
                    continue;
                }
            } else if ("-dstdir".equals(arg)) {
                if (dstDir == null && ++i < args.length) {
                    dstDir = new File(baseDir, args[i]);
                    continue;
                }
            } else if ("-version".equals(arg)) {
                if (version == null && ++i < args.length) {
                    version = args[i];
                    continue;
                }
            } else if ("-verbose".equals(arg)) {
                if (verbose == false) {
                    verbose = true;
                    continue;
                }
            } else if ("-help".equals(arg) == false) {
                System.out.println("Unrecognised option: " + arg);
            }
            outputHelp();
            return;
        }
        if (version == null) {
            System.out.println("Missing -version");
            return;
        }
        srcDir = (srcDir != null ? srcDir : baseDir);
        dstDir = (dstDir != null ? dstDir : baseDir);
        
        // parse source files
        if (i >= args.length) {
            System.out.println("Missing source files");
            outputHelp();
            return;
        }
        List<File> sourceFiles = new ArrayList<File>();
        for ( ; i < args.length; i++) {
            File file = new File(srcDir, args[i]);
            if (file.exists() == false) {
                System.out.println("Source file does not exist: " + file);
                return;
            }
            sourceFiles.add(file);
        }
        
        // check destination directory
        if (dstDir.exists() == false && dstDir.mkdirs() == false) {
            System.out.println("Destination directory could not be created: " + dstDir);
            return;
        }
        if (dstDir.isDirectory() == false) {
            System.out.println("Destination is not a directory: " + dstDir);
            return;
        }
        
        // compile
        OlsonZoneRulesCompiler compiler = new OlsonZoneRulesCompiler(version, sourceFiles, dstDir, verbose);
        try {
            compiler.compile();
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.toString());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Output usage text for the command line.
     */
    private static void outputHelp() {
        System.out.println("Usage: OlsonZoneRulesCompiler <options> <source files>");
        System.out.println("where options include:");
        System.out.println("   -version <version>      Specify the version name, such as 2009a (required)");
        System.out.println("   -srcdir <directory>     Specify where to find Olson source files");
        System.out.println("   -dstdir <directory>     Specify where to output the generated file");
        System.out.println("   -help                   Print this usage message");
        System.out.println("   -verbose                Output verbose information during compilation");
    }

    //-----------------------------------------------------------------------
    /** The version to produce. */
    private final String version;
    /** The version to produce. */
    private final List<File> sourceFiles;
    /** The version to produce. */
    private final File destinationDir;
    /** The version to produce. */
    private final boolean verbose;

    /**
     * Constructor used if you want to invoke the compiler manually.
     *
     * @param version  the version, such as 2009a, not null
     * @param sourceFiles  the list of source files, not empty, not null
     * @param destinationDir  the destination directory, not null
     * @param verbose  whether to output verbose messages
     */
    public OlsonZoneRulesCompiler(String version, List<File> sourceFiles, File destinationDir, boolean verbose) {
        this.version = version;
        this.sourceFiles = sourceFiles;
        this.destinationDir = destinationDir;
        this.verbose = verbose;
    }

    /**
     * Compile the rules file.
     * @throws Exception if an error occurs
     */
    public void compile() throws Exception {
        printVerbose("Compiling Olson version " + version + " to directory " + destinationDir);
        parseFiles();
        buildZoneRules();
        outputFile();
        printVerbose("Compiled Olson version " + version + " to directory " + destinationDir);
    }

    //-----------------------------------------------------------------------
    /**
     * Parses the source files.
     * @throws Exception if an error occurs
     */
    private void parseFiles() throws Exception {
        for (File file : sourceFiles) {
            printVerbose("Parsing file: " + file);
            parseFile(file);
        }
    }

    /**
     * Parses a source file.
     * @param file  the file being read, not null
     * @throws Exception if an error occurs
     */
    private void parseFile(File file) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        List<OlsonZone> openZone = null;
        String line;
        while ((line = in.readLine()) != null) {
            int index = line.indexOf('#');
            if (index >= 0) {
                line = line.substring(0, index);
            }
            if (line.trim().length() == 0) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(line, " \t");
            if (openZone != null && Character.isWhitespace(line.charAt(0)) && st.hasMoreTokens()) {
                if (parseZoneLine(st, openZone)) {
                    openZone = null;
                }
            } else {
                if (st.hasMoreTokens()) {
                    String first = st.nextToken();
                    if (first.equals("Zone")) {
                        if (st.countTokens() < 3) {
                            printVerbose("Invalid Zone line in file: " + file + ", line: " + line);
                            throw new IllegalArgumentException("Invalid Zone line in file: " + file);
                        }
                        openZone = new ArrayList<OlsonZone>();
                        zones.put(st.nextToken(), openZone);
                        if (parseZoneLine(st, openZone)) {
                            openZone = null;
                        }
                    } else {
                        openZone = null;
                        if (first.equals("Rule")) {
                            if (st.countTokens() < 9) {
                                printVerbose("Invalid Rule line in file: " + file + ", line: " + line);
                                throw new IllegalArgumentException("Invalid Rule line in file: " + file);
                            }
                            parseRuleLine(st);
                            
                        } else if (first.equals("Link")) {
                            if (st.countTokens() < 2) {
                                printVerbose("Invalid Link line in file: " + file + ", line: " + line);
                                throw new IllegalArgumentException("Invalid Link line in file: " + file);
                            }
                            links.put(st.nextToken(), st.nextToken());
                            
                        } else {
                            System.out.println("Unknown line: " + line);
                        }
                    }
                }
            }
        }
        in.close();
    }

    /**
     * Parses a Rule line.
     * @param st  the tokerizer, not null
     */
    private void parseRuleLine(StringTokenizer st) {
        OlsonRule rule = new OlsonRule();
        String name = st.nextToken();
        if (rules.containsKey(name) == false) {
            rules.put(name, new ArrayList<OlsonRule>());
        }
        rules.get(name).add(rule);
        rule.startYear = parseYear(st.nextToken(), 0);
        rule.endYear = parseYear(st.nextToken(), rule.startYear);
        if (rule.startYear > rule.endYear) {
            throw new IllegalArgumentException("Year order invalid: " + rule.startYear + " > " + rule.endYear);
        }
        parseOptional(st.nextToken());  // type is unused
        parseMonthDayTime(st, rule);
        rule.savingsAmount = parsePeriod(st.nextToken());
        rule.text = parseOptional(st.nextToken());
    }

    /**
     * Parses a Zone line.
     * @param st  the tokerizer, not null
     * @return true if the zone is complete
     */
    private boolean parseZoneLine(StringTokenizer st, List<OlsonZone> zoneList) {
        OlsonZone zone = new OlsonZone();
        zoneList.add(zone);
        zone.standardOffset = parseOffset(st.nextToken());
        String savingsRule = parseOptional(st.nextToken());
        if (savingsRule == null) {
            zone.fixedSavings = Period.ZERO;
            zone.savingsRule = null;
        } else {
            try {
                zone.fixedSavings = parsePeriod(savingsRule);
                zone.savingsRule = null;
            } catch (Exception ex) {
                zone.fixedSavings = null;
                zone.savingsRule = savingsRule;
            }
        }
        zone.text = st.nextToken();
        if (st.hasMoreTokens()) {
            zone.year = Year.isoYear(Integer.parseInt(st.nextToken()));
            if (st.hasMoreTokens()) {
                parseMonthDayTime(st, zone);
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Parses a Rule line.
     * @param st  the tokerizer, not null
     * @param mdt  the object to parse into, not null
     */
    private void parseMonthDayTime(StringTokenizer st, OlsonMonthDayTime mdt) {
        mdt.month = parseMonth(st.nextToken());
        if (st.hasMoreTokens()) {
            String dayRule = st.nextToken();
            if (dayRule.startsWith("last")) {
                mdt.dayOfMonth = -1;
                mdt.dayOfWeek = parseDayOfWeek(dayRule.substring(4));
                mdt.adjustForwards = false;
            } else {
                int index = dayRule.indexOf(">=");
                if (index > 0) {
                    mdt.dayOfWeek = parseDayOfWeek(dayRule.substring(0, index));
                    dayRule = dayRule.substring(index + 2);
                } else {
                    index = dayRule.indexOf("<=");
                    if (index > 0) {
                        mdt.dayOfWeek = parseDayOfWeek(dayRule.substring(0, index));
                        mdt.adjustForwards = false;
                        dayRule = dayRule.substring(index + 2);
                    }
                }
                mdt.dayOfMonth = Integer.parseInt(dayRule);
            }
            if (st.hasMoreTokens()) {
                String time = st.nextToken();
                mdt.time = parseTime(time);
                mdt.timeDefinition = parseTimeDefinition(time.charAt(time.length() - 1));
            }
        }
    }

    //-----------------------------------------------------------------------
    private int parseYear(String str, int defaultYear) {
        str = str.toLowerCase();
        if (str.equals("minimum") || str.equals("min")) {
            return Year.MIN_YEAR;
        } else if (str.equals("maximum") || str.equals("max")) {
            return Year.MAX_YEAR;
        } else if (str.equals("only")) {
            return defaultYear;
        }
        return Integer.parseInt(str);
    }

    private MonthOfYear parseMonth(String str) {
        int index = "JanFebMarAprMayJunJulAugSepOctNovDec".indexOf(str);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown month: " + str);
        }
        int month = index / 3 + 1;
        return MonthOfYear.monthOfYear(month);
    }

    private DayOfWeek parseDayOfWeek(String str) {
        int index = "MonTueWedThuFriSatSun".indexOf(str);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown day of week: " + str);
        }
        int dow = index / 3 + 1;
        return DayOfWeek.dayOfWeek(dow);
    }

    private String parseOptional(String str) {
        return str.equals("-") ? null : str;
    }

    private LocalTime parseTime(String str) {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
            .appendValue(HourOfDay.rule())
            .optionalStart().appendLiteral(':').appendValue(MinuteOfHour.rule(), 2)
            .optionalStart().appendLiteral(':').appendValue(SecondOfMinute.rule(), 2)
            .toFormatter();
        ParsePosition pp = new ParsePosition(0);
        Calendrical cal = f.parse(str, pp);
        if (pp.getErrorIndex() >= 0) {
            throw new IllegalArgumentException(str);
        }
        return deduplicate(cal.mergeStrict().toLocalTime());
    }

    private int parseSecs(String str) {
        DateTimeFormatter f = new DateTimeFormatterBuilder()
            .appendValue(HourOfDay.rule())
            .optionalStart().appendLiteral(':').appendValue(MinuteOfHour.rule(), 2)
            .optionalStart().appendLiteral(':').appendValue(SecondOfMinute.rule(), 2)
            .toFormatter();
        int pos = 0;
        if (str.startsWith("-")) {
            pos = 1;
        }
        ParsePosition pp = new ParsePosition(pos);
        Calendrical cal = f.parse(str, pp);
        if (pp.getErrorIndex() >= 0) {
            throw new IllegalArgumentException(str);
        }
        LocalTime time = cal.mergeStrict().toLocalTime();
        int secs = time.getHourOfDay() * 60 * 60 +
                time.getMinuteOfHour() * 60 + time.getSecondOfMinute();
        if (pos == 1) {
            secs = -secs;
        }
        return secs;
    }

    private ZoneOffset parseOffset(String str) {
        int secs = parseSecs(str);
        return ZoneOffset.forTotalSeconds(secs);
    }

    private Period parsePeriod(String str) {
        int secs = parseSecs(str);
        return deduplicate(Period.seconds(secs).normalized());
    }

    private TimeDefinition parseTimeDefinition(char c) {
        switch (c) {
            case 's':
            case 'S':
                // standard time
                return TimeDefinition.STANDARD;
            case 'u':
            case 'U':
            case 'g':
            case 'G':
            case 'z':
            case 'Z':
                // UTC
                return TimeDefinition.UTC;
            case 'w':
            case 'W':
            default:
                // wall time
                return TimeDefinition.WALL;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Build the rules, zones and links into real zones.
     */
    private void buildZoneRules() throws Exception {
        // build zones
        for (String zoneId : zones.keySet()) {
            printVerbose("Building zone " + zoneId);
            List<OlsonZone> olsonZones = zones.get(zoneId);
            ZoneRulesBuilder bld = new ZoneRulesBuilder();
            for (OlsonZone olsonZone : olsonZones) {
                bld = olsonZone.addToBuilder(bld, rules);
            }
            builtZones.put(zoneId, bld.toRules(zoneId, deduplicateMap));
        }
        
        // build aliases
        for (String realId : links.keySet()) {
            String aliasId = links.get(realId);
            printVerbose("Linking alias " + aliasId + " to " + realId);
            builtZones.put(aliasId, ZoneRulesBuilder.createAlias(aliasId, realId));
        }
    }

    private void outputFile() throws Exception {
        File outputFile = new File(destinationDir, "ZoneRuleInfo-Olson-" + version + ".dat");
        printVerbose("Outputting file: " + outputFile);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
        out.writeObject(builtZones);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 512);
//        DataOutputStream out = new DataOutputStream(baos);
//        Map<String, byte[]> data = new HashMap<String, byte[]>();
//        for (String zoneId : builtZones.keySet()) {
//            printVerbose("Outputting zone: " + zoneId);
//            out.
//        }
        out.close();
    }

    //-----------------------------------------------------------------------
    /**
     * Deduplicates an object instance.
     *
     * @param <T> the generic type
     * @param object  the object to deduplicate
     * @return the deduplicated object
     */
    @SuppressWarnings("unchecked")
    <T> T deduplicate(T object) {
        if (deduplicateMap.containsKey(object) == false) {
            deduplicateMap.put(object, object);
        }
        return (T) deduplicateMap.get(object);
    }

    /**
     * Prints a verbose message.
     * @param message  the message, not null
     */
    private void printVerbose(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class representing a month-day-time in the Olson file.
     */
    private abstract class OlsonMonthDayTime {
        /** The month of the cutover. */
        MonthOfYear month = MonthOfYear.JANUARY;
        /** The day of month of the cutover. */
        int dayOfMonth = 1;
        /** Whether to adjust forwards. */
        boolean adjustForwards = true;
        /** The day of week of the cutover. */
        DayOfWeek dayOfWeek;
        /** The time of the cutover. */
        LocalTime time = LocalTime.MIDNIGHT;
        /** The time of the cutover. */
        TimeDefinition timeDefinition = TimeDefinition.WALL;

        LocalDateTime toDateTime(int year) {
            adjustToFowards(year);
            LocalDate date;
            if (dayOfMonth == -1) {
                dayOfMonth = month.lengthInDays(Year.isoYear(year));
                date = LocalDate.date(year, month, dayOfMonth);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.previousOrCurrent(dayOfWeek));
                }
            } else {
                date = LocalDate.date(year, month, dayOfMonth);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.nextOrCurrent(dayOfWeek));
                }
            }
            date = deduplicate(date);
            return LocalDateTime.dateTime(date, time);
        }

        void adjustToFowards(int year) {
            if (adjustForwards == false && dayOfMonth > 0) {
                LocalDate adjustedDate = LocalDate.date(year, month, dayOfMonth).minusDays(6);
                dayOfMonth = adjustedDate.getDayOfMonth().getValue();
                month = adjustedDate.getMonthOfYear();
                adjustForwards = true;
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class representing a rule line in the Olson file.
     */
    private final class OlsonRule extends OlsonMonthDayTime {
        /** The start year. */
        int startYear;
        /** The end year. */
        int endYear;
        /** The amount of savings. */
        Period savingsAmount;
        /** The text name of the zone. */
        String text;

        void addToBuilder(ZoneRulesBuilder bld) {
            adjustToFowards(2001);
            bld.addRuleToWindow(startYear, endYear, month, dayOfMonth, dayOfWeek, time, timeDefinition, savingsAmount);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class representing a linked set of zone lines in the Olson file.
     */
    private final class OlsonZone extends OlsonMonthDayTime {
        /** The standard offset. */
        ZoneOffset standardOffset;
        /** The fixed savings amount. */
        Period fixedSavings;
        /** The savings rule. */
        String savingsRule;
        /** The text name of the zone. */
        String text;
        /** The year of the cutover. */
        Year year;

        ZoneRulesBuilder addToBuilder(ZoneRulesBuilder bld, Map<String, List<OlsonRule>> rules) {
            if (year != null) {
                bld.addWindow(standardOffset, toDateTime(year.getValue()), timeDefinition);
            } else {
                bld.addWindowForever(standardOffset);
            }
            
            if (fixedSavings != null) {
                bld.setFixedSavingsToWindow(fixedSavings);
            } else {
                List<OlsonRule> olsonRules = rules.get(savingsRule);
                if (olsonRules == null) {
                    throw new IllegalArgumentException("Rule not found: " + savingsRule);
                }
                for (OlsonRule olsonRule : olsonRules) {
                    olsonRule.addToBuilder(bld);
                }
            }
            
            return bld;
        }
    }

}
