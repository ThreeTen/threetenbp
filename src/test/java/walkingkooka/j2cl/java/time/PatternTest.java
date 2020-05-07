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
import walkingkooka.text.CharSequences;

import static org.testng.Assert.assertEquals;

@Test
public class PatternTest {

    @Test
    public void testFirstNonAlpha() {
        this.isZoneIdAndCheck("9AA", false);
    }

    @Test
    public void testAlphaAlpha() {
        this.isZoneIdAndCheck("AA", true);
    }

    @Test
    public void testAlphaAlpha2() {
        this.isZoneIdAndCheck("AZ", true);
    }

    @Test
    public void testAlphaAlpha3() {
        this.isZoneIdAndCheck("Aa", true);
    }

    @Test
    public void testAlphaAlpha4() {
        this.isZoneIdAndCheck("Az", true);
    }

    @Test
    public void testAlphaAlpha5() {
        this.isZoneIdAndCheck("ZA", true);
    }

    @Test
    public void testAlphaAlpha6() {
        this.isZoneIdAndCheck("ZZ", true);
    }

    @Test
    public void testAlphaAlpha7() {
        this.isZoneIdAndCheck("aA", true);
    }

    @Test
    public void testAlphaAlpha8() {
        this.isZoneIdAndCheck("aZ", true);
    }

    @Test
    public void testAlphaAlpha9() {
        this.isZoneIdAndCheck("zA", true);
    }

    @Test
    public void testAlphaAlpha10() {
        this.isZoneIdAndCheck("zZ", true);
    }

    @Test
    public void testAlphaAlphaAlpha() {
        this.isZoneIdAndCheck("ABC", true);
    }

    @Test
    public void testAlphaDigit() {
        this.isZoneIdAndCheck("A0", true);
    }

    @Test
    public void testAlphaDigit2() {
        this.isZoneIdAndCheck("A9", true);
    }

    @Test
    public void testAlphaTilde() {
        this.isZoneIdAndCheck("A~", true);
    }

    @Test
    public void testAlphaSlash() {
        this.isZoneIdAndCheck("A/", true);
    }

    @Test
    public void testAlphaDot() {
        this.isZoneIdAndCheck("A.", true);
    }

    @Test
    public void testAlphaUnderscore() {
        this.isZoneIdAndCheck("A_", true);
    }

    @Test
    public void testAlphaPlus() {
        this.isZoneIdAndCheck("A+", true);
    }

    @Test
    public void testAlphaMinus() {
        this.isZoneIdAndCheck("A-", true);
    }

    @Test
    public void testLong() {
        this.isZoneIdAndCheck("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~/._+-", true);
    }

    @Test
    public void testAlphaInvalid() {
        this.isZoneIdAndCheck("AAAA ", false);
    }

    private static void isZoneIdAndCheck(final String zoneId,
                                         final boolean expected) {
        assertEquals(Pattern.isZoneId(zoneId),
                expected,
                "zoneId " + CharSequences.quoteAndEscape(zoneId));
    }
}
