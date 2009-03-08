/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import java.io.Serializable;
import java.util.Map;

import javax.time.CalendricalException;
import javax.time.calendar.zone.ZoneRules;
import javax.time.calendar.zone.ZoneRulesGroup;
import javax.time.calendar.zone.ZoneRulesGroupVersion;

/**
 * A time zone representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * Time zones are geographical regions where the same rules for time apply.
 * The rules are defined by governments and change frequently.
 * <p>
 * There are a number of sources of time zone information available,
 * each represented by an instance of {@link ZoneRulesGroup}.
 * Two IDs are provided as standard - 'Fixed' and 'TZDB' - and more can be added.
 * <p>
 * Each group typically produces multiple versions of their data, which
 * is represented by {@link ZoneRulesGroupVersion}.
 * The format of the version is specific to the group.
 * For example, the 'TZDB' group use the format {year}{letter}, such as '2009b'.
 * <p>
 * Each group also has its own naming scheme for the time zone themselves.
 * This is expressed as the time zone ID. For example, the 'TZDB' group
 * typically use the format {area}/{city}, such as 'Europe/London'.
 * <p>
 * In combination, a unique key is created expressing the time-zone, formed from
 * {groupID}/{versionID}:{locationID}. The version and preceding slash are optional.
 * <p>
 * The purpose of capturing all this information is to handle issues when
 * manipulating and persisting time zones. For example, consider what happens if the
 * government of a country changed the start or end of daylight savings time.
 * If you created and stored a date using the old rules, and then load it up
 * when the new rules are in force, what should happen? The date might now be
 * invalid (due to a gap in the local time-line). By storing the version of the
 * time zone rules data together with the date, it is possible to tell that the
 * rules have changed and to process accordingly.
 * <p>
 * TimeZone is immutable and thread-safe.
 *
 * @author Stephen Colebourne
 */
public final class TimeZone implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 93618758758127L;
    /**
     * The time zone offset for UTC, with an id of 'UTC'.
     */
    public static final TimeZone UTC = new TimeZone("Fixed", "", "UTC", ZoneRules.fixed(ZoneOffset.UTC));

    /**
     * The zone rules group ID.
     */
    private final String groupID;
    /**
     * The zone rules version.
     */
    private final String versionID;
    /**
     * The time zone ID.
     */
    private final String locationID;
    /**
     * The time zone rules.
     */
    private transient volatile ZoneRules rules;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeZone</code> using its ID using a map
     * of aliases to supplement the standard zone IDs.
     * <p>
     * Many users of time zones use short abbreviations, such as PST for
     * 'Pacific Standard Time' and PDT for 'Pacific Daylight Time'.
     * These abbreviations are not unique, and so cannot be used as identifiers.
     * This method allows a map of string to time zone to be setup and reused
     * within an application.
     *
     * @param timeZoneIdentifier  the time zone id, not null
     * @param aliasMap  a map of time zone IDs (typically abbreviations) to time zones, not null
     * @return the TimeZone, never null
     * @throws IllegalArgumentException if the time zone cannot be found
     */
    public static TimeZone timeZone(String timeZoneIdentifier, Map<String, TimeZone> aliasMap) {
        // TODO: review
        ISOChronology.checkNotNull(timeZoneIdentifier, "Time Zone ID must not be null");
        ISOChronology.checkNotNull(aliasMap, "Alias map must not be null");
        TimeZone zone = aliasMap.get(timeZoneIdentifier);
        return zone == null ? timeZone(timeZoneIdentifier) : zone;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> from an identifier.
     * <p>
     * Four forms of identifier are recognized:
     * <ul>
     * <li><code>{groupID}/{version}:{locationID}</code> - full
     * <li><code>{groupID}:{locationID}</code> - implies latest available version
     * <li><code>{locationID} - implies 'TZDB' group and latest available version
     * <li><code>UTC{offset} - implies 'Fixed' group
     * </ul>
     * <p>
     * Most of the formats are based around the group, version and location IDs.
     * The version and location ID formats are specific to the group.
     * If a group does not support versioning, then the version must be an empty string.
     * <p>
     * The default group is 'TZDB' which has versions of the form {year}{letter}, such as '2009b'.
     * The location ID for the 'TZDB' group is generally of the form '{area}/{city}', such as 'Europe/Paris'.
     * This is compatible with most IDs from {@link java.util.TimeZone}.
     * <p>
     * For example, if a provider is loaded with the ID 'MyProvider' containing a zone ID of
     * 'France', then the unique key for version 2.1 would be 'MyProvider/2.1:France'.
     * A specific version of the TZDB provider can be specified using this format,
     * for example 'TZDB/2008g:Asia/Tokyo'.
     * <p>
     * The alternate format are fixed zones, where the offset never changes over time.
     * It is intended that {@link ZoneOffset} and {@link OffsetDateTime} are used in preference,
     * however sometimes it is necessary to have a fixed time zone.
     * The 'Fixed' group is used if the first three characters are 'UTC'.
     * The remainder of the ID must be a valid format for {@link ZoneOffset#zoneOffset(String)}.
     * Using 'UTCZ' is valid, but discouraged in favor of 'UTC'.
     * The full unique key is 'Fixed:UTC&plusmn;hh:mm:ss'.
     *
     * @param timeZoneIdentifier  the time zone identifier, not null
     * @return the TimeZone, never null
     * @throws IllegalArgumentException if the time zone cannot be found
     */
    public static TimeZone timeZone(String timeZoneIdentifier) {
        ISOChronology.checkNotNull(timeZoneIdentifier, "Time Zone ID must not be null");
        if (timeZoneIdentifier.equals("UTC")) {
            return UTC;
        } else if (timeZoneIdentifier.startsWith("UTC") || timeZoneIdentifier.startsWith("GMT")) {  // not sure about GMT
            return timeZone(ZoneOffset.zoneOffset(timeZoneIdentifier.substring(3)));
        } else {
            int pos = timeZoneIdentifier.indexOf(':');
            ZoneRulesGroupVersion gv;
            if (pos >= 0) {
                gv = ZoneRulesGroup.getGroupVersion(timeZoneIdentifier.substring(0, pos));
            } else {
                gv = ZoneRulesGroup.getGroupVersion("TZDB");
            }
            String tzid = timeZoneIdentifier.substring(pos + 1);
            ZoneRules zoneRules = gv.getZoneRules(tzid);
            return new TimeZone(gv.getGroup().getID(), gv.getID(), tzid, zoneRules);
        }
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using an offset.
     *
     * @param offset  the zone offset, not null
     * @return the TimeZone for the offset, never null
     */
    public static TimeZone timeZone(ZoneOffset offset) {
        ISOChronology.checkNotNull(offset, "ZoneOffset must not be null");
        if (offset == ZoneOffset.UTC) {
            return UTC;
        }
        String timeZoneID = "UTC" + offset.getID();
        ZoneRules zoneRules = ZoneRules.fixed(offset);  //ZoneRulesGroup.getGroupVersion("Fixed").getZoneRules(timeZoneID);
        return new TimeZone("Fixed", "", timeZoneID, zoneRules);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param groupID  the time zone rules group ID, not null
     * @param groupVersionID  the time zone rules group version ID, not null
     * @param locationID  the time zone location ID, not null
     */
    private TimeZone(String groupID, String groupVersionID, String locationID, ZoneRules rules) {
        super();
        this.groupID = groupID;
        this.versionID = groupVersionID;
        this.locationID = locationID;
        this.rules = rules;
    }

    /**
     * Handle UTC on deserialization.
     *
     * @return the resolved instance, never null
     */
    private Object readResolve() {
        return (this.equals(UTC) ? UTC : this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the unique time zone ID.
     * <p>
     * The unique key is created from the group ID, version ID and location ID.
     * The format is {groupID}/{versionID}:{locationID}.
     * If the group does not provide versioned data then the format is {groupID}:{locationID}.
     * If the group is 'Fixed', then the format is {locationID}.
     *
     * @return the time zone unique ID, never null
     */
    public String getID() {
        if (groupID.equals("Fixed")) {
            return locationID;
        }
        return groupID + (versionID.length() == 0 ? "" : "/" + versionID) + ":" + locationID;
    }

    /**
     * Gets the time zone rules group ID, such as 'TZDB'.
     * <p>
     * Time zone rules are provided by groups referenced by an ID.
     *
     * @return the time zone rules group ID, never null
     */
    public String getGroupID() {
        return groupID;
    }

    /**
     * Gets the time zone rules group version, such as '2009b'.
     * <p>
     * Time zone rules change over time as governments change the associated laws.
     * The time zone groups capture these changes by issuing multiple versions
     * of the data. An application can reference the exact set of rules used
     * by using the group ID and version.
     *
     * @return the time zone rules version ID, never null
     */
    public String getVersionID() {
        return versionID;
    }

    /**
     * Gets the time zone location identifier, such as 'Europe/London'.
     * <p>
     * The time zone location identifier is of a format specific to the group.
     * The default 'TZDB' group generally uses the format {area}/{city}, such as 'Europe/Paris'.
     *
     * @return the time zone rules location ID, never null
     */
    public String getLocationID() {
        return locationID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone rules allowing calculations to be performed.
     * <p>
     * The rules provide the functionality associated with a time zone,
     * such as finding the offset for a given instant or local date-time.
     * Different rules may be returned depending on the group, version and zone.
     * <p>
     * Callers of this method need to be aware of an unusual scenario.
     * It is possible to create a <code>TimeZone</code> instance even when the
     * rules are not available. This typically occurs when a <code>TimeZone</code>
     * is loaded from a previously stored version but the rules are not available.
     * In this case, the <code>TimeZone</code> instance is still valid, as is
     * any associated object, such as {@link ZonedDateTime}. It is impossible to
     * perform any calculations that require the rules however, and this method
     * will throw an exception.
     * <p>
     * A related aspect of serialization is that this class just stores the
     * unique identifier of a time zone, while serializing <code>ZoneRules</code>
     * will actually store the entire set of rules.
     *
     * @return the rules, never null
     * @throws CalendricalException if the zone is unknown or cannot be loaded
     */
    public ZoneRules getRules() {
        ZoneRules r = rules;
        if (r == null) {
            try {
                r = ZoneRulesGroup.getGroup(groupID).getVersion(versionID).getZoneRules(locationID);
            } catch (IllegalArgumentException ex) {
                // TODO: separate exception, as recoverable
                throw new CalendricalException("Unable to load zone rules: " + getID(), ex);
            }
            rules = r;
        }
        return r;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual name of this zone.
     *
     * @return the time zone name, never null
     */
    public String getName() {
        return locationID;  // TODO
    }

    /**
     * Gets the short textual name of this zone.
     *
     * @return the time zone short name, never null
     */
    public String getShortName() {
        return locationID;  // TODO
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets the offset applicable at the specified instant in this zone.
//     * <p>
//     * For any given instant there can only ever be one valid offset, which
//     * is returned by this method. To access more detailed information about
//     * the offset at and around the instant use {@link #getOffsetInfo(Instant)}.
//     *
//     * @param instantProvider  the instant provider to find the offset for, not null
//     * @return the offset, never null
//     */
//    public ZoneOffset getOffset(InstantProvider instantProvider) {
//        return getRules().getOffset(instantProvider);
//    }
//
//    /**
//     * Gets the offset information for the specified instant in this zone.
//     * <p>
//     * This provides access to full details as to the offset or offsets applicable
//     * for the local date-time. The mapping from an instant to an offset
//     * is not straightforward. There are two cases:
//     * <ul>
//     * <li>Normal. Where there is a single offset for the local date-time.</li>
//     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
//     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
//     * </ul>
//     * The third case, a gap in the local time-line, cannot be returned by this
//     * method as an instant will always represent a valid point and cannot be in a gap.
//     * The returned object provides information about the offset or overlap and it
//     * is vital to check {@link OffsetInfo#isDiscontinuity()} to handle the overlap.
//     *
//     * @param instant  the instant to find the offset information for, not null
//     * @return the offset information, never null
//     */
//    public OffsetInfo getOffsetInfo(Instant instant) {
//        ZoneOffset offset = getOffset(instant);
//        OffsetDateTime odt = OffsetDateTime.fromInstant(instant, offset);
//        return getOffsetInfo(odt.toLocalDateTime());
//    }
//
//    /**
//     * Gets the offset information for a local date-time in this zone.
//     * <p>
//     * This provides access to full details as to the offset or offsets applicable
//     * for the local date-time. The mapping from a local date-time to an offset
//     * is not straightforward. There are three cases:
//     * <ul>
//     * <li>Normal. Where there is a single offset for the local date-time.</li>
//     * <li>Gap. Where there is a gap in the local time-line normally caused by the
//     * spring cutover to daylight savings. There are no valid offsets within the gap</li>
//     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
//     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
//     * </ul>
//     * The returned object provides this information and it is vital to check
//     * {@link OffsetInfo#isDiscontinuity()} to handle the gap or overlap.
//     *
//     * @param dateTime  the date-time to find the offset information for, not null
//     * @return the offset information, never null
//     */
//    public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
//        return getRules().getOffsetInfo(dateTime);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Gets the standard offset for the specified instant in this zone.
//     * <p>
//     * This provides access to historic information on how the standard offset
//     * has changed over time.
//     * The standard offset is the offset before any daylight savings time is applied.
//     * This is typically the offset applicable during winter.
//     *
//     * @param instantProvider  the instant to find the offset information for, not null
//     * @return the standard offset, never null
//     */
//    public ZoneOffset getStandardOffset(InstantProvider instantProvider) {
//        return getRules().getStandardOffset(instantProvider);
//    }
//
//    /**
//     * Gets the amount of daylight savings in use for the specified instant in this zone.
//     * <p>
//     * This provides access to historic information on how the amount of daylight
//     * savings has changed over time.
//     * This is the difference between the standard offset and the actual offset.
//     * It is expressed in hours, minutes and seconds.
//     * Typically the amount is zero during winter and one hour during summer.
//     *
//     * @param instantProvider  the instant to find the offset information for, not null
//     * @return the standard offset, never null
//     */
//    public Period getDaylightSavings(InstantProvider instantProvider) {
//        Instant instant = Instant.instant(instantProvider);
//        ZoneOffset standardOffset = getStandardOffset(instant);
//        ZoneOffset actualOffset = getOffset(instant);
//        return actualOffset.toPeriod().minus(standardOffset.toPeriod()).normalized();
//    }
//
//    /**
//     * Gets the standard offset for the specified instant in this zone.
//     * <p>
//     * This provides access to historic information on how the standard offset
//     * has changed over time.
//     * The standard offset is the offset before any daylight savings time is applied.
//     * This is typically the offset applicable during winter.
//     *
//     * @param instant  the instant to find the offset information for, not null
//     * @return the standard offset, never null
//     */
//    public boolean isDaylightSavings(InstantProvider instant) {
//        return (getStandardOffset(instant).equals(getOffset(instant)) == false);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Is this time zone fixed, such that the offset never varies.
//     * <p>
//     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
//     * {@link OffsetTime} are used in preference to fixed offset time zones
//     * in {@link ZonedDateTime}.
//     * <p>
//     * The default implementation returns false and it is not intended that
//     * user-supplied subclasses override this.
//     *
//     * @return true if the time zone is fixed and the offset never changes
//     */
//    public boolean isFixed() {
//        return false;
//    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
     *
     * @param otherZone  the other zone, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherZone) {
        if (this == otherZone) {
           return true;
        }
        if (otherZone instanceof TimeZone) {
            TimeZone zone = (TimeZone) otherZone;
            return locationID.equals(zone.locationID) &&
                    versionID.equals(zone.versionID) &&
                    groupID.equals(zone.groupID);
        }
        return false;
    }

    /**
     * A hash code for the time zone object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return locationID.hashCode() ^ versionID.hashCode() ^ groupID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time zone using the unique time zone key.
     *
     * @return the unique time zone key, never null
     */
    @Override
    public String toString() {
        return getID();
    }

}
