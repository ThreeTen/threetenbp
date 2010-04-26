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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TestTimeZone;
import javax.time.calendar.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneRulesGroup.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestZoneRulesGroup {

    //-----------------------------------------------------------------------
    // isValidGroup()
    //-----------------------------------------------------------------------
    public void test_isValidGroup() {
        assertEquals(ZoneRulesGroup.isValidGroup("TZDB"), true);
        assertEquals(ZoneRulesGroup.isValidGroup("NOTREAL"), false);
        assertEquals(ZoneRulesGroup.isValidGroup(""), false);
        assertEquals(ZoneRulesGroup.isValidGroup(null), false);
    }

    //-----------------------------------------------------------------------
    // getGroup()
    //-----------------------------------------------------------------------
    public void test_getGroup() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").getID(), "TZDB");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getGroup_badGroup() {
        ZoneRulesGroup.getGroup("NOTREAL");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getGroup_badGroupBlank() {
        ZoneRulesGroup.getGroup("");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getGroup_null() {
        ZoneRulesGroup.getGroup(null);
    }

    //-----------------------------------------------------------------------
    // getAvailableGroups()
    //-----------------------------------------------------------------------
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
    public void test_getParsableIDs() {
        Set<String> parsableIDs = ZoneRulesGroup.getParsableIDs();
        assertEquals(parsableIDs.contains("Europe/London"), true);
        assertEquals(parsableIDs.contains("Europe/London#2008i"), true);
        assertEquals(parsableIDs.contains("TZDB:Europe/London"), true);
        assertEquals(parsableIDs.contains("TZDB:Europe/London#2008i"), true);
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getParsableIDs_unmodifiableClear() {
        ZoneRulesGroup.getParsableIDs().clear();
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getParsableIDs_unmodifiableRemove() {
        ZoneRulesGroup.getParsableIDs().remove("Europe/London");
    }

    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void test_getParsableIDs_unmodifiableAdd() {
        ZoneRulesGroup.getParsableIDs().add("Europe/Lon");
    }

    //-----------------------------------------------------------------------
    // registerProvider()
    //-----------------------------------------------------------------------
    public void test_registerProvider() {
        List<ZoneRulesGroup> pre = ZoneRulesGroup.getAvailableGroups();
        for (ZoneRulesGroup group : pre) {
            assertEquals(group.getID().equals("TEMPMOCK"), false);
        }
        
        ZoneRulesGroup.registerProvider(new MockTempProvider());
        
        for (ZoneRulesGroup group : pre) {
            assertEquals(group.getID().equals("TEMPMOCK"), false);
        }
        List<ZoneRulesGroup> post = ZoneRulesGroup.getAvailableGroups();
        assertEquals(post.contains(ZoneRulesGroup.getGroup("TEMPMOCK")), true);
        
        assertEquals(ZoneRulesGroup.getGroup("TEMPMOCK").getID(), "TEMPMOCK");
        assertEquals(ZoneRulesGroup.getGroup("TEMPMOCK").getRules("World", "").isFixedOffset(), true);
    }

    //-----------------------------------------------------------------------
    static class MockTempProvider implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "TEMPMOCK";
        }
        public Set<String> getIDs() {
            return Collections.singleton("World");
        }
        public ZoneRules getZoneRules(String regionID, String versionID) {
            return ZoneRules.fixed(ZoneOffset.of("+01:45"));
        }
    }

    //-----------------------------------------------------------------------
    // isValidRules()
    //-----------------------------------------------------------------------
    public void test_isValidRules() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.isValidRules("Europe/London", "2008i"), true);
        assertEquals(group.isValidRules("Europe/London", ""), true);  // latest version
        assertEquals(group.isValidRules("Europe/Lon", "2008i"), false);
        assertEquals(group.isValidRules("Europe/London", "20"), false);
        assertEquals(group.isValidRules("Europe/London", null), false);
        assertEquals(group.isValidRules(null, "2008i"), false);
    }

    //-----------------------------------------------------------------------
    // getRules()
    //-----------------------------------------------------------------------
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

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getRules_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/Lon", "2008i");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getRules_unknownVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/London", "20");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getRules_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules(null, "2008i");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getRules_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRules("Europe/London", null);
    }

    //-----------------------------------------------------------------------
    // isValidRules()
    //-----------------------------------------------------------------------
    public void test_isValidRulesFor() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        assertEquals(group.isValidRulesFor("Europe/London", "2008i", odt), true);
        assertEquals(group.isValidRulesFor("Europe/London", "", odt), true);  // latest version
        assertEquals(group.isValidRulesFor("Europe/Lon", "2008i", odt), false);
        assertEquals(group.isValidRulesFor("Europe/London", "20", odt), false);
        assertEquals(group.isValidRulesFor(null, "2008i", odt), false);
        assertEquals(group.isValidRulesFor("Europe/London", null, odt), false);
        assertEquals(group.isValidRulesFor("Europe/London", "2008i", null), false);
    }

    //-----------------------------------------------------------------------
    // getRulesValidFor()
    //-----------------------------------------------------------------------
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

    public void test_getRulesValidFor_floatingSearch() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(1970, 1, 1, 12, 0, ZoneOffset.ofHours(1));
        assertEquals(group.getRulesValidFor("Europe/London", "", odt), group.getRulesValidFor("Europe/London", "2008i", odt));
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getRulesValidFor_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/Lon", "2008i", odt);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getRulesValidFor_unknownVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/London", "20", odt);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getRulesValidFor_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor(null, "2008i", odt);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getRulesValidFor_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getRulesValidFor("Europe/London", null, odt);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getRulesValidFor_nullDateTime() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getRulesValidFor("Europe/London", "2008i", null);
    }

    //-----------------------------------------------------------------------
    // getLatestVersionIDValidFor()
    //-----------------------------------------------------------------------
    public void test_getLatestVersionIDValidFor() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        assertEquals(group.getLatestVersionIDValidFor("Europe/London", odt), TestTimeZone.LATEST_TZDB);
    }

    public void test_getLatestVersionIDValidFor_mock_latest() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.ofHoursMinutes(2, 45));
        assertEquals(group.getLatestVersionIDValidFor("RulesChange", odt), "v2");
    }

    public void test_getLatestVersionIDValidFor_mock_notLatest() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.ofHoursMinutes(1, 45));
        assertEquals(group.getLatestVersionIDValidFor("RulesChange", odt), "v1");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getLatestVersionIDValidFor_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getLatestVersionIDValidFor("Europe/Lon", odt);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getLatestVersionIDValidFor_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        OffsetDateTime odt = OffsetDateTime.of(2010, 1, 1, 12, 0, ZoneOffset.UTC);
        group.getLatestVersionIDValidFor(null, odt);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getLatestVersionIDValidFor_nullDateTime() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionIDValidFor("Europe/London", null);
    }

    //-----------------------------------------------------------------------
    // getAvailableRegionIDs()
    //-----------------------------------------------------------------------
    public void test_getAvailableRegionIDs() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getAvailableRegionIDs().contains("Europe/London"), true);
    }

    public void test_getAvailableRegionIDs_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableRegionIDs();
        assertEquals(regions.size(), 2);
        assertEquals(regions.contains("RulesChange"), true);
        assertEquals(regions.contains("NewPlace"), true);
        regions.clear();
        assertEquals(regions.size(), 0);
        List<String> regions2 = group.getAvailableRegionIDs();
        assertNotSame(regions2, regions);
        assertEquals(regions2.size(), 2);
        assertEquals(regions2.contains("RulesChange"), true);
        assertEquals(regions2.contains("NewPlace"), true);
    }

    //-----------------------------------------------------------------------
    // getAvailableRegionIDs(String)
    //-----------------------------------------------------------------------
    public void test_getAvailableRegionIDs_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getAvailableRegionIDs("2008i").contains("Europe/London"), true);
    }

    public void test_getAvailableRegionIDs_String_badVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getAvailableRegionIDs("20").isEmpty(), true);
    }

    public void test_getAvailableRegionIDs_String_mock_v1() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableRegionIDs("v1");
        assertEquals(regions.size(), 1);
        assertEquals(regions.contains("RulesChange"), true);
        regions.clear();
        assertEquals(regions.size(), 0);
        List<String> regions2 = group.getAvailableRegionIDs("v1");
        assertNotSame(regions2, regions);
        assertEquals(regions2.size(), 1);
        assertEquals(regions2.contains("RulesChange"), true);
    }

    public void test_getAvailableRegionIDs_String_mock_v2() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableRegionIDs("v2");
        assertEquals(regions.size(), 2);
        assertEquals(regions.contains("RulesChange"), true);
        assertEquals(regions.contains("NewPlace"), true);
    }

    public void test_getAvailableRegionIDs_String_mock_any() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableRegionIDs("");
        assertEquals(regions.size(), 2);
        assertEquals(regions.contains("RulesChange"), true);
        assertEquals(regions.contains("NewPlace"), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getAvailableRegionIDs_String_nullVersion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getAvailableRegionIDs(null);
    }

    //-----------------------------------------------------------------------
    // getLatestVersionID()
    //-----------------------------------------------------------------------
    public void test_getLatestVersionID() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getLatestVersionID("Europe/London"), TestTimeZone.LATEST_TZDB);
    }

    public void test_getLatestVersionID_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(group.getLatestVersionID("RulesChange"), "v2");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getLatestVersionID_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionID("Europe/Lon");
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getLatestVersionID_unknownRegion_mock() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        group.getLatestVersionID("Europe/Lon");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getLatestVersionID_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getLatestVersionID(null);
    }

    //-----------------------------------------------------------------------
    // getAvailableVersionIDs(String)
    //-----------------------------------------------------------------------
    public void test_getAvailableVersionIDs_String() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getAvailableVersionIDs("Europe/London").contains("2008i"), true);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getAvailableVersionIDs_String_unknownRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        assertEquals(group.getAvailableVersionIDs("Europe/Lon").isEmpty(), true);
    }

    @Test(expectedExceptions=CalendricalException.class)
    public void test_getAvailableVersionIDs_String_mock_emptyRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        group.getAvailableVersionIDs("");
    }

    public void test_getAvailableVersionIDs_String_mock_RulesChange() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableVersionIDs("RulesChange");
        assertEquals(regions.size(), 2);
        assertEquals(regions.get(0), "v1");
        assertEquals(regions.get(1), "v2");
        regions.clear();
        assertEquals(regions.size(), 0);
        List<String> regions2 = group.getAvailableVersionIDs("RulesChange");
        assertNotSame(regions2, regions);
        assertEquals(regions2.size(), 2);
        assertEquals(regions2.get(0), "v1");
        assertEquals(regions2.get(1), "v2");
    }

    public void test_getAvailableVersionIDs_String_mock_NewPlace() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("MOCK");
        List<String> regions = group.getAvailableVersionIDs("NewPlace");
        assertEquals(regions.size(), 1);
        assertEquals(regions.contains("v2"), true);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_getAvailableVersionIDs_String_nullRegion() {
        ZoneRulesGroup group = ZoneRulesGroup.getGroup("TZDB");
        group.getAvailableVersionIDs(null);
    }

    //-----------------------------------------------------------------------
    static class MockProvider implements ZoneRulesDataProvider {
        public String getGroupID() {
            return "MOCK";
        }
        public Set<String> getIDs() {
            return new HashSet<String>(Arrays.asList("RulesChange#v1", "RulesChange#v2", "NewPlace#v2"));
        }
        public ZoneRules getZoneRules(String regionID, String versionID) {
            if (regionID.equals("NewPlace")) {
                return ZoneRules.fixed(ZoneOffset.of("+01:00"));
            } else {
                if (versionID.equals("v1")) {
                    return ZoneRules.fixed(ZoneOffset.of("+01:45"));
                } else {
                    return ZoneRules.fixed(ZoneOffset.of("+02:45"));
                }
            }
        }
    }
    static {
        ZoneRulesGroup.registerProvider(new MockProvider());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        ZoneRulesGroup a = ZoneRulesGroup.getGroup("TZDB");
        ZoneRulesGroup b = ZoneRulesGroup.getGroup("MOCK");
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
        assertEquals(b.equals(b), true);
    }

    public void test_equals_string_false() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").equals("TZDB"), false);
    }

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
    public void test_toString() {
        assertEquals(ZoneRulesGroup.getGroup("TZDB").toString(), "TZDB");
        assertEquals(ZoneRulesGroup.getGroup("MOCK").toString(), "MOCK");
    }

}
