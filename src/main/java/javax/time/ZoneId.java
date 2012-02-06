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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.CalendricalRule;
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
 * <p>
 * A simple view considers that this class represents one geographical region
 * with time-zone rules. Viewed like that, this class is a direct replacement
 * for the {@link java.util.TimeZone} class, such that this class
 * accepts most of the same IDs. However, this class, and supporting classes
 * like {@link ZoneRules} provide significantly more power behind the scenes.
 * 
 * <h4>Time zones</h4>
 * There are a number of sources of time-zone information available,
 * each represented by an instance of {@link ZoneRulesGroup}.
 * One group is provided as standard - {@code TZDB} - and applications can add more as required.
 * <p>
 * Each group defines a naming scheme for the regions of the time-zone.
 * The format of the region is specific to the group.
 * For example, the {@code TZDB} group typically use the format {area}/{city},
 * such as {@code Europe/London}.
 * <p>
 * Each group typically produces multiple versions of their data.
 * The format of the version is specific to the group.
 * For example, the {@code TZDB} group use the format {year}{letter}, such as {@code 2009b}.
 * <p>
 * In combination, a unique ID is created expressing the time-zone, formed from
 * {groupID}:{regionID}#{versionID}.
 * <p>
 * The version can be set to an empty string. This represents the "floating version".
 * The floating version will always choose the latest applicable set of rules.
 * Applications will probably choose to use the floating version, as it guarantees
 * usage of the latest rules.
 * <p>
 * In addition to the group:region#version combinations, {@code ZoneId}
 * can represent a fixed offset. This has an empty group and version ID.
 * It is not possible to have an invalid instance of a fixed time-zone.
 * <p>
 * The purpose of capturing all this information is to handle issues when
 * manipulating and persisting time-zones. For example, consider what happens if the
 * government of a country changed the start or end of daylight savings time.
 * If a date-time is created and stored using one version of the rules, and then loaded
 * when a new version of the rules are in force, what should happen?
 * The date might now be invalid, for example due to a gap in the local time-line.
 * By storing the version of the time-zone rules data together with the date, it is
 * possible to tell that the rules have changed and to process accordingly.
 * <p>
 * {@code ZoneId} merely represents the identifier of the zone.
 * The actual rules are provided by {@link ZoneRules}.
 * One difference is that serializing this class only stores the reference to the zone,
 * whereas serializing {@code ZoneRules} stores the entire set of rules.
 * <p>
 * After deserialization, or by using the special factory {@link #ofUnchecked},
 * it is possible for the time-zone to represent a group/region/version combination that is unavailable.
 * Since this class can still be loaded even when the rules cannot, the application can
 * continue. For example, a {@link ZonedDateTime} instance could still be queried.
 * The application might also take appropriate corrective action.
 * For example, an application might choose to download missing rules from a central server.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public abstract class ZoneId implements Calendrical, Serializable {

    /**
     * The group:region#version ID pattern.
     */
    private static final Pattern PATTERN = Pattern.compile("(([A-Za-z0-9._-]+)[:])?([A-Za-z0-9%@~/+._-]+)([#]([A-Za-z0-9._-]+))?");
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
     * Gets the rule for {@code ZoneId}.
     *
     * @return the rule for the time-zone, not null
     */
    public static CalendricalRule<ZoneId> rule() {
        return ISOCalendricalRule.ZONE_ID;
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
        MathUtils.checkNotNull(timeZoneIdentifier, "Time Zone ID must not be null");
        MathUtils.checkNotNull(aliasMap, "Alias map must not be null");
        String zoneId = aliasMap.get(timeZoneIdentifier);
        zoneId = (zoneId != null ? zoneId : timeZoneIdentifier);
        return of(zoneId);
    }

    /**
     * Obtains an instance of {@code ZoneId} from an identifier ensuring that the
     * identifier is valid and available for use.
     * <p>
     * Six forms of identifier are recognized:
     * <ul>
     * <li>{@code {groupID}:{regionID}#{versionID}} - full
     * <li>{@code {groupID}:{regionID}} - implies the floating version
     * <li>{@code {regionID}#{versionID}} - implies 'TZDB' group and specific version
     * <li>{@code {regionID}} - implies 'TZDB' group and the floating version
     * <li>{@code UTC{offset}} - fixed time-zone
     * <li>{@code GMT{offset}} - fixed time-zone
     * </ul>
     * Group IDs must match regular expression {@code [A-Za-z0-9._-]+}.<br />
     * Region IDs must match regular expression {@code [A-Za-z0-9%@~/+._-]+}.<br />
     * Version IDs must match regular expression {@code [A-Za-z0-9._-]+}.
     * <p>
     * Most of the formats are based around the group, version and region IDs.
     * The version and region ID formats are specific to the group.
     * <p>
     * The default group is 'TZDB' which has versions of the form {year}{letter}, such as '2009b'.
     * The region ID for the 'TZDB' group is generally of the form {area}/{city}, such as 'Europe/Paris'.
     * This is compatible with most IDs from {@link java.util.TimeZone}.
     * <p>
     * For example, if a provider is loaded with the ID 'MyProvider' containing a zone ID of
     * 'France', then the unique key for version 2.1 would be 'MyProvider:France#2.1'.
     * A specific version of the 'TZDB' provider is 'TZDB:Asia/Tokyo#2008g'.
     * <p>
     * Once parsed, this factory will ensure that the group, region and version combination is valid
     * and rules can be obtained.
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
     * The identifier is parsed in a similar manner to {@link #of(String)}.
     * However, there is no check to ensure that the group, region and version resolve
     * to a set of rules that can be loaded.
     * This factory does however check that the identifier meets the acceptable format.
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
        MathUtils.checkNotNull(zoneID, "Time zone ID must not be null");
        
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
        String versionID = matcher.group(5);
        groupID = (groupID != null ? groupID : "TZDB");
        versionID = (versionID != null ? versionID : "");
        if (checkAvailable) {
            ZoneRulesGroup group = ZoneRulesGroup.getGroup(groupID);
            if (versionID.length() == 0) {
                if (group.isValidRegionID(regionID) == false) {
                    throw new CalendricalException("Unknown time-zone region: " + groupID + ':' + regionID);
                }
            } else {
                if (group.isValidRules(regionID, versionID) == false) {
                    throw new CalendricalException("Unknown time-zone region or version: " + groupID + ':' + regionID + '#' + versionID);
                }
            }
        }
        return new ID(groupID, regionID, versionID);
    }

    /**
     * Obtains an instance of {@code ZoneId} representing a fixed time-zone.
     * <p>
     * The time-zone returned from this factory has a fixed offset for all time.
     * The region ID will return an identifier formed from 'UTC' and the offset.
     * The group and version IDs will both return an empty string.
     * <p>
     * Fixed time-zones are {@link #isValid() always valid}.
     *
     * @param offset  the zone offset to create a fixed zone for, not null
     * @return the zone ID for the offset, not null
     */
    public static ZoneId of(ZoneOffset offset) {
        MathUtils.checkNotNull(offset, "ZoneOffset must not be null");
        if (offset == ZoneOffset.UTC) {
            return UTC;
        }
        return new Fixed(offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of {@code ZoneId} from a set of calendricals.
     * <p>
     * A calendrical represents some form of date and time information.
     * This method combines the input calendricals into a time-zone.
     *
     * @param calendricals  the calendricals to create a time-zone from, no nulls, not null
     * @return the time-zone, not null
     * @throws CalendricalException if unable to merge to a time-zone
     */
    public static ZoneId from(Calendrical... calendricals) {
        return CalendricalEngine.merge(calendricals).deriveChecked(rule());
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor only accessible within the package.
     */
    ZoneId() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value of the specified calendrical rule.
     * <p>
     * This method queries the value of the specified calendrical rule.
     * If the value cannot be returned for the rule from this offset then
     * {@code null} will be returned.
     *
     * @param ruleToDerive  the rule to derive, not null
     * @return the value for the rule, null if the value cannot be returned
     */
    public <T> T get(CalendricalRule<T> ruleToDerive) {
        return CalendricalEngine.derive(ruleToDerive, rule(), null, null, null, this, null, null);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unique time-zone ID.
     * <p>
     * The unique key is created from the group ID, version ID and region ID.
     * The format is {groupID}:{regionID}#{versionID}.
     * If the group is 'TZDB' then the {groupID}: is omitted.
     * If the version is floating, then the #{versionID} is omitted.
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

    /**
     * Gets the time-zone rules group version, such as '{@code 2009b}.
     * <p>
     * The version ID is the third part of the {@link #getID() full unique ID}.
     * Time zone rules change over time as governments change the associated laws.
     * The time-zone groups capture these changes by issuing multiple versions
     * of the data. An application can reference the exact set of rules used
     * by using the group ID and version. Once loaded, there is no way to unload
     * a version of the rules, however new versions may be added.
     * <p>
     * The version can be an empty string which represents the floating version.
     * This always uses the latest version of the rules available.
     * <p>
     * For fixed time-zones, the version ID will be an empty string.
     *
     * @return the time-zone rules version ID, empty if the version is floating, not null
     */
    public abstract String getVersionID();

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
     * Checks if the version is floating.
     * <p>
     * A floating version will track the latest available version of the rules.
     * <p>
     * For group based time-zones, this returns true if the version ID is empty,
     * which is the definition of a floating zone.
     * <p>
     * For fixed time-zones, true is returned as the data is always the latest.
     *
     * @return true if the version is floating
     */
    public boolean isFloatingVersion() {
        return getVersionID().length() == 0;
    }

    /**
     * Returns a copy of this time-zone with a floating version.
     * <p>
     * For group based time-zones, this returns a {@code ZoneId} with the
     * same group and region, but a floating version.
     * The group and region IDs are not validated.
     * <p>
     * For fixed time-zones, {@code this} is returned.
     *
     * @return the new updated time-zone, not null
     * @throws CalendricalException if the time-zone is fixed
     */
    public abstract ZoneId withFloatingVersion();

    //-----------------------------------------------------------------------
    /**
     * Checks if the version is the latest version.
     * <p>
     * For floating group based time-zones, true is returned.
     * <p>
     * For non-floating group based time-zones, this returns true if the version
     * stored is the same as the latest version available for the group and region.
     * The group and region IDs are validated in order to calculate the latest version.
     * <p>
     * For fixed time-zones, true is returned.
     *
     * @return true if the version is the latest available
     * @throws CalendricalException if the version is non-floating and the group or region ID is not found
     */
    public abstract boolean isLatestVersion();

    /**
     * Returns a copy of this time-zone with the latest available version ID.
     * <p>
     * For floating and non-floating group based time-zones, this returns a zone with the same
     * group and region, but the latest version that has been registered.
     * The group and region IDs are validated in order to calculate the latest version.
     * <p>
     * For fixed time-zones, {@code this} is returned.
     *
     * @return the new updated time-zone, not null
     * @throws CalendricalException if the version is non-floating and the group or region ID is not found
     */
    public abstract ZoneId withLatestVersion();

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this time-zone with the specified version ID.
     * <p>
     * For group based time-zones, this returns a {@code ZoneId}
     * with the same group and region, but the specified version.
     * The group and region IDs are validated to ensure that the version is valid.
     * <p>
     * For fixed time-zones, the version must be an empty string, otherwise an
     * exception is thrown.
     *
     * @param versionID  the version ID to use, empty means floating version, not null
     * @return the new updated time-zone, not null
     * @throws CalendricalException if the time-zone is fixed and the version is not empty
     * @throws CalendricalException if the group, region or version ID is not found
     */
    public abstract ZoneId withVersion(String versionID);

    /**
     * Returns a copy of this time-zone with the latest version that is valid
     * for the specified date-time and offset.
     * <p>
     * This will search for a version of the time-zone rules that would make the specified
     * date-time valid. This is needed for cases where the time-zone changes and you hold
     * a reference to a date-time created before the rules changed.
     * <p>
     * This method validates the group and region IDs.
     *
     * @param dateTime  the date-time to get the latest version for
     * @return the new updated time-zone, not null
     * @throws CalendricalException if the group or region ID is not found
     * @throws CalendricalException if there are no valid rules for the date-time
     */
    public abstract ZoneId withLatestVersionValidFor(OffsetDateTime dateTime);

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
     * @return the time-zone rules group ID, not null
     * @throws CalendricalException if the time-zone is fixed
     * @throws CalendricalException if the group ID cannot be found
     */
    public abstract ZoneRulesGroup getGroup();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it.
     * <p>
     * This will return true if the rules are available for the group, region
     * and version ID combination. If this method returns true, then
     * {@link #getRules()} will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     * <p>
     * If this is a fixed time-zone, then it is always valid.
     *
     * @return true if this time-zone is valid and rules are available
     */
    public abstract boolean isValid();

    /**
     * Gets the time-zone rules allowing calculations to be performed.
     * <p>
     * The rules provide the functionality associated with a time-zone,
     * such as finding the offset for a given instant or local date-time.
     * Different rules may be returned depending on the group, version and zone.
     * <p>
     * If this object declares a specific version of the rules, then the result will
     * be of that version. If this object declares a floating version of the rules,
     * then the latest version available will be returned.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
     *
     * @return the rules, not null
     * @throws CalendricalException if the group, region or version ID cannot be found
     */
    public abstract ZoneRules getRules();

    //-----------------------------------------------------------------------
    /**
     * Checks if this time-zone is valid such that rules can be obtained for it
     * which are valid for the specified date-time and offset.
     * <p>
     * This will return true if the rules are available for the group, region
     * and version ID combination that are valid for the specified date-time.
     * If this method returns true, then {@link #getRulesValidFor(OffsetDateTime)}
     * will return a valid rules instance.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
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
     * Different rules may be returned depending on the group, version and zone.
     * <p>
     * If this object declares a specific version of the rules, then the result will
     * be of that version providing that the specified date-time is valid for those rules.
     * If this object declares a floating version of the rules, then the latest
     * version of the rules where the date-time is valid will be returned.
     * <p>
     * A time-zone can be invalid if it is deserialized in a JVM which does not
     * have the same rules loaded as the JVM that stored it. In this case, calling
     * this method will throw an exception.
     * <p>
     * If this object declares a floating version of the rules and a background
     * thread is used to update the available rules, then the result of calling
     * this method may vary over time.
     * Each individual call will be still remain thread-safe.
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
                    getVersionID().equals(other.getVersionID()) &&
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
        return getGroupID().hashCode() ^ getRegionID().hashCode() ^ getVersionID().hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs this offset as a {@code String}, using the ID.
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
        /** The time-zone version ID, not null. */
        private final String versionID;

        /**
         * Constructor.
         *
         * @param groupID  the time-zone rules group ID, not null
         * @param regionID  the time-zone region ID, not null
         * @param versionID  the time-zone rules version ID, not null
         */
        ID(String groupID, String regionID, String versionID) {
            this.groupID = groupID;
            this.regionID = regionID;
            this.versionID = versionID;
        }

        /**
         * Validate deserialization.
         *
         * @param in  the input stream
         */
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            if (groupID == null || groupID.length() == 0 || regionID == null || versionID == null) {
                throw new StreamCorruptedException();
            }
        }

        //-----------------------------------------------------------------------
        @Override
        public String getID() {
            if (groupID.equals("TZDB")) {
                return regionID + (versionID.length() == 0 ? "" : '#' + versionID);
            }
            return groupID + ':' + regionID + (versionID.length() == 0 ? "" : '#' + versionID);
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
        public String getVersionID() {
            return versionID;
        }

        @Override
        public boolean isFixedOffset() {
            return false;
        }

        @Override
        public ZoneId withFloatingVersion() {
            if (isFloatingVersion()) {
                return this;
            }
            return new ID(groupID, regionID, "");
        }

        @Override
        public boolean isLatestVersion() {
            return isFloatingVersion() ||
                    versionID.equals(getGroup().getLatestVersionID(regionID));  // validates IDs
        }

        @Override
        public ZoneId withLatestVersion() {
            String versionID = getGroup().getLatestVersionID(regionID);  // validates IDs
            if (versionID.equals(this.versionID)) {
                return this;
            }
            return new ID(groupID, regionID, versionID);
        }

        @Override
        public ZoneId withVersion(String versionID) {
            MathUtils.checkNotNull(versionID, "Version ID must not be null");
            if (versionID.length() == 0) {
                return withFloatingVersion();
            }
            if (getGroup().isValidRules(regionID, versionID) == false) {
                throw new CalendricalException("Unknown version: " + groupID + ":" + regionID + '#' + versionID);
            }
            if (versionID.equals(this.versionID)) {
                return this;
            }
            return new ID(groupID, regionID, versionID);
        }

        @Override
        public ZoneId withLatestVersionValidFor(OffsetDateTime dateTime) {
            MathUtils.checkNotNull(dateTime, "OffsetDateTime must not be null");
            return withVersion(getGroup().getLatestVersionIDValidFor(regionID, dateTime));
        }

        @Override
        public ZoneRulesGroup getGroup() {
            return ZoneRulesGroup.getGroup(groupID);
        }

        @Override
        public boolean isValid() {
            if (isFloatingVersion()) {
                return ZoneRulesGroup.isValidGroupID(groupID) && getGroup().isValidRegionID(regionID);
            }
            return ZoneRulesGroup.isValidGroupID(groupID) && getGroup().isValidRules(regionID, versionID);
        }

        @Override
        public ZoneRules getRules() {
            ZoneRulesGroup group = getGroup();
            if (isFloatingVersion()) {
                return group.getRules(regionID, group.getLatestVersionID(regionID));
            }
            return group.getRules(regionID, versionID);
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
            MathUtils.checkNotNull(dateTime, "OffsetDateTime must not be null");
            ZoneRulesGroup group = getGroup();
            if (isFloatingVersion()) {
                return group.getRules(regionID, group.getLatestVersionIDValidFor(regionID, dateTime));
            }
            return group.getRulesValidFor(regionID, versionID, dateTime);
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
            this.offsetInfo = ZoneOffsetInfo.of(offset, null);
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
        public String getVersionID() {
            return "";
        }

        @Override
        public boolean isFloatingVersion() {
            return true;
        }

        @Override
        public ZoneId withFloatingVersion() {
            return this;
        }

        @Override
        public boolean isLatestVersion() {
            return true;
        }

        @Override
        public ZoneId withLatestVersion() {
            return this;
        }

        @Override
        public ZoneId withVersion(String versionID) {
            MathUtils.checkNotNull(versionID, "Version ID must not be null");
            if (versionID.length() > 0) {
                throw new CalendricalException("Fixed time-zone does not provide versions");
            }
            return this;
        }

        @Override
        public ZoneId withLatestVersionValidFor(OffsetDateTime dateTime) {
            getRulesValidFor(dateTime);  // validation
            return this;
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
            MathUtils.checkNotNull(dateTime, "OffsetDateTime must not be null");
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
            return Period.ZERO;
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
