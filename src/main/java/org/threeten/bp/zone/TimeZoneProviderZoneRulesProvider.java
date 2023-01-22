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

package org.threeten.bp.zone;

import walkingkooka.collect.map.Maps;
import walkingkooka.j2cl.java.util.locale.support.MultiLocaleValue;
import walkingkooka.j2cl.java.util.timezone.support.TimeZoneProviderReader;
import walkingkooka.j2cl.locale.LocaleAware;
import walkingkooka.j2cl.locale.TimeZoneCalendar;
import walkingkooka.j2cl.locale.TimeZoneDisplay;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

@LocaleAware
public final class TimeZoneProviderZoneRulesProvider extends ZoneRulesProvider {

    static TimeZoneProviderZoneRulesProvider load()  {
        final Map<String, ZoneRules> zoneIdToZoneRule = Maps.sorted();

        new TimeZoneProviderReader<StandardZoneRules>() {
            @Override
            public StandardZoneRules readZoneRules(final DataInput data) throws IOException {
                return StandardZoneRules.readExternal(data);
            }

            @Override
            public void record(final String id,
                               final int rawOffset,
                               final StandardZoneRules zoneRules,
                               final List<MultiLocaleValue<TimeZoneCalendar>> timeZoneCalendar,
                               final List<MultiLocaleValue<TimeZoneDisplay>> allDisplayLocales) {
                zoneIdToZoneRule.put(id, zoneRules);
            }
        }.read(walkingkooka.j2cl.java.util.timezone.generated.TimeZoneProvider.DATA);

        return new TimeZoneProviderZoneRulesProvider(zoneIdToZoneRule);
    }

    private TimeZoneProviderZoneRulesProvider(final Map<String, ZoneRules> zoneIdToZoneRule) {
        super();
        this.zoneIdToZoneRules = zoneIdToZoneRule;
    }

    @Override
    protected Set<String> provideZoneIds() {
        return this.zoneIdToZoneRules.keySet();
    }

    @Override
    protected ZoneRules provideRules(final String regionId, final boolean forCaching) {
        return this.zoneIdToZoneRules.get(regionId);
    }

    private final Map<String, ZoneRules> zoneIdToZoneRules;

    @Override
    protected NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
        throw new UnsupportedOperationException();
    }
}
