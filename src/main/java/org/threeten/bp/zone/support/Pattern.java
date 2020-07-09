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

package org.threeten.bp.zone.support;

public final class Pattern {

    /**
     * Replaces the j2cl unsupported {@link java.util.regex.Pattern} used to validate the {@link String zoneId}
     */
    // Pattern.compile("[A-Za-z][A-Za-z0-9~/._+-]+");
    public static boolean isZoneId(final String zoneId) {
        boolean valid = isAlpha(zoneId.charAt(0));
        final int length = zoneId.length();
        int i = 1;

        while (valid && i < length) {
            final char c = zoneId.charAt(i);
            valid &= isAlpha(c) || "0123456789~/._+-".indexOf(c) != -1;
            i++;
        }

        return valid;
    }

    private static boolean isAlpha(final char c) {
        return (c >= 'A' && c <= 'Z') ||
                (c >= 'a' && c <= 'z');
    }

    private Pattern() {
        throw new UnsupportedOperationException();
    }
}
