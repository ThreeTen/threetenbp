/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.calendrical.DateTimeAccessor;
import javax.time.calendrical.DateTimeAccessor.Query;
import javax.time.format.TextStyle;
import javax.time.zone.ZoneRules;
import javax.time.zone.ZoneRulesProvider;

/**
 * A time-zone ID representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * Time-zones are geographical regions where the same rules for time apply.
 * The rules are defined by governments and change frequently.
 * This class provides an identifier that locates a single set of rules.
 * <p>
 * The rule data is split across two main classes:
 * <p><ul>
 * <li>{@code ZoneId}, which only represents the identifier of the rule-set
 * <li>{@link ZoneRules}, which defines the set of rules themselves
 * </ul><p>
 * <p>
 * One benefit of this separation occurs in serialization. Storing this class will
 * only store the reference to the zone, whereas serializing {@code ZoneRules} will
 * store the entire set of rules.
 * <p>
 * Similarly, comparing two {@code ZoneId} instances will only compare the identifier,
 * whereas comparing two {@code ZoneRules} instances will actually check to see if the
 * rules represent the same set of data.
 * <p>
 * After deserialization, or by using the special factory {@link #ofUnchecked}, it is
 * possible for the {@code ZoneId} to represent an identifier that has no available rules.
 * This approach allows the application to continue and some operations to be performed.
 * It also allows an application to dynamically download missing rules from a central
 * server, if desired.
 *
 * <h4>Time-zone identifiers</h4>
 * A unique time-zone identifier is formed from two parts, the group and the region.
 * They are combined using a colon to make a full identifier - <code>{groupID}:{regionID}</code>.
 * <p>
 * The group represents the source of time-zone information.
 * This is necessary as multiple companies and organizations provide time-zone data.
 * Two groups are provided as standard, 'TZDB' and 'UTC'.
 * <p>
 * The 'TZDB' group represents the main public time-zone database.
 * It typically uses region identifiers of the form <code>{area}/{city}</code>,
 * such as {@code Europe/London}. The 'TZDB' group is considered to be the
 * default such that normally only the region ID is seen in identifiers.
 * <p>
 * The 'UTC' group represents fixed offsets from UTC/Greenwich.
 * That concept is best represented using {@link ZoneOffset} directly, but a fixed
 * offset is also a valid {@code ZoneId}, hence the 'UTC' group.
 * The region identifier for the 'UTC' group is the {@code ZoneOffset} identifier.
 *
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public abstract class ZoneId {

    /**
     * The time-zone offset for 'UTC'.
     */
    public static final ZoneOffset UTC = ZoneOffset.UTC;

    /**
     * A map of zone overrides to enable the older US time-zone names to be used.
     * <p>
     * This maps as follows:
     * <p><ul>
     * <li>EST - America/Indianapolis</li>
     * <li>MST - America/Phoenix</li>
     * <li>HST - Pacific/Honolulu</li>
     * <li>ACT - Australia/Darwin</li>
     * <li>AET - Australia/Sydney</li>
     * <li>AGT - America/Argentina/Buenos_Aires</li>
     * <li>ART - Africa/Cairo</li>
     * <li>AST - America/Anchorage</li>
     * <li>BET - America/Sao_Paulo</li>
     * <li>BST - Asia/Dhaka</li>
     * <li>CAT - Africa/Harare</li>
     * <li>CNT - America/St_Johns</li>
     * <li>CST - America/Chicago</li>
     * <li>CTT - Asia/Shanghai</li>
     * <li>EAT - Africa/Addis_Ababa</li>
     * <li>ECT - Europe/Paris</li>
     * <li>IET - America/Indiana/Indianapolis</li>
     * <li>IST - Asia/Kolkata</li>
     * <li>JST - Asia/Tokyo</li>
     * <li>MIT - Pacific/Apia</li>
     * <li>NET - Asia/Yerevan</li>
     * <li>NST - Pacific/Auckland</li>
     * <li>PLT - Asia/Karachi</li>
     * <li>PNT - America/Phoenix</li>
     * <li>PRT - America/Puerto_Rico</li>
     * <li>PST - America/Los_Angeles</li>
     * <li>SST - Pacific/Guadalcanal</li>
     * <li>VST - Asia/Ho_Chi_Minh</li>
     * </ul><p>
     * The map is unmodifiable.
     */
    public static final Map<String, String> OLD_IDS_PRE_2005;
    /**
     * A map of zone overrides to enable the older US time-zone names to be used.
     * <p>
     * This maps as follows:
     * <p><ul>
     * <li>EST - -05:00</li>
     * <li>HST - -10:00</li>
     * <li>MST - -07:00</li>
     * <li>ACT - Australia/Darwin</li>
     * <li>AET - Australia/Sydney</li>
     * <li>AGT - America/Argentina/Buenos_Aires</li>
     * <li>ART - Africa/Cairo</li>
     * <li>AST - America/Anchorage</li>
     * <li>BET - America/Sao_Paulo</li>
     * <li>BST - Asia/Dhaka</li>
     * <li>CAT - Africa/Harare</li>
     * <li>CNT - America/St_Johns</li>
     * <li>CST - America/Chicago</li>
     * <li>CTT - Asia/Shanghai</li>
     * <li>EAT - Africa/Addis_Ababa</li>
     * <li>ECT - Europe/Paris</li>
     * <li>IET - America/Indiana/Indianapolis</li>
     * <li>IST - Asia/Kolkata</li>
     * <li>JST - Asia/Tokyo</li>
     * <li>MIT - Pacific/Apia</li>
     * <li>NET - Asia/Yerevan</li>
     * <li>NST - Pacific/Auckland</li>
     * <li>PLT - Asia/Karachi</li>
     * <li>PNT - America/Phoenix</li>
     * <li>PRT - America/Puerto_Rico</li>
     * <li>PST - America/Los_Angeles</li>
     * <li>SST - Pacific/Guadalcanal</li>
     * <li>VST - Asia/Ho_Chi_Minh</li>
     * </ul><p>
     * The map is unmodifiable.
     */
    public static final Map<String, String> OLD_IDS_POST_2005;
    static {
        Map<String, String> base = new HashMap<>();
        base.put("ACT", "Australia/Darwin");
        base.put("AET", "Australia/Sydney");
        base.put("AGT", "America/Argentina/Buenos_Aires");
        base.put("ART", "Africa/Cairo");
        base.put("AST", "America/Anchorage");
        base.put("BET", "America/Sao_Paulo");
        base.put("BST", "Asia/Dhaka");
        base.put("CAT", "Africa/Harare");
        base.put("CNT", "America/St_Johns");
        base.put("CST", "America/Chicago");
        base.put("CTT", "Asia/Shanghai");
        base.put("EAT", "Africa/Addis_Ababa");
        base.put("ECT", "Europe/Paris");
        base.put("IET", "America/Indiana/Indianapolis");
        base.put("IST", "Asia/Kolkata");
        base.put("JST", "Asia/Tokyo");
        base.put("MIT", "Pacific/Apia");
        base.put("NET", "Asia/Yerevan");
        base.put("NST", "Pacific/Auckland");
        base.put("PLT", "Asia/Karachi");
        base.put("PNT", "America/Phoenix");
        base.put("PRT", "America/Puerto_Rico");
        base.put("PST", "America/Los_Angeles");
        base.put("SST", "Pacific/Guadalcanal");
        base.put("VST", "Asia/Ho_Chi_Minh");
        Map<String, String> pre = new HashMap<>(base);
        pre.put("EST", "America/Indianapolis");
        pre.put("MST", "America/Phoenix");
        pre.put("HST", "Pacific/Honolulu");
        OLD_IDS_PRE_2005 = Collections.unmodifiableMap(pre);
        Map<String, String> post = new HashMap<>(base);
        post.put("EST", "-05:00");
        post.put("MST", "-07:00");
        post.put("HST", "-10:00");
        OLD_IDS_POST_2005 = Collections.unmodifiableMap(post);
    }

    /**
     * The group:region ID pattern.
     */
    private static final Pattern PATTERN = Pattern.compile("(([A-Za-z0-9._-]+)[:])?([A-Za-z0-9%@~/+._-]+)");

    //-----------------------------------------------------------------------
    /**
     * Gets the system default time-zone.
     * <p>
     * This queries {@link TimeZone#getDefault()} to find the default time-zone
     * and converts it to a {@code ZoneId}. If the system default time-zone is changed,
     * then the result of this method will also change.
     *
     * @return the zone ID, not null
     * @throws DateTimeException if a zone ID cannot be created from the TimeZone object
     */
    public static ZoneId systemDefault() {
        return ZoneId.of(TimeZone.getDefault().getID(), OLD_IDS_POST_2005);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneId} using its ID using a map
     * of aliases to supplement the standard zone IDs.
     * <p>
     * Many users of time-zones use short abbreviations, such as PST for
     * 'Pacific Standard Time' and PDT for 'Pacific Daylight Time'.
     * These abbreviations are not unique, and so cannot be used as identifiers.
     * This method allows a map of string to time-zone to be setup and reused
     * within an application.
     *
     * @param zoneId  the time-zone ID, not null
     * @param aliasMap  a map of alias zone IDs (typically abbreviations) to real zone IDs, not null
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    public static ZoneId of(String zoneId, Map<String, String> aliasMap) {
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(aliasMap, "aliasMap");
        String id = aliasMap.get(zoneId);
        id = (id != null ? id : zoneId);
        return of(id);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier ensuring that the
     * identifier is valid and available for use.
     * <p>
     * This method parses the ID, applies any appropriate normalization, and validates it
     * against the known set of IDs for which rules are available.
     * <p>
     * Four forms of identifier are recognized:
     * <p><ul>
     * <li>{@code {groupID}:{regionID}} - full
     * <li>{@code {regionID}} - implies 'TZDB' group and specific version
     * <li>{@code UTC{offset}} - implies 'UTC' group with fixed offset
     * <li>{@code GMT{offset}} - implies 'UTC' group with fixed offset
     * </ul><p>
     * Group IDs must match regular expression {@code [A-Za-z0-9._-]+}.<br />
     * Region IDs must match regular expression {@code [A-Za-z0-9%@~/+._-]+}, except
     * if the group ID is 'UTC' when the regular expression is {@code [Z0-9+:-]+}.<br />
     * <p>
     * The detailed format of the region ID depends on the group.
     * The default group is 'TZDB' which has region IDs generally of the form '{area}/{city}',
     * such as 'Europe/Paris' or 'America/New_York'.
     * This is compatible with most IDs from {@link java.util.TimeZone}.
     * <p>
     * For example, the ID in use in Tokyo, Japan is 'Asia/Tokyo'.
     * Passing either 'Asia/Tokyo' or 'TZDB:Asia/Tokyo' will create a valid object for that city.
     * <p>
     * The three additional special cases can match where the group ID is not specified.
     * If the input starts with UTC or GMT then the remainder is parsed to find an offset
     * and the group ID is treated as 'UTC'.
     * Otherwise, the group ID is considered to be 'TZDB'.
     *
     * @param zoneId  the time-zone ID, not null
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    public static ZoneId of(String zoneId) {
        return ofId(zoneId, true);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier.
     *
     * @param zoneId  the time-zone ID, not null
     * @param checkAvailable  whether to check if the zone ID is available
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    private static ZoneId ofId(String zoneId, boolean checkAvailable) {
        Objects.requireNonNull(zoneId, "zoneId");

        // handle most zone offset cases
        if (zoneId.equals("Z") || zoneId.equals("UTC") || zoneId.equals("GMT")) {
            return UTC;
        }
        if (zoneId.startsWith("UTC") || zoneId.startsWith("GMT")) {
            try {
                return ZoneOffset.of(zoneId.substring(3));
            } catch (IllegalArgumentException ex) {
                // continue, in case it is something like GMT0, GMT+0, GMT-0
            }
        }

        // normal non-fixed IDs
        Matcher matcher = PATTERN.matcher(zoneId);
        if (matcher.matches() == false) {
            throw new DateTimeException("Invalid time-zone ID: " + zoneId);
        }
        String groupId = matcher.group(2);
        String regionId = matcher.group(3);
        groupId = (groupId != null ? (groupId.equals(GROUP_TZDB) ? GROUP_TZDB : groupId) : GROUP_TZDB);
        ZoneRulesProvider provider = null;
        try {
            // always attempt load for better behavior after deserialization
            provider = ZoneRulesProvider.getProvider(groupId);
            if (checkAvailable && provider.isValid(regionId, null) == false) {
                throw new DateTimeException("Unknown time-zone: " + groupId + ':' + regionId);
            }
        } catch (DateTimeException ex) {
            if (checkAvailable) {
                throw ex;
            }
        }
        return new ZoneRegion(zoneId, provider);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneId} from a date-time object.
     * <p>
     * A {@code DateTimeAccessor} represents some form of date and time information.
     * This factory converts the arbitrary date-time object to an instance of {@code ZoneId}.
     *
     * @param dateTime  the date-time object to convert, not null
     * @return the zone ID, not null
     * @throws DateTimeException if unable to convert to a {@code ZoneId}
     */
    public static ZoneId from(DateTimeAccessor dateTime) {
        ZoneId obj = dateTime.query(Query.ZONE_ID);
        if (obj == null) {
            throw new DateTimeException("Unable to convert DateTimeAccessor to ZoneId: " + dateTime.getClass());
        }
        return obj;
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor only accessible within the package.
     */
    ZoneId() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unique time-zone ID.
     * <p>
     * This ID uniquely defines this object.
     * The format is defined by {@link ZoneOffset} and {@link ZoneRegion}.
     *
     * @return the time-zone unique ID, not null
     */
    public abstract String getId();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it.
     * <p>
     * This will return true if rules are available for this ID. If this method
     * returns true, then {@link #getRules()} will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules available as the JVM that serialized it.
     * {@link ZoneOffset} will always return true.
     *
     * @return true if this time-zone is valid and rules are available
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Gets the time-zone rules for this ID allowing calculations to be performed.
     * <p>
     * The rules provide the functionality associated with a time-zone,
     * such as finding the offset for a given instant or local date-time.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     * <p>
     * The rules are supplied by {@link ZoneRulesProvider}. An advanced provider may
     * support dynamic updates to the rules without restarting the JVM.
     * If so, then the result of this method may change over time.
     * Each individual call will be still remain thread-safe.
     * <p>
     * {@link ZoneOffset} will always return a set of rules where the offset never changes.
     *
     * @return the rules, not null
     * @throws DateTimeException if no rules are available for this ID
     */
    public abstract ZoneRules getRules();

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of the zone, such as 'British Time' or
     * '+02:00'.
     * <p>
     * This returns a textual description for the time-zone ID.
     * <p>
     * If no textual mapping is found then the {@link #getId() full ID} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day-of-week, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return getId();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone ID is equal to another time-zone ID.
     * <p>
     * The comparison is based on the ID.
     *
     * @param obj  the object to check, null returns false
     * @return true if this is equal to the other time-zone ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
           return true;
        }
        if (obj instanceof ZoneId) {
            ZoneId other = (ZoneId) obj;
            return getId().equals(other.getId());
        }
        return false;
    }

    /**
     * A hash code for this time-zone ID.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this zone as a {@code String}, using the ID.
     *
     * @return a string representation of this time-zone ID, not null
     */
    @Override
    public String toString() {
        return getId();
    }

}
