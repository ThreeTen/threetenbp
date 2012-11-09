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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.time.DateTimeException;
import javax.time.DayOfWeek;
import javax.time.Month;
import javax.time.ZoneId;
import javax.time.ZoneOffset;

import org.testng.annotations.Test;

/**
 * Test ZoneRulesProvider.
 */
@Test
public class TCKZoneRulesProvider {

    //-----------------------------------------------------------------------
    // getProvider()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getProvider() {
        assertEquals(ZoneRulesProvider.getProvider("TZDB").getGroupId(), "TZDB");
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_getProvider_badGroup() {
        ZoneRulesProvider.getProvider("NOTREAL");
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_getProvider_badGroupBlank() {
        ZoneRulesProvider.getProvider("");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getProvider_null() {
        ZoneRulesProvider.getProvider(null);
    }

    //-----------------------------------------------------------------------
    // getAvailableGroupIds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAvailableGroupIds() {
        Set<String> groups = ZoneRulesProvider.getAvailableGroupIds();
        assertEquals(groups.contains("TZDB"), true);
        groups.clear();
        assertEquals(groups.size(), 0);
        Set<String> groups2 = ZoneRulesProvider.getAvailableGroupIds();
        assertEquals(groups2.contains("TZDB"), true);
    }

// TODO
//    //-----------------------------------------------------------------------
//    // getParsableIDs()
//    //-----------------------------------------------------------------------
//    @Test(groups={"tck"})
//    public void test_getParsableIDs() {
//        Set<String> parsableIDs = ZoneRulesProvider.getParsableIDs();
//        assertEquals(parsableIDs.contains("Europe/London"), true);
//        assertEquals(parsableIDs.contains("TZDB:Europe/London"), true);
//    }
//
//    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
//    public void test_getParsableIDs_unmodifiableClear() {
//        ZoneRulesProvider.getParsableIDs().clear();
//    }
//
//    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
//    public void test_getParsableIDs_unmodifiableRemove() {
//        ZoneRulesProvider.getParsableIDs().remove("Europe/London");
//    }
//
//    @Test(expectedExceptions=UnsupportedOperationException.class, groups={"tck"})
//    public void test_getParsableIDs_unmodifiableAdd() {
//        ZoneRulesProvider.getParsableIDs().add("Europe/Lon");
//    }

    //-----------------------------------------------------------------------
    // registerProvider()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_registerProvider() {
        Set<String> pre = ZoneRulesProvider.getAvailableGroupIds();
        assertEquals(pre.contains("TEMPMOCK.-_"), false);
        ZoneRulesProvider.registerProvider(new MockTempProvider());
        assertEquals(pre.contains("TEMPMOCK.-_"), false);
        Set<String> post = ZoneRulesProvider.getAvailableGroupIds();
        assertEquals(post.contains("TEMPMOCK.-_"), true);

        assertEquals(ZoneRulesProvider.getProvider("TEMPMOCK.-_").getGroupId(), "TEMPMOCK.-_");
        assertEquals(ZoneRulesProvider.getProvider("TEMPMOCK.-_").getRules("World%@~.-_", "1.-_").isFixedOffset(), true);
    }

    static class MockTempProvider extends ZoneRulesProvider {
        public MockTempProvider() {
            super("TEMPMOCK.-_");
        }
        @Override
        public Set<String> getAvailableRegionIds() {
            return new HashSet<String>(Arrays.asList("World%@~.-_"));
        }
        @Override
        public SortedSet<String> getAvailableVersionIds() {
            return new TreeSet<String>(Arrays.asList("1.-_"));
        }
        @Override
        public boolean isValid(String regionId, String versionId) {
            return "World%@~.-_".equals(regionId) && (versionId == null || "1.-_".equals(versionId));
        }
        @Override
        public ZoneRules getRules(String regionId, String versionId) {
            if (isValid(regionId, versionId)) {
                return ZoneId.of(ZoneOffset.of("+01:45")).getRules();
            }
            throw new DateTimeException("Invalid");
        }
        @Override
        public boolean refresh(ClassLoader classLoader) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_registerProvider_invalidGroupId() {
        ZoneRulesProvider.registerProvider(new MockTempProviderInvalidGroupId());
    }

    static class MockTempProviderInvalidGroupId extends ZoneRulesProvider {
        public MockTempProviderInvalidGroupId() {
            super("TEMPMOCK%");
        }
        @Override
        public Set<String> getAvailableRegionIds() {
            throw new UnsupportedOperationException();
        }
        @Override
        public SortedSet<String> getAvailableVersionIds() {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isValid(String regionId, String versionId) {
            throw new UnsupportedOperationException();
        }
        @Override
        public ZoneRules getRules(String regionId, String versionId) {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean refresh(ClassLoader classLoader) {
            return false;
        }
    }

    //-----------------------------------------------------------------------
    // isValidRules()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isValidRules() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        assertEquals(group.isValid("Europe/London", "2008i"), true);
        assertEquals(group.isValid("Europe/Lon", "2008i"), false);
        assertEquals(group.isValid("Europe/London", "20"), false);
        assertEquals(group.isValid("Europe/London", null), true);
        assertEquals(group.isValid(null, "2008i"), false);
    }

    //-----------------------------------------------------------------------
    // getRules()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getRules() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        ZoneRules rules = group.getRules("Europe/London", "2008i");
        assertEquals(rules.getTransitionRules().size(), 2);
        assertEquals(rules.getTransitionRules().get(0).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(0).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(0).getMonth(), Month.MARCH);
        assertEquals(rules.getTransitionRules().get(1).getDayOfWeek(), DayOfWeek.SUNDAY);
        assertEquals(rules.getTransitionRules().get(1).getDayOfMonthIndicator(), 25);
        assertEquals(rules.getTransitionRules().get(1).getMonth(), Month.OCTOBER);
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_getRules_unknownRegion() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        group.getRules("Europe/Lon", "2008i");
    }

    @Test(expectedExceptions=DateTimeException.class, groups={"tck"})
    public void test_getRules_unknownVersion() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        group.getRules("Europe/London", "20");
    }

    @Test(expectedExceptions=NullPointerException.class, groups={"tck"})
    public void test_getRules_nullRegion() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        group.getRules(null, "2008i");
    }

    @Test(groups={"tck"})
    public void test_getRules_nullVersionLatest() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        SortedSet<String> versionIds = group.getAvailableVersionIds();
        String latest = versionIds.last();
        assertEquals(group.getRules("Europe/London", null), group.getRules("Europe/London", latest));
    }

    //-----------------------------------------------------------------------
    // getAvailableVersionIds()
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_getAvailableVersionIds_String() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        SortedSet<String> versions = group.getAvailableVersionIds();
        assertEquals(versions.contains("2008i"), true);
    }

    @Test(groups={"tck"})
    public void test_getAvailableVersionIds_String_mock() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("MOCK");
        SortedSet<String> versions = group.getAvailableVersionIds();
        assertEquals(versions.size(), 2);
        assertEquals(versions.contains("v1"), true);
        assertEquals(versions.contains("v2"), true);
    }

    @Test(groups={"tck"})
    public void test_getAvailableVersionIds_String_modifiable() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("MOCK");
        SortedSet<String> versionsPre = group.getAvailableVersionIds();
        assertEquals(versionsPre.size(), 2);
        versionsPre.clear();
        SortedSet<String> versionsPost = group.getAvailableVersionIds();
        assertEquals(versionsPost.size(), 2);
    }

    //-----------------------------------------------------------------------
    // getAvailableRegionIds()
    //-----------------------------------------------------------------------
    public void test_getAvailableRegionIds_String() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        assertEquals(group.getAvailableRegionIds().contains("Europe/London"), true);
    }

    public void test_getAvailableRegionIds_String_mock() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("MOCK");
        Set<String> regions = group.getAvailableRegionIds();
        assertEquals(regions.size(), 2);
        assertEquals(regions.contains("RulesChange"), true);
        assertEquals(regions.contains("NewPlace"), true);
        regions.clear();
        assertEquals(regions.size(), 0);
        Set<String> regions2 = group.getAvailableRegionIds();
        assertNotSame(regions2, regions);
        assertEquals(regions2.size(), 2);
        assertEquals(regions2.contains("RulesChange"), true);
        assertEquals(regions2.contains("NewPlace"), true);
    }

    //-----------------------------------------------------------------------
    // isValid(String,String)
    //-----------------------------------------------------------------------
    @Test(groups={"tck"})
    public void test_isValid_String() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        assertEquals(group.isValid("Europe/London", null), true);
        assertEquals(group.isValid("Rubbish", null), false);
    }

    @Test(groups={"tck"})
    public void test_isValid_String_mock() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("MOCK");
        assertEquals(group.isValid("RulesChange", "v1"), true);
        assertEquals(group.isValid("RulesChange", "v2"), true);
        assertEquals(group.isValid("NewPlace", "v1"), false);
        assertEquals(group.isValid("NewPlace", "v2"), true);
        assertEquals(group.isValid("Rubbish", "v1"), false);
    }

    public void test_isValid_String_nullRegion() {
        ZoneRulesProvider group = ZoneRulesProvider.getProvider("TZDB");
        assertEquals(group.isValid(null, null), false);
    }

    //-----------------------------------------------------------------------
    static class MockProvider extends ZoneRulesProvider {
        public MockProvider() {
            super("MOCK");
        }
        @Override
        public Set<String> getAvailableRegionIds() {
            return new HashSet<String>(Arrays.asList("NewPlace", "RulesChange"));
        }
        @Override
        public SortedSet<String> getAvailableVersionIds() {
            return new TreeSet<String>(Arrays.asList("v1", "v2"));
        }
        @Override
        public boolean isValid(String regionId, String versionId) {
            return ("RulesChange".equals(regionId) && ("v1".equals(versionId) || "v2".equals(versionId) || versionId == null)) ||
                        ("NewPlace".equals(regionId) && ("v2".equals(versionId) || versionId == null));
        }
        @Override
        public ZoneRules getRules(String regionId, String versionId) {
            if (isValid(regionId, versionId)) {
                if (regionId.equals("NewPlace")) {
                    return ZoneId.of(ZoneOffset.of("+01:00")).getRules();
                } else {
                    return ZoneId.of(ZoneOffset.of("+02:45")).getRules();
                }
            }
            throw new DateTimeException("Invalid");
        }
        @Override
        public boolean refresh(ClassLoader classLoader) {
            return false;
        }
    }
    static {
        ZoneRulesProvider.registerProvider(new MockProvider());
    }

}
