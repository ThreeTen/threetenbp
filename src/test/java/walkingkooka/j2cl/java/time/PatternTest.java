/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.j2cl.java.time;

import org.testng.annotations.Test;
import walkingkooka.ToStringBuilder;
import walkingkooka.text.CharSequences;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public final class PatternTest {

    @org.junit.jupiter.api.Test
    public void testFirstNonAlpha() {
        this.isZoneIdAndCheck("9AA", false);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha() {
        this.isZoneIdAndCheck("AA", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha2() {
        this.isZoneIdAndCheck("AZ", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha3() {
        this.isZoneIdAndCheck("Aa", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha4() {
        this.isZoneIdAndCheck("Az", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha5() {
        this.isZoneIdAndCheck("ZA", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha6() {
        this.isZoneIdAndCheck("ZZ", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha7() {
        this.isZoneIdAndCheck("aA", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha8() {
        this.isZoneIdAndCheck("aZ", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha9() {
        this.isZoneIdAndCheck("zA", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlpha10() {
        this.isZoneIdAndCheck("zZ", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaAlphaAlpha() {
        this.isZoneIdAndCheck("ABC", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaDigit() {
        this.isZoneIdAndCheck("A0", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaDigit2() {
        this.isZoneIdAndCheck("A9", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaTilde() {
        this.isZoneIdAndCheck("A~", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaSlash() {
        this.isZoneIdAndCheck("A/", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaDot() {
        this.isZoneIdAndCheck("A.", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaUnderscore() {
        this.isZoneIdAndCheck("A_", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaPlus() {
        this.isZoneIdAndCheck("A+", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaMinus() {
        this.isZoneIdAndCheck("A-", true);
    }

    @org.junit.jupiter.api.Test
    public void testLong() {
        this.isZoneIdAndCheck("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~/._+-", true);
    }

    @org.junit.jupiter.api.Test
    public void testAlphaInvalid() {
        this.isZoneIdAndCheck("AAAA ", false);
    }

    private static void isZoneIdAndCheck(final String zoneId,
                                         final boolean expected) {
        assertEquals(Pattern.isZoneId(zoneId),
                expected,
                "zoneId " + CharSequences.quoteAndEscape(zoneId));
    }
    
    // Pattern.periodParse..............................................................................................

    @Test
    public void testPeriodParseEmptyFails() {
        periodParseFails("");
    }

    @Test
    public void testPeriodParseSpacesFails() {
        periodParseFails("");
    }

    @Test
    public void testPeriodParsePlusSignFails() {
        periodParseFails("+");
    }

    @Test
    public void testPeriodParseMinusSignFails() {
        periodParseFails("-");
    }

    @Test
    public void testPeriodParseNotPFails() {
        periodParseFails("Q");
    }

    @Test
    private static void periodParseFails(final String text) {
        assertEquals(null,
                Pattern.periodParse(text),
                "periodParse " + CharSequences.quoteAndEscape(text));
    }

    // Pattern.durationParse............................................................................................

    @Test
    public void testDurationParseEmptyFails() {
        durationParseFails("");
    }

    @Test
    public void testDurationParseSpacesFails() {
        durationParseFails("");
    }

    @Test
    public void testDurationParsePlusSignFails() {
        durationParseFails("+");
    }

    @Test
    public void testDurationParseMinusSignFails() {
        durationParseFails("-");
    }

    @Test
    public void testDurationParseNotPFails() {
        durationParseFails("Q");
    }

    @Test
    public void testDurationParseMissingDSuffixFails() {
        durationParseFails("P5");
    }

    @Test
    public void testDurationParseMissingHSuffixFails() {
        durationParseFails("P5DT6");
    }

    @Test
    public void testDurationParseMissingMSuffixFails() {
        durationParseFails("P5DT6H7");
    }

    @Test
    public void testDurationParseMissingMSuffix2Fails() {
        durationParseFails("P5DT7");
    }

    @Test
    public void testDurationParseMissingSSuffixFails() {
        durationParseFails("P5DT6H7M8");
    }

    @Test
    public void testDurationParseMissingSSuffix2Fails() {
        durationParseFails("P5D8");
    }

    @Test
    public void testDurationParseMissingSSuffix3Fails() {
        durationParseFails("P5D8.");
    }

    @Test
    public void testDurationParseNumberAfterSecondsFails() {
        durationParseFails("P5D8S9");
    }

    @Test
    public void testDurationParseSecondsTooLongFails() {
        durationParseFails("P1.01234567890S");
    }

    @Test
    private static void durationParseFails(final String text) {
        assertEquals(null,
                Pattern.durationParse(text),
                "durationParse " + CharSequences.quoteAndEscape(text));
    }

    @Test
    public void testDurationParseMinusSignDays() {
        durationParseAndCheck("P-5D",
                null,
                "-5", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParsePlusSignDays() {
        durationParseAndCheck("P+5D",
                null,
                "+5", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDays() {
        durationParseAndCheck("P5D",
                null,
                "5", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDays2() {
        durationParseAndCheck("P56D",
                null,
                "56", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDaysNegative() {
        durationParseAndCheck("P-5D",
                null,
                "-5", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDaysNegative2() {
        durationParseAndCheck("P-56D",
                null,
                "-56", // D
                null, // T
                null, // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseHours() {
        durationParseAndCheck("P6H",
                null,
                null, // D
                null, // T
                "6", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseHours2() {
        durationParseAndCheck("P67H",
                null,
                null, // D
                null, // T
                "67", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseHours3() {
        durationParseAndCheck("P-6H",
                null,
                null, // D
                null, // T
                "-6", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseHours4() {
        durationParseAndCheck("P-67H",
                null,
                null, // D
                null, // T
                "-67", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseMinutes() {
        durationParseAndCheck("P6M",
                null,
                null, // D
                null, // T
                null, // H
                "6", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseMinutes2() {
        durationParseAndCheck("P67M",
                null,
                null, // D
                null, // T
                null, // H
                "67", // M
                null, // S
                null); // F
    }


    @Test
    public void testDurationParseMinutes3() {
        durationParseAndCheck("P-6M",
                null,
                null, // D
                null, // T
                null, // H
                "-6", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseMinutes4() {
        durationParseAndCheck("P-67M",
                null,
                null, // D
                null, // T
                null, // H
                "-67", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseSeconds() {
        durationParseAndCheck("P6S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "6", // S
                null); // F
    }

    @Test
    public void testDurationParseSeconds2() {
        durationParseAndCheck("P67S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "67", // S
                null); // F
    }


    @Test
    public void testDurationParseSeconds3() {
        durationParseAndCheck("P-6S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "-6", // S
                null); // F
    }

    @Test
    public void testDurationParseSeconds4() {
        durationParseAndCheck("P-67S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "-67", // S
                null); // F
    }

    @Test
    public void testDurationParseSecondsFractions() {
        durationParseAndCheck("P6.7S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "6", // S
                "7"); // F
    }

    @Test
    public void testDurationParseSecondsFractions2() {
        durationParseAndCheck("P67.8S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "67", // S
                "8"); // F
    }

    @Test
    public void testDurationParseSecondsFractions3() {
        durationParseAndCheck("P67.89S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "67", // S
                "89"); // F
    }

    @Test
    public void testDurationParseSecondsFractions4() {
        durationParseAndCheck("P67.S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "67", // S
                null); // F
    }

    @Test
    public void testDurationParseSecondsFractions5() {
        durationParseAndCheck("P67,8S",
                null,
                null, // D
                null, // T
                null, // H
                null, // M
                "67", // S
                "8"); // F
    }

    @Test
    public void testDurationParseDaysHours() {
        durationParseAndCheck("P1DT2H",
                null,
                "1", // D
                "T", // T
                "2", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDaysMinutes() {
        durationParseAndCheck("P1DT2M",
                null,
                "1", // D
                "T", // T
                null, // H
                "2", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseDaysSeconds() {
        durationParseAndCheck("P1DT3S",
                null,
                "1", // D
                "T", // T
                null, // H
                null, // M
                "3", // S
                null); // F
    }

    @Test
    public void testDurationParseDaysHoursMinutesSeconds() {
        durationParseAndCheck("P1DT2H3M4S",
                null,
                "1", // D
                "T", // T
                "2", // H
                "3", // M
                "4", // S
                null); // F
    }

    @Test
    public void testDurationParseDaysHoursMinutesSecondsMillis() {
        durationParseAndCheck("P1DT2H3M4.5S",
                null,
                "1", // D
                "T", // T
                "2", // H
                "3", // M
                "4", // S
                "5"); // F
    }

    @Test
    public void testDurationParseTHours() {
        durationParseAndCheck("PT6H",
                null,
                null, // D
                "T", // T
                "6", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTHours2() {
        durationParseAndCheck("Pt6H",
                null,
                null, // D
                "t", // T
                "6", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTMinusHours() {
        durationParseAndCheck("PT-6H",
                null,
                null, // D
                "T", // T
                "-6", // H
                null, // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTMinutes() {
        durationParseAndCheck("PT6M",
                null,
                null, // D
                "T", // T
                null, // H
                "6", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTMinutes2() {
        durationParseAndCheck("PT67M",
                null,
                null, // D
                "T", // T
                null, // H
                "67", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTMinusMinutes() {
        durationParseAndCheck("PT-6M",
                null,
                null, // D
                "T", // T
                null, // H
                "-6", // M
                null, // S
                null); // F
    }

    @Test
    public void testDurationParseTSeconds() {
        durationParseAndCheck("PT6S",
                null,
                null, // D
                "T", // T
                null, // H
                null, // M
                "6", // S
                null); // F
    }

    @Test
    public void testDurationParseTSecond2s() {
        durationParseAndCheck("PT67S",
                null,
                null, // D
                "T", // T
                null, // H
                null, // M
                "67", // S
                null); // F
    }

    @Test
    public void testDurationParseTMinusSeconds() {
        durationParseAndCheck("PT-6S",
                null,
                null, // D
                "T", // T
                null, // H
                null, // M
                "-6", // S
                null); // F
    }

    @Test
    private static void durationParseAndCheck(final String text,
                                              final String sign,
                                              final String days,
                                              final String t,
                                              final String hours,
                                              final String minutes,
                                              final String seconds,
                                              final String fraction) {
        final String[] groups = Pattern.durationParse(text);
        assertNotNull(groups,
                "durationParse " + CharSequences.quoteAndEscape(text));

        final String actual = ToStringBuilder.empty()
                .label("sign").value(groups[1])
                .label("days").value(groups[2])
                .label("t").value(groups[3])
                .label("hours").value(groups[4])
                .label("minutes").value(groups[5])
                .label("seconds").value(groups[6])
                .label("fraction").value(groups[7])
                .build();

        final String expected = ToStringBuilder.empty()
                .label("sign").value(sign)
                .label("days").value(days)
                .label("t").value(t)
                .label("hours").value(hours)
                .label("minutes").value(minutes)
                .label("seconds").value(seconds)
                .label("fraction").value(fraction)
                .build();

        assertEquals(actual,
                expected,
                "durationParse " + CharSequences.quoteAndEscape(text));
    }
}
