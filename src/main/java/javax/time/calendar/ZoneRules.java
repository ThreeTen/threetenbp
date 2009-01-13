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
package javax.time.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.time.Instant;
import javax.time.calendar.field.Year;

/**
 * The rules describing how the zone offset varies through the year and historically.
 * <p>
 * TimeZone is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * It is only intended that the abstract methods are overridden.
 *
 * @author Stephen Colebourne
 */
class ZoneRules extends TimeZone {
    // TODO: optimise the internal structue
    // Instant[] and LocalDateTime[] ?

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 224698619L;
    /**
     * The last year to have its transitions cached.
     */
    private static final Year LAST_CACHED_YEAR = Year.isoYear(2100);

    /**
     * The base offset.
     */
    private final ZoneOffset baseOffset;
    /**
     * The transitions between standard offsets.
     */
    private final OffsetDateTime[] standardOffsetTransitionList;
    /**
     * The map of transitions.
     */
    private final TreeMap<Year, Transition[]> transitionMap = new TreeMap<Year, Transition[]>();
    /**
     * The transitions between instants (epoch seconds).
     */
    private final long[] instantTransitions;
    /**
     * The transitions between local date-times.
     */
    private final LocalDateTime[] localTransitions;
    /**
     * The offsets for the transitions between local date-times.
     */
    private final ZoneOffset[] localTransitionOffsets;
    /**
     * The year that the last rule applies from.
     */
    private Year lastRuleYear = Year.isoYear(Year.MAX_YEAR);
    /**
     * The last rule.
     */
    private TransitionRule[] lastRules = null;

    /**
     * Constructor.
     *
     * @param id  the time zone id, not null
     * @param baseOffset  the offset to use before legal rules were set, not null
     * @param standardOffsetTransitionList  the list of changes to the standard offset, not null
     * @param transitionList  the list of transitions, not null
     * @param lastRuleYear  the year from which the last rules apply, null if no last rules
     * @param lastRules  the recurring last rules, not null
     */
    ZoneRules(
            String id,
            ZoneOffset baseOffset,
            List<OffsetDateTime> standardOffsetTransitionList,
            List<Transition> transitionList,
            Year lastRuleYear,
            List<TransitionRule> lastRules) {
        super(id);
        this.baseOffset = baseOffset;
        this.standardOffsetTransitionList = (OffsetDateTime[]) standardOffsetTransitionList.toArray(
                new OffsetDateTime[standardOffsetTransitionList.size()]);
        TreeMap<Year, Set<Transition>> transitionMap = new TreeMap<Year, Set<Transition>>();
        for (Transition trans : transitionList) {
            Year year = trans.getDateTime().getYear();
            Set<Transition> transSet = transitionMap.get(year);
            if (transSet == null) {
                transSet = new TreeSet<Transition>();
                transitionMap.put(year, transSet);
            }
            transSet.add(trans);
        }
        for (Entry<Year, Set<Transition>> entry : transitionMap.entrySet()) {
            Transition[] rules = (Transition[]) entry.getValue().toArray(new Transition[entry.getValue().size()]);
            this.transitionMap.put(entry.getKey(), rules);
        }
        this.lastRuleYear = lastRuleYear;
        this.lastRules = (TransitionRule[]) lastRules.toArray(new TransitionRule[lastRules.size()]);
        
        // convert transitions to locals
        List<LocalDateTime> localTransitionList = new ArrayList<LocalDateTime>();
        List<ZoneOffset> localTransitionOffsetList = new ArrayList<ZoneOffset>();
        localTransitionOffsetList.add(baseOffset);
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
        this.localTransitions = (LocalDateTime[]) localTransitionList.toArray(
                new LocalDateTime[localTransitionList.size()]);
        this.localTransitionOffsets = (ZoneOffset[]) localTransitionOffsetList.toArray(
                new ZoneOffset[localTransitionOffsetList.size()]);
        
        // convert transitions to instants
        this.instantTransitions = new long[transitionList.size()];
        for (int i = 0; i < transitionList.size(); i++) {
            instantTransitions[i] = transitionList.get(i).getInstant().getEpochSeconds();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Resolves singletons.
     * @return the singleton instance
     */
    private Object readResolve() {
        return TimeZone.timeZone(getID());
    }
    /** {@inheritDoc} */
    @Override
    public ZoneOffset getOffset(Instant instant) {
//        // TODO: last rules
//        // using historic rules
//        int index  = Arrays.binarySearch(instantTransitions, instant.getEpochSeconds());
//        if (index < 0) {
//            // switch negative insert position to start of matched range
//            index = -index - 2;
//        }
//        return localTransitionOffsets[index + 1];
        
        OffsetDateTime dt = OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
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

    /** {@inheritDoc} */
    @Override
    public OffsetInfo getOffsetInfo(LocalDateTime dt) {
        // check if using last rules
        if (lastRules.length > 0 && dt.getYear().isBefore(lastRuleYear) == false) {
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
        int index  = Arrays.binarySearch(localTransitions, dt);
        if (index == -1) {
            // before first transition
            return createOffsetInfo(dt, baseOffset);
        }
        if (index < 0) {
            // switch negative insert position to start of matched range
            index = -index - 2;
        } else if (index < localTransitions.length - 1 &&
                localTransitions[index].equals(localTransitions[index + 1])) {
            // handle overlap immediately following gap
            index++;
        }
        if ((index & 1) == 0) {
            // gap or overlap
            LocalDateTime dtBefore = localTransitions[index];
            LocalDateTime dtAfter = localTransitions[index + 1];
            ZoneOffset offsetBefore = localTransitionOffsets[index / 2];
            ZoneOffset offsetAfter = localTransitionOffsets[index / 2 + 1];
            if (offsetAfter.getAmountSeconds() > offsetBefore.getAmountSeconds()) {
                // gap
                return createOffsetInfo(dt, OffsetDateTime.dateTime(dtBefore, offsetBefore), offsetAfter);
            } else {
                // overlap
                return createOffsetInfo(dt, OffsetDateTime.dateTime(dtAfter, offsetBefore), offsetAfter);
            }
        } else {
            // normal (neither gap or overlap)
            return createOffsetInfo(dt, localTransitionOffsets[index / 2 + 1]);
        }
    }

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
        Transition[] transArray = transitionMap.get(year);
        if (transArray != null) {
            return transArray;
        }
        TransitionRule[] ruleArray = null;
        if (lastRules.length > 0 && year.isBefore(lastRuleYear) == false) {
            ruleArray = lastRules;
        } else {
            return findTransitionArray(year.previous());
        }
//        TransitionRule[] ruleArray = lastRules;
        transArray  = new Transition[ruleArray.length];
        for (int i = 0; i < ruleArray.length; i++) {
            transArray[i] = ruleArray[i].createTransition(year);
        }
        if (year.isAfter(lastRuleYear) && year.isBefore(LAST_CACHED_YEAR)) {
            transitionMap.put(year, transArray);
        }
        return transArray;
    }

}
