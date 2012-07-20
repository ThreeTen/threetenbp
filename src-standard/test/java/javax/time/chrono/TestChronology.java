/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.chrono;

import java.util.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.testng.Assert;

/**
 * Test Chrono class.
 */
@Test
public class TestChronology {

    //-----------------------------------------------------------------------
    // regular data factory for names and descriptions of available calendars
    //-----------------------------------------------------------------------
    @DataProvider(name = "Calendars")
    Object[][] data_of_calendars() {
        return new Object[][] {
                    {"Coptic", null, "Coptic calendar"},
                    {"Hijrah", null, "Hijrah calendar"},
                    {"ISO", null, "ISO calendar"},
                    {"Japanese", null, "Japanese calendar"},
                    {"Minguo", null, "Minguo Calendar"}, //          {"Islamic", null, "Islamic"},
                    {"ThaiBuddhist", null, "Thai Buddhist calendar"},
                //          {"Chinese", null, "Traditional Chinese calendar" },
                //          {"Ethioaa", "ethiopic-amete-alem", "Ethiopic calendar, Amete Alem (epoch approx. 5493 B.C.E)" },
                //          {"Ethiopic", null, "Ethiopic calendar, Amete Mihret (epoch approx, 8 C.E.)" },
                //          {"Hebrew", null, "Traditional Hebrew calendar" },
                };
    }

    @BeforeMethod
    public void setUp() {
        // Ensure each of the classes are initialized (until initialization is fixed)
        Chronology c;
        c = CopticChronology.INSTANCE;
        c = HijrahChronology.INSTANCE;
        c = ISOChronology.INSTANCE;
        c = JapaneseChronology.INSTANCE;
        c = MinguoChronology.INSTANCE;
        c = ThaiBuddhistChronology.INSTANCE;
    }

    @Test(dataProvider = "Calendars")
    public void test_required_calendars(String name, String alias, String description) {
        Chronology chrono = Chronology.ofName(name);
        Assert.assertNotNull(chrono, "Required calendar not found: " + name);
        Set<String> cals = Chronology.getAvailableNames();
        Assert.assertTrue(cals.contains(name), "Required calendar not found in set of available calendars");
    }

    @Test()
    public void test_calendar_list() {
        Set<String> names = Chronology.getAvailableNames();
        Assert.assertNotNull(names, "Required list of calendars must be non-null");
        for (String name : names) {
            Chronology chrono = Chronology.ofName(name);
            Assert.assertNotNull(chrono, "Required calendar not found: " + name);
        }
        Assert.assertEquals(names.size(), 6, "Required list of calendars too short");
    }


    /**
     * Compute the number of days from the Epoch and compute the date from the number of days.
     */
    @Test(dataProvider = "Calendars")
    public void test_epoch(String name, String alias, String description) {
        Chronology chrono = Chronology.ofName(name); // a chronology. In practice this is rarely hardcoded
        ChronoDate date1 = chrono.now();
        long epoch1 = date1.toEpochDay();
        ChronoDate date2 = chrono.dateFromEpochDay(epoch1);
        Assert.assertEquals(date1, date2, "Date from epoch day is not same date: " + date1 + " != " + date2);
        long epoch2 = date1.toEpochDay();
        Assert.assertEquals(epoch1, epoch2, "Epoch day not the same: " + epoch1 + " != " + epoch2);
    }

}