/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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
 *
 * @author Stephen Colebourne
 */
class ZoneOffsetPrinterParser implements DateTimePrinter, DateTimeParser {

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
        ZoneOffset offset = calendrical.getOffset();
        if (offset == null) {
            throw new CalendricalFormatException("Unable to print ZoneOffset");
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
        return (calendrical.getOffset() != null);
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public int parse(DateTimeParseContext context, String parseText, int position) {
        int length = parseText.length();
        if (position > length) {
            throw new IndexOutOfBoundsException();
        }
        // TODO
        return position;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Offset('" + utcText + "'," + includeColon + "," + allowSeconds + ")";
    }

}
