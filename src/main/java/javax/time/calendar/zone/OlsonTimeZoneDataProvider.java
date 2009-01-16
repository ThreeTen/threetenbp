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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import javax.time.calendar.TimeZone;

/**
 * Provides rules based on the Olson time zone data.
 * <p>
 * OlsonTimeZoneDataProvider is thread-safe.
 *
 * @author Stephen Colebourne
 */
public class OlsonTimeZoneDataProvider implements TimeZoneDataProvider {

    /**
     * Cache of time zone data providers.
     */
    private final Map<String, TimeZone> zones;

    /**
     * Loads the time zone data.
     *
     * @param version  the version of the Olson rules to load, not null
     */
    @SuppressWarnings("unchecked")
    public OlsonTimeZoneDataProvider(String version) {
        String fileName = "javax/time/calendar/zone/ZoneRuleInfo-Olson-" + version + ".dat";
        InputStream resorceStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (resorceStream == null) {
            throw new IllegalArgumentException("Unable to load time zone rules, file not found: " + fileName);
        }
        try {
            ObjectInputStream in = new ObjectInputStream(resorceStream);
            zones = (Map<String, TimeZone>) in.readObject();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to load time zone rules: " + fileName, ex);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone implementation for the specified id and version.
     *
     * @param zoneId  the time zone id, not null
     * @return the matched time zone, null if not found
     */
    public TimeZone getTimeZone(String zoneId) {
        return zones.get(zoneId);
    }

    /**
     * Gets the set of available time zone ids.
     * The list will always contain the zone 'UTC'.
     *
     * @return the available IDs, never null
     */
    public Set<String> getAvailableIDs() {
        return zones.keySet();
    }

}
