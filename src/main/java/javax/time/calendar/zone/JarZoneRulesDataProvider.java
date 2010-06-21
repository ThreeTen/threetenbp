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
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.time.CalendricalException;

/**
 * Loads time-zone rules stored in a file accessed via class loader.
 * <p>
 * JarZoneRulesDataProvider is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
final class JarZoneRulesDataProvider implements ZoneRulesDataProvider {

    /**
     * The data-source URL for lazy loading.
     */
    private final URL url;
    /**
     * The group ID.
     */
    private final String groupID;
    /**
     * All the versions in the provider.
     */
    private final ZoneRulesVersion[] versions;
    /**
     * All the regions in the provider.
     */
    private final String[] regions;
    /**
     * The group.
     */
    private volatile ZoneRulesGroup group;

    /**
     * Loads any time-zone rules data stored in files.
     *
     * @throws RuntimeException if the time-zone rules cannot be loaded
     */
    static void load() {
        for (JarZoneRulesDataProvider provider : loadResources()) {
            ZoneRulesGroup group = ZoneRulesGroup.registerProvider(provider);
            provider.setGroup(group);
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
        this.url = url;
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            DataInputStream dis = new DataInputStream(in);
            dis.readInt();  // length of header (ignore for now)
            this.groupID = dis.readUTF();
            int versionCount = dis.readShort();
            this.versions = new ZoneRulesVersion[versionCount];
            for (int i = 0; i < versionCount; i++) {
                String versionID = dis.readUTF();
                versions[i] = new ResourceZoneRulesVersion(this, versionID, i);
            }
            int regionCount = dis.readShort();
            this.regions = new String[regionCount];
            for (int i = 0; i < regionCount; i++) {
                regions[i] = dis.readUTF();
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

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    public String getGroupID() {
        return groupID;
    }

    /** {@inheritDoc} */
    public Set<ZoneRulesVersion> getVersions() {
        return Collections.unmodifiableSet(new HashSet<ZoneRulesVersion>(Arrays.asList(versions)));
    }

    /**
     * Gets the group.
     * @return the group, not null
     */
    private ZoneRulesGroup getGroup() {
        return group;
    }

    /**
     * Sets the group.
     * @param group  the group, not null
     */
    private void setGroup(ZoneRulesGroup group) {
        this.group = group;
    }

    //-------------------------------------------------------------------------
    /**
     * Loads the version.
     * @param versionID  the version, not null
     * @param index  the index, not null
     * @throws Exception if an error occurs
     */
    Map<String, ZoneRules> loadVersion(String versionID, int index) throws Exception {
        boolean throwing = false;
        InputStream in = null;
        try {
            in = url.openStream();
            DataInputStream dis = new DataInputStream(in);
            // skip header
            int header = dis.readInt();
            skipFully(in, header);
            // link version-region-rules
            Map<String, Integer> ruleIndexMap = new HashMap<String, Integer>();
            for (int i = 0; i < versions.length; i++) {
                int versionRegionCount = dis.readShort();
                for (int j = 0; j < versionRegionCount; j++) {
                    int regionIndex = dis.readShort();
                    int rulesIndex = dis.readShort();
                    if (i == index) {
                        ruleIndexMap.put(regions[regionIndex], rulesIndex);
                    }
                }
            }
            // rules
            int ruleCount = dis.readShort();
            ZoneRules[] ruleArray = new ZoneRules[ruleCount];
            for (int i = 0; i < ruleCount; i++) {
                ruleArray[i] = (ZoneRules) Ser.read(dis);
            }
            Map<String, ZoneRules> ruleMap = new HashMap<String, ZoneRules>();
            for (Entry<String, Integer> entry : ruleIndexMap.entrySet()) {
                ruleMap.put(entry.getKey(), ruleArray[entry.getValue()]);
            }
            return ruleMap;
        } catch (IOException ex) {
            throwing = true;
            throw ex;
        } catch (ClassNotFoundException ex) {
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
     * Implement skip properly, as JDK doesn't have a suitable method.
     * @param in  the input stream, not null
     * @param amount  the amount to skip
     * @throws IOException if an error occurs
     */
    private static void skipFully(InputStream in, long amount) throws IOException {
        while (amount > 0) {
            long skipped = in.skip(amount);
            if (skipped == 0) {
                if (in.read() == -1) {
                    throw new EOFException();
                }
                amount--;
            } else {
                amount -= skipped;
            }
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Version.
     */
    static class ResourceZoneRulesVersion implements ZoneRulesVersion {
        /** Provider. */
        private volatile JarZoneRulesDataProvider provider;
        /** Version ID. */
        private final String versionID;
        /** Version ID. */
        private final int index;
        /** Version ID. */
        private volatile Map<String, ZoneRules> regionRuleMap;
        /** Constructor. */
        ResourceZoneRulesVersion(JarZoneRulesDataProvider provider, String versionID, int index) {
            this.provider = provider;
            this.versionID = versionID;
            this.index = index;
        }
        public String getVersionID() {
            return versionID;
        }
        public boolean isRegionID(String regionID) {
            if (regionRuleMap == null && Arrays.binarySearch(provider.regions, regionID) < 0) {  // avoid loading file
                return false;
            }
            return regionRuleMap().containsKey(regionID);
        }
        public Set<String> getRegionIDs() {
            return Collections.unmodifiableSet(regionRuleMap().keySet());
        }
        public ZoneRules getZoneRules(String regionID) {
            return regionRuleMap().get(regionID);
        }
        private Map<String, ZoneRules> regionRuleMap() {
            Map<String, ZoneRules> map = regionRuleMap;
            if (map == null) {
                try {
                    regionRuleMap = map = provider.loadVersion(versionID, index);
                } catch (Exception ex) {
                    throw new CalendricalException("Unable to load version '" + versionID + "': " + ex.getMessage(), ex);
                }
            }
            return map;
        }
    }

}
