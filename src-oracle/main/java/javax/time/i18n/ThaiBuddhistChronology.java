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
 * The Thai Buddhist calendar system.
 * <p>
 * ThaiBuddhistChronology defines the rules of the Thai Buddhist calendar
 * system.
 * <p>
 * ThaiBuddhistChronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 */
public final class ThaiBuddhistChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of <code>ThaiBuddhistChronology</code>.
     */
    public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = -543;

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
    private static final String TARGET_LANGUAGE = "th";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"BB", "BE"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"BB", "BE"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"B.B.", "B.E."});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e.\u0e28.",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Before Buddhist", "Budhhist Era"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"\u0e1e\u0e38\u0e17\u0e18\u0e28\u0e31\u0e01\u0e23\u0e32\u0e0a",
                "\u0e1b\u0e35\u0e01\u0e48\u0e2d\u0e19\u0e04\u0e23\u0e34\u0e2a\u0e15\u0e4c\u0e01\u0e32\u0e25\u0e17\u0e35\u0e48"});
    }

    //-----------------------------------------------------------------------
    /**
     * Restrictive constructor.
     */
    private ThaiBuddhistChronology() {
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
        return "ThaiBuddhist";
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for the era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year field, never null
     */
    public DateTimeFieldRule era() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public DateTimeFieldRule yearOfEra() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year field in the Thai Buddhist chronology.
     * <p>
     * The values for the year field match those for year of era in the current era.
     * For the previous era, the values run backwards and include zero, thus
     * BEFORE_BUDDHIST 1 has the value 0, and BEFORE_BUDDHIST 2 has the value -1.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Thai Buddhist chronology.
     *
     * @return the rule for the month of year field, never null
     */
    @Override
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Thai Buddhist chronology.
     *
     * @return the rule for the day of month field, never null
     */
    @Override
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Thai Buddhist chronology.
     *
     * @return the rule for the day of year field, never null
     */
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Thai Buddhist chronology.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * The hour of day field is not supported by the Thai Buddhist chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule hourOfDay() {
        throw new UnsupportedOperationException("ThaiBuddhistChronology does not support HourOfDay");
    }

    /**
     * The minute of hour field is not supported by the Thai Buddhist chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule minuteOfHour() {
        throw new UnsupportedOperationException("ThaiBuddhistChronology does not support MinuteOfHour");
    }

    /**
     * The second of minute field is not supported by the Thai Buddhist chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule secondOfMinute() {
        throw new UnsupportedOperationException("ThaiBuddhistChronology does not support SecondOfMinute");
   }

    /**
     * The nano of second field is not supported by the Thai Buddhist chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        throw new UnsupportedOperationException("ThaiBuddhistChronology does not support NanoOfSecond");
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
            super(ThaiBuddhistChronology.INSTANCE, "Year", YEARS, null,
                    -ThaiBuddhistDate.MAX_YEAR_OF_ERA + 1, ThaiBuddhistDate.MAX_YEAR_OF_ERA);
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
            Integer moyVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                int year = merger.getValue(this);
                int yearOfEra = Math.abs(year);
                ThaiBuddhistEra era = (year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST);
                ThaiBuddhistDate date;
                if (merger.isStrict()) {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
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
            super(ThaiBuddhistChronology.INSTANCE, "Era", DECADES, null, 0, 1);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ThaiBuddhistDate.thaiBuddhistDate(date).getEra().getValue());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = ThaiBuddhistChronology.INSTANCE.year().getValueQuiet(fieldMap);
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
            super(ThaiBuddhistChronology.INSTANCE, "YearOfEra", YEARS, null,
                    ThaiBuddhistDate.MIN_YEAR_OF_ERA, ThaiBuddhistDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ThaiBuddhistDate.thaiBuddhistDate(date).getYearOfEra());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = ThaiBuddhistChronology.INSTANCE.year().getValueQuiet(fieldMap);
            if (yearVal == null) {
                return null;
            }
            int year = yearVal;
            return (year < 1 ? -(year - 1) : year);
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer eraVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.era());
            ThaiBuddhistEra era = (eraVal != null ? ThaiBuddhistEra.thaiBuddhistEra(eraVal) : ThaiBuddhistEra.BUDDHIST);
            int yearOfEra = merger.getValue(this);
            int year = (era == ThaiBuddhistEra.BEFORE_BUDDHIST ? -yearOfEra + 1 : yearOfEra);
            merger.storeMergedField(ThaiBuddhistChronology.INSTANCE.year(), year);
            if (eraVal != null) {
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.era());
            }
            merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.yearOfEra());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                Integer eraVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.era());
                ThaiBuddhistEra era = (eraVal != null ? ThaiBuddhistEra.thaiBuddhistEra(eraVal) : ThaiBuddhistEra.BUDDHIST);
                int yearOfEra = merger.getValue(this);
                ThaiBuddhistDate date;
                if (merger.isStrict()) {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                if (eraVal != null) {
                    merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.era());
                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
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
            super(ThaiBuddhistChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ThaiBuddhistDate.thaiBuddhistDate(date).getMonthOfYear());
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
            super(ThaiBuddhistChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
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
            Integer year = calendrical.deriveValueQuiet(ThaiBuddhistChronology.INSTANCE.year());
            Integer moy = calendrical.deriveValueQuiet(ThaiBuddhistChronology.INSTANCE.monthOfYear());
            if (year != null && moy != null) {
                int isoYear = year + YEAR_OFFSET;
                MonthOfYear month = MonthOfYear.monthOfYear(moy);
                return month.lengthInDays(isoYear);
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ThaiBuddhistDate.thaiBuddhistDate(date).getDayOfMonth());
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
            super(ThaiBuddhistChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
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
            Integer yearVal = calendrical.deriveValueQuiet(ThaiBuddhistChronology.INSTANCE.year());
            if (yearVal != null) {
                int isoYear = yearVal + YEAR_OFFSET;
                Year year = Year.isoYear(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : ThaiBuddhistDate.thaiBuddhistDate(date).getDayOfYear());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer yearVal = merger.getValueQuiet(ThaiBuddhistChronology.INSTANCE.year());
            if (yearVal != null) {
                int doyVal = merger.getValue(this);
                int yearOfEra = Math.abs(yearVal);
                ThaiBuddhistEra era = (yearVal < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST);
                ThaiBuddhistDate date;
                if (merger.isStrict()) {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).withDayOfYear(doyVal);
                } else {
                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).plusDays(doyVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.year());
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
            super(ThaiBuddhistChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
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
