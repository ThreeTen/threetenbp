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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Loads time-zone rules stored in a file accessed via class loader.
 * <p>
 * JarZoneRulesDataProvider is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
final class JarZoneRulesDataProvider implements ZoneRulesDataProvider {

    /**
     * The group ID.
     */
    private final String groupID;
    /**
     * Cache of time-zone data providers.
     */
    private final Map<String, ZoneRules> zones;

    /**
     * Loads any time-zone rules data stored in files.
     *
     * @throws RuntimeException if the time-zone rules cannot be loaded
     */
    static void load() {
        for (ZoneRulesDataProvider provider : loadResources()) {
            ZoneRulesGroup.registerProvider(provider);
        }
    }

    /**
     * Loads the rules from files in the class loader, often jar files.
     *
     * @return the list of loaded rules, never null
     * @throws Exception if an error occurs
     */
    private static List<ZoneRulesDataProvider> loadResources() {
        List<ZoneRulesDataProvider> providers = new ArrayList<ZoneRulesDataProvider>();
        URL url = null;
        try {
            Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("javax/time/calendar/zone/ZoneRules.dat");
            Set<String> loaded = new HashSet<String>();  // avoid equals() on URL
            while (en.hasMoreElements()) {
                url = en.nextElement();
                if (loaded.add(url.toExternalForm())) {
                    loadResource(providers, url);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load time-zone rule data: " + url, ex);
        }
        return providers;
    }

    /**
     * Loads the rules from a URL, often in a jar file.
     *
     * @param providers  the list to add to, not null
     * @param url  the jar file to load, not null 
     * @throws Exception if an error occurs
     */
    private static void loadResource(List<ZoneRulesDataProvider> providers, URL url) throws ClassNotFoundException, IOException {
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            String groupID = ois.readUTF();
            int versionCount = ois.readShort();
            String[] versionArray = new String[versionCount];
            for (int i = 0; i < versionCount; i++) {
                versionArray[i] = ois.readUTF();
            }
            int regionCount = ois.readShort();
            String[] regionArray = new String[regionCount];
            for (int i = 0; i < regionCount; i++) {
                regionArray[i] = ois.readUTF();
            }
            Map<String, Integer> ruleIndexMap = new HashMap<String, Integer>();
            for (int i = 0; i < versionCount; i++) {
                int versionRegionCount = ois.readShort();
                for (int j = 0; j < versionRegionCount; j++) {
                    int regionIndex = ois.readShort();
                    int rulesIndex = ois.readInt();
                    String id = regionArray[regionIndex] + '#' + versionArray[i];
                    ruleIndexMap.put(id, rulesIndex);
                }
            }
            int ruleCount = ois.readInt();
            ZoneRules[] ruleArray = new ZoneRules[ruleCount];
            for (int i = 0; i < ruleCount; i++) {
                ruleArray[i] = (ZoneRules) Ser.read(ois);
            }
            Map<String, ZoneRules> ruleMap = new HashMap<String, ZoneRules>();
            for (Entry<String, Integer> entry : ruleIndexMap.entrySet()) {
                ruleMap.put(entry.getKey(), ruleArray[entry.getValue()]);
            }
            
            providers.add(new JarZoneRulesDataProvider(groupID, ruleMap));
            
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
     * Loads the time-zone data.
     *
     * @param groupID  the group ID of the rules, not null
     * @param zones  the loaded zones, not null
     */
    public JarZoneRulesDataProvider(String groupID, Map<String, ZoneRules> zones) {
        this.groupID = groupID;
        this.zones = zones;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return groupID;
    }

    /** {@inheritDoc} */
    public Set<String> getIDs() {
        return Collections.unmodifiableSet(zones.keySet());
    }

    /** {@inheritDoc} */
    public ZoneRules getZoneRules(String regionID, String versionID) {
        return zones.get(regionID + '#' + versionID);
    }

}
