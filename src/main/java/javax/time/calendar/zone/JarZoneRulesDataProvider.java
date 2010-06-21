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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * All the versions in the provider.
     */
    private final Set<ZoneRulesVersion> versions;

    /**
     * Loads any time-zone rules data stored in files.
     *
     * @throws RuntimeException if the time-zone rules cannot be loaded
     */
    static void load() {
        for (JarZoneRulesDataProvider provider : loadResources()) {
            ZoneRulesGroup.registerProvider(provider);
        }
    }

    /**
     * Loads the rules from files in the class loader, often jar files.
     *
     * @return the list of loaded rules, never null
     * @throws Exception if an error occurs
     */
    private static List<JarZoneRulesDataProvider> loadResources() {
        List<JarZoneRulesDataProvider> providers = new ArrayList<JarZoneRulesDataProvider>();
        URL url = null;
        try {
            Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("javax/time/calendar/zone/ZoneRules.dat");
            Set<String> loaded = new HashSet<String>();  // avoid equals() on URL
            while (en.hasMoreElements()) {
                url = en.nextElement();
                if (loaded.add(url.toExternalForm())) {
                    providers.add(new JarZoneRulesDataProvider(url));
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
    private JarZoneRulesDataProvider(URL url) throws ClassNotFoundException, IOException {
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            DataInputStream dis = new DataInputStream(in);
            dis.readInt();  // length of header (ignore for now)
            this.groupID = dis.readUTF();
            int versionCount = dis.readShort();
            String[] versionArray = new String[versionCount];
            for (int i = 0; i < versionCount; i++) {
                versionArray[i] = dis.readUTF();
            }
            int regionCount = dis.readShort();
            String[] regionArray = new String[regionCount];
            for (int i = 0; i < regionCount; i++) {
                regionArray[i] = dis.readUTF();
            }
            // link version-region-rules
            Object[] versionRuleTable = new Object[versionCount];
            for (int i = 0; i < versionCount; i++) {
                int versionRegionCount = dis.readShort();
                short[] regionTable = new short[versionRegionCount * 2];
                versionRuleTable[i] = regionTable;
                for (int j = 0; j < versionRegionCount; j++) {
                    regionTable[j * 2] = dis.readShort();
                    regionTable[j * 2 + 1] = dis.readShort();
                }
            }
            // rules
            int ruleCount = dis.readShort();
            ZoneRules[] ruleArray = new ZoneRules[ruleCount];
            for (int i = 0; i < ruleCount; i++) {
                ruleArray[i] = (ZoneRules) Ser.read(dis);
            }
            Set<ZoneRulesVersion> versionSet = new HashSet<ZoneRulesVersion>(versionCount);
            for (int i = 0; i < versionCount; i++) {
                short[] regionTable = (short[]) versionRuleTable[i];
                Map<String, ZoneRules> ruleMap = new HashMap<String, ZoneRules>();
                for (int j = 0; j < regionTable.length; j += 2) {
                    ruleMap.put(regionArray[regionTable[j]], ruleArray[regionTable[j + 1]]);
                }
                versionSet.add(new ResourceZoneRulesVersion(versionArray[i], ruleMap));
            }
            this.versions = Collections.unmodifiableSet(versionSet);
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

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return groupID;
    }

    /** {@inheritDoc} */
    public Set<ZoneRulesVersion> getVersions() {
        return versions;
    }

    //-------------------------------------------------------------------------
    /**
     * Version.
     */
    static class ResourceZoneRulesVersion implements ZoneRulesVersion {
        /** Version ID. */
        private final String versionID;
        /** Version ID. */
        private volatile Map<String, ZoneRules> ruleMap;
        /** Constructor. */
        ResourceZoneRulesVersion(String versionID, Map<String, ZoneRules> ruleMap) {
            this.versionID = versionID;
            this.ruleMap = ruleMap;
        }
        public String getVersionID() {
            return versionID;
        }
        public boolean isRegionID(String regionID) {
            return ruleMap.containsKey(regionID);
        }
        public Set<String> getRegionIDs() {
            return Collections.unmodifiableSet(ruleMap.keySet());
        }
        public ZoneRules getZoneRules(String regionID) {
            return ruleMap.get(regionID);
        }
    }

}
