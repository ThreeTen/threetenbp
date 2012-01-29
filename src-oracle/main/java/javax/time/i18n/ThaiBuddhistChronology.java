/*
 * Copyright (c) 2009 Oracle All Rights Reserved.
 */
package javax.time.i18n;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

import javax.time.MonthOfYear;
import javax.time.Year;
import javax.time.calendar.format.TextStyle;
import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalEngine;
import javax.time.calendrical.Chronology;
import javax.time.calendrical.DateTimeField;
import javax.time.calendrical.DateTimeRule;
import javax.time.calendrical.DateTimeRuleRange;
import javax.time.calendrical.ISOPeriodUnit;
import javax.time.calendrical.PeriodUnit;

/**
 * The Thai Buddhist calendar system.
 * <p>
 * {@code ThaiBuddhistChronology} defines the rules of the Thai Buddhist calendar system.
 * <p>
 * The Thai Buddhist calendar system is the same as the ISO calendar system apart from the year.
 * <p>
 * ThaiBuddhistChronology is immutable and thread-safe.
 *
 * @author Ryoji Suzuki
 * @author Stephen Colebourne
 */
public final class ThaiBuddhistChronology extends Chronology implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 5856454970865881985L;

    /**
     * The singleton instance of {@code ThaiBuddhistChronology}.
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
//    /**
//     * Gets the rule for the year field in the Thai Buddhist chronology.
//     * <p>
//     * The values for the year field match those for year of era in the current era.
//     * For the previous era, the values run backwards and include zero, thus
//     * BEFORE_BUDDHIST 1 has the value 0, and BEFORE_BUDDHIST 2 has the value -1.
//     *
//     * @return the rule for the year field, never null
//     */
//    public static DateTimeFieldRule year() {
//        return YearRule.INSTANCE;
//    }

    /**
     * Gets the rule for the era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year field, never null
     */
    public static DateTimeRule eraRule() {
        return EraRule.INSTANCE;
    }

    /**
     * Gets the rule for the year of era field in the Thai Buddhist chronology.
     *
     * @return the rule for the year of era field, never null
     */
    public static DateTimeRule yearOfEraRule() {
        return YearOfEraRule.INSTANCE;
    }

    /**
     * Gets the rule for the month-of-year field in the Thai Buddhist chronology.
     *
     * @return the rule for the month-of-year field, never null
     */
    public static DateTimeRule monthOfYearRule() {
        return MonthOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-month field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-month field, never null
     */
    public static DateTimeRule dayOfMonthRule() {
        return DayOfMonthRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-year field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-year field, never null
     */
    public static DateTimeRule dayOfYearRule() {
        return DayOfYearRule.INSTANCE;
    }

    /**
     * Gets the rule for the day-of-week field in the Thai Buddhist chronology.
     *
     * @return the rule for the day-of-week field, never null
     */
    public static DateTimeRule dayOfWeekRule() {
        return DayOfWeekRule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit for eras.
     * <p>
     * The period unit defines the concept of a period of an era.
     * This is equivalent to the ISO eras period unit.
     * <p>
     * See {@link #eraRule()} for the main date-time field.
     *
     * @return the period unit for eras, never null
     */
    public static PeriodUnit periodEras() {
        return ISOPeriodUnit.ERAS;
    }

    /**
     * Gets the period unit for years.
     * <p>
     * The period unit defines the concept of a period of a year.
     * This is equivalent to the ISO years period unit.
     * <p>
     * See {@link #yearOfEraRule()} for the main date-time field.
     *
     * @return the period unit for years, never null
     */
    public static PeriodUnit periodYears() {
        return ISOPeriodUnit.YEARS;
    }

    /**
     * Gets the period unit for months.
     * <p>
     * The period unit defines the concept of a period of a month.
     * This is equivalent to the ISO months period unit.
     * <p>
     * See {@link #monthOfYearRule()} for the main date-time field.
     *
     * @return the period unit for months, never null
     */
    public static PeriodUnit periodMonths() {
        return ISOPeriodUnit.MONTHS;
    }

    /**
     * Gets the period unit for weeks.
     * <p>
     * The period unit defines the concept of a period of a week.
     * This is equivalent to the ISO weeks period unit.
     *
     * @return the period unit for weeks, never null
     */
    public static PeriodUnit periodWeeks() {
        return ISOPeriodUnit.WEEKS;
    }

    /**
     * Gets the period unit for days.
     * <p>
     * The period unit defines the concept of a period of a day.
     * This is equivalent to the ISO days period unit.
     * <p>
     * See {@link #dayOfMonthRule()} for the main date-time field.
     *
     * @return the period unit for days, never null
     */
    public static PeriodUnit periodDays() {
        return ISOPeriodUnit.DAYS;
    }

    //-----------------------------------------------------------------------
//    /**
//     * Rule implementation.
//     */
//    private static final class YearRule extends DateTimeFieldRule implements Serializable {
//        /** Singleton instance. */
//        private static final DateTimeFieldRule INSTANCE = new YearRule();
//        /** A serialization identifier for this class. */
//        private static final long serialVersionUID = 1L;
//        /** Constructor. */
//        private YearRule() {
//            super(Integer.class, ThaiBuddhistChronology.INSTANCE, "Year", YEARS, null,
//                    -ThaiBuddhistDate.MAX_YEAR_OF_ERA + 1, ThaiBuddhistDate.MAX_YEAR_OF_ERA);
//        }
//        private Object readResolve() {
//            return INSTANCE;
//        }
//        @Override
//        public Integer getValueQuiet(LocalDate date, LocalTime time) {
//            return (date == null ? null : date.getYear() - YEAR_OFFSET);
//        }
//        @Override
//        protected void mergeDateTime(Calendrical.Merger engine) {
//            Integer moyVal = engine.getValueQuiet(ThaiBuddhistChronology.INSTANCE.monthOfYear());
//            Integer domVal = engine.getValueQuiet(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
//            if (moyVal != null && domVal != null) {
//                int year = engine.getParsed(this);
//                int yearOfEra = Math.abs(year);
//                ThaiBuddhistEra era = (year < 1 ? ThaiBuddhistEra.BEFORE_BUDDHIST : ThaiBuddhistEra.BUDDHIST);
//                ThaiBuddhistDate date;
//                if (engine.isStrict()) {
//                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, moyVal, domVal);
//                } else {
//                    date = ThaiBuddhistDate.thaiBuddhistDate(era, yearOfEra, 1, 1).plusMonths(moyVal)
//                            .plusMonths(-1).plusDays(domVal).plusDays(-1);
//                }
//                engine.storeMergedDate(date.toLocalDate());
//                engine.markFieldAsProcessed(this);
//                engine.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.monthOfYear());
//                engine.markFieldAsProcessed(ThaiBuddhistChronology.INSTANCE.dayOfMonth());
//            }
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class EraRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new EraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private EraRule() {
            super("ThaiBuddhistEra", periodEras(), null, 0, 1, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getEra().getValue()) : null;
        }
        // TODO: never worked properly, needs to use proper provider
        @Override
        public String getText(long value, TextStyle textStyle, Locale locale) {
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
            return names == null ? Long.toString(value) : names[(int) value];
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class YearOfEraRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new YearOfEraRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private YearOfEraRule() {
            super("ThaiBuddhistYearOfEra", periodYears(), periodEras(),
                    ThaiBuddhistDate.MIN_YEAR_OF_ERA, ThaiBuddhistDate.MAX_YEAR_OF_ERA, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        // TODO: min/max years based on era
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getYearOfEra()) : null;
        }
        @Override
        protected void normalize(CalendricalEngine engine) {
            DateTimeField eraVal = engine.getFieldDerived(ThaiBuddhistChronology.eraRule(), false);
            ThaiBuddhistEra era = (eraVal != null ? ThaiBuddhistEra.of(eraVal.getValidIntValue()) : ThaiBuddhistEra.BUDDHIST);
            DateTimeField yoeVal = engine.getFieldDerived(this, false);
            // era, year, month, day-of-month
            DateTimeField moyVal = engine.getFieldDerived(ThaiBuddhistChronology.monthOfYearRule(), false);
            DateTimeField domVal = engine.getFieldDerived(ThaiBuddhistChronology.dayOfMonthRule(), false);
            if (moyVal != null && domVal != null) {
                ThaiBuddhistDate date = ThaiBuddhistDate.of(era, yoeVal.getValidIntValue(), MonthOfYear.of(moyVal.getValidIntValue()), domVal.getValidIntValue());
                engine.setDate(date.toLocalDate(), true);
//                engine.removeProcessed(ThaiBuddhistChronology.eraRule());
//                engine.removeProcessed(this);
//                engine.removeProcessed(ThaiBuddhistChronology.monthOfYearRule());
//                engine.removeProcessed(ThaiBuddhistChronology.dayOfMonthRule());
            }
            // era, year, day-of-year
            DateTimeField doyVal = engine.getFieldDerived(ThaiBuddhistChronology.dayOfYearRule(), false);
            if (doyVal != null) {
                ThaiBuddhistDate date = ThaiBuddhistDate.of(era, yoeVal.getValidIntValue(), MonthOfYear.JANUARY, 1).plusDays(doyVal.getValidIntValue() - 1);
                engine.setDate(date.toLocalDate(), true);
//                engine.removeProcessed(ThaiBuddhistChronology.eraRule());
//                engine.removeProcessed(this);
//                engine.removeProcessed(ThaiBuddhistChronology.yearOfEraRule());
//                engine.removeProcessed(ThaiBuddhistChronology.dayOfYearRule());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class MonthOfYearRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new MonthOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private MonthOfYearRule() {
            super("ThaiBuddhistMonthOfYear", periodMonths(), periodYears(), 1, 12, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getMonthOfYear().getValue()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfMonthRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfMonthRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfMonthRule() {
            super("ThaiBuddhistDayOfMonth", periodDays(), periodMonths(), DateTimeRuleRange.of(1, 28, 31), null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public DateTimeRuleRange getValueRange(Calendrical calendrical) {
            DateTimeField moyVal = calendrical.get(ThaiBuddhistChronology.monthOfYearRule());
            if (moyVal != null) {
                MonthOfYear moy = MonthOfYear.of(moyVal.getValidIntValue());
                if (moy == MonthOfYear.FEBRUARY) {
                    DateTimeField eraVal = calendrical.get(ThaiBuddhistEra.rule());
                    DateTimeField yoeVal = calendrical.get(ThaiBuddhistChronology.yearOfEraRule());
                    if (eraVal != null && yoeVal != null) {
                        int yoe = yoeVal.getValidIntValue();
                        int isoYear = (eraVal.getValidIntValue() == ThaiBuddhistEra.BEFORE_BUDDHIST.getValue() ? 1 - yoe : yoe) + YEAR_OFFSET;
                        return DateTimeRuleRange.of(1, moy.lengthInDays(Year.isLeap(isoYear)));
                    }
                    return DateTimeRuleRange.of(1, 28, 29);
                } else {
                    return DateTimeRuleRange.of(1, moy.maxLengthInDays());
                }
            }
            return getValueRange();
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getDayOfMonth()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfYearRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfYearRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfYearRule() {
            super("ThaiBuddhistDayOfYear", periodDays(), periodYears(), DateTimeRuleRange.of(1, 365, 366), null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        public DateTimeRuleRange getValueRange(Calendrical calendrical) {
            DateTimeField eraVal = calendrical.get(ThaiBuddhistEra.rule());
            DateTimeField yoeVal = calendrical.get(ThaiBuddhistChronology.yearOfEraRule());
            if (eraVal != null && yoeVal != null) {
                int yoe = yoeVal.getValidIntValue();
                int isoYear = (eraVal.getValidIntValue() == ThaiBuddhistEra.BEFORE_BUDDHIST.getValue() ? 1 - yoe : yoe) + YEAR_OFFSET;
                return DateTimeRuleRange.of(1, Year.isLeap(isoYear) ? 366 : 365);
            }
            return getValueRange();
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getDayOfYear()) : null;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Rule implementation.
     */
    private static final class DayOfWeekRule extends DateTimeRule implements Serializable {
        /** Singleton instance. */
        private static final DateTimeRule INSTANCE = new DayOfWeekRule();
        /** A serialization identifier for this class. */
        private static final long serialVersionUID = 1L;
        /** Constructor. */
        private DayOfWeekRule() {
            super("ThaiBuddhistDayOfWeek", periodDays(), periodWeeks(), 1, 7, null);
        }
        private Object readResolve() {
            return INSTANCE;
        }
        @Override
        protected DateTimeField deriveFrom(CalendricalEngine engine) {
            ThaiBuddhistDate date = engine.derive(ThaiBuddhistDate.rule());
            return date != null ? field(date.getDayOfWeek().getValue()) : null;
        }
    }

}
