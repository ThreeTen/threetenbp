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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.calendrical.DateTime;
import javax.time.format.TextStyle;
import javax.time.zone.ZoneOffsetInfo;
import javax.time.zone.ZoneOffsetTransition;
import javax.time.zone.ZoneOffsetTransitionRule;
import javax.time.zone.ZoneRules;
import javax.time.zone.ZoneRulesGroup;

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
 * <h4>Time zone rule data</h4>
 * There are a number of sources of time-zone information available,
 * each represented by an instance of {@link ZoneRulesGroup}.
 * One group is provided as standard - {@code TZDB} - and applications can add more as required.
 * <p>
 * Each group defines a naming scheme for the regions of the time-zone.
 * The format of the region is specific to the group.
 * For example, the {@code TZDB} group typically use the format {area}/{city},
 * such as {@code Europe/London}.
 * <p>
 * In combination, a unique ID is created expressing the time-zone, formed using a colon:
 * {groupID}:{regionID}
 * <p>
 * In addition to the group:region combinations, {@code ZoneId} can represent a fixed offset.
 * The groupId of a fixed offset is the empty string.
 * <p>
 * The set of time-zone rules changes over time.
 * To handle this, each group produces multiple versions of their data, with a release perhaps
 * several time per year. The format of the version is specific to the group.
 * For example, the {@code TZDB} group use the format {year}{letter}, such as {@code 2009b}.
 * These changes are modeled in another class TODO.
 * <p>
 * The purpose of capturing all the time-zone information is to handle issues when
 * manipulating and persisting time-zones. For example, consider what happens if the
 * government of a country changed the start or end of daylight saving time.
 * If a date-time is created and stored using one version of the rules, and then loaded
 * when a new version of the rules are in force, what should happen?
 * The date might now be invalid, for example due to a gap in the local time-line.
 * By storing the version of the time-zone rules data together with the date, it is
 * possible to tell that the rules have changed and to process accordingly.
 * Note however that this API aims to provide the data to support this behavior,
 * rather than a working implementation.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
public abstract class ZoneId implements Serializable {

    /**
     * The group:region ID pattern.
     */
    private static final Pattern PATTERN = Pattern.compile("(([A-Za-z0-9._-]+)[:])?([A-Za-z0-9%@~/+._-]+)");
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The time-zone id for 'UTC'.
     * Note that it is intended that fixed offset time-zones like this are rarely used.
     * Applications should use {@link ZoneOffset} and {@link OffsetDateTime} in preference.
     */
    public static final ZoneId UTC = new Fixed(ZoneOffset.UTC);
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
        Map<String, String> base = new HashMap<String, String>();
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
        Map<String, String> pre = new HashMap<String, String>(base);
        pre.put("EST", "America/Indianapolis");
        pre.put("MST", "America/Phoenix");
        pre.put("HST", "Pacific/Honolulu");
        OLD_IDS_PRE_2005 = Collections.unmodifiableMap(pre);
        Map<String, String> post = new HashMap<String, String>(base);
        post.put("EST", "UTC-05:00");
        post.put("MST", "UTC-07:00");
        post.put("HST", "UTC-10:00");
        OLD_IDS_POST_2005 = Collections.unmodifiableMap(post);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the system default time-zone.
     * <p>
     * This queries {@link TimeZone#getDefault()} to find the default time-zone
     * and converts it to a {@code ZoneId}. If the system default time-zone is changed,
     * then the result of this method will also change.
     *
     * @return the zone ID, not null
     * @throws CalendricalException if a zone ID cannot be created from the TimeZone object
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
     * @throws CalendricalException if the zone ID cannot be found
     */
    public static ZoneId of(String timeZoneIdentifier, Map<String, String> aliasMap) {
        DateTimes.checkNotNull(timeZoneIdentifier, "Time Zone ID must not be null");
        DateTimes.checkNotNull(aliasMap, "Alias map must not be null");
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
     * <li>{@code UTC{offset}} - fixed time-zone
     * <li>{@code GMT{offset}} - fixed time-zone
     * </ul>
     * Group IDs must match regular expression {@code [A-Za-z0-9._-]+}.<br />
     * Region IDs must match regular expression {@code [A-Za-z0-9%@~/+._-]+}.<br />
     * <p>
     * The detailed format of the region ID depends on the group.
     * The default group is 'TZDB' which has region IDs generally of the form {area}/{city},
     * such as 'Europe/Paris' or 'America/New_York'.
     * This is compatible with most IDs from {@link java.util.TimeZone}.
     * <p>
     * For example, the ID in use in Tokyo, Japan is 'Asia/Tokyo'.
     * Passing either 'Asia/Tokyo' or 'TZDB:Asia/Tokyo' will create a valid object for that city.
     * <p>
     * The alternate format is for fixed time-zones, where the offset never changes over time.
     * A fixed time-zone is returned if the first three characters are 'UTC' or 'GMT' and
     * the remainder of the ID is a valid format for {@link ZoneOffset#of(String)}.
     * The result will have a normalized time-zone ID of 'UTC{offset}', or just 'UTC' if the offset is zero.
     * <p>
     * Note that it is intended that fixed offset time-zones are rarely used. Applications should use
     * {@link ZoneOffset} and {@link OffsetDateTime} in preference.
     *
     * @param zoneID  the time-zone identifier, not null
     * @return the zone ID, not null
     * @throws CalendricalException if the zone ID cannot be found
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
     * One example might be a system that always retrieves time-zone rules from a remote server.
     * Using this factory allows a {@code ZoneId}, and thus a {@code ZonedDateTime},
     * to be created without loading the rules from the remote server.
     *
     * @param zoneID  the time-zone identifier, not null
     * @return the zone ID, not null
     * @throws CalendricalException if the zone ID cannot be found
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
     * @throws CalendricalException if the zone ID cannot be found
     */
    private static ZoneId ofID(String zoneID, boolean checkAvailable) {
        DateTimes.checkNotNull(zoneID, "Time zone ID must not be null");
        
        // special fixed cases
        if (zoneID.equals("UTC") || zoneID.equals("GMT")) {
            return UTC;
        }
        if ((zoneID.startsWith("UTC") || zoneID.startsWith("GMT")) && zoneID.indexOf('#') < 0) {
            try {
                return of(ZoneOffset.of(zoneID.substring(3)));
            } catch (IllegalArgumentException ex) {
                // continue, in case it is something like GMT0, GMT+0, GMT-0
            }
        }
        
        // normal non-fixed IDs
        Matcher matcher = PATTERN.matcher(zoneID);
        if (matcher.matches() == false) {
            throw new CalendricalException("Invalid time-zone ID: " + zoneID);
        }
        String groupID = matcher.group(2);
        String regionID = matcher.group(3);
        groupID = (groupID != null ? groupID : "TZDB");
        if (checkAvailable) {
            ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
            if (group.isValidRegionID(regionID) == false) {
                throw new CalendricalException("Unknown time-zone region: " + groupID + ':' + regionID);
            }
        }
        return new ID(groupID, regionID);
    }

    /**
     * Obtains an instance of {@code ZoneId} representing a fixed time-zone.
     * <p>
     * The time-zone returned from this factory has a fixed offset for all time.
     * The region ID will return an identifier formed from 'UTC' and the offset.
     * The group ID will return an empty string.
     * <p>
     * Fixed time-zones are {@link #isValid() always valid}.
     *
     * @param offset  the zone offset to create a fixed zone for, not null
     * @return the zone ID for the offset, not null
     */
    public static ZoneId of(ZoneOffset offset) {
        DateTimes.checkNotNull(offset, "ZoneOffset must not be null");
        if (offset == ZoneOffset.UTC) {
            return UTC;
        }
        return new Fixed(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneId} from a calendrical.
     * <p>
     * A calendrical represents some form of date and time information.
     * This factory converts the arbitrary calendrical to an instance of {@code ZoneId}.
     * 
     * @param calendrical  the calendrical to convert, not null
     * @return the zone ID, not null
     * @throws CalendricalException if unable to convert to a {@code ZoneId}
     */
    public static ZoneId from(DateTime calendrical) {
        ZoneId obj = calendrical.extract(ZoneId.class);
        return DateTimes.ensureNotNull(obj, "Unable to convert calendrical to ZoneId: ", calendrical.getClass());
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
     * The format is {groupID}:{regionID}.
     * If the group is 'TZDB' then the {groupID}: is omitted.
     * Fixed time-zones will only output the region ID.
     *
     * @return the time-zone unique ID, not null
     */
    public abstract String getID();

    /**
     * Gets the time-zone rules group ID, such as {@code TZDB}.
     * <p>
     * The group ID is the first part of the {@link #getID() full unique ID}.
     * Time zone rule data is supplied by a group, typically a company or organization.
     * The default group is 'TZDB' representing the public time-zone database.
     * <p>
     * For fixed time-zones, the group ID will be an empty string.
     *
     * @return the time-zone rules group ID, not null
     */
    public abstract String getGroupID();

    /**
     * Gets the time-zone region identifier, such as {@code Europe/London}.
     * <p>
     * The region ID is the second part of the {@link #getID() full unique ID}.
     * Time zone rules are defined for a region and this element represents that region.
     * The ID uses a format specific to the group.
     * The default 'TZDB' group generally uses the format {area}/{city}, such as 'Europe/Paris'.
     *
     * @return the time-zone rules region ID, not null
     */
    public abstract String getRegionID();

    //-----------------------------------------------------------------------
    /**
     * Checks of the time-zone is fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time-zones
     * in {@link ZonedDateTime}.
     *
     * @return true if the time-zone is fixed and the offset never changes
     */
    public abstract boolean isFixedOffset();

    //-----------------------------------------------------------------------
    /**
     * Finds the zone rules group for the stored group ID, such as 'TZDB'.
     * <p>
     * Time zone rules are provided by groups referenced by an ID.
     * <p>
     * Fixed time-zones are not provided by a group, thus this method throws
     * an exception if the time-zone is fixed.
     * <p>
     * Callers of this method need to be aware of an unusual scenario.
     * It is possible to obtain a {@code ZoneId} instance even when the
     * rules are not available. This typically occurs when a {@code ZoneId}
     * is loaded from a previously stored version but the rules are not available.
     * In this case, the {@code ZoneId} instance is still valid, as is
     * any associated object, such as {@link ZonedDateTime}. It is impossible to
     * perform any calculations that require the rules however, and this method
     * will throw an exception.
     *
     * @return the time-zone rules group, not null
     * @throws CalendricalException if the time-zone is fixed
     * @throws CalendricalException if the group ID cannot be found
     */
    public abstract ZoneRulesGroup getGroup();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it.
     * <p>
     * This will return true if the rules are available for this ID. If this method
     * returns true, then {@link #getRules()} will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules available as the JVM that stored it.
     * <p>
     * If this is a fixed time-zone, then it is always valid.
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
     * If a background thread is used to update the available rules, then the result
     * of calling this method may vary over time.
     * Each individual call will be still remain thread-safe.
     *
     * @return the rules, not null
     * @throws CalendricalException if no rules are available for this ID
     */
    public abstract ZoneRules getRules();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it
     * which are valid for the specified date-time and offset.
     * <p>
     * This will return true if the rules declare that the specified date-time is valid.
     * If this method returns true, then {@link #getRulesValidFor(OffsetDateTime)}
     * will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it.
     * <p>
     * If this is a fixed time-zone, then it is valid if the offset matches the date-time.
     *
     * @param dateTime  a date-time for which the rules must be valid, null returns false
     * @return true if this time-zone is valid and rules are available
     */
    public abstract boolean isValidFor(OffsetDateTime dateTime);

    /**
     * Gets the time-zone rules allowing calculations to be performed, ensuring that
     * the date-time and offset specified is valid for the returned rules.
     * <p>
     * The rules provide the functionality associated with a time-zone,
     * such as finding the offset for a given instant or local date-time.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     *
     * @param dateTime  a date-time for which the rules must be valid, not null
     * @return the latest rules for this zone where the date-time is valid, not null
     * @throws CalendricalException if the zone ID cannot be found
     * @throws CalendricalException if no rules match the zone ID and date-time
     */
    public abstract ZoneRules getRulesValidFor(OffsetDateTime dateTime);

    //-----------------------------------------------------------------------
    /**
     * Gets the textual representation of the zone, such as 'British Time'.
     * <p>
     * This returns a textual description for the time-zone ID.
     * <p>
     * If no textual mapping is found then the {@link #getRegionID() region ID} is returned.
     *
     * @param locale  the locale to use, not null
     * @return the short text value of the day-of-week, not null
     */
    public String getText(TextStyle style, Locale locale) {
        return getRegionID();  // TODO
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
    static final class ID extends ZoneId {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The time-zone group ID, not null. */
        private final String groupID;
        /** The time-zone region ID, not null. */
        private final String regionID;

        /**
         * Constructor.
         *
         * @param groupID  the time-zone rules group ID, not null
         * @param regionID  the time-zone region ID, not null
         */
        ID(String groupID, String regionID) {
            this.groupID = groupID;
            this.regionID = regionID;
        }

        /**
         * Validate deserialization.
         *
         * @param in  the input stream
         */
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            if (groupID == null || groupID.length() == 0 || regionID == null) {
                throw new StreamCorruptedException();
            }
        }

        //-----------------------------------------------------------------------
        @Override
        public String getID() {
            if (groupID.equals("TZDB")) {
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
        public boolean isFixedOffset() {
            return false;
        }

        @Override
        public ZoneRulesGroup getGroup() {
            return ZoneRulesGroup.getGroup(groupID);
        }

        @Override
        public boolean isValid() {
            return ZoneRulesGroup.isValidGroupID(groupID) && getGroup().isValidRegionID(regionID);
        }

        @Override
        public ZoneRules getRules() {
            return getGroup().getRules(regionID, getGroup().getLatestVersionID(regionID));
        }

        @Override
        public boolean isValidFor(OffsetDateTime dateTime) {
            if (dateTime == null) {
                return false;
            }
            try {
                getRulesValidFor(dateTime);
                return true;
            } catch (CalendricalException ex) {
                return false;
            }
        }

        @Override
        public ZoneRules getRulesValidFor(OffsetDateTime dateTime) {
            DateTimes.checkNotNull(dateTime, "OffsetDateTime must not be null");
            return getGroup().getRules(regionID, getGroup().getLatestVersionIDValidFor(regionID, dateTime));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fixed time-zone.
     */
    static final class Fixed extends ZoneId implements ZoneRules {
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** The zone id. */
        private final String id;
        /** The offset. */
        private final transient ZoneOffsetInfo offsetInfo;

        /**
         * Constructor.
         *
         * @param offset  the offset, not null
         */
        Fixed(ZoneOffset offset) {
            this.id = (offset == ZoneOffset.UTC ? "UTC" : "UTC" + offset.getID());
            this.offsetInfo = ZoneOffsetInfo.ofOffset(offset);
        }

        /**
         * Handle deserialization.
         *
         * @return the resolved instance, not null
         */
        private Object readResolve() throws ObjectStreamException {
            if (id == null || id.startsWith("UTC") == false) {
                throw new StreamCorruptedException();
            }
            // fixed time-zone must always be valid
            return ZoneId.of(id);
        }

        //-----------------------------------------------------------------------
        @Override
        public String getID() {
            return id;
        }

        @Override
        public String getGroupID() {
            return "";
        }

        @Override
        public String getRegionID() {
            return id;
        }

        @Override
        public ZoneRulesGroup getGroup() {
            throw new CalendricalException("Fixed ZoneId is not provided by a group");
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public ZoneRules getRules() {
            return this;
        }

        @Override
        public boolean isValidFor(OffsetDateTime dateTime) {
            if (dateTime == null) {
                return false;
            }
            return offsetInfo.getOffset().equals(dateTime.getOffset());
        }

        @Override
        public ZoneRules getRulesValidFor(OffsetDateTime dateTime) {
            DateTimes.checkNotNull(dateTime, "OffsetDateTime must not be null");
            if (isValidFor(dateTime) == false) {
                throw new CalendricalException("Fixed ZoneId " + getID() + " is invalid for date-time " + dateTime);
            }
            return this;
        }

        //-------------------------------------------------------------------------
        @Override
        public boolean isFixedOffset() {
            return true;
        }

        @Override
        public ZoneOffset getOffset(Instant instant) {
            return offsetInfo.getOffset();
        }

        @Override
        public ZoneOffsetInfo getOffsetInfo(LocalDateTime dateTime) {
            return offsetInfo;
        }

        @Override
        public boolean isValidDateTime(OffsetDateTime dateTime) {
            return dateTime.getOffset().equals(offsetInfo.getOffset());
        }

        //-------------------------------------------------------------------------
        @Override
        public ZoneOffset getStandardOffset(Instant instant) {
            return offsetInfo.getOffset();
        }

        @Override
        public Period getDaylightSavings(Instant instant) {
            return Period.ZERO_SECONDS;
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
            if (obj instanceof Fixed) {
                return offsetInfo.getOffset().equals(((Fixed) obj).offsetInfo.getOffset());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return offsetInfo.getOffset().hashCode() + 1;
        }
    }

}
