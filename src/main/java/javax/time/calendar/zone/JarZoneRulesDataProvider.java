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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads time zone rules stored in a jar file.
 * <p>
 * JarZoneRulesDataProvider is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
class JarZoneRulesDataProvider implements ZoneRulesDataProvider {

    /**
     * The group ID.
     */
    private final String groupID;
    /**
     * The version ID.
     */
    private final String versionID;
    /**
     * Cache of time zone data providers.
     */
    private final Map<String, ZoneRules> zones;

    /**
     * Loads any time zone rules data stored in jar files.
     *
     * @throws RuntimeException if the time zone rules cannot be loaded
     */
    static void load() {
        for (ZoneRulesDataProvider provider : loadJars()) {
            ZoneRulesGroup.registerProvider(provider);
        }
    }

    /**
     * Loads the rules from the jar files.
     *
     * @return the list of loaded rules, never null
     * @throws Exception if an error occurs
     */
    private static List<ZoneRulesDataProvider> loadJars() {
        List<ZoneRulesDataProvider> providers = new ArrayList<ZoneRulesDataProvider>();
        URL url = null;
        try {
            Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("javax/time/calendar/zone/ZoneRuleInfo.dat");
            Set<String> loaded = new HashSet<String>();  // avoid equals() on URL
            while (en.hasMoreElements()) {
                url = en.nextElement();
                if (loaded.add(url.toExternalForm())) {
                    loadJar(providers, url);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load time zone rule data: " + url, ex);
        }
        return providers;
    }

    /**
     * Loads the rules from a jar file.
     *
     * @param providers  the list to add to, not null
     * @param url  the jar file to load, not null 
     * @throws Exception if an error occurs
     */
    @SuppressWarnings("unchecked")
    private static void loadJar(List<ZoneRulesDataProvider> providers, URL url) throws ClassNotFoundException, IOException {
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            int dataSets = ois.readInt();
            for (int i = 0; i < dataSets; i++) {
                String groupID = ois.readUTF();
                String versionID = ois.readUTF();
                Map<String, ZoneRules> zones = (Map<String, ZoneRules>) ois.readObject();
                providers.add(new JarZoneRulesDataProvider(groupID, versionID, zones));
            }
            
        } catch (IOException ex) {
            throwing = true;
            throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    if (throwing == false) {
                        throw ex;
                    }
                }
            }
        }
    }

    /**
     * Loads the time zone data.
     *
     * @param groupID  the group ID of the rules, not null
     * @param versionID  the version ID of the rules, not null
     * @param zones  the loaded zones, not null
     */
    public JarZoneRulesDataProvider(String groupID, String versionID, Map<String, ZoneRules> zones) {
        this.groupID = groupID;
        this.versionID = versionID;
        this.zones = zones;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return groupID;
    }

    /** {@inheritDoc} */
    public Set<String> getIDs() {
        Set<String> ids = new HashSet<String>(zones.size());
        for (String id : zones.keySet()) {
            ids.add(id + '#' + versionID);
        }
        return Collections.unmodifiableSet(ids);
    }

    /** {@inheritDoc} */
    public ZoneRules getZoneRules(String regionID, String versionID) {
        if (regionID == null || versionID == null || versionID.equals(this.versionID) == false) {
            return null;
        }
        return zones.get(regionID);
    }

}
