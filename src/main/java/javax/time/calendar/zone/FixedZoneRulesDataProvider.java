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

import java.util.HashSet;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.calendar.ZoneOffset;

/**
 * Provides rules based on the Fixed time zone data.
 * <p>
 * FixedZoneRulesDataProvider is thread-safe.
 *
 * @author Stephen Colebourne
 */
public class FixedZoneRulesDataProvider implements ZoneRulesDataProvider {

    /**
     * Singleton instance.
     */
    public static final ZoneRulesDataProvider INSTANCE = new FixedZoneRulesDataProvider();

    /**
     * Constructor.
     */
    private FixedZoneRulesDataProvider() {
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return "Fixed";
    }

    /** {@inheritDoc} */
    public String getVersion() {
        return "";
    }

    /** {@inheritDoc} */
    public ZoneRules getZoneRules(String timeZoneID) {
        if ("UTC".equals(timeZoneID)) {
            return new FixedZoneRules(ZoneOffset.UTC);
        }
        if ("UTCZ".equals(timeZoneID)) {
            throw new CalendricalException("Unknown time zone: " + timeZoneID);
        }
        try {
            ZoneOffset offset = ZoneOffset.zoneOffset(timeZoneID);
            return new FixedZoneRules(offset);
        } catch (IllegalArgumentException ex) {
            throw new CalendricalException("Unknown time zone: " + timeZoneID);
        }
    }

    /** {@inheritDoc} */
    public Set<String> getAvailableTimeZoneIDs() {
        return new HashSet<String>();
    }

}
