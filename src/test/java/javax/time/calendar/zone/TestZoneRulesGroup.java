/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.DayOfWeek;
import javax.time.MonthOfYear;
import javax.time.OffsetDateTime;
import javax.time.TestZoneId;
import javax.time.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneRulesGroup.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneRulesGroup {

    //-----------------------------------------------------------------------
    // isValidGroupID()
    //-----------------------------------------------------------------------
	@Test(groups={"tck"})
    public void test_isValidGroup() {
        assertEquals(ZoneRulesGroup.isValidGroupID("TZDB"), true);
        assertEquals(ZoneRulesGroup.isValidGroupID("NOTREAL"), false);
        assertEquals(ZoneRulesGroup.isValidGroupID(""), false);
        assertEquals(ZoneRulesGroup.isValidGroupID(null), false);
    }

    //-----------------------------------------------------------------------
    // getGroup()
    //-----------------------------------------------------------------------
	@Test(groups={"tck"})
    public void test_getGroup() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").getID(), "TZDB");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getGroup_badGroup() {
        ZoneRulesGroup.getGroup("NOTREAL");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getGroup_badGroupBlank() {
        ZoneRulesGroup.getGroup("");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getGroup_null() {
        ZoneRulesGroup.getGroup(null);
    }

    //-----------------------------------------------------------------------
    // getAvailableGroups()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAvailableGroups() {
        List<ZoneRulesGroup> groups = ZoneRulesGroup.getAvailableGroups();
        assertEquals(groups.contains(ZoneRulesGroup.getGroup("TZDB")), true);
        groups.clear();
        assertEquals(groups.size(), 0);
        List<ZoneRulesGroup> groups2 = ZoneRulesGroup.getAvailableGroups();
        assertEquals(groups2.contains(ZoneRulesGroup.getGroup("TZDB")), true);
    }

    //-----------------------------------------------------------------------
    // getParsableIDs()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getParsableIDs() {
        Set<String> parsableIDs = ZoneRulesGroup.getParsableIDs();
        assertEquals(parsableIDs.contains("Europe/London"), true);
        assertEquals(parsableIDs.contains("TZDB:Europe/London"), true);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
    public void test_getParsableIDs_unmodifiableClear() {
        ZoneRulesGroup.getParsableIDs().clear();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
    public void test_getParsableIDs_unmodifiableRemove() {
        ZoneRulesGroup.getParsableIDs().remove("Europe/London");
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
    public void test_getParsableIDs_unmodifiableAdd() {
        ZoneRulesGroup.getParsableIDs().add("Europe/Lon");
    }

    //-----------------------------------------------------------------------
    // registerProvider()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_registerProvider() {
        List<ZoneRulesGroup> pre = ZoneRulesGroup.getAvailableGroups();
        for (ZoneRulesGroup group : pre) {
            assertEquals(group.getID().equals("TEMPMOCK.-_"), false);
        }
        
        ZoneRulesGroup.registerProvider(new MockTempProvider());
        
        for (ZoneRulesGroup group : pre) {
            assertEquals(group.getID().equals("TEMPMOCK.-_"), false);
        }
        List<ZoneRulesGroup> post = ZoneRulesGroup.getAvailableGroups();
        assertEquals(post.contains(ZoneRulesGroup.getGroup("TEMPMOCK.-_")), true);
        
        assertEquals(ZoneRulesGroup.getGroup("TEMPMOCK.-_").getID(), "TEMPMOCK.-_");
        assertEquals(ZoneRulesGroup.getGroup("TEMPMOCK.-_").getRules("World%@~.-_", "1.-_").isFixedOffset(), true);
    }

    static class MockTempProvider implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "TEMPMOCK.-_";
        }
        public Set<ZoneRulesVersion> getVersions() {
            ZoneRulesVersion version = new ZoneRulesVersion() {
                public String getVersionID() {
                    return "1.-_";
                }
                public boolean isRegionID(String regionID) {
                    return regionID.equals("World%@~.-_");
                }
                public Set<String> getRegionIDs() {
                    return new HashSet<String>(Arrays.asList("World%@~.-_"));
                }
                public ZoneRules getZoneRules(String regionID) {
                    return ZoneRules.ofFixed(ZoneOffset.of("+01:45"));
                }
            };
            return new HashSet<ZoneRulesVersion>(Arrays.asList(version));
        }
        public Set<String> getRegionIDs() {
            return new HashSet<String>(Arrays.asList("World%@~.-_"));
        }
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_registerProvider_invalidGroupID() {
        ZoneRulesGroup.registerProvider(new MockTempProviderInvalidGroupID());
    }

    static class MockTempProviderInvalidGroupID implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "TEMPMOCK%";
        }
        public Set<ZoneRulesVersion> getVersions() {
            throw new UnsupportedOperationException();
        }
        public Set<String> getRegionIDs() {
            throw new UnsupportedOperationException();
        }
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_registerProvider_invalidRegionVersionID() {
        ZoneRulesGroup.registerProvider(new MockTempProviderInvalidGroupID());
    }

    static class MockTempProviderInvalidVersionID implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "TEMPMOCK";
        }
        public Set<ZoneRulesVersion> getVersions() {
            ZoneRulesVersion version = new ZoneRulesVersion() {
                public String getVersionID() {
                    return "1%";
                }
                public boolean isRegionID(String regionID) {
                    throw new UnsupportedOperationException();
                }
                public Set<String> getRegionIDs() {
                    throw new UnsupportedOperationException();
                }
                public ZoneRules getZoneRules(String regionID) {
                    throw new UnsupportedOperationException();
                }
            };
            return new HashSet<ZoneRulesVersion>(Arrays.asList(version));
        }
        public Set<String> getRegionIDs() {
            throw new UnsupportedOperationException();
        }
    }

    //-----------------------------------------------------------------------
    // isValidRules()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isValidRules() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.isValidRules("Europe/London", "2008i"), true);
        assertEquals(group.isValidRules("Europe/Lon", "2008i"), false);
        assertEquals(group.isValidRules("Europe/London", "20"), false);
        assertEquals(group.isValidRules("Europe/London", null), false);
        assertEquals(group.isValidRules(null, "2008i"), false);
    }

    //-----------------------------------------------------------------------
    // getRules()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getRules() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        ZoneRules rules = group.getRules("Europe/London", "2008i");
        assertEquals(rules.getTransitionRules().size(), 2);
        assertEquals(rules.getTransitionRules().get(0).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(0).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(0).getMonthOfYear(), MonthOfYear.MARCH);
        assertEquals(rules.getTransitionRules().get(1).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(1).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(1).getMonthOfYear(), MonthOfYear.OCTOBER);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getRules_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/Lon", "2008i");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getRules_unknownVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/London", "20");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRules_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules(null, "2008i");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRules_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/London", null);
    }

    //-----------------------------------------------------------------------
    // getRulesValidFor()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getRulesValidFor() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        ZoneRules rules = group.getRulesValidFor("Europe/London", "2008i", odt);
        assertEquals(rules.getTransitionRules().size(), 2);
        assertEquals(rules.getTransitionRules().get(0).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(0).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(0).getMonthOfYear(), MonthOfYear.MARCH);
        assertEquals(rules.getTransitionRules().get(1).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(1).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(1).getMonthOfYear(), MonthOfYear.OCTOBER);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getRulesValidFor_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/Lon", "2008i", odt);
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getRulesValidFor_unknownVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/London", "20", odt);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRulesValidFor_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor(null, "2008i", odt);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRulesValidFor_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/London", null, odt);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRulesValidFor_nullDateTime() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRulesValidFor("Europe/London", "2008i", null);
    }

    //-----------------------------------------------------------------------
    // getLatestVersionIDValidFor()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getLatestVersionIDValidFor() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        assertEquals(group.getLatestVersionIDValidFor("Europe/London", odt), TestZoneId.LATEST_TZDB);
    }

    @Test(groups={"tck"})
    public void test_getLatestVersionIDValidFor_mock_latest() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.ofHoursMinutes(2, 45));
        assertEquals(group.getLatestVersionIDValidFor("RulesChange", odt), "v2");
    }

    @Test(groups={"tck"})
    public void test_getLatestVersionIDValidFor_mock_notLatest() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.ofHoursMinutes(1, 45));
        assertEquals(group.getLatestVersionIDValidFor("RulesChange", odt), "v1");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getLatestVersionIDValidFor_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getLatestVersionIDValidFor("Europe/Lon", odt);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getLatestVersionIDValidFor_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getLatestVersionIDValidFor(null, odt);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getLatestVersionIDValidFor_nullDateTime() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionIDValidFor("Europe/London", null);
    }

    //-----------------------------------------------------------------------
    // getAvailableVersionIDs()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAvailableVersionIDs_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        Set<String> versions = group.getAvailableVersionIDs();
        assertEquals(versions.contains("2008i"), true);
    }

    @Test(groups={"tck"})
    public void test_getAvailableVersionIDs_String_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        Set<String> versions = group.getAvailableVersionIDs();
        assertEquals(versions.size(), 2);
        assertEquals(versions.contains("v1"), true);
        assertEquals(versions.contains("v2"), true);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
    public void test_getAvailableVersionIDs_String_unmodifiable() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        Set<String> versions = group.getAvailableVersionIDs();
        versions.clear();
    }

//    //-----------------------------------------------------------------------
//    // getAvailableRegionIDs()
//    //-----------------------------------------------------------------------
//    public void test_getAvailableRegionIDs() {
//        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
//        assertEquals(group.getAvailableRegionIDs().contains("Europe/London"), true);
//    }
//
//    public void test_getAvailableRegionIDs_mock() {
//        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
//        List<String> regions = group.getAvailableRegionIDs();
//        assertEquals(regions.size(), 2);
//        assertEquals(regions.contains("RulesChange"), true);
//        assertEquals(regions.contains("NewPlace"), true);
//        regions.clear();
//        assertEquals(regions.size(), 0);
//        List<String> regions2 = group.getAvailableRegionIDs();
//        assertNotSame(regions2, regions);
//        assertEquals(regions2.size(), 2);
//        assertEquals(regions2.contains("RulesChange"), true);
//        assertEquals(regions2.contains("NewPlace"), true);
//    }

    //-----------------------------------------------------------------------
    // getLatestVersionID(String)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getLatestVersionID() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getLatestVersionID(), TestZoneId.LATEST_TZDB);
    }

    @Test(groups={"tck"})
    public void test_getLatestVersionID_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(group.getLatestVersionID(), "v2");
    }

    //-----------------------------------------------------------------------
    // getLatestVersionID(String)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getLatestVersionID_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getLatestVersionID("Europe/London"), TestZoneId.LATEST_TZDB);
    }

    @Test(groups={"tck"})
    public void test_getLatestVersionID_String_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(group.getLatestVersionID("RulesChange"), "v2");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getLatestVersionID_String_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionID("Europe/Lon");
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getLatestVersionID_String_unknownRegion_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        group.getLatestVersionID("Europe/Lon");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getLatestVersionID_String_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionID(null);
    }

    //-----------------------------------------------------------------------
    // isValidRegionID(String)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isValidRegionID_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.isValidRegionID("Europe/London"), true);
        assertEquals(group.isValidRegionID("Rubbish"), false);
    }

    @Test(groups={"tck"})
    public void test_isValidRegionID_String_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(group.isValidRegionID("RulesChange"), true);
        assertEquals(group.isValidRegionID("Rubbish"), false);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_isValidRegionID_String_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.isValidRegionID(null);
    }

    //-----------------------------------------------------------------------
    // getRegionIDs(String)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getRegionIDs_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getRegionIDs("2008i").contains("Europe/London"), true);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
    public void test_getRegionIDs_String_unmodifiable() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        Set<String> regionIDs = group.getRegionIDs("v1");
        regionIDs.clear();
    }

    @Test(expectedExceptions=CalendricalException.class, groups={"tck"})
    public void test_getRegionIDs_String_badVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        group.getRegionIDs("20");
    }

    @Test(groups={"tck"})
    public void test_getRegionIDs_String_mock_v1() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        Set<String> regions = group.getRegionIDs("v1");
        assertEquals(regions.size(), 1);
        assertEquals(regions.contains("RulesChange"), true);
    }

    @Test(groups={"tck"})
    public void test_getRegionIDs_String_mock_v2() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        Set<String> regions = group.getRegionIDs("v2");
        assertEquals(regions.size(), 2);
        assertEquals(regions.contains("RulesChange"), true);
        assertEquals(regions.contains("NewPlace"), true);
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRegionIDs_String_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRegionIDs(null);
    }

    //-----------------------------------------------------------------------
    static class MockProvider implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "MOCK";
        }
        public Set<ZoneRulesVersion> getVersions() {
            ZoneRulesVersion v1 = new ZoneRulesVersion() {
                public String getVersionID() {
                    return "v1";
                }
                public boolean isRegionID(String regionID) {
                    return getRegionIDs().contains(regionID);
                }
                public Set<String> getRegionIDs() {
                    return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("RulesChange")));
                }
                public ZoneRules getZoneRules(String regionID) {
                    return ZoneRules.ofFixed(ZoneOffset.of("+01:45"));
                }
            };
            ZoneRulesVersion v2 = new ZoneRulesVersion() {
                public String getVersionID() {
                    return "v2";
                }
                public boolean isRegionID(String regionID) {
                    return getRegionIDs().contains(regionID);
                }
                public Set<String> getRegionIDs() {
                    return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("NewPlace", "RulesChange")));
                }
                public ZoneRules getZoneRules(String regionID) {
                    if (regionID.equals("NewPlace")) {
                        return ZoneRules.ofFixed(ZoneOffset.of("+01:00"));
                    } else {
                        return ZoneRules.ofFixed(ZoneOffset.of("+02:45"));
                    }
                }
            };
            return new HashSet<ZoneRulesVersion>(Arrays.asList(v1, v2));
        }
        public Set<String> getRegionIDs() {
            return new HashSet<String>(Arrays.asList("RulesChange", "NewPlace"));
        }
    }
    static {
        ZoneRulesGroup.registerProvider(new MockProvider());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_equals() {
        ZoneRulesGroup a = ZoneRulesGroup.getGroup("TZDB");
        ZoneRulesGroup b = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(b.equals(b), true);
    }

    @Test(groups={"tck"})
    public void test_equals_string_false() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").equals("TZDB"), false);
    }

    @Test(groups={"tck"})
    public void test_equals_null_false() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
//    public void test_hashCode() {
//        ZoneRulesGroup a = ZoneRulesGroup.getGroup("TZDB");
//        ZoneRulesGroup b = ZoneRulesGroup.getGroup("MOCK");
//        assertEquals(a.hashCode() == b.hashCode(), false);  // highly likely to be true, but not guaranteed
//    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_toString() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").toString(), "TZDB");
        assertEquals(ZoneRulesGroup.getGroup("MOCK").toString(), "MOCK");
    }

}
