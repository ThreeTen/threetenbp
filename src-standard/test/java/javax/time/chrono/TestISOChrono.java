/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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

import static javax.time.chrono.ISOEra.ISO_BCE;
import static javax.time.chrono.ISOEra.ISO_CE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.time.CalendricalException;
import javax.time.LocalDate;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

// TODO: use more data driven tests
// TDOO: merge with other test!
public class TestISOChrono {

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void instanceNotNull() {
        assertNotNull(ISOChronology.INSTANCE);
    }
    
    @Test(groups="tck")
    public void isoChronoIsRegistered() {
        assertTrue(Chronology.getAvailableNames().contains(ISOChronology.INSTANCE.getName()));
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void erasInstantiated() {
        assertEquals(ISOChronology.INSTANCE.createEra(0), ISO_BCE);
        assertEquals(ISOChronology.INSTANCE.createEra(1), ISO_CE);
    }
    
    //-----------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void dateCreated_null() {
        ISOChronology.INSTANCE.date(null);
    }
    
    @Test(groups="tck")
    public void dateCreated_fromLocal() {
        LocalDate localDate = LocalDate.now();
        ChronoDate chronoForLocal = ISOChronology.INSTANCE.date(localDate);
        assertEquals(chronoForLocal.getProlepticYear(), localDate.getYear());
        assertEquals(chronoForLocal.getMonth(), localDate.getMonthValue());
        assertEquals(chronoForLocal.getDayOfMonth(), localDate.getDayOfMonth());
        assertEquals(chronoForLocal.getDayOfWeek(), localDate.getDayOfWeek());
        assertEquals(chronoForLocal.getDayOfYear(), localDate.getDayOfYear());
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void dateCreated_fromValues() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int dayOfMonth = localDate.getDayOfMonth();
        ChronoDate chronoDate = ISOChronology.INSTANCE.date(year, month, dayOfMonth);
        assertEquals(chronoDate.getProlepticYear(), year);
        assertEquals(chronoDate.getMonth(), month);
        assertEquals(chronoDate.getDayOfMonth(), dayOfMonth);
    }

    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void dateCreated_withEra() {
        int yearOfEra = 5;
        int month = 5;
        int dayOfMonth = 5;
        ChronoDate chronoDate = ISOChronology.INSTANCE.date(ISO_BCE, yearOfEra, month, dayOfMonth);
        assertEquals(chronoDate.getProlepticYear(), 1 + (-1 * yearOfEra));
        assertEquals(chronoDate.getEra(), ISO_BCE);
        assertEquals(chronoDate.getYearOfEra(), yearOfEra);
        assertEquals(chronoDate.getMonth(), month);
        assertEquals(chronoDate.getDayOfMonth(), dayOfMonth);
    }

    @Test(expectedExceptions=NullPointerException.class, groups="tck")
    public void dateCreated_withEra_null() {
        ISOChronology.INSTANCE.date(null, 1, 1, 1);
    }

    @Test(expectedExceptions=CalendricalException.class, groups="tck")
    public void dateCreated_withWrongEra() {        
        ISOChronology.INSTANCE.date(CopticEra.AM, 1, 1, 1);
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testEquals() {        
        assertTrue(ISOChronology.INSTANCE.equals(ISOChronology.INSTANCE));
        assertFalse(ISOChronology.INSTANCE.equals(CopticChronology.INSTANCE));
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testName() {
        assertEquals(ISOChronology.INSTANCE.getName(), "ISO");
    }
    
    //-----------------------------------------------------------------
    @DataProvider(name="leapYears")
    Object[][] leapYearInformation() {
        return new Object[][] {
                {2000, true},
                {1996, true},
                {1600, true},
                
                {1900, false},
                {2100, false},
        };
    }
    
    @Test(dataProvider="leapYears", groups="tck")
    public void testIsLeapYear(int year, boolean isLeapYear) {        
        assertEquals(ISOChronology.INSTANCE.isLeapYear(year), isLeapYear);
    }
    
    //-----------------------------------------------------------------
    @Test(groups="tck")
    public void testNow() {
        assertEquals(ISOChronology.INSTANCE.now().toLocalDate(), LocalDate.now());
    }
    
}

