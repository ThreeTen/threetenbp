/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

import javax.time.DateTimes;
import javax.time.ZoneOffset;

/**
 * Prints or parses a zone offset.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class ZoneOffsetPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The patterns to use.
     */
    static final String[] PATTERNS = new String[] {
        "+HH", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS",
    };  // order used in pattern builder

    /**
     * The text to use for UTC.
     */
    private final String noOffsetText;
    /**
     * The pattern type.
     */
    private final int type;

    /**
     * Constructor.
     *
     * @param noOffsetText  the text to use for UTC, not null
     * @param pattern  the pattern
     */
    ZoneOffsetPrinterParser(String noOffsetText, String pattern) {
        DateTimes.checkNotNull(noOffsetText, "No offset text must not be null");
        DateTimes.checkNotNull(pattern, "Pattern must not be null");
        this.noOffsetText = noOffsetText;
        this.type = checkPattern(pattern);
    }

    private int checkPattern(String pattern) {
        for (int i = 0; i < PATTERNS.length; i++) {
            if (PATTERNS[i].equals(pattern)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid zone offset pattern");
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        ZoneOffset offset = context.getValue(ZoneOffset.class);
        if (offset == null) {
            return false;
        }
        int totalSecs = offset.getTotalSeconds();
        if (totalSecs == 0) {
            buf.append(noOffsetText);
        } else if (type == 4 || (type == 2 && offset.getSecondsField() == 0)) {
            buf.append(offset.getID());
        } else {
            int absHours = Math.abs(offset.getHoursField());
            int absMinutes = Math.abs(offset.getMinutesField());
            int absSeconds = Math.abs(offset.getSecondsField());
            buf.append(totalSecs < 0 ? "-" : "+")
                .append((char) (absHours / 10 + '0')).append((char) (absHours % 10 + '0'));
            if (type >= 1) {
                buf.append((type % 2) == 0 ? ":" : "")
                    .append((char) (absMinutes / 10 + '0')).append((char) (absMinutes % 10 + '0'));
                if (type >= 5 || (type >= 3 && absSeconds > 0)) {
                    buf.append((type % 2) == 0 ? ":" : "")
                        .append((char) (absSeconds / 10 + '0')).append((char) (absSeconds % 10 + '0'));
                }
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, CharSequence text, int position) {
        ZoneOffset offset = null;
        int length = text.length();
        int utcLen = noOffsetText.length();
        if (utcLen == 0) {
            if (position == length) {
                context.setParsed(ZoneOffset.UTC);
                return position;
            }
        } else {
            if (position == length) {
                return ~position;
            }
            if (context.subSequenceEquals(text, position, noOffsetText, 0, utcLen)) {
                context.setParsed(ZoneOffset.UTC);
                return position + utcLen;
            }
        }
        
        char sign = text.charAt(position);  // IOOBE if invalid position
        if (sign == '+' || sign == '-') {
            int negative = (sign == '-' ? -1 : 1);
            int[] array = new int[4];
            array[0] = position + 1;
            if (parseNumber(array, 1, text, true) ||
                    parseNumber(array, 2, text, true) ||
                    parseNumber(array, 3, text, false)) {
                return ~position;
            }
            int total = (array[1] * 60 * 60) + (array[2] * 60) + array[3];
            if (total > 18 * 60 * 60) {  // max +18:00:00
                return ~position;
            }
            offset = ZoneOffset.ofHoursMinutesSeconds(negative * array[1], negative * array[2], negative * array[3]);
            context.setParsed(offset);
            return array[0];
        } else {
            if (utcLen == 0) {
                context.setParsed(ZoneOffset.UTC);
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
    private boolean parseNumber(int[] array, int arrayIndex, CharSequence parseText, boolean required) {
        if ((type + 3) / 2 < arrayIndex) {
            return false;  // ignore seconds/minutes
        }
        int pos = array[0];
        if ((type % 2) == 0 && arrayIndex > 1) {
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

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        String converted = noOffsetText.replace("'", "''");
        return "Offset('" + converted + "'," + PATTERNS[type] + ")";
    }

}
