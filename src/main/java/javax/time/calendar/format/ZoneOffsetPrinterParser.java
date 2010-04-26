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

import javax.time.calendar.Calendrical;
import javax.time.calendar.ZoneOffset;

/**
 * Prints or parses a zone offset.
 * <p>
 * ZoneOffsetPrinterParser is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
final class ZoneOffsetPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The text to use for UTC.
     */
    private final String utcText;
    /**
     * Whether to include a colon.
     */
    private final boolean includeColon;
    /**
     * Whether to allow seconds.
     */
    private final boolean allowSeconds;

    /**
     * Constructor.
     *
     * @param utcText  the text to use for UTC, not null
     * @param includeColon  whether to include a colon
     * @param allowSeconds  whether to allow seconds
     */
    ZoneOffsetPrinterParser(String utcText, boolean includeColon, boolean allowSeconds) {
        // validated by caller
        this.utcText = utcText;
        this.includeColon = includeColon;
        this.allowSeconds = allowSeconds;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public void print(Calendrical calendrical, Appendable appendable, DateTimeFormatSymbols symbols) throws IOException {
        ZoneOffset offset = calendrical.get(ZoneOffset.rule());
        if (offset == null) {
            throw new CalendricalPrintException("Unable to print ZoneOffset");
        }
        int totalSecs = offset.getAmountSeconds();
        if (totalSecs == 0) {
            appendable.append(utcText);
        } else if (includeColon && (allowSeconds || offset.getSecondsField() == 0)) {
            appendable.append(offset.getID());
        } else {
            int absHours = Math.abs(offset.getHoursField());
            int absMinutes = Math.abs(offset.getMinutesField());
            int absSeconds = Math.abs(offset.getSecondsField());
            appendable
                .append(totalSecs < 0 ? "-" : "+")
                .append((char) (absHours / 10 + '0')).append((char) (absHours % 10 + '0'))
                .append(includeColon ? ":" : "")
                .append((char) (absMinutes / 10 + '0')).append((char) (absMinutes % 10 + '0'));
            if (allowSeconds && absSeconds > 0) {
                appendable
                    .append(includeColon ? ":" : "")
                    .append((char) (absSeconds / 10 + '0')).append((char) (absSeconds % 10 + '0'));
            }
        }
    }

    /** {@inheritDoc} */
    public boolean isPrintDataAvailable(Calendrical calendrical) {
        return (calendrical.get(ZoneOffset.rule()) != null);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        ZoneOffset offset = null;
        int length = parseText.length();
        int utcLen = utcText.length();
        if (utcLen == 0) {
            if (position == length) {
                context.setParsed(ZoneOffset.rule(), ZoneOffset.UTC);
                return position;
            }
        } else {
            if (position == length) {
                return ~position;
            }
            // TODO: should the comparison use the locale?
            if (parseText.regionMatches(!context.isCaseSensitive(), position, utcText, 0, utcLen)) {
                context.setParsed(ZoneOffset.rule(), ZoneOffset.UTC);
                return position + utcLen;
            }
        }
        
        char sign = parseText.charAt(position);  // IOOBE if invalid position
        if (sign == '+' || sign == '-') {
            int negative = (sign == '-' ? -1 : 1);
            int[] array = new int[4];
            array[0] = position + 1;
            if (parseNumber(array, 1, parseText, true) ||
                    parseNumber(array, 2, parseText, true) ||
                    parseNumber(array, 3, parseText, false)) {
                return ~position;
            }
            int total = (array[1] * 60 * 60) + (array[2] * 60) + array[3];
            if (total > 18 * 60 * 60) {  // max +18:00:00
                return ~position;
            }
            offset = ZoneOffset.ofHoursMinutesSeconds(negative * array[1], negative * array[2], negative * array[3]);
            context.setParsed(ZoneOffset.rule(), offset);
            return array[0];
        } else {
            if (utcLen == 0) {
                context.setParsed(ZoneOffset.rule(), ZoneOffset.UTC);
                return position + utcLen;
            }
            return ~position;
        }
    }

    /**
     * Parse a two digit zero-prefixed number.
     *
     * @param array  the array of parsed data, 0=pos,1=hours,2=mins,3=secs, not null
     * @param arrayIndex  the index to parse the value into
     * @param parseText  the offset id, not null
     * @param required  whether this number is required
     * @return true if an error occurred
     */
    private boolean parseNumber(int[] array, int arrayIndex, String parseText, boolean required) {
        if (allowSeconds == false && arrayIndex == 3) {
            return false;  // ignore seconds
        }
        int pos = array[0];
        if (includeColon && arrayIndex > 1) {
            if (pos + 1 > parseText.length() || parseText.charAt(pos) != ':') {
                return required;
            }
            pos++;
        }
        if (pos + 2 > parseText.length()) {
            return required;
        }
        char ch1 = parseText.charAt(pos++);
        char ch2 = parseText.charAt(pos++);
        if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
            return required;
        }
        int value = (ch1 - 48) * 10 + (ch2 - 48);
        if (value < 0 || value > 59) {
            return required;
        }
        array[arrayIndex] = value;
        array[0] = pos;
        return false;
    }

//            try {
//                if (includeColon) {
//                    offset = ZoneOffset.zoneOffset(parseText.substring(position, position + 6));
//                    endPos += 6;
//                } else {
//                    offset = ZoneOffset.zoneOffset(parseText.substring(position, position + 5));
//                    endPos += 5;
//                }
//            } catch (Exception ex) {
//                return ~position;
//            }
//            try {
//                if (includeColon) {
//                    offset = ZoneOffset.zoneOffset(parseText.substring(position, position + 9));
//                    endPos += 3;
//                } else {
//                    offset = ZoneOffset.zoneOffset(parseText.substring(position, position + 7));
//                    endPos += 2;
//                }
//            } catch (Exception ex) {
//                // ignore
//            }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (utcText.equals("Z") && includeColon && allowSeconds) {
            return "OffsetId()";
        }
        String converted = utcText.replace("'", "''");
        return "Offset('" + converted + "'," + includeColon + "," + allowSeconds + ")";
    }

}
