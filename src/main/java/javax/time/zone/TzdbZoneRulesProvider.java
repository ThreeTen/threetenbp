/*
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.zone;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.time.DateTimeException;

/**
 * Loads time-zone rules for 'TZDB'.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public final class TzdbZoneRulesProvider extends ZoneRulesProvider {

    /**
     * All the regions that are available.
     */
    private final Set<String> regionIds = new CopyOnWriteArraySet<>();
    /**
     * All the versions that are available.
     */
    private final ConcurrentNavigableMap<String, Version> versions = new ConcurrentSkipListMap<>();
    /**
     * All the URLs that have been loaded.
     * Uses String to avoid equals() on URL.
     */
    private Set<String> loadedUrls = new CopyOnWriteArraySet<>();

    /**
     * Creates an instance.
     * Created by the {@code ServiceLoader}.
     */
    public TzdbZoneRulesProvider() {
        super("TZDB");
        if (refresh(ClassLoader.getSystemClassLoader()) == false) {
            throw new DateTimeException("No time-zone rules found for 'TZDB'");
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public Set<String> getAvailableRegionIds() {
        return new HashSet<>(regionIds);
    }

    @Override
    public SortedSet<String> getAvailableVersionIds() {
        return new TreeSet<>(versions.keySet());
    }

    @Override
    public boolean isValid(String regionId, String versionId) {
        if (versionId == null) {
            return (regionId != null && versions.lastEntry().getValue().isValid(regionId));
        }
        Version version = versions.get(versionId);
        return (regionId != null && version != null && version.isValid(regionId));
    }

    @Override
    public ZoneRules getRules(String regionId, String versionId) {
        Objects.requireNonNull(regionId, "regionId");
        if (versionId == null) {
            return versions.lastEntry().getValue().getRules(regionId);            
        }
        Version version = versions.get(versionId);
        if (version == null) {
            throw new DateTimeException("Unsupported TZDB time-zone version: " + versionId);
        }
        return version.getRules(regionId);
    }

    @Override
    public boolean refresh(ClassLoader classLoader) {
        boolean updated = false;
        URL url = null;
        try {
            Enumeration<URL> en = classLoader.getResources("javax/time/zone/TZDB.dat");
            while (en.hasMoreElements()) {
                url = en.nextElement();
                if (loadedUrls.add(url.toExternalForm())) {
                    Iterable<Version> loadedVersions = load(url);
                    for (Version loadedVersion : loadedVersions) {
                        if (versions.putIfAbsent(loadedVersion.versionId, loadedVersion) != null) {
                            throw new DateTimeException("Data already loaded for TZDB time-zone rules version: " + loadedVersion.versionId);
                        }
                    }
                    updated = true;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load TZDB time-zone rules: " + url, ex);
        }
        return updated;
    }

    /**
     * Loads the rules from a URL, often in a jar file.
     *
     * @param url  the jar file to load, not null 
     * @throws Exception if an error occurs
     */
    private Iterable<Version> load(URL url) throws ClassNotFoundException, IOException {
        try (InputStream in = url.openStream()) {
            DataInputStream dis = new DataInputStream(in);
            if (dis.readByte() != 1) {
                throw new StreamCorruptedException("File format not recognised");
            }
            // group
            String groupId = dis.readUTF();
            if ("TZDB".equals(groupId) == false) {
                throw new StreamCorruptedException("File format not recognised");
            }
            // versions
            int versionCount = dis.readShort();
            String[] versionArray = new String[versionCount];
            for (int i = 0; i < versionCount; i++) {
                versionArray[i] = dis.readUTF();
            }
            // regions
            int regionCount = dis.readShort();
            String[] regionArray = new String[regionCount];
            for (int i = 0; i < regionCount; i++) {
                regionArray[i] = dis.readUTF();
            }
            regionIds.addAll(Arrays.asList(regionArray));
            // rules
            int ruleCount = dis.readShort();
            Object[] ruleArray = new Object[ruleCount];
            for (int i = 0; i < ruleCount; i++) {
                byte[] bytes = new byte[dis.readShort()];
                dis.readFully(bytes);
                ruleArray[i] = bytes;
            }
            AtomicReferenceArray<Object> ruleData = new AtomicReferenceArray<>(ruleArray);
            // link version-region-rules
            Set<Version> versionSet = new HashSet<Version>(versionCount);
            for (int i = 0; i < versionCount; i++) {
                int versionRegionCount = dis.readShort();
                String[] versionRegionArray = new String[versionRegionCount];
                short[] versionRulesArray = new short[versionRegionCount];
                for (int j = 0; j < versionRegionCount; j++) {
                    versionRegionArray[j] = regionArray[dis.readShort()];
                    versionRulesArray[j] = dis.readShort();
                }
                versionSet.add(new Version(versionArray[i], versionRegionArray, versionRulesArray, ruleData));
            }
            return versionSet;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A version of the TZDB rules.
     */
    static class Version {
        private final String versionId;
        private final String[] regionArray;
        private final short[] ruleIndices;
        private final AtomicReferenceArray<Object> ruleData;

        Version(String versionId, String[] regionIds, short[] ruleIndices, AtomicReferenceArray<Object> ruleData) {
            this.ruleData = ruleData;
            this.versionId = versionId;
            this.regionArray = regionIds;
            this.ruleIndices = ruleIndices;
        }

        Version withRuleData(AtomicReferenceArray<Object> ruleData) {
            return new Version(versionId, regionArray, ruleIndices, ruleData);
        }

        boolean isValid(String regionId) {
            return (Arrays.binarySearch(regionArray, regionId) >= 0);
        }

        ZoneRules getRules(String regionId) {
            int regionIndex = Arrays.binarySearch(regionArray, regionId);
            if (regionIndex < 0) {
                throw new DateTimeException("Unsupported time-zone: TZDB:" + regionId + ", version: " + versionId);
            }
            try {
                return createRule(ruleIndices[regionIndex]);
            } catch (Exception ex) {
                throw new DateTimeException("Invalid binary time-zone data: TZDB:" + regionId + ", version: " + versionId, ex);
            }
        }

        ZoneRules createRule(short index) throws Exception {
            Object obj = ruleData.get(index);
            if (obj instanceof byte[]) {
                byte[] bytes = (byte[]) obj;
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
                obj = Ser.read(dis);
                ruleData.set(index, obj);
            }
            return (ZoneRules) obj;
        }

        @Override
        public String toString() {
            return versionId;
        }
    }

}
