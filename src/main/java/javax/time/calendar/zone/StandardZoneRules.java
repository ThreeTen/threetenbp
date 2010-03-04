/*
 * Copyright (c) 2009-2010, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar.zone;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.Year;
import javax.time.calendar.ZoneOffset;

/**
 * The rules describing how the zone offset varies through the year and historically.
 * <p>
 * ZoneRules is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
final class StandardZoneRules extends ZoneRules implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 224698619L;
    /**
     * The last year to have its transitions cached.
     */
    private static final Year LAST_CACHED_YEAR = Year.of(2100);

    /**
     * The transitions between standard offsets (epoch seconds), sorted.
     */
    private final long[] standardTransitions;
    /**
     * The standard offsets.
     */
    private final ZoneOffset[] standardOffsets;
    /**
     * The transitions between instants (epoch seconds), sorted.
     */
    private final long[] savingsInstantTransitions;
    /**
     * The transitions between local date-times, sorted.
     * This is a paired array, where the first entry is the start of the transition
     * and the second entry is the end of the transition.
     */
    private final LocalDateTime[] savingsLocalTransitions;  // TODO: transient
    /**
     * The wall offsets.
     */
    private final ZoneOffset[] wallOffsets;
    /**
     * The last rule.
     */
    private final ZoneOffsetTransitionRule[] lastRules;
    /**
     * The map of recent transitions.
     */
    private transient volatile ConcurrentMap<Year, ZoneOffsetTransition[]> lastRulesCache =
                new ConcurrentHashMap<Year, ZoneOffsetTransition[]>();

    /**
     * Constructor.
     *
     * @param baseStandardOffset  the standard offset to use before legal rules were set, not null
     * @param baseWallOffset  the wall offset to use before legal rules were set, not null
     * @param standardOffsetTransitionList  the list of changes to the standard offset, not null
     * @param transitionList  the list of transitions, not null
     * @param lastRules  the recurring last rules, not null
     */
    StandardZoneRules(
            ZoneOffset baseStandardOffset,
            ZoneOffset baseWallOffset,
            List<OffsetDateTime> standardOffsetTransitionList,
            List<ZoneOffsetTransition> transitionList,
            List<ZoneOffsetTransitionRule> lastRules) {
        super();
        
        // convert standard transitions
        this.standardTransitions = new long[standardOffsetTransitionList.size()];
        this.standardOffsets = new ZoneOffset[standardOffsetTransitionList.size() + 1];
        this.standardOffsets[0] = baseStandardOffset;
        for (int i = 0; i < standardOffsetTransitionList.size(); i++) {
            this.standardTransitions[i] = standardOffsetTransitionList.get(i).toEpochSeconds();
            this.standardOffsets[i + 1] = standardOffsetTransitionList.get(i).getOffset();
        }
        
        // convert savings transitions to locals
        List<LocalDateTime> localTransitionList = new ArrayList<LocalDateTime>();
        List<ZoneOffset> localTransitionOffsetList = new ArrayList<ZoneOffset>();
        localTransitionOffsetList.add(baseWallOffset);
        for (ZoneOffsetTransition trans : transitionList) {
            if (trans.isGap()) {
                localTransitionList.add(trans.getDateTime().toLocalDateTime());
                localTransitionList.add(trans.getDateTimeAfter().toLocalDateTime());
            } else {
                localTransitionList.add(trans.getDateTimeAfter().toLocalDateTime());
                localTransitionList.add(trans.getDateTime().toLocalDateTime());
            }
            localTransitionOffsetList.add(trans.getOffsetAfter());
        }
        this.savingsLocalTransitions = localTransitionList.toArray(new LocalDateTime[localTransitionList.size()]);
        this.wallOffsets = localTransitionOffsetList.toArray(new ZoneOffset[localTransitionOffsetList.size()]);
        
        // convert savings transitions to instants
        this.savingsInstantTransitions = new long[transitionList.size()];
        for (int i = 0; i < transitionList.size(); i++) {
            this.savingsInstantTransitions[i] = transitionList.get(i).getInstant().getEpochSeconds();
        }
        
        // last rules
        this.lastRules = lastRules.toArray(new ZoneOffsetTransitionRule[lastRules.size()]);
    }

    /**
     * Reinstate the cache.
     *
     * @param in  the input
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        lastRulesCache = new ConcurrentHashMap<Year, ZoneOffsetTransition[]>();
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Resolves singletons.
//     * @return the singleton instance
//     */
//    private Object readResolve() {
//        return TimeZone.timeZone(getID());
//    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public ZoneOffset getOffset(InstantProvider instantProvider) {
        Instant instant = Instant.from(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        
        // check if using last rules
        if (lastRules.length > 0 &&
                epochSecs > savingsInstantTransitions[savingsInstantTransitions.length - 1]) {
            OffsetDateTime dt = OffsetDateTime.fromInstant(instant, wallOffsets[wallOffsets.length - 1]);
            ZoneOffsetTransition[] transArray = findTransitionArray(dt.toYear());
            ZoneOffsetTransition trans = null;
            for (int i = 0; i < transArray.length; i++) {
                trans = transArray[i];
                if (instant.isBefore(trans.getInstant())) {
                    return trans.getOffsetBefore();
                }
            }
            return trans.getOffsetAfter();
        }
        
        // using historic rules
        int index  = Arrays.binarySearch(savingsInstantTransitions, epochSecs);
        if (index < 0) {
            // switch negative insert position to start of matched range
            index = -index - 2;
        }
        return wallOffsets[index + 1];
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public ZoneOffsetInfo getOffsetInfo(LocalDateTime dt) {
        // check if using last rules
        if (lastRules.length > 0 &&
                dt.isAfter(savingsLocalTransitions[savingsLocalTransitions.length - 1])) {
            ZoneOffsetTransition[] transArray = findTransitionArray(dt.toYear());
            ZoneOffsetInfo info = null;
            for (ZoneOffsetTransition trans : transArray) {
                info = findOffsetInfo(dt, trans);
                if (info.isTransition() || info.getOffset().equals(trans.getOffsetBefore())) {
                    return info;
                }
            }
            return info;
        }
        
        // using historic rules
        int index  = Arrays.binarySearch(savingsLocalTransitions, dt);
        if (index == -1) {
            // before first transition
            return createOffsetInfo(dt, wallOffsets[0]);
        }
        if (index < 0) {
            // switch negative insert position to start of matched range
            index = -index - 2;
        } else if (index < savingsLocalTransitions.length - 1 &&
                savingsLocalTransitions[index].equals(savingsLocalTransitions[index + 1])) {
            // handle overlap immediately following gap
            index++;
        }
        if ((index & 1) == 0) {
            // gap or overlap
            LocalDateTime dtBefore = savingsLocalTransitions[index];
            LocalDateTime dtAfter = savingsLocalTransitions[index + 1];
            ZoneOffset offsetBefore = wallOffsets[index / 2];
            ZoneOffset offsetAfter = wallOffsets[index / 2 + 1];
            if (offsetAfter.getAmountSeconds() > offsetBefore.getAmountSeconds()) {
                // gap
                return createOffsetInfo(dt, OffsetDateTime.from(dtBefore, offsetBefore), offsetAfter);
            } else {
                // overlap
                return createOffsetInfo(dt, OffsetDateTime.from(dtAfter, offsetBefore), offsetAfter);
            }
        } else {
            // normal (neither gap or overlap)
            return createOffsetInfo(dt, wallOffsets[index / 2 + 1]);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Finds the offset info for a local date-time and transition.
     *
     * @param dt  the date-time, not null
     * @param trans  the transition, not null
     * @return the offset info, never null
     */
    private ZoneOffsetInfo findOffsetInfo(LocalDateTime dt, ZoneOffsetTransition trans) {
        if (trans.isGap()) {
            if (dt.isBefore(trans.getLocal())) {
                return createOffsetInfo(dt, trans.getOffsetBefore());
            }
            if (dt.isBefore(trans.getDateTimeAfter().toLocalDateTime())) {
                return createOffsetInfo(dt, trans.getDateTime(), trans.getOffsetAfter());
            } else {
                return createOffsetInfo(dt, trans.getOffsetAfter());
            }
        } else {
            if (dt.isBefore(trans.getLocal()) == false) {
                return createOffsetInfo(dt, trans.getOffsetAfter());
            }
            if (dt.isBefore(trans.getDateTimeAfter().toLocalDateTime())) {
                return createOffsetInfo(dt, trans.getOffsetBefore());
            } else {
                return createOffsetInfo(dt, trans.getDateTime(), trans.getOffsetAfter());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Finds the appropriate transition array for the given year.
     *
     * @param year  the year, not null
     * @return the transition array, never null
     */
    private ZoneOffsetTransition[] findTransitionArray(Year year) {
        ZoneOffsetTransition[] transArray = lastRulesCache.get(year);
        if (transArray != null) {
            return transArray;
        }
        ZoneOffsetTransitionRule[] ruleArray = lastRules;
        transArray  = new ZoneOffsetTransition[ruleArray.length];
        for (int i = 0; i < ruleArray.length; i++) {
            transArray[i] = ruleArray[i].createTransition(year.getValue());
        }
        if (year.isBefore(LAST_CACHED_YEAR)) {
            lastRulesCache.putIfAbsent(year, transArray);
        }
        return transArray;
    }

    //-----------------------------------------------------------------------
    /** {@inheritDoc} */
    @Override
    public ZoneOffset getStandardOffset(InstantProvider instantProvider) {
        Instant instant = Instant.from(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        int index  = Arrays.binarySearch(standardTransitions, epochSecs);
        if (index < 0) {
            // switch negative insert position to start of matched range
            index = -index - 2;
        }
        return standardOffsets[index + 1];
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the next transition after the specified transition.
     *
     * @param instantProvider  the instant to get the next transition after, not null
     * @return the next transition after the specified instant, null if this is after the last transition
     */
    @Override
    public ZoneOffsetTransition nextTransition(InstantProvider instantProvider) {
        Instant instant = Instant.from(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        
        // check if using last rules
        if (epochSecs >= savingsInstantTransitions[savingsInstantTransitions.length - 1]) {
            if (lastRules.length == 0) {
                return null;
            }
            OffsetDateTime dt = OffsetDateTime.fromInstant(instant, wallOffsets[wallOffsets.length - 1]);
            for (Year year = dt.toYear(); true; year = year.next()) {
                ZoneOffsetTransition[] transArray = findTransitionArray(year);
                for (ZoneOffsetTransition trans : transArray) {
                    if (instant.isBefore(trans.getInstant())) {
                        return trans;
                    }
                }
                if (year.getValue() == Year.MAX_YEAR) {
                    return null;
                }
            }
        }
        
        // using historic rules
        int index  = Arrays.binarySearch(savingsInstantTransitions, epochSecs);
        if (index < 0) {
            index = -index - 1;  // switched value is the next transition
        } else {
            index += 1;  // exact match, so need to add one to get the next
        }
        Instant transitionInstant = Instant.seconds(savingsInstantTransitions[index]);
        OffsetDateTime trans = OffsetDateTime.fromInstant(transitionInstant, wallOffsets[index]);
        return createTransition(trans, wallOffsets[index + 1]);
    }

    /**
     * Gets the previous transition after the specified transition.
     *
     * @param instantProvider  the instant to get the previous transition after, not null
     * @return the previous transition after the specified instant, null if this is before the first transition
     */
    @Override
    public ZoneOffsetTransition previousTransition(InstantProvider instantProvider) {
        Instant instant = Instant.from(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        if (instant.getNanoOfSecond() > 0 && epochSecs < Long.MAX_VALUE) {
            epochSecs += 1;  // allow rest of method to only use seconds
        }
        
        // check if using last rules
        long lastHistoric = savingsInstantTransitions[savingsInstantTransitions.length - 1];
        if (lastRules.length > 0 && epochSecs > lastHistoric) {
            ZoneOffset lastHistoricOffset = wallOffsets[wallOffsets.length - 1];
            OffsetDateTime dt = OffsetDateTime.fromInstant(instant, lastHistoricOffset);
            OffsetDateTime lastHistoricDT = OffsetDateTime.fromInstant(Instant.seconds(lastHistoric), lastHistoricOffset);
            for (Year year = dt.toYear(); year.getValue() > lastHistoricDT.getYear(); year = year.previous()) {
                ZoneOffsetTransition[] transArray = findTransitionArray(year);
                for (int i = transArray.length - 1; i >= 0; i--) {
                    if (instant.isAfter(transArray[i].getInstant())) {
                        return transArray[i];
                    }
                }
            }
        }
        
        // using historic rules
        int index  = Arrays.binarySearch(savingsInstantTransitions, epochSecs);
        if (index < 0) {
            index = -index - 1;
        }
        if (index <= 0) {
            return null;
        }
        Instant transitionInstant = Instant.seconds(savingsInstantTransitions[index - 1]);
        OffsetDateTime trans = OffsetDateTime.fromInstant(transitionInstant, wallOffsets[index - 1]);
        return createTransition(trans, wallOffsets[index]);
    }

    /**
     * Gets the complete list of transitions.
     * <p>
     * This list normally contains a complete historical set of transitions
     * that have occurred. Some transitions may be in the future, although
     * generally the transition rules handle future years.
     *
     * @return independent, modifiable copy of the list of transitions, never null
     */
    @Override
    public List<ZoneOffsetTransition> getTransitions() {
        List<ZoneOffsetTransition> list = new ArrayList<ZoneOffsetTransition>();
        for (int i = 0; i < savingsInstantTransitions.length; i++) {
            Instant instant = Instant.seconds(savingsInstantTransitions[i]);
            OffsetDateTime trans = OffsetDateTime.fromInstant(instant, wallOffsets[i]);
            list.add(createTransition(trans, wallOffsets[i + 1]));
        }
        return list;
    }

    /**
     * Gets the list of transition rules for years beyond those defined in the transition list.
     * <p>
     * The list represents all the transitions that are expected in each year
     * beyond those in the transition list. Normally, there are two transitions
     * per year - into and out of daylight savings time. If daylight savings
     * time does not occur then the list will be empty.
     *
     * @return independent, modifiable copy of the list of transition rules, never null
     */
    @Override
    public List<ZoneOffsetTransitionRule> getTransitionRules() {
        return new ArrayList<ZoneOffsetTransitionRule>(Arrays.asList(lastRules));
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the complete set of rules.
     *
     * @param otherRules  the other rules, null returns false
     * @return true if this rules is the same as that specified
     */
    @Override
    public boolean equals(Object otherRules) {
        if (this == otherRules) {
           return true;
        }
        if (otherRules instanceof StandardZoneRules) {
            StandardZoneRules other = (StandardZoneRules) otherRules;
            return Arrays.equals(standardTransitions, other.standardTransitions) &&
                    Arrays.equals(standardOffsets, other.standardOffsets) &&
                    Arrays.equals(savingsInstantTransitions, other.savingsInstantTransitions) &&
                    Arrays.equals(wallOffsets, other.wallOffsets) &&
                    Arrays.equals(lastRules, other.lastRules);
        }
        return false;
    }

    /**
     * A hash code for the rules object.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(standardTransitions) ^
                Arrays.hashCode(standardOffsets) ^
                Arrays.hashCode(savingsInstantTransitions) ^
                Arrays.hashCode(wallOffsets) ^
                Arrays.hashCode(lastRules);
    }

}
