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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.time.calendar.DateAdjusters;
import javax.time.calendar.DayOfWeek;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.IllegalCalendarFieldValueException;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.Period;
import javax.time.calendar.Year;
import javax.time.calendar.ZoneOffset;

/**
 * A mutable builder used to create all the rules for a historic time-zone.
 * <p>
 * The rules of a time-zone describe how the offset changes over time.
 * The rules are created by building windows on the time-line within which
 * the different rules apply. The rules may be one of two kinds:
 * <ul>
 * <li>Fixed savings - A single fixed amount of savings from the standard offset will apply.</li>
 * <li>Rules - A set of one or more rules describe how daylight savings changes during the window.</li>
 * </ul>
 * <p>
 * TransitionRulesBuilder is a mutable class used to create instances of TimeZone.
 * It must only be used from a single thread.
 * The created TimeZone instances are immutable and thread-safe.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public class ZoneRulesBuilder {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The maximum date-time.
     */
    private static final LocalDateTime MAX_DATE_TIME = LocalDateTime.of(Year.MAX_YEAR, 12, 31, 23, 59, 59, 999999999);

    /**
     * The list of windows.
     */
    private List<TZWindow> windowList = new ArrayList<TZWindow>();
    /**
     * A map for deduplicating the output.
     */
    private Map<Object, Object> deduplicateMap;

    /**
     * Validates that the input value is not null.
     *
     * @param object  the object to check
     * @param errorMessage  the error to throw
     * @throws NullPointerException if the object is null
     */
    private static void checkNotNull(Object object, String errorMessage) {
        if (object == null) {
            throw new NullPointerException(errorMessage);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance of the builder that can be used to create zone rules.
     * <p>
     * The builder is used by adding one or more windows representing portions
     * of the time-line. The standard offset from UTC will be constant within a window,
     * although two adjacent windows can have the same standard offset.
     * <p>
     * Within each window, there can either be a
     * {@link #setFixedSavingsToWindow fixed savings amount} or a
     * {@link #addRuleToWindow list of rules}.
     */
    public ZoneRulesBuilder() {
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a window to the builder that can be used to filter a set of rules.
     * <p>
     * This method defines and adds a window to the zone where the standard offset is specified.
     * The window limits the effect of subsequent additions of transition rules
     * or fixed savings. If neither rules or fixed savings are added to the window
     * then the window will default to no savings.
     * <p>
     * Each window must be added sequentially, as the start instant of the window
     * is derived from the until instant of the previous window.
     *
     * @param standardOffset  the standard offset, not null
     * @param until  the date-time that the offset applies until, not null
     * @param untilDefinition  the time type for the until date-time, not null
     * @return this, for chaining
     * @throws IllegalStateException if the window order is invalid
     */
    public ZoneRulesBuilder addWindow(
            ZoneOffset standardOffset,
            LocalDateTime until,
            TimeDefinition untilDefinition) {
        checkNotNull(standardOffset, "Standard offset must not be null");
        checkNotNull(until, "Until date-time must not be null");
        checkNotNull(untilDefinition, "Time definition must not be null");
        TZWindow window = new TZWindow(standardOffset, until, untilDefinition);
        if (windowList.size() > 0) {
            TZWindow previous = windowList.get(windowList.size() - 1);
            window.validateWindowOrder(previous);
        }
        windowList.add(window);
        return this;
    }

    /**
     * Adds a window that applies until the end of time to the builder that can be
     * used to filter a set of rules.
     * <p>
     * This method defines and adds a window to the zone where the standard offset is specified.
     * The window limits the effect of subsequent additions of transition rules
     * or fixed savings. If neither rules or fixed savings are added to the window
     * then the window will default to no savings.
     * <p>
     * This must be added after all other windows.
     * No more windows can be added after this one.
     *
     * @param standardOffset  the standard offset, not null
     * @return this, for chaining
     * @throws IllegalStateException if a forever window has already been added
     */
    public ZoneRulesBuilder addWindowForever(ZoneOffset standardOffset) {
        return addWindow(standardOffset, MAX_DATE_TIME, TimeDefinition.WALL);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the previously added window to have fixed savings.
     * <p>
     * Setting a window to have fixed savings simply means that a single daylight
     * savings amount applies throughout the window. The window could be small,
     * such as a single summer, or large, such as a multi-year daylight savings.
     * <p>
     * A window can either have fixed savings or rules but not both.
     *
     * @param fixedSavingAmount  the amount of saving to use for the whole window, not null
     * @return this, for chaining
     * @throws IllegalStateException if no window has yet been added
     * @throws IllegalStateException if the window already has rules
     */
    public ZoneRulesBuilder setFixedSavingsToWindow(Period fixedSavingAmount) {
        checkNotNull(fixedSavingAmount, "Fixed savings amount must not be null");
        if (windowList.isEmpty()) {
            throw new IllegalStateException("Must add a window before setting the fixed savings");
        }
        TZWindow window = windowList.get(windowList.size() - 1);
        window.setFixedSavings(fixedSavingAmount);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a single transition rule to the current window.
     * <p>
     * This adds a rule such that the offset, expressed as a daylight savings amount,
     * changes at the specified date-time.
     *
     * @param dateTime  the date-time that the transition occurs as defined by timeDefintion, not null
     * @param timeDefinition  the definition of how to convert local to actual time, not null
     * @param savingAmount  the amount of saving from the standard offset after the transition, not null
     * @return this, for chaining
     * @throws IllegalStateException if no window has yet been added
     * @throws IllegalStateException if the window already has fixed savings
     * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
     */
    public ZoneRulesBuilder addRuleToWindow(
            LocalDateTime dateTime,
            TimeDefinition timeDefinition,
            Period savingAmount) {
        checkNotNull(dateTime, "Rule end date-time must not be null");
        return addRuleToWindow(
                dateTime.getYear(), dateTime.getYear(),
                dateTime.getMonthOfYear(), dateTime.getDayOfMonth(),
                null, dateTime.toLocalTime(), false, timeDefinition, savingAmount);
    }

    /**
     * Adds a single transition rule to the current window.
     * <p>
     * This adds a rule such that the offset, expressed as a daylight savings amount,
     * changes at the specified date-time.
     *
     * @param year  the year of the transition, from MIN_YEAR to MAX_YEAR
     * @param month  the month of the transition, not null
     * @param dayOfMonthIndicator  the day-of-month of the transition, adjusted by dayOfWeek,
     *   from 1 to 31 adjusted later, or -1 to -28 adjusted earlier from the last day of the month
     * @param time  the time that the transition occurs as defined by timeDefintion, not null
     * @param timeEndOfDay  whether midnight is at the end of day
     * @param timeDefinition  the definition of how to convert local to actual time, not null
     * @param savingAmount  the amount of saving from the standard offset after the transition, not null
     * @return this, for chaining
     * @throws IllegalCalendarFieldValueException if a date-time field is out of range
     * @throws IllegalStateException if no window has yet been added
     * @throws IllegalStateException if the window already has fixed savings
     * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
     */
    public ZoneRulesBuilder addRuleToWindow(
            int year,
            MonthOfYear month,
            int dayOfMonthIndicator,
            LocalTime time,
            boolean timeEndOfDay,
            TimeDefinition timeDefinition,
            Period savingAmount) {
        return addRuleToWindow(year, year, month, dayOfMonthIndicator, null, time, timeEndOfDay, timeDefinition, savingAmount);
    }

    /**
     * Adds a multi-year transition rule to the current window.
     * <p>
     * This adds a rule such that the offset, expressed as a daylight savings amount,
     * changes at the specified date-time for each year in the range.
     *
     * @param startYear  the start year of the rule, from MIN_YEAR to MAX_YEAR
     * @param endYear  the end year of the rule, from MIN_YEAR to MAX_YEAR
     * @param month  the month of the transition, not null
     * @param dayOfMonthIndicator  the day-of-month of the transition, adjusted by dayOfWeek,
     *   from 1 to 31 adjusted later, or -1 to -28 adjusted earlier from the last day of the month
     * @param dayOfWeek  the day-of-week to adjust to, null if day-of-month should not be adjusted
     * @param time  the time that the transition occurs as defined by timeDefintion, not null
     * @param timeEndOfDay  whether midnight is at the end of day
     * @param timeDefinition  the definition of how to convert local to actual time, not null
     * @param savingAmount  the amount of saving from the standard offset after the transition, not null
     * @return this, for chaining
     * @throws IllegalCalendarFieldValueException if a date-time field is out of range
     * @throws IllegalArgumentException if the day of month indicator is invalid
     * @throws IllegalArgumentException if the end of day midnight flag does not match the time
     * @throws IllegalStateException if no window has yet been added
     * @throws IllegalStateException if the window already has fixed savings
     * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
     */
    public ZoneRulesBuilder addRuleToWindow(
            int startYear,
            int endYear,
            MonthOfYear month,
            int dayOfMonthIndicator,
            DayOfWeek dayOfWeek,
            LocalTime time,
            boolean timeEndOfDay,
            TimeDefinition timeDefinition,
            Period savingAmount) {
        
        checkNotNull(month, "Rule end month must not be null");
        checkNotNull(time, "Rule end time must not be null");
        checkNotNull(timeDefinition, "Time definition must not be null");
        checkNotNull(savingAmount, "Savings amount must not be null");
        ISOChronology.yearRule().checkValue(startYear);
        ISOChronology.yearRule().checkValue(endYear);
        if (dayOfMonthIndicator < -28 || dayOfMonthIndicator > 31 || dayOfMonthIndicator == 0) {
            throw new IllegalArgumentException("Day of month indicator must be between -28 and 31 inclusive excluding zero");
        }
        if (timeEndOfDay && time.equals(LocalTime.MIDNIGHT) == false) {
            throw new IllegalArgumentException("Time must be midnight when end of day flag is true");
        }
        if (windowList.isEmpty()) {
            throw new IllegalStateException("Must add a window before adding a rule");
        }
        TZWindow window = windowList.get(windowList.size() - 1);
        window.addRule(startYear, endYear, month, dayOfMonthIndicator, dayOfWeek, time, timeEndOfDay, timeDefinition, savingAmount);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Completes the build converting the builder to a set of time-zone rules.
     * <p>
     * Calling this method alters the state of the builder.
     * Further rules should not be added to this builder once this method is called.
     *
     * @param id  the time-zone id, not null
     * @return the zone rules, never null
     * @throws IllegalStateException if no windows have been added
     * @throws IllegalStateException if there is only one rule defined as being forever for any given window
     */
    public ZoneRules toRules(String id) {
        return toRules(id, new HashMap<Object, Object>());
    }

    /**
     * Completes the build converting the builder to a set of time-zone rules.
     * <p>
     * Calling this method alters the state of the builder.
     * Further rules should not be added to this builder once this method is called.
     *
     * @param id  the time-zone id, not null
     * @param deduplicateMap  a map for deduplicating the values, not null
     * @return the zone rules, never null
     * @throws IllegalStateException if no windows have been added
     * @throws IllegalStateException if there is only one rule defined as being forever for any given window
     */
    ZoneRules toRules(String id, Map<Object, Object> deduplicateMap) {
        checkNotNull(id, "Time zone id must not be null");
        this.deduplicateMap = deduplicateMap;
        if (windowList.isEmpty()) {
            throw new IllegalStateException("No windows have been added to the builder");
        }
        if (windowList.size() == 1) {
            TZWindow window = windowList.get(0);
            if (window.isSingleWindowStandardOffset()) {
                return ZoneRules.ofFixed(window.standardOffset);
            }
        }
        
        List<OffsetDateTime> standardOffsetList = new ArrayList<OffsetDateTime>(4);
        List<ZoneOffsetTransition> transitionList = new ArrayList<ZoneOffsetTransition>(256);
        List<ZoneOffsetTransitionRule> lastTransitionRuleList = new ArrayList<ZoneOffsetTransitionRule>(2);
        
        // initialize the standard offset calculation
        TZWindow firstWindow = windowList.get(0);
        ZoneOffset standardOffset = firstWindow.standardOffset;
        Period savings = Period.ZERO;
        if (firstWindow.fixedSavingAmount != null) {
            savings = firstWindow.fixedSavingAmount;
        }
        ZoneOffset firstWallOffset = deduplicate(standardOffset.plus(savings));
        OffsetDateTime windowStart = deduplicate(OffsetDateTime.of(Year.MIN_YEAR, 1, 1, 0, 0, firstWallOffset));
        
        // build the windows and rules to interesting data
        for (TZWindow window : windowList) {
            // tidy the state
            window.tidy(windowStart.getYear());
            
            // calculate effective savings at the start of the window
            Period effectiveSavings = window.fixedSavingAmount;
            if (effectiveSavings == null) {
                // apply rules from this window together with the standard offset and
                // savings from the last window to find the savings amount applicable
                // at start of this window
                effectiveSavings = Period.ZERO;
                for (TZRule rule : window.ruleList) {
                    ZoneOffsetTransition trans = rule.toTransition(standardOffset, savings);
                    if (trans.getDateTime().isAfter(windowStart)) {
                        // previous savings amount found, which could be the savings amount at
                        // the instant that the window starts (hence isAfter)
                        break;
                    }
                    effectiveSavings = rule.savingAmount;
                }
            }
            
            // check if standard offset changed, and update it
            if (standardOffset.equals(window.standardOffset) == false) {
                standardOffset = deduplicate(window.standardOffset);
                standardOffsetList.add(deduplicate(windowStart.withOffsetSameInstant(standardOffset)));
            }
            
            // check if the start of the window represents a transition
            ZoneOffset effectiveWallOffset = deduplicate(standardOffset.plus(effectiveSavings));
            if (windowStart.getOffset().equals(effectiveWallOffset) == false) {
                ZoneOffsetTransition trans = deduplicate(new ZoneOffsetTransition(windowStart, effectiveWallOffset));
                transitionList.add(trans);
            }
            savings = effectiveSavings;
            
            // apply rules within the window
            for (TZRule rule : window.ruleList) {
                ZoneOffsetTransition trans = deduplicate(rule.toTransition(standardOffset, savings));
                if (trans.getDateTime().isBefore(windowStart) == false &&
                        trans.getDateTime().isBefore(window.createDateTime(savings)) &&
                        trans.getOffsetBefore().equals(trans.getOffsetAfter()) == false) {
                    transitionList.add(trans);
                    savings = rule.savingAmount;
                }
            }
            
            // calculate last rules
            for (TZRule lastRule : window.lastRuleList) {
                ZoneOffsetTransitionRule transitionRule = deduplicate(lastRule.toTransitionRule(standardOffset, savings));
                lastTransitionRuleList.add(transitionRule);
                savings = lastRule.savingAmount;
            }
            
            // finally we can calculate the true end of the window, passing it to the next window
            windowStart = deduplicate(window.createDateTime(savings));
        }
        return new StandardZoneRules(
                firstWindow.standardOffset, firstWallOffset, standardOffsetList,
                transitionList, lastTransitionRuleList);
    }

    /**
     * Deduplicates an object instance.
     *
     * @param <T> the generic type
     * @param object  the object to deduplicate
     * @return the deduplicated object
     */
    @SuppressWarnings("unchecked")
    <T> T deduplicate(T object) {
        if (deduplicateMap.containsKey(object) == false) {
            deduplicateMap.put(object, object);
        }
        return (T) deduplicateMap.get(object);
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of the way a local time can be converted to an offset time.
     * <p>
     * Time zone rules are expressed in one of three ways:
     * <ul>
     * <li>Relative to UTC</li>
     * <li>Relative to the standard offset in force</li>
     * <li>Relative to the wall offset (what you would see on a clock on the wall)</li>
     * </ul>
     */
    public static enum TimeDefinition {
        /** The local date-time is expressed in terms of the UTC offset. */
        UTC,
        /** The local date-time is expressed in terms of the wall offset. */
        WALL,
        /** The local date-time is expressed in terms of the standard offset. */
        STANDARD;

        /**
         * Creates the offset date-time from the specified local date-time.
         * <p>
         * This method converts a local date-time to an offset date-time using an
         * algorithm based on the definition type.
         * The UTC type builds the offset date-time using the UTC offset.
         * The STANDARD type builds the offset date-time using the standard offset.
         * The WALL type builds the offset date-time using the wall offset.
         * The result always uses the wall-offset, thus a conversion may occur.
         *
         * @param dateTime  the local date-time, not null
         * @param standardOffset  the standard offset, not null
         * @param wallOffset  the wall offset, not null
         * @return the created offset date-time in the wall offset, never null
         */
        public OffsetDateTime createDateTime(LocalDateTime dateTime, ZoneOffset standardOffset, ZoneOffset wallOffset) {
            switch (this) {
                case UTC:
                    return OffsetDateTime.of(dateTime, ZoneOffset.UTC).withOffsetSameInstant(wallOffset);
                case STANDARD:
                    return OffsetDateTime.of(dateTime, standardOffset).withOffsetSameInstant(wallOffset);
                default:  // WALL
                    return OffsetDateTime.of(dateTime, wallOffset);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of a window in the time-line.
     * The window will have one standard offset and will either have a
     * fixed DST savings or a set of rules.
     */
    class TZWindow {
        /** The standard offset during the window, not null. */
        private final ZoneOffset standardOffset;
        /** The end local time, not null. */
        private final LocalDateTime windowEnd;
        /** The type of the end time, not null. */
        private final TimeDefinition timeDefinition;

        /** The fixed amount of the saving to be applied during this window. */
        private Period fixedSavingAmount;
        /** The rules for the current window. */
        private List<TZRule> ruleList = new ArrayList<TZRule>();
        /** The latest year that the last year starts at. */
        private int maxLastRuleStartYear = Year.MIN_YEAR;
        /** The last rules. */
        private List<TZRule> lastRuleList = new ArrayList<TZRule>();

        /**
         * Constructor.
         *
         * @param standardOffset  the standard offset applicable during the window, not null
         * @param windowEnd  the end of the window, relative to the time definition, null if forever
         * @param timeDefinition  the time definition for calculating the true end, not null
         */
        TZWindow(
                ZoneOffset standardOffset,
                LocalDateTime windowEnd,
                TimeDefinition timeDefinition) {
            super();
            this.windowEnd = windowEnd;
            this.timeDefinition = timeDefinition;
            this.standardOffset = standardOffset;
        }

        /**
         * Sets the fixed savings amount for the window.
         *
         * @param fixedSavingAmount  the amount of daylight saving to apply throughout the window, may be null
         * @throws IllegalStateException if the window already has rules
         */
        void setFixedSavings(Period fixedSavingAmount) {
            if (ruleList.size() > 0 || lastRuleList.size() > 0) {
                throw new IllegalStateException("Window has DST rules, so cannot have fixed savings");
            }
            this.fixedSavingAmount = fixedSavingAmount;
        }

        /**
         * Adds a rule to the current window.
         *
         * @param startYear  the start year of the rule, from MIN_YEAR to MAX_YEAR
         * @param endYear  the end year of the rule, from MIN_YEAR to MAX_YEAR
         * @param month  the month of the transition, not null
         * @param dayOfMonthIndicator  the day-of-month of the transition, adjusted by dayOfWeek,
         *   from 1 to 31 adjusted later, or -1 to -28 adjusted earlier from the last day of the month
         * @param dayOfWeek  the day-of-week to adjust to, null if day-of-month should not be adjusted
         * @param time  the time that the transition occurs as defined by timeDefintion, not null
         * @param timeEndOfDay  whether midnight is at the end of day
         * @param timeDefinition  the definition of how to convert local to actual time, not null
         * @param savingAmount  the amount of saving from the standard offset, not null
         * @throws IllegalStateException if the window already has fixed savings
         * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
         */
        void addRule(
                int startYear,
                int endYear,
                MonthOfYear month,
                int dayOfMonthIndicator,
                DayOfWeek dayOfWeek,
                LocalTime time,
                boolean timeEndOfDay,
                TimeDefinition timeDefinition,
                Period savingAmount) {
            
            if (fixedSavingAmount != null) {
                throw new IllegalStateException("Window has a fixed DST saving, so cannot have DST rules");
            }
            if (ruleList.size() >= 2000) {
                throw new IllegalStateException("Window has reached the maximum number of allowed rules");
            }
            boolean lastRule = false;
            if (endYear == Year.MAX_YEAR) {
                lastRule = true;
                endYear = startYear;
            }
            int year = startYear;
            while (year <= endYear) {
                TZRule rule = new TZRule(year, month, dayOfMonthIndicator, dayOfWeek, time, timeEndOfDay, timeDefinition, savingAmount);
                if (lastRule) {
                    lastRuleList.add(rule);
                    maxLastRuleStartYear = Math.max(startYear, maxLastRuleStartYear);
                } else {
                    ruleList.add(rule);
                }
                year++;
            }
        }

        /**
         * Validates that this window is after the previous one.
         *
         * @param previous  the previous window, not null
         * @throws IllegalStateException if the window order is invalid
         */
        void validateWindowOrder(TZWindow previous) {
            if (windowEnd.isBefore(previous.windowEnd)) {
                throw new IllegalStateException("Windows must be added in date-time order: " +
                        windowEnd + " < " + previous.windowEnd);
            }
        }

        /**
         * Adds rules to make the last rules all start from the same year.
         * Also add one more year to avoid weird case where penultimate year has odd offset.
         *
         * @param windowStartYear  the window start year
         * @throws IllegalStateException if there is only one rule defined as being forever
         */
        void tidy(int windowStartYear) {
            if (lastRuleList.size() == 1) {
                throw new IllegalStateException("Cannot have only one rule defined as being forever");
            }
            
            // handle last rules
            if (windowEnd.equals(MAX_DATE_TIME)) {
                // setup at least one real rule, which closes off other windows nicely
                maxLastRuleStartYear = Math.max(maxLastRuleStartYear, windowStartYear) + 1;
                for (TZRule lastRule : lastRuleList) {
                    addRule(lastRule.year, maxLastRuleStartYear, lastRule.month, lastRule.dayOfMonthIndicator,
                        lastRule.dayOfWeek, lastRule.time, lastRule.timeEndOfDay, lastRule.timeDefinition, lastRule.savingAmount);
                    lastRule.year = maxLastRuleStartYear + 1;
                }
                if (maxLastRuleStartYear == Year.MAX_YEAR) {
                    lastRuleList.clear();
                } else {
                    maxLastRuleStartYear++;
                }
            } else {
                // convert all within the endYear limit
                int endYear = windowEnd.getYear();
                for (TZRule lastRule : lastRuleList) {
                    addRule(lastRule.year, endYear + 1, lastRule.month, lastRule.dayOfMonthIndicator,
                        lastRule.dayOfWeek, lastRule.time, lastRule.timeEndOfDay, lastRule.timeDefinition, lastRule.savingAmount);
                }
                lastRuleList.clear();
                maxLastRuleStartYear = Year.MAX_YEAR;
            }
            
            // ensure lists are sorted
            Collections.sort(ruleList);
            Collections.sort(lastRuleList);
            
            // default fixed savings to zero
            if (ruleList.size() == 0 && fixedSavingAmount == null) {
                fixedSavingAmount = Period.ZERO;
            }
        }

        /**
         * Checks if the window is empty.
         *
         * @return true if the window is only a standard offset
         */
        boolean isSingleWindowStandardOffset() {
            return windowEnd.equals(MAX_DATE_TIME) && timeDefinition == TimeDefinition.WALL &&
                    fixedSavingAmount == null && lastRuleList.isEmpty() && ruleList.isEmpty();
        }

        /**
         * Creates the offset date-time for the local date-time at the end of the window.
         *
         * @param savings  the amount of savings in use, not null
         * @return the created offset date-time in the wall offset, never null
         */
        OffsetDateTime createDateTime(Period savings) {
            ZoneOffset wallOffset = standardOffset.plus(savings);
            return timeDefinition.createDateTime(windowEnd, standardOffset, wallOffset);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of the way a local time can be converted to an offset time.
     */
    class TZRule implements Comparable<TZRule> {
        /** The year. */
        private int year;
        /** The month. */
        private MonthOfYear month;
        /** The day-of-month. */
        private int dayOfMonthIndicator;
        /** The day-of-month. */
        private DayOfWeek dayOfWeek;
        /** The local time. */
        private LocalTime time;
        /** Whether the local time is end of day. */
        private boolean timeEndOfDay;
        /** The type of the time. */
        private TimeDefinition timeDefinition;
        /** The amount of the saving to be applied after this point. */
        private Period savingAmount;

        /**
         * Constructor.
         *
         * @param year  the year
         * @param month  the month, not null
         * @param dayOfMonthIndicator  the day-of-month of the transition, adjusted by dayOfWeek,
         *   from 1 to 31 adjusted later, or -1 to -28 adjusted earlier from the last day of the month
         * @param dayOfWeek  the day-of-week, null if day-of-month is exact
         * @param time  the time, not null
         * @param timeEndOfDay  whether midnight is at the end of day
         * @param timeDefinition  the time definition, not null
         * @param savingAfter  the savings amount, not null
         */
        TZRule(int year, MonthOfYear month, int dayOfMonthIndicator,
                DayOfWeek dayOfWeek, LocalTime time, boolean timeEndOfDay,
                TimeDefinition timeDefinition, Period savingAfter) {
            super();
            this.year = year;
            this.month = month;
            this.dayOfMonthIndicator = dayOfMonthIndicator;
            this.dayOfWeek = dayOfWeek;
            this.time = time;
            this.timeEndOfDay = timeEndOfDay;
            this.timeDefinition = timeDefinition;
            this.savingAmount = savingAfter;
        }

        /**
         * Converts this to a transition.
         *
         * @param standardOffset  the active standard offset, not null
         * @param savingsBefore  the active savings, not null
         * @return the transition, never null
         */
        ZoneOffsetTransition toTransition(ZoneOffset standardOffset, Period savingsBefore) {
            // copy of code in ZoneOffsetTransitionRule to avoid infinite loop
            LocalDate date;
            if (dayOfMonthIndicator < 0) {
                date = LocalDate.of(year, month, month.getLastDayOfMonth(ISOChronology.isLeapYear(year)) + 1 + dayOfMonthIndicator);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.previousOrCurrent(dayOfWeek));
                }
            } else {
                date = LocalDate.of(year, month, dayOfMonthIndicator);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.nextOrCurrent(dayOfWeek));
                }
            }
            if (timeEndOfDay) {
                date = date.plusDays(1);
            }
            date = deduplicate(date);
            LocalDateTime ldt = deduplicate(LocalDateTime.of(date, time));
            ZoneOffset wallOffset = deduplicate(standardOffset.plus(savingsBefore));
            OffsetDateTime dt = deduplicate(timeDefinition.createDateTime(ldt, standardOffset, wallOffset));
            ZoneOffset offsetAfter = deduplicate(standardOffset.plus(savingAmount));
            return new ZoneOffsetTransition(dt, offsetAfter);
        }

        /**
         * Converts this to a transition rule.
         *
         * @param standardOffset  the active standard offset, not null
         * @param savingsBefore  the active savings before the transition, not null
         * @return the transition, never null
         */
        ZoneOffsetTransitionRule toTransitionRule(ZoneOffset standardOffset, Period savingsBefore) {
            // optimize stored format
            if (dayOfMonthIndicator < 0) {
                if (month != MonthOfYear.FEBRUARY) {
                    dayOfMonthIndicator = month.maxLengthInDays() - 6;
                }
            }
            if (timeEndOfDay && dayOfMonthIndicator > 0 && (dayOfMonthIndicator == 28 && month == MonthOfYear.FEBRUARY) == false) {
                LocalDate date = LocalDate.of(2004, month, dayOfMonthIndicator).plusDays(1);  // leap-year
                month = date.getMonthOfYear();
                dayOfMonthIndicator = date.getDayOfMonth();
                if (dayOfWeek != null) {
                    dayOfWeek = dayOfWeek.next();
                }
                timeEndOfDay = false;
            }
            
            // build rule
            ZoneOffsetTransition trans = toTransition(standardOffset, savingsBefore);
            return new ZoneOffsetTransitionRule(
                    month, dayOfMonthIndicator, dayOfWeek, time, timeEndOfDay, timeDefinition,
                    standardOffset, trans.getOffsetBefore(), trans.getOffsetAfter());
        }

        /** {@inheritDoc}. */
        public int compareTo(TZRule other) {
            int cmp = year - other.year;
            cmp = (cmp == 0 ? month.compareTo(other.month) : cmp);
            cmp = (cmp == 0 ? dayOfMonthIndicator - other.dayOfMonthIndicator : cmp);
            cmp = (cmp == 0 ? time.compareTo(other.time) : cmp);
            return cmp;
        }

        // no equals() or hashCode()
    }

}
