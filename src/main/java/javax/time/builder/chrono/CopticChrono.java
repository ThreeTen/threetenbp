/*
 * Copyright (c) 2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.builder.chrono;

import java.io.Serializable;

import javax.time.LocalDate;

/**
 * The Coptic calendar system.
 * <p>
 * This chronology defines the rules of the Coptic calendar system.
 * The Coptic calendar has twelve months of 30 days followed by an additional
 * period of 5 or 6 days, modeled as the thirteenth month in this implementation.
 * <p>
 * Years are measured in the 'Era of the Martyrs' - AM.
 * 0001-01-01 (Coptic) equals 0284-08-29 (ISO).
 * The supported range is from 1 to 99999999 (inclusive) in both eras.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class CopticChrono extends Chrono implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance.
     */
    public static final CopticChrono INSTANCE = new CopticChrono();

//    /**
//     * There are 13 months in the Coptic year.
//     */
//    private static final int MONTHS_PER_YEAR = 13;
//    /**
//     * This is a Non-proleptic Calendar, and it starts at year 1.
//     */
//    private static final int MIN_YEAR = 1;
//    /**
//     * The maximum permitted year.
//     */
//    private static final int MAX_YEAR = 999999999;
//    /**
//     * The minimum permitted epoch-month.
//     */
//    private static final long MIN_EPOCH_MONTH = (MIN_YEAR - 1970L) * MONTHS_PER_YEAR;
//    /**
//     * The maximum permitted epoch-month.
//     */
//    private static final long MAX_EPOCH_MONTH = (MAX_YEAR - 1970L) * MONTHS_PER_YEAR - 1L;
//    /**
//     * The minimum permitted epoch-day.
//     */
//    private static final long MIN_EPOCH_DAY = 0;
//    /**
//     * The maximum permitted epoch-day.
//     */
//    private static final long MAX_EPOCH_DAY = (long) (MAX_YEAR * 365.25);
//    
//    private static final long EPOCH_DAYS_OFFSET = 615558;
//    
//    private static final long YEAR_BLOCK = 3 * 365 + 366;

    /**
     * Restricted constructor.
     */
    private CopticChrono() {
    }

    /**
     * Resolve singleton.
     * 
     * @return the singleton instance, not null
     */
    private Object readResolve() {
        return INSTANCE;
    }

    //-----------------------------------------------------------------------
    @Override
    public String getName() {
        return "Coptic";
    }

    //-----------------------------------------------------------------------
    @Override
    public int getField(ChronoField field, ChronoDate<?> chronoDate) {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoDate<CopticChrono> createDate(LocalDate date) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoDate<CopticChrono> createDate(int prolepticYear, int monthOfYear, int dayOfMonth) {
        return null;
    }

    @Override
    public int getDayOfYear(ChronoDate<?> date) {
        return 0;
    }

    @Override
    public boolean isLeapYear(ChronoDate<?> date) {
        int year = date.getProlepticYear();
        return ((year % 4) == 3);  // TODO: negatives
    }

    @Override
    public CopticEra createEra(int eraValue) {
        return CopticEra.of(eraValue);
    }

//    //-----------------------------------------------------------------------
//    @Override
//    public DateTimeRuleRange getDateValueRange(DateTimeField field, LocalDate date) {
//        if (field instanceof ChronoField) {
//            ChronoField standardField = (ChronoField) field;
//            switch (standardField) {
//                case ERA: return DateTimeRuleRange.of(0, 1);
//                case YEAR: return DateTimeRuleRange.of(MIN_YEAR, MAX_YEAR);
//                case YEAR_OF_ERA: return DateTimeRuleRange.of(1, MAX_YEAR);
//                case EPOCH_MONTH: return DateTimeRuleRange.of(MIN_EPOCH_MONTH, MAX_EPOCH_MONTH);
//                case MONTH_OF_YEAR: return DateTimeRuleRange.of(1, 13);
//                case EPOCH_DAY: return DateTimeRuleRange.of(MIN_EPOCH_DAY, MAX_EPOCH_DAY);
//                case DAY_OF_MONTH: {
//                    if (date != null) {
//                        if (getMonthOfYear(date) == 13) {
//                            return DateTimeRuleRange.of(1, isLeapYear(getYear(date)) ? 6 : 5);
//                        }
//                        return DateTimeRuleRange.of(1, 30);
//                    }
//                    return DateTimeRuleRange.of(1, 5, 30);
//                }
//                case DAY_OF_YEAR: {
//                    if (date != null) {
//                        return DateTimeRuleRange.of(1, isLeapYear(getYear(date)) ? 366 : 365);
//                    }
//                    return DateTimeRuleRange.of(1, 365, 366);
//                }
//                case DAY_OF_WEEK: return DateTimeRuleRange.of(1, 7);
//            }
//        }
//        return field.implementationRules(this).getDateValueRange(field, date);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Checks if the specified year is a leap year.
//     * <p>
//     * A year is leap if the remainder after division by four equals three.
//     * This method does not validate the year passed in, and only has a
//     * well-defined result for years in the supported range.
//     *
//     * @param year  the year to check, not validated for range
//     * @return true if the year is a leap year
//     */
//    public static boolean isLeapYear(long year) {
//        return ((year % 4) == 3);
//    }
//
//    //-----------------------------------------------------------------------
//    @Override
//    public long getDateValue(LocalDate date, DateTimeField field) {
//        if (field instanceof ChronoField) {
//            switch ((ChronoField) field) {
//                case ERA: return (getYear(date) > 0 ? 1 : 0);   // TODO
//                case YEAR: return getYear(date);
//                case YEAR_OF_ERA: return (getYear(date) > 0 ? getYear(date) : 1 - getYear(date));   // TODO
//                case EPOCH_MONTH: return ((getYear(date) - 1970) * 13L) + getMonthOfYear(date);
//                case MONTH_OF_YEAR: return getMonthOfYear(date);
//                case EPOCH_DAY: return getEpochDay(date);
//                case DAY_OF_MONTH: return getDayOfMonth(date);
//                case DAY_OF_YEAR: return getDayOfYear(date);
//                case DAY_OF_WEEK: return date.getDayOfWeek().getValue();
//            }
//            throw new CalendricalException("Unsupported field");
//        }
//        return field.implementationRules(this).getDateValue(date, field);
//    }
//
//    /**
//     * Abstracted common logic, readability.
//     */
//    private long getMonthOfYear(LocalDate date) {
//        return (getDayOfYear(date) / 30) + 1;
//    }
//    
//    private long getYear(LocalDate date) {
//        long epochDay = getEpochDay(date);
//        long yearPrefix = (epochDay / YEAR_BLOCK) * 4;
//        long yearSuffix = (epochDay % YEAR_BLOCK) / 365;
//        return 1 + (yearPrefix + yearSuffix);
//    }
//    
//    private long getDayOfYear(LocalDate date) {
//        long blockNumber = getEpochDay(date) % YEAR_BLOCK;
//        long leapSum = (blockNumber / 365) == 3 ? 0 : 1;
//        return leapSum + (blockNumber % 365);
//    }
//    
//    private long getEpochDay(LocalDate date) {
//        return EPOCH_DAYS_OFFSET + date.toEpochDay();
//    }
//    
//    private long getDayOfMonth(LocalDate date) {
//        return ((getDayOfYear(date) - 1) % 30) + 1;
//    }
//
//    //-----------------------------------------------------------------------
//    @Override
//    public LocalDate setDate(LocalDate date, DateTimeField field, long newValue) {
//        if (field instanceof ChronoField) {
//            if (getDateValueRange(field, date).isValidValue(newValue) == false) {
//                throw new IllegalArgumentException();  // TODO
//            }
//            switch ((ChronoField) field) {
//                case ERA: {   // TODO
//                    if ((getYear(date) > 0 && newValue == 0) && (getYear(date)) <= 0 && newValue == 1) {
//                        return date.withYear(1 - safeToInt(getYear(date)));
//                    }
//                    return date;
//                }
//                case YEAR: return setYear(date, newValue);
//                case YEAR_OF_ERA: throw new UnsupportedOperationException("Not implemented yet");
//                case EPOCH_MONTH: return setMonthOfYear(setYear(date, newValue / MONTHS_PER_YEAR), newValue % MONTHS_PER_YEAR);
//                case MONTH_OF_YEAR: return setMonthOfYear(date, newValue);
//                case EPOCH_DAY: return LocalDate.ofEpochDay(newValue - 615558);
//                case DAY_OF_MONTH: return setDayOfYear(date, safeToInt((getMonthOfYear(date) - 1) * 30 + newValue));
//                case DAY_OF_YEAR: return setDayOfYear(date, newValue);
//                case DAY_OF_WEEK: return date.plusDays(newValue - date.getDayOfWeek().getValue());
//            }
//            throw new CalendricalException("Unsupported field on LocalDate: " + field);
//        }
//        return field.implementationRules(this).setDate(date, field, newValue);
//    }
//    
//    private LocalDate setYear(LocalDate date, long newValue) {
//        long diff = newValue - getYear(date);
//        return date.plusYears(diff);
//    }
//    
//    private LocalDate setDayOfYear(LocalDate date, long newValue) {
//        long diff = newValue - getDayOfYear(date);
//        return date.plusDays(diff);
//    }
//    
//    private LocalDate setMonthOfYear(LocalDate date, long newValue) {
//        long diff = newValue - getMonthOfYear(date);
//        return date.plusMonths(diff);
//    }
//
//    //-----------------------------------------------------------------------
//    @Override
//    public LocalDate setDateLenient(LocalDate date, DateTimeField field, long newValue) {
//        return null;
//    }
//
//    //-----------------------------------------------------------------------    
//    @Override
//    public LocalDate addToDate(LocalDate date, PeriodUnit unit, long amount) {
//        if (unit instanceof StandardPeriodUnit) {
//            StandardPeriodUnit std = (StandardPeriodUnit) unit;
//            long months;
//            switch (std) {
//                case DAYS: return date.plusDays(amount);
//                case WEEKS: return date.plusWeeks(amount);
//                case MONTHS: months = amount; break;
//                case YEARS: months = MathUtils.safeMultiply(amount, 13); break;
//                case DECADES: months = MathUtils.safeMultiply(amount, 130); break;
//                case CENTURIES: months = MathUtils.safeMultiply(amount, 1300); break;
//                case MILLENIA: months = MathUtils.safeMultiply(amount, 13000); break;
//                case ERAS: {
//                    if (amount == 0) {
//                        return date;
//                    } else if (amount == 1 && getYear(date) <= 0) {
//                        return setYear(date, 1 - getYear(date));
//                    } else if (amount == -1 && getYear(date) > 0) {
//                        return setYear(date, 1 - getYear(date));
//                    } else {
//                        throw new CalendricalException("Unable to add eras: " + amount);
//                    }
//                }
//                default:
//                    throw new CalendricalException("Unsupported unit: " + unit);
//            }
//            long cycles = months / 52;  // 4 years of 13 months
//            date = date.plusDays(MathUtils.safeMultiply(cycles, 365 * 4 + 1));
//            months = months % 52;
//            return date; // TODO
//        }
//        return unit.implementationRules(this).addToDate(date, unit, amount);
//    }
//
//    //-----------------------------------------------------------------------
//    @Override
//    public long getPeriodBetweenDates(PeriodUnit unit, LocalDate date1, LocalDate date2) {
//        return 0;
//    }
//
//    @Override
//    public long getPeriodBetweenTimes(PeriodUnit unit, LocalTime time1, LocalTime time2) {
//        return 0;
//    }
//
//    @Override
//    public long getPeriodBetweenDateTimes(PeriodUnit unit, LocalDateTime dateTime1, LocalDateTime dateTime2) {
//        return 0;
//    }
//    
//    @Override
//    public Duration getEstimatedDuration(PeriodUnit unit) {
//        if (unit instanceof StandardPeriodUnit) {
//            StandardPeriodUnit standardUnit = (StandardPeriodUnit) unit;
//            if (standardUnit == MONTHS) {
//                return Duration.ofSeconds(31556952L / 13);
//            } else {
//                return standardUnit.getEstimatedDuration();
//            }
//        } else {
//            return unit.implementationRules(this).getEstimatedDuration(unit);
//        }
//    }
//
//    @Override
//    public LocalDate buildDate(DateTimeBuilder builder) {
//        checkNotNull(builder, "builder cannot be null");
//        if (builder.hasAllFields(YEAR, MONTH_OF_YEAR, DAY_OF_MONTH)) {
//            LocalDate date = setDate(LocalDate.now(), YEAR, builder.getInt(YEAR));
//            date = setDate(date, MONTH_OF_YEAR, builder.getInt(MONTH_OF_YEAR));
//            return setDate(date, DAY_OF_MONTH, builder.getInt(DAY_OF_MONTH));
//        } else if (builder.hasAllFields(EPOCH_DAY)) {
//            return LocalDate.ofEpochDay(builder.getValue(EPOCH_DAY));
//        } else if (builder.hasAllFields(YEAR, DAY_OF_YEAR)) {
//            LocalDate date = setDate(LocalDate.now(), YEAR, builder.getInt(YEAR));
//            return setDate(date, DAY_OF_YEAR, builder.getInt(DAY_OF_YEAR));
//        }
//        throw new CalendricalException("Unable to build Date due to missing fields"); // TODO
//    }
    
}
