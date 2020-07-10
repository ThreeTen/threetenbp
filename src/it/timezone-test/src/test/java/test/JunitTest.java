/*
 * Copyright © 2020 Miroslav Pokorny
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;
import walkingkooka.j2cl.locale.Calendar;
import walkingkooka.j2cl.locale.GregorianCalendar;
import walkingkooka.j2cl.locale.HasTimeZoneCalendar;
import walkingkooka.j2cl.locale.TimeZoneCalendar;
import walkingkooka.text.CharSequences;

import java.util.Locale;
import java.util.TimeZone;

@J2clTestInput(JunitTest.class)
public class JunitTest {

    @Test
    public void testTimeZoneDefault() {
        final String hobart = "Australia/Hobart";
        Assert.assertEquals(TimeZone.getTimeZone(hobart), TimeZone.getDefault());
    }

    @Test
    public void testRawOffsetSydney() {
        this.getRawOffsetAndCheck("Australia/Sydney", 10);
    }

    @Test
    public void testRawOffsetPerth() {
        this.getRawOffsetAndCheck("Australia/Perth", 8);
    }

    @Test
    public void testRawOffsetMadrid() {
        this.getRawOffsetAndCheck("Europe/Madrid", 1);
    }

    private void getRawOffsetAndCheck(final String id, final int offset) {
        Assert.assertEquals("TimeZone " + CharSequences.quote(id),
                offset * 60 * 60 * 1000,
                TimeZone.getTimeZone(id).getRawOffset());
    }

    @Test
    public void testTimeZoneCalendar() {
        final Object timeZone = TimeZone.getTimeZone("Australia/Sydney");
        final HasTimeZoneCalendar has = (HasTimeZoneCalendar) timeZone;
        Assert.assertEquals(TimeZoneCalendar.with(2, 1),
                has.timeZoneCalendar(Locale.forLanguageTag("EU-AU")));
    }

    // getOffset........................................................................................................

    @Test
    public void testGetOffsetAustraliaSydney202005181432() {
        this.getOffsetAndCheck("Australia/Sydney",
                GregorianCalendar.AD,
                2020,
                Calendar.MAY,
                18,
                Calendar.MONDAY, // day of Week guessed
                0,
                10 * 60 * 60 * 1000);
    }

    @Test
    public void testGetOffsetEuropeParis202005181432() {
        this.getOffsetAndCheck("Europe/Paris",
                GregorianCalendar.AD,
                2020,
                Calendar.MAY,
                18,
                Calendar.MONDAY, // day of Week guessed
                0,
                2 * 60 * 60 * 1000);
    }

    @Test
    public void testGetOffsetEuropeLondon202005181432() {
        this.getOffsetAndCheck("Europe/London",
                GregorianCalendar.AD,
                2020,
                Calendar.MAY,
                18,
                Calendar.MONDAY, // day of Week guessed
                0,
                1 * 60 * 60 * 1000);
    }

    private void getOffsetAndCheck(final String timeZone,
                                   final int era,
                                   final int year,
                                   final int month,
                                   final int day,
                                   final int dayOfWeek,
                                   final int time,
                                   final long offset) {
        Assert.assertEquals("TimeZone " + CharSequences.quote(timeZone) + " getOffset(era=" + era + ", year=" + year + ", month=" + month + ", day=" + day + ", dayOfWeek=" + dayOfWeek + ", time=" + time + ")",
                offset,
                TimeZone.getTimeZone(timeZone)
                        .getOffset(era, year, month, day, dayOfWeek, time));
    }

    // getDisplay.......................................................................................................

    @Test
    public void testGetDisplayNameAustraliaSydneyEnAULong() {
        this.getDisplayNameAndCheck("Australia/Sydney",
                "EN-AU",
                false,
                TimeZone.LONG,
                "Australian Eastern Standard Time");
    }

    @Test
    public void testGetDisplayNameAustraliaSydneyEnAUShort() {
        this.getDisplayNameAndCheck("Australia/Sydney",
                "EN-AU",
                false,
                TimeZone.SHORT,
                "AEST");
    }

    @Test
    public void testGetDisplayNameAustraliaAdelaideEnAULong() {
        this.getDisplayNameAndCheck("Australia/Adelaide",
                "DE-DE",
                false,
                TimeZone.LONG,
                "Zentralaustralische Normalzeit");
    }

    private void getDisplayNameAndCheck(final String timeZoneId,
                                        final String locale,
                                        final boolean daylight,
                                        final int style,
                                        final String expected) {
        Assert.assertEquals(expected, TimeZone.getTimeZone(timeZoneId).getDisplayName(daylight, style, Locale.forLanguageTag(locale)));
    }
}
