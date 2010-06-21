/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.time.calendar.DateAdjusters;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.Period;
import javax.time.calendar.Year;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;
import javax.time.calendar.format.DateTimeParseContext;
import javax.time.calendar.zone.ZoneRulesBuilder.TimeDefinition;

/**
 * A builder that can read the TZDB TimeZone files and build ZoneRules instances.
 * <p>
 * TZDBZoneRulesCompiler is thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TZDBZoneRulesCompiler {

    /**
     * Time parser.
     */
    private static final DateTimeFormatter TIME_PARSER;
    static {
        TIME_PARSER = new DateTimeFormatterBuilder()
            .appendValue(ISOChronology.hourOfDayRule())
            .optionalStart().appendLiteral(':').appendValue(ISOChronology.minuteOfHourRule(), 2)
            .optionalStart().appendLiteral(':').appendValue(ISOChronology.secondOfMinuteRule(), 2)
            .toFormatter();
    }

    /**
     * Reads a set of TZDB files and builds a single combined data file.
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
        File baseSrcDir = null;
        File dstDir = null;
        boolean verbose = false;
        
        // parse options
        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-") == false) {
                break;
            }
            if ("-srcdir".equals(arg)) {
                if (baseSrcDir == null && ++i < args.length) {
                    baseSrcDir = new File(args[i]);
                    continue;
                }
            } else if ("-dstdir".equals(arg)) {
                if (dstDir == null && ++i < args.length) {
                    dstDir = new File(args[i]);
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
        
        // check source directory
        if (baseSrcDir == null) {
            System.out.println("Source directory must be specified using -srcdir: " + baseSrcDir);
            return;
        }
        if (baseSrcDir.isDirectory() == false) {
            System.out.println("Source does not exist or is not a directory: " + baseSrcDir);
            return;
        }
        dstDir = (dstDir != null ? dstDir : baseSrcDir);
        
        // parse source file names
        List<String> srcFileNames = Arrays.asList(Arrays.copyOfRange(args, i, args.length));
        if (srcFileNames.isEmpty()) {
            System.out.println("Source filenames not specified, using default set");
            System.out.println("(africa antarctica asia australasia backward etcetera europe northamerica southamerica)");
            srcFileNames = Arrays.asList("africa", "antarctica", "asia", "australasia", "backward",
                    "etcetera", "europe", "northamerica", "southamerica");
        }
        
        // find source directories to process
        List<File> srcDirs = new ArrayList<File>();
        if (version != null) {
            File srcDir = new File(baseSrcDir, version);
            if (srcDir.isDirectory() == false) {
                System.out.println("Version does not represent a valid source directory : " + srcDir);
                return;
            }
            srcDirs.add(srcDir);
        } else {
            File[] dirs = baseSrcDir.listFiles();
            for (File dir : dirs) {
                if (dir.isDirectory() && dir.getName().matches("[12][0-9][0-9][0-9][A-Za-z0-9._-]+")) {
                    srcDirs.add(dir);
                }
            }
        }
        if (srcDirs.isEmpty()) {
            System.out.println("Source directory contains no valid source folders: " + baseSrcDir);
            return;
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
        process(srcDirs, srcFileNames, dstDir, verbose);
        System.exit(0);
    }

    /**
     * Output usage text for the command line.
     */
    private static void outputHelp() {
        System.out.println("Usage: TZDBZoneRulesCompiler <options> <tzdb source filenames>");
        System.out.println("where options include:");
        System.out.println("   -srcdir <directory>   Where to find source directories (required)");
        System.out.println("   -dstdir <directory>   Where to output generated files (default srcdir)");
        System.out.println("   -version <version>    Specify the version, such as 2009a (optional)");
        System.out.println("   -help                 Print this usage message");
        System.out.println("   -verbose              Output verbose information during compilation");
        System.out.println(" There must be one directory for each version in srcdir");
        System.out.println(" Each directory must have the name of the version, such as 2009a");
        System.out.println(" Each directory must contain the unpacked tzdb files, such as asia or europe");
        System.out.println(" Directories must match the regex [12][0-9][0-9][0-9][A-Za-z0-9._-]+");
        System.out.println(" There will be one jar file for each version and one combined jar in dstdir");
        System.out.println(" If the version is specified, only that version is processed");
    }

    /**
     * Process to create the jar files.
     */
    private static void process(List<File> srcDirs, List<String> srcFileNames, File dstDir, boolean verbose) {
        // build actual jar files
        Map<Object, Object> deduplicateMap = new HashMap<Object, Object>();
        Map<String, SortedMap<String, ZoneRules>> allBuiltZones = new TreeMap<String, SortedMap<String, ZoneRules>>();
        Set<String> allRegionIds = new TreeSet<String>();
        Set<ZoneRules> allRules = new HashSet<ZoneRules>();
        for (File srcDir : srcDirs) {
            // source files in this directory
            List<File> srcFiles = new ArrayList<File>();
            for (String srcFileName : srcFileNames) {
                File file = new File(srcDir, srcFileName);
                if (file.exists()) {
                    srcFiles.add(file);
                }
            }
            if (srcFiles.isEmpty()) {
                continue;  // nothing to process
            }
            
            // compile
            String loopVersion = srcDir.getName();
            TZDBZoneRulesCompiler compiler = new TZDBZoneRulesCompiler(loopVersion, srcFiles, verbose);
            compiler.setDeduplicateMap(deduplicateMap);
            try {
                // compile
                SortedMap<String, ZoneRules> builtZones = compiler.compile();
                
                // output version-specific file
                File dstFile = new File(dstDir, "jsr-310-TZDB-" + loopVersion + ".jar");
                if (verbose) {
                    System.out.println("Outputting file: " + dstFile);
                }
                outputFile(dstFile, loopVersion, builtZones);
                
                // create totals
                allBuiltZones.put(loopVersion, builtZones);
                allRegionIds.addAll(builtZones.keySet());
                allRules.addAll(builtZones.values());
                
            } catch (Exception ex) {
                System.out.println("Failed: " + ex.toString());
                ex.printStackTrace();
                System.exit(1);
            }
        }
        
        // output merged file
        File dstFile = new File(dstDir, "jsr-310-TZDB-all.jar");
        if (verbose) {
            System.out.println("Outputting combined file: " + dstFile);
        }
        outputFile(dstFile, allBuiltZones, allRegionIds, allRules);
    }

    /**
     * Outputs the file.
     */
    private static void outputFile(File dstFile, String version, SortedMap<String, ZoneRules> builtZones) {
        Map<String, SortedMap<String, ZoneRules>> loopAllBuiltZones = new TreeMap<String, SortedMap<String, ZoneRules>>();
        loopAllBuiltZones.put(version, builtZones);
        Set<String> loopAllRegionIds = new TreeSet<String>(builtZones.keySet());
        Set<ZoneRules> loopAllRules = new HashSet<ZoneRules>(builtZones.values());
        outputFile(dstFile, loopAllBuiltZones, loopAllRegionIds, loopAllRules);
    }

    /**
     * Outputs the file.
     */
    private static void outputFile(
            File dstFile, Map<String, SortedMap<String, ZoneRules>> allBuiltZones,
            Set<String> allRegionIds, Set<ZoneRules> allRules) {
        // this format is not part of the jsr-310 specification
        try {
            JarOutputStream jos = new JarOutputStream(new FileOutputStream(dstFile));
            jos.putNextEntry(new ZipEntry("javax/time/calendar/zone/ZoneRules.dat"));
            DataOutputStream out = new DataOutputStream(jos);
            
            // file version
            out.writeByte(1);
            // group
            out.writeUTF("TZDB");
            // all versions and regions
            String[] versionArray = allBuiltZones.keySet().toArray(new String[allBuiltZones.size()]);
            out.writeShort(versionArray.length);
            for (String version : versionArray) {
                out.writeUTF(version);
            }
            String[] regionArray = allRegionIds.toArray(new String[allRegionIds.size()]);
            out.writeShort(regionArray.length);
            for (String regionId : regionArray) {
                out.writeUTF(regionId);
            }
            // link version-region-rules
            List<ZoneRules> rulesList = new ArrayList<ZoneRules>(allRules);
            for (String version : allBuiltZones.keySet()) {
                out.writeShort(allBuiltZones.get(version).size());
                for (Entry<String, ZoneRules> entry : allBuiltZones.get(version).entrySet()) {
                     int regionIndex = Arrays.binarySearch(regionArray, entry.getKey());
                     int rulesIndex = rulesList.indexOf(entry.getValue());
                     out.writeShort(regionIndex);
                     out.writeShort(rulesIndex);
                }
            }
            // rules
            out.writeShort(rulesList.size());
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            for (ZoneRules rules : rulesList) {
                baos.reset();
                DataOutputStream dataos = new DataOutputStream(baos);
                Ser.write(rules, dataos);
                dataos.close();
                byte[] bytes = baos.toByteArray();
                out.writeShort(bytes.length);
                out.write(bytes);
            }
            
            jos.closeEntry();
            out.close();
        } catch (Exception ex) {
            System.out.println("Failed: " + ex.toString());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    //-----------------------------------------------------------------------
    /** The TZDB rules. */
    private final Map<String, List<TZDBRule>> rules = new HashMap<String, List<TZDBRule>>();
    /** The TZDB zones. */
    private final Map<String, List<TZDBZone>> zones = new HashMap<String, List<TZDBZone>>();
    /** The TZDB links. */
    private final Map<String, String> links = new HashMap<String, String>();
    /** The built zones. */
    private final SortedMap<String, ZoneRules> builtZones = new TreeMap<String, ZoneRules>();
    /** A map to deduplicate object instances. */
    private Map<Object, Object> deduplicateMap = new HashMap<Object, Object>();

    /** The version to produce. */
    private final String version;
    /** The source files. */
    private final List<File> sourceFiles;
    /** The version to produce. */
    private final boolean verbose;

    /**
     * Constructor used if you want to invoke the compiler manually.
     *
     * @param version  the version, such as 2009a, not null
     * @param sourceFiles  the list of source files, not empty, not null
     * @param verbose  whether to output verbose messages
     */
    public TZDBZoneRulesCompiler(String version, List<File> sourceFiles, boolean verbose) {
        this.version = version;
        this.sourceFiles = sourceFiles;
        this.verbose = verbose;
    }

    /**
     * Compile the rules file.
     * @return the map of region ID to rules, not null
     * @throws Exception if an error occurs
     */
    public SortedMap<String, ZoneRules> compile() throws Exception {
        printVerbose("Compiling TZDB version " + version);
        parseFiles();
        buildZoneRules();
        printVerbose("Compiled TZDB version " + version);
        return builtZones;
    }

    /**
     * Sets the deduplication map.
     */
    void setDeduplicateMap(Map<Object, Object> deduplicateMap) {
        this.deduplicateMap = deduplicateMap;
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
        int lineNumber = 1;
        String line = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            List<TZDBZone> openZone = null;
            for (; (line = in.readLine()) != null; lineNumber++) {
                int index = line.indexOf('#');  // remove comments (doesn't handle # in quotes)
                if (index >= 0) {
                    line = line.substring(0, index);
                }
                if (line.trim().length() == 0) {  // ignore blank lines
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
                                throw new IllegalArgumentException("Invalid Zone line");
                            }
                            openZone = new ArrayList<TZDBZone>();
                            zones.put(st.nextToken(), openZone);
                            if (parseZoneLine(st, openZone)) {
                                openZone = null;
                            }
                        } else {
                            openZone = null;
                            if (first.equals("Rule")) {
                                if (st.countTokens() < 9) {
                                    printVerbose("Invalid Rule line in file: " + file + ", line: " + line);
                                    throw new IllegalArgumentException("Invalid Rule line");
                                }
                                parseRuleLine(st);
                                
                            } else if (first.equals("Link")) {
                                if (st.countTokens() < 2) {
                                    printVerbose("Invalid Link line in file: " + file + ", line: " + line);
                                    throw new IllegalArgumentException("Invalid Link line");
                                }
                                String realId = st.nextToken();
                                String aliasId = st.nextToken();
                                links.put(aliasId, realId);
                                
                            } else {
                                throw new IllegalArgumentException("Unknown line");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new Exception("Failed while processing file '" + file + "' on line " + lineNumber + " '" + line + "'", ex);
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                // ignore NPE and IOE
            }
        }
    }

    /**
     * Parses a Rule line.
     * @param st  the tokenizer, not null
     */
    private void parseRuleLine(StringTokenizer st) {
        TZDBRule rule = new TZDBRule();
        String name = st.nextToken();
        if (rules.containsKey(name) == false) {
            rules.put(name, new ArrayList<TZDBRule>());
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
     * @param st  the tokenizer, not null
     * @return true if the zone is complete
     */
    private boolean parseZoneLine(StringTokenizer st, List<TZDBZone> zoneList) {
        TZDBZone zone = new TZDBZone();
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
            zone.year = Year.of(Integer.parseInt(st.nextToken()));
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
     * @param st  the tokenizer, not null
     * @param mdt  the object to parse into, not null
     */
    private void parseMonthDayTime(StringTokenizer st, TZDBMonthDayTime mdt) {
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
                String timeStr = st.nextToken();
                int secsOfDay = parseSecs(timeStr);
                if (secsOfDay == 86400) {
                    mdt.endOfDay = true;
                    secsOfDay = 0;
                }
                LocalTime time = deduplicate(LocalTime.ofSecondOfDay(secsOfDay));
                mdt.time = time;
                mdt.timeDefinition = parseTimeDefinition(timeStr.charAt(timeStr.length() - 1));
            }
        }
    }

    private int parseYear(String str, int defaultYear) {
        str = str.toLowerCase();
        if (matches(str, "minimum")) {
            return Year.MIN_YEAR;
        } else if (matches(str, "maximum")) {
            return Year.MAX_YEAR;
        } else if (str.equals("only")) {
            return defaultYear;
        }
        return Integer.parseInt(str);
    }

    private MonthOfYear parseMonth(String str) {
        str = str.toLowerCase();
        for (MonthOfYear moy : MonthOfYear.values()) {
            if (matches(str, moy.name().toLowerCase())) {
                return moy;
            }
        }
        throw new IllegalArgumentException("Unknown month: " + str);
    }

    private DayOfWeek parseDayOfWeek(String str) {
        str = str.toLowerCase();
        for (DayOfWeek dow : DayOfWeek.values()) {
            if (matches(str, dow.name().toLowerCase())) {
                return dow;
            }
        }
        throw new IllegalArgumentException("Unknown day-of-week: " + str);
    }

    private boolean matches(String str, String search) {
        return str.startsWith(search.substring(0, 3)) && search.startsWith(str) && str.length() <= search.length();
    }

    private String parseOptional(String str) {
        return str.equals("-") ? null : str;
    }

    private int parseSecs(String str) {
        if (str.equals("-")) {
            return 0;
        }
        int pos = 0;
        if (str.startsWith("-")) {
            pos = 1;
        }
        ParsePosition pp = new ParsePosition(pos);
        DateTimeParseContext cal = TIME_PARSER.parse(str, pp);
        if (pp.getErrorIndex() >= 0) {
            throw new IllegalArgumentException(str);
        }
        int hour = (Integer) cal.getParsed(ISOChronology.hourOfDayRule());
        Integer min = (Integer) cal.getParsed(ISOChronology.minuteOfHourRule());
        Integer sec = (Integer) cal.getParsed(ISOChronology.secondOfMinuteRule());
        int secs = hour * 60 * 60 +
            (min != null ? min : 0) * 60 + (sec != null ? sec : 0);
        if (pos == 1) {
            secs = -secs;
        }
        return secs;
    }

    private ZoneOffset parseOffset(String str) {
        int secs = parseSecs(str);
        return ZoneOffset.ofTotalSeconds(secs);
    }

    private Period parsePeriod(String str) {
        int secs = parseSecs(str);
        return deduplicate(Period.ofSeconds(secs).normalized());
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
     * @throws Exception if an error occurs
     */
    private void buildZoneRules() throws Exception {
        // build zones
        for (String zoneId : zones.keySet()) {
            printVerbose("Building zone " + zoneId);
            zoneId = deduplicate(zoneId);
            List<TZDBZone> tzdbZones = zones.get(zoneId);
            ZoneRulesBuilder bld = new ZoneRulesBuilder();
            for (TZDBZone tzdbZone : tzdbZones) {
                bld = tzdbZone.addToBuilder(bld, rules);
            }
            ZoneRules buildRules = bld.toRules(zoneId, deduplicateMap);
            builtZones.put(zoneId, deduplicate(buildRules));
        }
        
        // build aliases
        for (String aliasId : links.keySet()) {
            aliasId = deduplicate(aliasId);
            String realId = links.get(aliasId);
            printVerbose("Linking alias " + aliasId + " to " + realId);
            ZoneRules realRules = builtZones.get(realId);
            if (realRules == null) {
                realId = links.get(realId);  // try again (handle alias liked to alias)
                printVerbose("Relinking alias " + aliasId + " to " + realId);
                realRules = builtZones.get(realId);
                if (realRules == null) {
                    throw new IllegalArgumentException("Alias '" + aliasId + "' links to invalid zone '" + realId + "' for '" + version + "'");
                }
            }
            builtZones.put(aliasId, realRules);
        }
        
        // remove UTC and GMT
        builtZones.remove("UTC");
        builtZones.remove("GMT");
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

    //-----------------------------------------------------------------------
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
     * Class representing a month-day-time in the TZDB file.
     */
    abstract class TZDBMonthDayTime {
        /** The month of the cutover. */
        MonthOfYear month = MonthOfYear.JANUARY;
        /** The day-of-month of the cutover. */
        int dayOfMonth = 1;
        /** Whether to adjust forwards. */
        boolean adjustForwards = true;
        /** The day-of-week of the cutover. */
        DayOfWeek dayOfWeek;
        /** The time of the cutover. */
        LocalTime time = LocalTime.MIDNIGHT;
        /** Whether this is midnight end of day. */
        boolean endOfDay;
        /** The time of the cutover. */
        TimeDefinition timeDefinition = TimeDefinition.WALL;

        void adjustToFowards(int year) {
            if (adjustForwards == false && dayOfMonth > 0) {
                LocalDate adjustedDate = LocalDate.of(year, month, dayOfMonth).minusDays(6);
                dayOfMonth = adjustedDate.getDayOfMonth();
                month = adjustedDate.getMonthOfYear();
                adjustForwards = true;
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class representing a rule line in the TZDB file.
     */
    final class TZDBRule extends TZDBMonthDayTime {
        /** The start year. */
        int startYear;
        /** The end year. */
        int endYear;
        /** The amount of savings. */
        Period savingsAmount;
        /** The text name of the zone. */
        String text;

        void addToBuilder(ZoneRulesBuilder bld) {
            adjustToFowards(2004);  // irrelevant, treat as leap year
            bld.addRuleToWindow(startYear, endYear, month, dayOfMonth, dayOfWeek, time, endOfDay, timeDefinition, savingsAmount);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Class representing a linked set of zone lines in the TZDB file.
     */
    final class TZDBZone extends TZDBMonthDayTime {
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

        ZoneRulesBuilder addToBuilder(ZoneRulesBuilder bld, Map<String, List<TZDBRule>> rules) {
            if (year != null) {
                bld.addWindow(standardOffset, toDateTime(year.getValue()), timeDefinition);
            } else {
                bld.addWindowForever(standardOffset);
            }
            
            if (fixedSavings != null) {
                bld.setFixedSavingsToWindow(fixedSavings);
            } else {
                List<TZDBRule> tzdbRules = rules.get(savingsRule);
                if (tzdbRules == null) {
                    throw new IllegalArgumentException("Rule not found: " + savingsRule);
                }
                for (TZDBRule tzdbRule : tzdbRules) {
                    tzdbRule.addToBuilder(bld);
                }
            }
            
            return bld;
        }

        private LocalDateTime toDateTime(int year) {
            adjustToFowards(year);
            LocalDate date;
            if (dayOfMonth == -1) {
                dayOfMonth = month.getLastDayOfMonth(ISOChronology.isLeapYear(year));
                date = LocalDate.of(year, month, dayOfMonth);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.previousOrCurrent(dayOfWeek));
                }
            } else {
                date = LocalDate.of(year, month, dayOfMonth);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.nextOrCurrent(dayOfWeek));
                }
            }
            date = deduplicate(date);
            LocalDateTime ldt = LocalDateTime.of(date, time);
            if (endOfDay) {
                ldt = ldt.plusDays(1);
            }
            return ldt;
        }
    }

}
