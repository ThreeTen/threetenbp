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
import java.util.Collections;
import java.util.List;

import javax.time.InstantProvider;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.period.Period;

/**
 * A mutable builder used to create all the rules for a historic time zone.
 * <p>
 * The rules of a time zone describe how the offset changes over time.
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
    private static final long serialVersionUID = 9375261659767L;
    /**
     * The maximum date-time.
     */
    private static final LocalDateTime MAX_DATE_TIME = LocalDateTime.dateTime(Year.MAX_YEAR, 12, 31, 23, 59, 59, 999999999);

    /**
     * The first window with a fixed offset, no savings and no rules.
     */
    private TZWindow firstWindow;
    /**
     * The list of windows.
     */
    private List<TZWindow> windowList = new ArrayList<TZWindow>();

    /**
     * Creates an alias zone that wraps a zone with a different one id.
     *
     * @param zoneId  the new zone id, not null
     * @param baseZoneId  the base zone id, not null
     * @return the created zone, never null
     */
    public static TimeZone createAlias(String zoneId, String baseZoneId) {
        checkNotNull(zoneId, "Zone id must not be null");
        checkNotNull(baseZoneId, "Base zone id must not be null");
        return new AliasZone(zoneId, baseZoneId);
    }

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
     * The constructor specifies the period of time before which there was no concept
     * of standard or daylight savings time. The earliest recorded introducion of
     * standard time is in December 1835 in Amsterdam and the date varies by country.
     * Before the standardisation of time, clocks showed a time relative to midday
     * in that specific location. The offset passed in to this constructor is normally
     * an estimate of that time based solely on geograpic latitude and longitude.
     *
     * @param baseOffset  the offset to use before legal rules were set, not null
     * @param until  the date-time that the offset applies until, not null
     * @param untilDefinition  the time type for the until date-time, not null
     */
    public ZoneRulesBuilder(
            ZoneOffset baseOffset,
            LocalDateTime until,
            TimeDefinition untilDefinition) {
        checkNotNull(baseOffset, "Base offset must not be null");
        checkNotNull(until, "Until date-time must not be null");
        checkNotNull(untilDefinition, "Time definition must not be null");
        firstWindow = new TZWindow(baseOffset, until, untilDefinition);
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
     * Each window must be addded sequentially, as the start instant of the window
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
        TZWindow previous = windowList.isEmpty() ? firstWindow : windowList.get(windowList.size() - 1);
        window.validateWindowOrder(previous);
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
    public ZoneRulesBuilder addWindowForever(
            ZoneOffset standardOffset) {
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
                dateTime.getYear().getValue(), dateTime.getYear().getValue(),
                dateTime.getMonthOfYear(), dateTime.getDayOfMonth().getValue(),
                null, dateTime.toLocalTime(), timeDefinition, savingAmount);
    }

    /**
     * Adds a single transition rule to the current window.
     * <p>
     * This adds a rule such that the offset, expressed as a daylight savings amount,
     * changes at the specified date-time.
     *
     * @param year  the year of the transition, from MIN_YEAR to MAX_YEAR
     * @param month  the month of the transition, not null
     * @param dayOfMonth  the day of month of the transition, from 1 to 31, or -1 for the last day of month
     * @param time  the time that the transition occurs as defined by timeDefintion, not null
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
            int dayOfMonth,
            LocalTime time,
            TimeDefinition timeDefinition,
            Period savingAmount) {
        return addRuleToWindow(year, year, month, dayOfMonth, null, time, timeDefinition, savingAmount);
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
     * @param dayOfMonth  the day of month of the transition, adjusted by dayOfWeek,
     *   from 1 to 31 adjusted later, or -1 adjusted earlier from the last day of the month
     * @param dayOfWeek  the day of week to adjust to, null if day of month should not be adjusted
     * @param time  the time that the transition occurs as defined by timeDefintion, not null
     * @param timeDefinition  the definition of how to convert local to actual time, not null
     * @param savingAmount  the amount of saving from the standard offset after the transition, not null
     * @return this, for chaining
     * @throws IllegalCalendarFieldValueException if a date-time field is out of range
     * @throws IllegalStateException if no window has yet been added
     * @throws IllegalStateException if the window already has fixed savings
     * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
     */
    public ZoneRulesBuilder addRuleToWindow(
            int startYear,
            int endYear,
            MonthOfYear month,
            int dayOfMonth,
            DayOfWeek dayOfWeek,
            LocalTime time,
            TimeDefinition timeDefinition,
            Period savingAmount) {
        
        checkNotNull(month, "Rule end month must not be null");
        checkNotNull(time, "Rule end time must not be null");
        checkNotNull(timeDefinition, "Time definition must not be null");
        checkNotNull(savingAmount, "Savings amount must not be null");
        Year.rule().checkValue(startYear);
        Year.rule().checkValue(endYear);
        if (dayOfMonth != -1) {
            DayOfMonth.rule().checkValue(dayOfMonth);
        }
        if (windowList.isEmpty()) {
            throw new IllegalStateException("Must add a window before adding a rule");
        }
        TZWindow window = windowList.get(windowList.size() - 1);
        window.addRule(startYear, endYear, month, dayOfMonth, dayOfWeek, time, timeDefinition, savingAmount);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Completes the build converting the builder to a set of time zone rules.
     *
     * @param id  the time zone id, not null
     * @return the zone rules, never null
     * @throws IllegalStateException if there is only one rule defined as being forever for any given window
     */
    public TimeZone toRules(String id) {
        checkNotNull(id, "Time zone id must not be null");
        if (windowList.isEmpty()) {
            return TimeZone.timeZone(firstWindow.standardOffset);
        }
        
        List<OffsetDateTime> standardOffsetList = new ArrayList<OffsetDateTime>(4);
        List<Transition> transitionList = new ArrayList<Transition>(256);
        List<TransitionRule> lastTransitionRuleList = new ArrayList<TransitionRule>(2);
        
        // initialise the standard offset calculation
        ZoneOffset standardOffset = firstWindow.standardOffset;
        ZoneOffset wallOffset = standardOffset;
        OffsetDateTime windowStart = firstWindow.createDateTime(wallOffset);
        
        // build the windows and rules to interesting data
        Year lastRulesStartYear = null;
        for (TZWindow window : windowList) {
            // check if standard offset change
            if (standardOffset.equals(window.standardOffset) == false) {
                standardOffset = window.standardOffset;
                standardOffsetList.add(windowStart.adjustLocalDateTime(standardOffset));
            }
            
            // check if the start of the window represents a transition
            ZoneOffset initialWallOffset = standardOffset;
            if (window.fixedSavingAmount != null) {
                initialWallOffset = initialWallOffset.plus(window.fixedSavingAmount);
            }
            if (wallOffset.equals(initialWallOffset) == false) {
                Transition trans = new Transition(windowStart, initialWallOffset);
                transitionList.add(trans);
                wallOffset = initialWallOffset;
            }
            
            // convert last rules to real rules
            lastRulesStartYear = window.tidyLastRules();
            
            // apply rules
            Collections.sort(window.ruleList);
            for (TZRule rule : window.ruleList) {
                Transition trans = rule.toTransition(standardOffset, wallOffset);
                if (trans.getDateTime().isBefore(windowStart) == false &&
                        trans.getDateTime().isBefore(window.createDateTime(wallOffset)) &&
                        trans.getOffsetBefore().equals(trans.getOffsetAfter()) == false) {
                    transitionList.add(trans);
                    wallOffset = trans.getOffsetAfter();
                }
            }
            
            // calculate last rules
            Collections.sort(window.lastRuleList);
            for (TZRule lastRule : window.lastRuleList) {
                Transition trans = lastRule.toTransition(standardOffset, wallOffset);
                TransitionRule transitionRule = lastRule.toTransitionRule(
                        trans.getLocal().toLocalTime(), trans.getOffsetBefore(), trans.getOffsetAfter());
                lastTransitionRuleList.add(transitionRule);
                wallOffset = trans.getOffsetAfter();
            }
            
            // finally we can calculate the true end of the window, passing it to the next window
            windowStart = window.createDateTime(wallOffset);
        }
        return new ZoneRules(
                id, firstWindow.standardOffset, standardOffsetList,
                transitionList, lastRulesStartYear, lastTransitionRuleList);
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
         * alorithm based on the definition type.
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
                    return OffsetDateTime.dateTime(dateTime, ZoneOffset.UTC).adjustLocalDateTime(wallOffset);
                case STANDARD:
                    return OffsetDateTime.dateTime(dateTime, standardOffset).adjustLocalDateTime(wallOffset);
                default:  // WALL
                    return OffsetDateTime.dateTime(dateTime, wallOffset);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of a window in the time-line.
     * The window will have one standard offset and will either have a
     * fixed DST savings or a set of rules.
     */
    static class TZWindow {
        /** The standard offset during the window. */
        private final ZoneOffset standardOffset;
        /** The end local time. */
        private final LocalDateTime windowEnd;
        /** The type of the end time. */
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
         * @param standardOffset  the standard offset applicable during the winow, not null
         * @param windowEnd  the end of the window, wrt the time definition, null if forever
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
         * @param dayOfMonth  the day of month of the transition, adjusted by dayOfWeek,
         *   from 1 to 31 adjusted later, or -1 adjusted earlier from the last day of the month
         * @param dayOfWeek  the day of week to adjust to, null if day of month should not be adjusted
         * @param time  the time that the transition occurs as defined by timeDefintion, not null
         * @param timeDefinition  the definition of how to convert local to actual time, not null
         * @param savingAmount  the amount of saving from the standard offset, not null
         * @throws IllegalStateException if the window already has fixed savings
         * @throws IllegalStateException if the window has reached the maximum capacity of 2000 rules
         */
        void addRule(
                int startYear,
                int endYear,
                MonthOfYear month,
                int dayOfMonth,
                DayOfWeek dayOfWeek,
                LocalTime time,
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
                TZRule rule = new TZRule(year, month, dayOfMonth, dayOfWeek, time, timeDefinition, savingAmount);
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
                throw new IllegalStateException("Windows must be added in date-time order");
            }
        }

        /**
         * Adds rules to make the last rules all start from the same year.
         * Also add one more year to avoid weird case where penultimate year has odd offset.
         *
         * @return the start year of the last year rules, MAX_YEAR if no last rules
         * @throws IllegalStateException if there is only one rule defined as being forever
         */
        Year tidyLastRules() {
            if (lastRuleList.size() == 1) {
                throw new IllegalStateException("Cannot have only one rule defined as being forever");
            }
            if (isForever()) {
                // handle unusual offsets in year before rule starts
                for (TZRule lastRule : lastRuleList) {
                    addRule(lastRule.year, maxLastRuleStartYear, lastRule.month, lastRule.dayOfMonth,
                        lastRule.dayOfWeek, lastRule.time, lastRule.timeDefinition, lastRule.savingAmount);
                    lastRule.year = maxLastRuleStartYear + 1;
                }
                if (maxLastRuleStartYear == Year.MAX_YEAR) {
                    lastRuleList.clear();
                    return null;
                } else {
                    maxLastRuleStartYear++;
                    return Year.isoYear(maxLastRuleStartYear);
                }
            } else {
                // convert all within the endYear limit
                int endYear = windowEnd.getYear().getValue();
                for (TZRule lastRule : lastRuleList) {
                    addRule(lastRule.year, endYear + 1, lastRule.month, lastRule.dayOfMonth,
                        lastRule.dayOfWeek, lastRule.time, lastRule.timeDefinition, lastRule.savingAmount);
                }
                lastRuleList.clear();
                maxLastRuleStartYear = Year.MAX_YEAR;
                return null;
            }
        }

        /**
         * Checks if the window is the forever window.
         *
         * @return true if forever
         */
        boolean isForever() {
            return windowEnd.equals(MAX_DATE_TIME);
        }

        /**
         * Creates the offset date-time for the local date-time at the end of the window.
         *
         * @param wallOffset  the wall offset, not null
         * @return the created offset date-time in the wall offset, never null
         */
        OffsetDateTime createDateTime(ZoneOffset wallOffset) {
            return timeDefinition.createDateTime(windowEnd, standardOffset, wallOffset);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of the way a local time can be converted to an offset time.
     */
    static class TZRule implements Comparable<TZRule> {
        /** The year. */
        private int year;
        /** The month. */
        private final MonthOfYear month;
        /** The day of month. */
        private final int dayOfMonth;
        /** The day of month. */
        private final DayOfWeek dayOfWeek;
        /** The local time. */
        private final LocalTime time;
        /** The type of the time. */
        private final TimeDefinition timeDefinition;
        /** The amount of the saving to be applied after this point. */
        private final Period savingAmount;

        /**
         * Constructor.
         *
         * @param year  the year
         * @param month  the month, not null
         * @param dayOfMonth  the day of month
         * @param dayOfWeek  the day of week, null if day of month is exact
         * @param time  the time, not null
         * @param timeDefinition  the time definition, not null
         * @param savingAfter  the savings amount, not null
         */
        TZRule(int year, MonthOfYear month, int dayOfMonth,
                DayOfWeek dayOfWeek, LocalTime time,
                TimeDefinition timeDefinition, Period savingAfter) {
            super();
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
            this.dayOfWeek = dayOfWeek;
            this.time = time;
            this.timeDefinition = timeDefinition;
            this.savingAmount = savingAfter;
        }

        /**
         * Converts this to a transition.
         *
         * @param standardOffset  the active standard offset, not null
         * @param wallOffset  the active wall offset, not null
         * @return the transition, never null
         */
        Transition toTransition(ZoneOffset standardOffset, ZoneOffset wallOffset) {
            ZoneOffset offsetAfter = standardOffset.plus(savingAmount);
            LocalDate date;
            if (dayOfMonth == -1) {
                Year yr = Year.isoYear(year);
                date = LocalDate.date(yr, month, month.getLastDayOfMonth(yr));
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.previousOrCurrent(dayOfWeek));
                }
            } else {
                date = LocalDate.date(year, month, dayOfMonth);
                if (dayOfWeek != null) {
                    date = date.with(DateAdjusters.nextOrCurrent(dayOfWeek));
                }
            }
            LocalDateTime ldt = LocalDateTime.dateTime(date, time);
            OffsetDateTime dt = timeDefinition.createDateTime(ldt, standardOffset, wallOffset);
            return new Transition(dt, offsetAfter);
        }

        /**
         * Converts this to a transition rule.
         *
         * @param time  the calculated local time, not null
         * @param offsetBefore  the offset before the transition, not null
         * @param offsetAfter  the offset after the transition, not null
         * @return the transition, never null
         */
        TransitionRule toTransitionRule(LocalTime time, ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            DayOfMonth dom;
            if (dayOfMonth == -1) {
                if (month == MonthOfYear.FEBRUARY) {
                    dom = null;
                } else {
                    dom = DayOfMonth.dayOfMonth(month.maxLengthInDays() - 6);
                }
            } else {
                dom = DayOfMonth.dayOfMonth(dayOfMonth);
            }
            return new TransitionRule(month, dom, dayOfWeek, time, offsetBefore, offsetAfter);
        }

        /** {@inheritDoc}. */
        public int compareTo(TZRule other) {
            int cmp = year - other.year;
            cmp = (cmp == 0 ? month.compareTo(other.month) : cmp);
            cmp = (cmp == 0 ? dayOfMonth - other.dayOfMonth : cmp);
            cmp = (cmp == 0 ? time.compareTo(other.time) : cmp);
            return cmp;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A definition of the way a local time can be converted to an offset time.
     */
    static class AliasZone extends TimeZone {
        /**
         * A serialization identifier for this class.
         */
        private static final long serialVersionUID = 93618758758127L;
        /**
         * The time zone being aliased.
         */
        private final String baseZoneId;

        /**
         * Constructor.
         *
         * @param id  the time zone id, not null
         * @param baseZoneId  the id of the base zone, not null
         */
        AliasZone(String id, String baseZoneId) {
            super(id);
            this.baseZoneId = baseZoneId;
        }

        /** {@inheritDoc}. */
        @Override
        public ZoneOffset getOffset(InstantProvider instantProvider) {
            return TimeZone.timeZone(baseZoneId).getOffset(instantProvider);
        }

        /** {@inheritDoc}. */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
            return TimeZone.timeZone(baseZoneId).getOffsetInfo(dateTime);
        }

        /** {@inheritDoc}. */
        @Override
        public ZoneOffset getStandardOffset(InstantProvider instantProvider) {
            return TimeZone.timeZone(baseZoneId).getStandardOffset(instantProvider);
        }
    }

}
