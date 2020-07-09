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
package walkingkooka.j2cl.java.util.timezone.zonerulesreader;

import org.threeten.bp.zone.StandardZoneRules;
import walkingkooka.j2cl.locale.TimeZoneOffsetAndDaylightSavings;
import walkingkooka.reflect.PublicStaticHelper;

import java.io.DataInput;
import java.io.IOException;

/**
 * The default which provides time zone data. This will be consumed by java.util.TimeZone.
 */
public final class ZoneRulesReader implements PublicStaticHelper {

    public static TimeZoneOffsetAndDaylightSavings readZoneRules(final DataInput data) throws IOException {
        return StandardZoneRules.readExternal(data);
    }

    /**
     * Stop creation
     */
    private ZoneRulesReader() {
        throw new UnsupportedOperationException();
    }
}
