/*
 * Copyright (c) 2008-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.time.calendar.Calendrical;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;
import javax.time.calendar.zone.ZoneRulesGroup;

/**
 * Prints or parses a zone offset.
 * <p>
 * ZonePrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class ZonePrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The text style to output, null means the id.
     */
    private final TextStyle textStyle;

    /**
     * Constructor.
     */
    ZonePrinterParser() {
        this.textStyle = null;
    }

    /**
     * Constructor.
     *
     * @param textStyle  the test style to output, not null
     */
    ZonePrinterParser(TextStyle textStyle) {
        // validated by caller
        this.textStyle = textStyle;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        TimeZone zone = calendrical.get(TimeZone.rule());
        if (zone == null) {
            throw new CalendricalPrintException("Unable to print TimeZone");
        }
        if (textStyle == null) {
            appendable.append(zone.getID());
        } else if (textStyle == TextStyle.FULL) {
            appendable.append(zone.getName());  // TODO: Use symbols
        } else {
            appendable.append(zone.getShortName());  // TODO: Use symbols
        }
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return (calendrical.get(TimeZone.rule()) != null);
    }

    //-----------------------------------------------------------------------
    /**
     * The cached tree to speed up parsing.
     */
    private static SubstringTree preparedTree;
    /**
     * The cached IDs.
     */
    private static Set<String> preparedIDs;

    /**
     * {@inheritDoc}
     * <p>
     * This implementation looks for the longest matching string.
     * For example, parsing Etc/GMT-2 will return Etc/GMC-2 rather than just
     * Etc/GMC although both are valid.
     * <p>
     * This implementation uses a tree to search for valid time-zone names in
     * the parseText. The top level node of the tree has a length equal to the
     * length of the shortest time-zone as well as the beginning characters of
     * all other time-zones.
     */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        if (position > length) {
            throw new IndexOutOfBoundsException();
        }
        
        // setup parse tree
        Set<String> ids = ZoneRulesGroup.getParsableIDs();
        if (ids.size() == 0) {
            return ~position;
        }
        SubstringTree tree;
        synchronized (ZonePrinterParser.class) {
            if (preparedTree == null || preparedIDs.size() < ids.size()) {
                ids = new HashSet<String>(ids);
                preparedTree = prepareParser(ids);
                preparedIDs = ids;
            }
            tree = preparedTree;
        }
        
        // handle fixed time-zone ids
        if (parseText.substring(position).startsWith("UTC")) {
            DateTimeParseContext newContext = new DateTimeParseContext(context.getSymbols());
            int startPos = position + 3;
            int endPos = new ZoneOffsetPrinterParser("", true, true).parse(newContext, parseText, startPos);
            if (endPos < 0) {
                context.setParsed(TimeZone.rule(), TimeZone.UTC);
                return startPos;
            }
            TimeZone zone = TimeZone.of((ZoneOffset) newContext.getParsed(ZoneOffset.rule()));
            context.setParsed(TimeZone.rule(), zone);
            return endPos;
        }
        
        // parse
        String parsedZoneId = null;
        int count = 0;
        while (tree != null) {
            int nodeLength = tree.length;
            if (position + nodeLength > length) {
                break;
            }
            parsedZoneId = parseText.substring(position, position + nodeLength);
            tree = tree.get(parsedZoneId);
            ++count;
        }
        
        if (parsedZoneId != null && preparedIDs.contains(parsedZoneId)) {
            // handle zone version
            TimeZone zone = TimeZone.of(parsedZoneId);
            int pos = position + parsedZoneId.length();
            if (pos + 1 < length && parseText.charAt(pos) == '#') {
                Set<String> versions = zone.getGroup().getAvailableVersionIDs();
                for (String version : versions) {
                    if (parseText.regionMatches(pos + 1, version, 0, version.length())) {
                        zone = zone.withVersion(version);
                        pos += version.length() + 1;
                        break;
                    }
                }
            }
            context.setParsed(TimeZone.rule(), zone);
            return pos;
        } else {
            return ~position;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Model a tree of substrings to make the parsing easier. Due to the nature
     * of time-zone names, it can be faster to parse based in unique substrings
     * rather than just a character by character match.
     * <p>
     * For example, to parse America/Denver we can look at the first two
     * character "Am". We then notice that the shortest time-zone that starts
     * with Am is America/Nome which is 12 characters long. Checking the first
     * 12 characters of America/Denver giver America/Denv which is a substring
     * of only 1 time-zone: America/Denver. Thus, with just 3 comparisons that
     * match can be found.
     * <p>
     * This structure maps substrings to substrings of a longer length. Each
     * node of the tree contains a length and a map of valid substrings to
     * sub-nodes. The parser gets the length from the root node. It then
     * extracts a substring of that length from the parseText. If the map
     * contains the substring, it is set as the possible time-zone and the
     * sub-node for that substring is retrieved. The process continues until the
     * substring is no longer found, at which point the matched text is checked
     * against the real time-zones.
     */
    private static class SubstringTree {
        /**
         * The length of the substring this node of the tree contains.
         * Subtrees will have a longer length.
         */
        final int length;
        /**
         * Map of a substring to a set of substrings that contain the key.
         */
        private final Map<String, SubstringTree> substringMap = new HashMap<String, SubstringTree>();

        /**
         * Constructor.
         *
         * @param length  the length of this tree
         */
        private SubstringTree(int length) {
            this.length = length;
        }

        private SubstringTree get(String substring2) {
            return substringMap.get(substring2);

        }

        /**
         * Values must be added from shortest to longest.
         *
         * @param newSubstring  the substring to add, not null
         */
        private void add(String newSubstring) {
            int idLen = newSubstring.length();
            if (idLen == length) {
                substringMap.put(newSubstring, null);
            } else if (idLen > length) {
                String substring = newSubstring.substring(0, length);
                SubstringTree parserTree = substringMap.get(substring);
                if (parserTree == null) {
                    parserTree = new SubstringTree(idLen);
                    substringMap.put(substring, parserTree);
                }
                parserTree.add(newSubstring);
            }
        }
    }

    /**
     * Builds an optimized parsing tree.
     *
     * @param availableIDs  the available IDs, not null, not empty
     * @return the tree, never null
     */
    private static SubstringTree prepareParser(Set<String> availableIDs) {
        // sort by length
        List<String> ids = new ArrayList<String>(availableIDs);
        Collections.sort(ids, new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.length() == str2.length() ? str1.compareTo(str2) : str1.length() - str2.length();
            }
        });
        
        // build the tree
        SubstringTree tree = new SubstringTree(ids.get(0).length());
        for (String id : ids) {
            tree.add(id);
        }
        return tree;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (textStyle == null) {
            return "ZoneId()";
        }
        return "ZoneText(" + textStyle + ")";
    }

}
