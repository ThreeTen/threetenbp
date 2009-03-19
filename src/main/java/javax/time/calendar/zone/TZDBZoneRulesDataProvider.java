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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides rules based on the TZDB time zone data.
 * <p>
 * TZDBTimeZoneDataProvider is thread-safe.
 *
 * @author Stephen Colebourne
 */
public class TZDBZoneRulesDataProvider implements ZoneRulesDataProvider {

    /**
     * Version.
     */
    private final String version;
    /**
     * Cache of time zone data providers.
     */
    private final Map<String, ZoneRules> zones;

    /**
     * Loads the time zone data.
     *
     * @param version  the version of the TZDB rules to load, not null
     */
    @SuppressWarnings("unchecked")
    public TZDBZoneRulesDataProvider(String version) {
        String fileName = "javax/time/calendar/zone/ZoneRuleInfo-TZDB-" + version + ".dat";
        InputStream resorceStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (resorceStream == null) {
            throw new IllegalArgumentException("Unable to load time zone rules, file not found: " + fileName);
        }
        try {
            ObjectInputStream in = new ObjectInputStream(resorceStream);
            zones = (Map<String, ZoneRules>) in.readObject();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to load time zone rules: " + fileName, ex);
        }
        this.version = version;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return "TZDB";
    }

    /** {@inheritDoc} */
    public Set<String> getIDs() {
        Set<String> ids = new HashSet<String>(zones.size());
        for (String id : zones.keySet()) {
            ids.add(id + ':' + version);
        }
        return Collections.unmodifiableSet(ids);
    }

    /** {@inheritDoc} */
    public ZoneRules getZoneRules(String regionID, String versionID) {
        if (regionID == null || versionID == null || versionID.equals(version) == false) {
            return null;
        }
        return zones.get(regionID);
    }

}
