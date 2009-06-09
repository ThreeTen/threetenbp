/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import static javax.time.period.PeriodUnits.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.calendar.Calendrical;
import javax.time.calendar.CalendricalProvider;
import javax.time.calendar.Chronology;
import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.Calendrical.FieldMap;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.Year;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

/**
 * The Minguo calendar system.
 * <p>
 * MinguoChronology defines the rules of the Minguo calendar
 * system.
 * <p>
 * MinguoChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class MinguoChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of <code>MinguoChronology</code>.
     */
    public static final MinguoChronology INSTANCE = new MinguoChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = 1911;

    /**
     * Narrow names for eras.
     */
    private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap<String, String[]>();

    /**
     * Short names for eras.
     */
    private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap<String, String[]>();

    /**
     * Full names for eras.
     */
    private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap<String, String[]>();

    /**
     * Fallback language for the era names.
     */
    private static final String FALLBACK_LANGUAGE = "en";

    /**
     * Language that has the era names.
     */
    private static final String TARGET_LANGUAGE = "zh";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BM", "AM"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"\u6c11\u570b", "\u6c11\u524d"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.M.", "A.M."});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE, new String[]{"\u6c11\u570b", "\u6c11\u524d"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Minguo", "Minguo"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE, new String[]{"\u4e2d\u83ef\u6c11\u570b", "\u6c11\u570b\u524d"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private MinguoChronology() {
    }

    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "Minguo";
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the era field in the Minguo chronology.
     *
     * @return the rule for the year field, never null
     */
    public DateTimeFieldRule era() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Minguo chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public DateTimeFieldRule yearOfEra() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year field in the Minguo chronology.
     * <p>
     * The values for the year field match those for year of era in the current era.
     * For the previous era, the values run backwards and include zero, thus
     * BEFORE_MINGUO 1 has the value 0, and BEFORE_MINGUO 2 has the value -1.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Minguo chronology.
     *
     * @return the rule for the month of year field, never null
     */
    @Override
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Minguo chronology.
     *
     * @return the rule for the day of month field, never null
     */
    @Override
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Minguo chronology.
     *
     * @return the rule for the day of year field, never null
     */
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Minguo chronology.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * The hour of day field is not supported by the Minguo chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule hourOfDay() {
        throw new UnsupportedOperationException("MinguoChronology does not support HourOfDay");
    }

    /**
     * The minute of hour field is not supported by the Minguo chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule minuteOfHour() {
        throw new UnsupportedOperationException("MinguoChronology does not support MinuteOfHour");
    }

    /**
     * The second of minute field is not supported by the Minguo chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule secondOfMinute() {
        throw new UnsupportedOperationException("MinguoChronology does not support SecondOfMinute");
    }

    /**
     * The nano of second field is not supported by the Minguo chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        throw new UnsupportedOperationException("MinguoChronology does not support NanoOfSecond");
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(MinguoChronology.INSTANCE, "Year", YEARS, null,
                    -MinguoDate.MAX_YEAR_OF_ERA + 1, MinguoDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getYear() - YEAR_OFFSET);
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(MinguoChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(MinguoChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                int year = merger.getValue(this);
                int yearOfEra = Math.abs(year);
                MinguoEra era = (year < 1 ? MinguoEra.BEFORE_MINGUO : MinguoEra.MINGUO);
                MinguoDate date;
                if (merger.isStrict()) {
                    date = MinguoDate.minguoDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = MinguoDate.minguoDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.dayOfMonth());
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            // Use DECADES for now as there is no ERAS defined.
            super(MinguoChronology.INSTANCE, "Era", DECADES, null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : MinguoDate.minguoDate(date).getEra().getValue());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = MinguoChronology.INSTANCE.year().getValueQuiet(fieldMap);
            if (yearVal == null) {
                return null;
            }
            return (yearVal < 1 ? 0 : 1);
        }
        /**
         * Gets the text for this field.
         * <p>
         * The value is queried using {@link #getValue(CalendricalProvider)}. The text
         * is then obtained for that value. If there is no textual mapping, then
         * the value is returned as per {@link Integer#toString()}.
         *
         * @param calendricalProvider  the calendrical provider, not null
         * @param locale  the locale to use, not null
         * @param textStyle  the text style, not null
         * @return the text of the field, never null
         * @throws UnsupportedCalendarFieldException if the value cannot be extracted
         */
        public String getText(CalendricalProvider calendricalProvider, Locale locale, TextStyle textStyle) {
            int value = getValue(calendricalProvider);
            String[] names = null;
            String language = locale.getLanguage();
            
            if (textStyle == TextStyle.NARROW) {
                names = ERA_NARROW_NAMES.get(language);
                if (names == null) {
                    names = ERA_NARROW_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            if (textStyle == TextStyle.SHORT) {
                names = ERA_SHORT_NAMES.get(language);
                if (names == null) {
                    names = ERA_SHORT_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            if (textStyle == TextStyle.FULL) {
                names = ERA_FULL_NAMES.get(language);
                if (names == null) {
                    names = ERA_FULL_NAMES.get(FALLBACK_LANGUAGE);
                }
            }
            return names == null ? Integer.toString(value) : names[value];
        }
    }

    /**
     * Rule implementation.
     */
    private static final class YearOfEraRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearOfEraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearOfEraRule() {
            super(MinguoChronology.INSTANCE, "YearOfEra", YEARS, null, MinguoDate.MIN_YEAR_OF_ERA, MinguoDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : MinguoDate.minguoDate(date).getYearOfEra());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = MinguoChronology.INSTANCE.year().getValueQuiet(fieldMap);
            if (yearVal == null) {
                return null;
            }
            int year = yearVal;
            return (year < 1 ? -(year - 1) : year);
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer eraVal = merger.getValueQuiet(MinguoChronology.INSTANCE.era());
            MinguoEra era = (eraVal != null ? MinguoEra.minguoEra(eraVal) : MinguoEra.MINGUO);
            int yearOfEra = merger.getValue(this);
            int year = (era == MinguoEra.BEFORE_MINGUO ? -yearOfEra + 1 : yearOfEra);
            merger.storeMergedField(MinguoChronology.INSTANCE.year(), year);
            if (eraVal != null) {
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.era());
            }
            merger.markFieldAsProcessed(MinguoChronology.INSTANCE.yearOfEra());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(MinguoChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(MinguoChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                Integer eraVal = merger.getValueQuiet(MinguoChronology.INSTANCE.era());
                MinguoEra era = (eraVal != null ? MinguoEra.minguoEra(eraVal) : MinguoEra.MINGUO);
                int yearOfEra = merger.getValue(this);
                MinguoDate date;
                if (merger.isStrict()) {
                    date = MinguoDate.minguoDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = MinguoDate.minguoDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                if (eraVal != null) {
                    merger.markFieldAsProcessed(MinguoChronology.INSTANCE.era());
                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.dayOfMonth());
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super(MinguoChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : MinguoDate.minguoDate(date).getMonthOfYear());
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeFieldRule
            implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super(MinguoChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 28;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer year = calendrical.deriveValueQuiet(MinguoChronology.INSTANCE.year());
            Integer moy = calendrical.deriveValueQuiet(MinguoChronology.INSTANCE.monthOfYear());
            if (year != null && moy != null) {
                int isoYear = year + YEAR_OFFSET;
                MonthOfYear month = MonthOfYear.monthOfYear(moy);
                return month.lengthInDays(isoYear);
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : MinguoDate.minguoDate(date).getDayOfMonth());
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeFieldRule
            implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super(MinguoChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public int getSmallestMaximumValue() {
            return 365;
        }
        @Override
        public int getMaximumValue(Calendrical calendrical) {
            Integer yearVal = calendrical.deriveValueQuiet(MinguoChronology.INSTANCE.year());
            if (yearVal != null) {
                int isoYear = yearVal + YEAR_OFFSET;
                Year year = Year.isoYear(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : MinguoDate.minguoDate(date).getDayOfYear());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer yearVal = merger.getValueQuiet(MinguoChronology.INSTANCE.year());
            if (yearVal != null) {
                int doyVal = merger.getValue(this);
                int yearOfEra = Math.abs(yearVal);
                MinguoEra era = (yearVal < 1 ? MinguoEra.BEFORE_MINGUO : MinguoEra.MINGUO);
                MinguoDate date;
                if (merger.isStrict()) {
                    date = MinguoDate.minguoDate(era, yearOfEra, 1, 1).withDayOfYear(doyVal);
                } else {
                    date = MinguoDate.minguoDate(era, yearOfEra, 1, 1).plusDays(doyVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(MinguoChronology.INSTANCE.year());
                merger.markFieldAsProcessed(this);
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeFieldRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super(MinguoChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getDayOfWeek().getValue());
        }
    }
}
