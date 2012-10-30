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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.calendrical.DateTimeAccessor;
import javax.time.format.TextStyle;
import javax.time.zone.ZoneOffsetInfo;
import javax.time.zone.ZoneOffsetTransition;
import javax.time.zone.ZoneOffsetTransitionRule;
import javax.time.zone.ZoneRules;
import javax.time.zone.ZoneRulesProvider;

/**
 * A time-zone id representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * Time-zones are geographical regions where the same rules for time apply.
 * The rules are defined by governments and change frequently.
 * This class provides an identifier that locates a single set of rules.
 * <p>
 * The rule data is split across two main classes:
 * <ul>
 * <li>{@code ZoneId}, which only represents the identifier of the rule-set
 * <li>{@link ZoneRules}, which defines the set of rules themselves
 * </ul>
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
public abstract class ZoneId implements Serializable {

    /**
     * The time-zone id for 'UTC'.
     * Note that it is intended that fixed offset time-zones like this are rarely used.
     * Applications should use {@link ZoneOffset} and {@link OffsetDateTime} in preference.
     */
    public static final ZoneId UTC = new FixedZone(ZoneOffset.UTC);
    /**
     * The time-zone group id for 'TZDB'.
     * <p>
     * The 'TZDB' group represents the main public time-zone database.
     */
    public static final String GROUP_TZDB = "TZDB";
    /**
     * The time-zone group id for 'UTC'.
     * <p>
     * The 'UTC' group represents fixed offsets from UTC/Greenwich, which should
     * normally be used directly via {@link ZoneOffset}.
     */
    public static final String GROUP_UTC = "UTC";

    /**
     * A map of zone overrides to enable the older US time-zone names to be used.
     * <p>
     * This maps as follows:
     * <ul>
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
     * </ul>
     * The map is unmodifiable.
     */
    public static final Map<String, String> OLD_IDS_PRE_2005;
    /**
     * A map of zone overrides to enable the older US time-zone names to be used.
     * <p>
     * This maps as follows:
     * <ul>
     * <li>EST - UTC-05:00</li>
     * <li>HST - UTC-10:00</li>
     * <li>MST - UTC-07:00</li>
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
     * </ul>
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
        post.put("EST", "UTC-05:00");
        post.put("MST", "UTC-07:00");
        post.put("HST", "UTC-10:00");
        OLD_IDS_POST_2005 = Collections.unmodifiableMap(post);
    }

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The group:region ID pattern.
     */
    private static final Pattern PATTERN = Pattern.compile("(([A-Za-z0-9._-]+)[:])?([A-Za-z0-9%@~/+._-]+)");
    /**
     * The time-zone group id for 'UTC:'.
     */
    private static final String GROUP_UTC_COLON = "UTC:";

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
     * @param timeZoneIdentifier  the time-zone id, not null
     * @param aliasMap  a map of alias zone IDs (typically abbreviations) to real zone IDs, not null
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    public static ZoneId of(String timeZoneIdentifier, Map<String, String> aliasMap) {
        Objects.requireNonNull(timeZoneIdentifier, "Time-zone ID");
        Objects.requireNonNull(aliasMap, "Alias map");
        String zoneId = aliasMap.get(timeZoneIdentifier);
        zoneId = (zoneId != null ? zoneId : timeZoneIdentifier);
        return of(zoneId);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier ensuring that the
     * identifier is valid and available for use.
     * <p>
     * This method parses the ID, applies any appropriate normalization, and validates it
     * against the known set of IDs for which rules are available.
     * <p>
     * Four forms of identifier are recognized:
     * <ul>
     * <li>{@code {groupID}:{regionID}} - full
     * <li>{@code {regionID}} - implies 'TZDB' group and specific version
     * <li>{@code UTC{offset}} - implies 'UTC' group with fixed offset
     * <li>{@code GMT{offset}} - implies 'UTC' group with fixed offset
     * </ul>
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
     * @param zoneID  the time-zone identifier, not null
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    public static ZoneId of(String zoneID) {
        return ofID(zoneID, true);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier without checking
     * if the time-zone has available rules.
     * <p>
     * This method parses the ID and applies any appropriate normalization.
     * Unlike {@link #of(String)}, it does not validates the ID against the known set of IDs
     * for which rules are available.
     * <p>
     * This method is intended for advanced use cases.
     * For example, consider a system that always retrieves time-zone rules from a remote server.
     * Using this factory would allow a {@code ZoneId}, and thus a {@code ZonedDateTime},
     * to be created without loading the rules from the remote server.
     *
     * @param zoneID  the time-zone identifier, not null
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    public static ZoneId ofUnchecked(String zoneID) {
        return ofID(zoneID, false);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier.
     *
     * @param zoneID  the time-zone identifier, not null
     * @param checkAvailable  whether to check if the zone ID is available
     * @return the zone ID, not null
     * @throws DateTimeException if the zone ID cannot be found
     */
    private static ZoneId ofID(String zoneID, boolean checkAvailable) {
        Objects.requireNonNull(zoneID, "Time-zone ID");
        
        // special fixed cases
        if (zoneID.equals("UTC") || zoneID.equals("GMT")) {
            return UTC;
        }
        if (zoneID.startsWith(GROUP_UTC_COLON)) {
            try {
                return of(ZoneOffset.of(zoneID.substring(4)));
            } catch (IllegalArgumentException ex) {
                throw new DateTimeException("Unknown time-zone offset", ex);
            }
        }
        if (zoneID.startsWith("UTC") || zoneID.startsWith("GMT")) {
            try {
                return of(ZoneOffset.of(zoneID.substring(3)));
            } catch (IllegalArgumentException ex) {
                // continue, in case it is something like GMT0, GMT+0, GMT-0
            }
        }
        
        // normal non-fixed IDs
        Matcher matcher = PATTERN.matcher(zoneID);
        if (matcher.matches() == false) {
            throw new DateTimeException("Invalid time-zone ID: " + zoneID);
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
        return new RulesZone(groupId, regionId, provider);
    }

    /**
     * Obtains an instance of {@code ZoneId} representing a fixed time-zone.
     * <p>
     * The time-zone returned from this factory has a fixed offset for all time.
     * The group identifier is 'UTC' and the region is the identifier of
     * the {@code ZoneOffset}.
     *
     * @param offset  the zone offset to create a fixed zone for, not null
     * @return the zone ID for the offset, not null
     */
    public static ZoneId of(ZoneOffset offset) {
        Objects.requireNonNull(offset, "ZoneOffset");
        if (offset == ZoneOffset.UTC) {
            return UTC;
        }
        return new FixedZone(offset);
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
        ZoneId obj = dateTime.extract(ZoneId.class);
        return DateTimes.ensureNotNull(obj, "Unable to convert DateTimeAccessor to ZoneId: ", dateTime.getClass());
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
     * The unique key is created from the group ID and region ID.
     * The format is <code>{groupID}:{regionID}</code>.
     * If the group is 'TZDB' then only the region identifier is returned.
     *
     * @return the time-zone unique ID, not null
     */
    public abstract String getID();

    /**
     * Gets the time-zone rules group ID, such as 'TZDB'.
     * <p>
     * The group ID is the first part of the {@link #getID() full unique ID}.
     * Time zone rule data is supplied by a group, typically a company or organization.
     * The default group is 'TZDB' representing the public time-zone database.
     *
     * @return the time-zone rules group ID, not empty, not null
     */
    public abstract String getGroupID();

    /**
     * Gets the time-zone region identifier, such as 'Europe/London'.
     * <p>
     * The region ID is the second part of the {@link #getID() full unique ID}.
     * Time zone rules are defined for a region and this element represents that region.
     * The ID uses a format specific to the group.
     * The default 'TZDB' group generally uses the format '{area}/{city}', such as 'Europe/Paris'.
     *
     * @return the time-zone rules region ID, not empty, not null
     */
    public abstract String getRegionID();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it.
     * <p>
     * This will return true if the rules are available for this ID. If this method
     * returns true, then {@link #getRules()} will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules available as the JVM that serialized it.
     * <p>
     * If this is a fixed time-zone of group 'UTC', then it is always valid.
     *
     * @return true if this time-zone is valid and rules are available
     */
    public abstract boolean isValid();

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
     * If this is a fixed time-zone of group 'UTC', then the rules never change.
     *
     * @return the rules, not null
     * @throws DateTimeException if no rules are available for this ID
     */
    public abstract ZoneRules getRules();

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of the zone, such as 'British Time'.
     * <p>
     * This returns a textual description for the time-zone ID.
     * <p>
     * If no textual mapping is found then the {@link #getID() full ID} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day-of-week, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return getID();  // TODO
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
            return getRegionID().equals(other.getRegionID()) &&
                    getGroupID().equals(other.getGroupID());
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
        return getGroupID().hashCode() ^ getRegionID().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this zone as a {@code String}, using the ID.
     *
     * @return a string representation of this time-zone ID, not null
     */
    @Override
    public String toString() {
        return getID();
    }

    //-----------------------------------------------------------------------
    /**
     * ID based time-zone.
     * This can refer to an id that does not have available rules.
     */
    static final class RulesZone extends ZoneId {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;

        /** The time-zone group ID, not null. */
        private final String groupID;
        /** The time-zone region ID, not null. */
        private final String regionID;
        /** The time-zone group provider, null if zone ID is unchecked. */
        private final transient ZoneRulesProvider provider;

        /**
         * Constructor.
         *
         * @param groupID  the time-zone rules group ID, not null
         * @param regionID  the time-zone region ID, not null
         * @param provider  the provider, null if zone is unchecked
         */
        RulesZone(String groupID, String regionID, ZoneRulesProvider provider) {
            this.groupID = groupID;
            this.regionID = regionID;
            this.provider = provider;
        }

        /**
         * Handle deserialization.
         *
         * @return the resolved instance, not null
         */
        private Object readResolve() throws ObjectStreamException {
            return ZoneId.ofUnchecked(groupID + ":" + regionID);
        }

        //-----------------------------------------------------------------------
        @Override
        public String getID() {
            if (groupID.equals(GROUP_TZDB)) {
                return regionID;
            }
            return groupID + ':' + regionID;
        }

        @Override
        public String getGroupID() {
            return groupID;
        }

        @Override
        public String getRegionID() {
            return regionID;
        }

        @Override
        public boolean isValid() {
            return getProvider().isValid(regionID, null);
        }

        @Override
        public ZoneRules getRules() {
            return getProvider().getRules(regionID, null);
        }

        private ZoneRulesProvider getProvider() {
            // additional query for group provider when null allows for possibility
            // that the provider was added after the ZoneId was created
            return (provider != null ? provider : ZoneRulesProvider.getProvider(groupID));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fixed time-zone.
     */
    static final class FixedZone extends ZoneId implements ZoneRules {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The zone id. */
        private final String id;
        /** The offset. */
        private final transient ZoneOffset offset;

        /**
         * Constructor.
         *
         * @param offset  the offset, not null
         */
        FixedZone(ZoneOffset offset) {
            this.id = GROUP_UTC_COLON + offset.getID();
            this.offset = offset;
        }

        /**
         * Handle deserialization.
         *
         * @return the resolved instance, not null
         */
        private Object readResolve() throws ObjectStreamException {
            return ZoneId.of(id);
        }

        //-----------------------------------------------------------------------
        @Override
        public String getID() {
            return id;
        }

        @Override
        public String getGroupID() {
            return GROUP_UTC;
        }

        @Override
        public String getRegionID() {
            return id.substring(4);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public ZoneRules getRules() {
            return this;
        }

        //-------------------------------------------------------------------------
        @Override
        public boolean isFixedOffset() {
            return true;
        }

        @Override
        public ZoneOffset getOffset(Instant instant) {
            return offset;
        }

        @Override
        public ZoneOffsetInfo getOffsetInfo(LocalDateTime dateTime) {
            return offset;
        }

        @Override
        public boolean isValidDateTime(OffsetDateTime dateTime) {
            return dateTime.getOffset().equals(offset);
        }

        //-------------------------------------------------------------------------
        @Override
        public ZoneOffset getStandardOffset(Instant instant) {
            return offset;
        }

        @Override
        public Duration getDaylightSavings(Instant instant) {
            return Duration.ZERO;
        }

        @Override
        public boolean isDaylightSavings(Instant instant) {
            return false;
        }

        //-------------------------------------------------------------------------
        @Override
        public ZoneOffsetTransition nextTransition(Instant instant) {
            return null;
        }

        @Override
        public ZoneOffsetTransition previousTransition(Instant instant) {
            return null;
        }

        @Override
        public List<ZoneOffsetTransition> getTransitions() {
            return Collections.emptyList();
        }

        @Override
        public List<ZoneOffsetTransitionRule> getTransitionRules() {
            return Collections.emptyList();
        }

        //-----------------------------------------------------------------------
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
               return true;
            }
            if (obj instanceof FixedZone) {
                return offset.equals(((FixedZone) obj).offset);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return offset.hashCode() + 1;
        }
    }

}
