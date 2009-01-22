/*
 * Copyright (c) 2009, Stephen Colebourne & Michael Nascimento Santos
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.Instant;
import javax.time.InstantProvider;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.field.Year;

/**
 * The rules describing how the zone offset varies through the year and historically.
 * <p>
 * ZoneRules is immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
final class ZoneRules extends TimeZone {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 224698619L;
    /**
     * The last year to have its transitions cached.
     */
    private static final Year LAST_CACHED_YEAR = Year.isoYear(2100);

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
    private final LocalDateTime[] savingsLocalTransitions;
    /**
     * The wall offsets.
     */
    private final ZoneOffset[] wallOffsets;
    /**
     * The last rule.
     */
    private final TransitionRule[] lastRules;
    /**
     * The map of transitions.
     */
    private transient ConcurrentMap<Year, Transition[]> lastRulesCache = new ConcurrentHashMap<Year, Transition[]>();

    /**
     * Constructor.
     *
     * @param id  the time zone id, not null
     * @param baseStandardOffset  the standard offset to use before legal rules were set, not null
     * @param baseWallOffset  the wall offset to use before legal rules were set, not null
     * @param standardOffsetTransitionList  the list of changes to the standard offset, not null
     * @param transitionList  the list of transitions, not null
     * @param lastRules  the recurring last rules, not null
     */
    ZoneRules(
            String id,
            ZoneOffset baseStandardOffset,
            ZoneOffset baseWallOffset,
            List<OffsetDateTime> standardOffsetTransitionList,
            List<Transition> transitionList,
            List<TransitionRule> lastRules) {
        super(id);
        
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
        for (Transition trans : transitionList) {
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
        this.lastRules = lastRules.toArray(new TransitionRule[lastRules.size()]);
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
        lastRulesCache = new ConcurrentHashMap<Year, Transition[]>();
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
        Instant instant = Instant.instant(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        
        // check if using last rules
        if (lastRules.length > 0 &&
                epochSecs > savingsInstantTransitions[savingsInstantTransitions.length - 1]) {
            OffsetDateTime dt = OffsetDateTime.dateTime(instant, wallOffsets[wallOffsets.length - 1]);
            Transition[] transArray = findTransitionArray(dt.getYear());
            Transition trans = null;
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
    public OffsetInfo getOffsetInfo(LocalDateTime dt) {
        // check if using last rules
        if (lastRules.length > 0 &&
                dt.isAfter(savingsLocalTransitions[savingsLocalTransitions.length - 1])) {
            Transition[] transArray = findTransitionArray(dt.getYear());
            OffsetInfo info = null;
            for (Transition trans : transArray) {
                info = findOffsetInfo(dt, trans);
                if (info.isDiscontinuity() || info.getOffset().equals(trans.getOffsetBefore())) {
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
                return createOffsetInfo(dt, OffsetDateTime.dateTime(dtBefore, offsetBefore), offsetAfter);
            } else {
                // overlap
                return createOffsetInfo(dt, OffsetDateTime.dateTime(dtAfter, offsetBefore), offsetAfter);
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
    private OffsetInfo findOffsetInfo(LocalDateTime dt, Transition trans) {
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
    private Transition[] findTransitionArray(Year year) {
        Transition[] transArray = lastRulesCache.get(year);
        if (transArray != null) {
            return transArray;
        }
        TransitionRule[] ruleArray = lastRules;
        transArray  = new Transition[ruleArray.length];
        for (int i = 0; i < ruleArray.length; i++) {
            transArray[i] = ruleArray[i].createTransition(year);
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
        Instant instant = Instant.instant(instantProvider);
        long epochSecs = instant.getEpochSeconds();
        int index  = Arrays.binarySearch(standardTransitions, epochSecs);
        if (index < 0) {
            // switch negative insert position to start of matched range
            index = -index - 2;
        }
        return standardOffsets[index + 1];
    }

}
