/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import static javax.time.period.PeriodUnits.DAYS;
import static javax.time.period.PeriodUnits.DECADES;
import static javax.time.period.PeriodUnits.MONTHS;
import static javax.time.period.PeriodUnits.WEEKS;
import static javax.time.period.PeriodUnits.YEARS;

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
 * The Japanese Imperial calendar system.
 * <p>
 * Japanesehronology defines the rules of the Japanese Imperial calendar system.
 * Only Keio (1865-04-07 - 1868-09-07) and later eras are supported.
 * Older eras are recognized as unknown era, and the year of era of
 * unknown era is Gregorian year.
 * <p>
 * Japanesehronology is thread-safe and immutable.
 *
 * @author Ryoji Suzuki
 */
public final class JapaneseChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -4760300484384995747L;

    /**
     * The singleton instance of <code>JapaneseChronology</code>.
     */
    public static final JapaneseChronology INSTANCE = new JapaneseChronology();

    /**
     * Containing the offset from the ISO year.
     */
    static final int YEAR_OFFSET = JapaneseEra.SHOWA.getYearOffset();

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
    private static final String TARGET_LANGUAGE = "ja";

    /**
     * Name data.
     */
    static {
        ERA_NARROW_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_NARROW_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "K", "M", "T", "S", "H"});
        ERA_SHORT_NAMES.put(TARGET_LANGUAGE, new String[]{"Unknown", "\u6176", "\u660e", "\u5927", "\u662d", "\u5e73"});
        ERA_FULL_NAMES.put(FALLBACK_LANGUAGE, new String[]{"Unknown", "Keio", "Meiji", "Taisho", "Showa", "Heisei"});
        ERA_FULL_NAMES.put(TARGET_LANGUAGE,
                new String[]{"Unknown", "\u6176\u5fdc", "\u660e\u6cbb", "\u5927\u6b63", "\u662d\u548c", "\u5e73\u6210"});
    }

    /**
     * Restrictive constructor.
     */
    private JapaneseChronology() {
    }
    
    /**
     * Resolves singleton.
     *
     * @return the singleton instance
     */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Gets the name of the chronology.
     *
     * @return the name of the chronology, never null
     */
    @Override
    public String getName() {
        return "Japanese";
    }

    /**
     * Gets the rule for the era field in the Japanese chronology.
     *
     * @return the rule for the year field, never null
     */
    public DateTimeFieldRule era() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Japanese chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public DateTimeFieldRule yearOfEra() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year field in the Japanese chronology.
     * <p>
     * The values for the year field match those for year of SHOWA era that contains 1970-01-01.
     * For the previous era, TAISHO, the values run backwards and include zero, thus
     * TAISHO 14 has the value 0, and TAISHO 13 has the value -1.
     *
     * @return the rule for the year field, never null
     */
    @Override
    public DateTimeFieldRule year() {
        return YearRule.INSTANCE;
    }

    /**
     * Gets the rule for the month of year field in the Japanese chronology.
     *
     * @return the rule for the month of year field, never null
     */
    @Override
    public DateTimeFieldRule monthOfYear() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of month field in the Japanese chronology.
     *
     * @return the rule for the day of month field, never null
     */
    @Override
    public DateTimeFieldRule dayOfMonth() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of year field in the Japanese chronology.
     *
     * @return the rule for the day of year field, never null
     */
    @Override
    public DateTimeFieldRule dayOfYear() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day of week field in the Japanese chronology.
     *
     * @return the rule for the day of week field, never null
     */
    @Override
    public DateTimeFieldRule dayOfWeek() {
        return DayOfWeekRule.INSTANCE;
    }

    /**
     * The hour of day field is not supported by the Japanese chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule hourOfDay() {
        throw new UnsupportedOperationException("JapaneseChronology does not support the hour of day field");
    }

    /**
     * The minute of hour field is not supported by the Japanese chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule minuteOfHour() {
        throw new UnsupportedOperationException("JapaneseChronology does not support the minute of hour field");
    }

    /**
     * The second of minute field is not supported by the Japanese chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule secondOfMinute() {
        throw new UnsupportedOperationException("JapaneseChronology does not support the second of minute field");
    }

    /**
     * The nano of second field is not supported by the Japanese chronology.
     *
     * @return never
     * @throws UnsupportedOperationException always
     */
    @Override
    public DateTimeFieldRule nanoOfSecond() {
        throw new UnsupportedOperationException("JapaneseChronology does not support the nano of second field");
    }

    /**
     * Rule implementation.
     */
    private static final class YearRule extends DateTimeFieldRule implements
            Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new YearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearRule() {
            super(JapaneseChronology.INSTANCE, "Year", YEARS,
                    null, -JapaneseDate.MAX_YEAR_OF_ERA + 1, JapaneseDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : date.getYear() - YEAR_OFFSET);
        }
        /*
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearOfEraVal = JapaneseChronology.INSTANCE.yearOfEra().getValueQuiet(fieldMap);
            Integer eraVal = JapaneseChronology.INSTANCE.era().getValueQuiet(fieldMap);
            if (yearOfEraVal == null || eraVal == null) {
                return null;
            }
            JapaneseEra era = JapaneseEra.japaneseEra(eraVal);
            int year = yearOfEraVal.intValue() + era.getYearOffset() - YEAR_OFFSET;
            return year;
        }
        */
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(JapaneseChronology.INSTANCE
                    .monthOfYear());
            Integer domVal = merger.getValueQuiet(JapaneseChronology.INSTANCE
                    .dayOfMonth());
            if (moyVal != null && domVal != null) {
                int yearVal = merger.getValue(this);
                int year = yearVal + YEAR_OFFSET;
                JapaneseEra era = JapaneseEra.japaneseEra(LocalDate.date(year, moyVal, domVal));
                int yearOfEra = year - era.getYearOffset();
                JapaneseDate date;
                if (merger.isStrict()) {
                    date = JapaneseDate.japaneseDate(era, yearOfEra, moyVal,
                            domVal);
                } else {
                    date = JapaneseDate.japaneseDate(era, yearOfEra, 1, 1)
                            .plusMonths(moyVal).plusMonths(-1).plusDays(domVal)
                            .plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE
                        .monthOfYear());
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE
                        .dayOfMonth());
            }
        }
    }

    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeFieldRule implements
            Serializable {
        /** Singleton instance. */
        private static final DateTimeFieldRule INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            // Use DECACES for now as there is no ERAS defined.
            super(JapaneseChronology.INSTANCE, "Era", DECADES, null, -3, 2);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : JapaneseDate.japaneseDate(date)
                    .getEra().getValue());
        }
        /*
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = JapaneseChronology.INSTANCE.year().getValueQuiet(fieldMap);
            Integer moyVal = JapaneseChronology.INSTANCE.monthOfYear().getValueQuiet(fieldMap);
            Integer domVal = JapaneseChronology.INSTANCE.dayOfMonth().getValueQuiet(fieldMap);
            if (yearVal == null || moyVal == null || domVal == null) {
                return null;
            }
            int year = yearVal + YEAR_OFFSET;
            JapaneseEra era = JapaneseEra.japaneseEra(LocalDate.date(year, moyVal, domVal));
            return era.getValue();
        }
        */
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
            value = value + 3; // -3 is the min value.
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
            super(JapaneseChronology.INSTANCE, "YearOfEra", YEARS, null,
                    JapaneseDate.MIN_YEAR_OF_ERA, JapaneseDate.MAX_YEAR_OF_ERA);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : JapaneseDate.japaneseDate(date).getYearOfEra());
        }
        @Override
        protected Integer deriveValue(FieldMap fieldMap) {
            Integer yearVal = JapaneseChronology.INSTANCE.year().getValueQuiet(fieldMap);
            Integer eraVal = JapaneseChronology.INSTANCE.era().getValueQuiet(fieldMap);
            if (yearVal == null || eraVal == null) {
                return null;
            }
            JapaneseEra era = JapaneseEra.japaneseEra(eraVal);
            int yearOfEra = yearVal + YEAR_OFFSET - era.getYearOffset();
            return yearOfEra;
        }
        @Override
        protected void mergeFields(Calendrical.Merger merger) {
            Integer eraVal = merger.getValueQuiet(JapaneseChronology.INSTANCE.era());
            JapaneseEra era = (eraVal != null ? JapaneseEra.japaneseEra(eraVal) : JapaneseEra.SHOWA);
            int yearOfEra = merger.getValue(this);
            int year = era.getYearOffset() + yearOfEra - YEAR_OFFSET;
            //System.out.println(merger.getValueQuiet(JapaneseChronology.INSTANCE.year()));
            //System.out.println(merger.getValueQuiet(JapaneseChronology.INSTANCE.yearOfEra()));
            merger.storeMergedField(JapaneseChronology.INSTANCE.year(), year);
            if (eraVal != null) {
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.era());
            }
            merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.yearOfEra());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer moyVal = merger.getValueQuiet(JapaneseChronology.INSTANCE.monthOfYear());
            Integer domVal = merger.getValueQuiet(JapaneseChronology.INSTANCE.dayOfMonth());
            if (moyVal != null && domVal != null) {
                Integer eraVal = merger.getValueQuiet(JapaneseChronology.INSTANCE.era());
                JapaneseEra era = (eraVal != null ? JapaneseEra.japaneseEra(eraVal) : JapaneseEra.HEISEI);
                int yearOfEra = merger.getValue(this);
                JapaneseDate date;
                if (merger.isStrict()) {
                    date = JapaneseDate.japaneseDate(era, yearOfEra, moyVal, domVal);
                } else {
                    date = JapaneseDate.japaneseDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                if (eraVal != null) {
                    merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.era());
                }
                merger.markFieldAsProcessed(this);
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.monthOfYear());
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.dayOfMonth());
            }
        }
    }
    
    /**
     * Rule implementation.
     */
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
            super(JapaneseChronology.INSTANCE, "MonthOfYear", MONTHS, YEARS, 1, 12);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : JapaneseDate.japaneseDate(date).getMonthOfYear());
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
            super(JapaneseChronology.INSTANCE, "DayOfMonth", DAYS, MONTHS, 1, 31);
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
            Integer year = calendrical.deriveValueQuiet(JapaneseChronology.INSTANCE.year());
            Integer moy = calendrical.deriveValueQuiet(JapaneseChronology.INSTANCE.monthOfYear());
            if (year != null && moy != null) {
                int isoYear = year + YEAR_OFFSET;
                MonthOfYear month = MonthOfYear.monthOfYear(moy);
                return month.lengthInDays(isoYear);
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : JapaneseDate.japaneseDate(date).getDayOfMonth());
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
            super(JapaneseChronology.INSTANCE, "DayOfYear", DAYS, YEARS, 1, 366);
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
            Integer yearVal = calendrical.deriveValueQuiet(JapaneseChronology.INSTANCE.year());
            if (yearVal != null) {
                int isoYear = yearVal + YEAR_OFFSET;
                Year year = Year.isoYear(isoYear);
                return year.lengthInDays();
            }
            return getMaximumValue();
        }
        @Override
        public Integer getValueQuiet(LocalDate date, LocalTime time) {
            return (date == null ? null : JapaneseDate.japaneseDate(date).getDayOfYear());
        }
        @Override
        protected void mergeDateTime(Calendrical.Merger merger) {
            Integer eraVal = merger.getValueQuiet(JapaneseChronology.INSTANCE
                    .era());
            Integer yearOfEraVal = merger.getValueQuiet(JapaneseChronology.INSTANCE.yearOfEra());
            if (eraVal != null && yearOfEraVal != null) {
                JapaneseEra era = JapaneseEra.japaneseEra(eraVal);
                int doyVal = merger.getValue(this);
                JapaneseDate date;
                if (merger.isStrict()) {
                    date = JapaneseDate.japaneseDate(era, yearOfEraVal, 1, 1).withDayOfYear(doyVal);
                } else {
                    date = JapaneseDate.japaneseDate(era, yearOfEraVal, 1, 1).plusDays(doyVal).plusDays(-1);
                }
                merger.storeMergedDate(date.toLocalDate());
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.era());
                merger.markFieldAsProcessed(JapaneseChronology.INSTANCE.yearOfEra());
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
            super(JapaneseChronology.INSTANCE, "DayOfWeek", DAYS, WEEKS, 1, 7);
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
