/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.Locale;

import javax.time.calendar.format.DateTimeFormatSymbols;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;
import javax.time.period.PeriodUnit;

/**
 * The rule defining how a measurable field of time operates.
 * <p>
 * Time field rule implementations define how a field like 'day of month' operates.
 * This includes the field name and minimum/maximum values.
 * <p>
 * DateTimeFieldRule is an abstract class and must be implemented with care to
 * ensure other classes in the framework operate correctly.
 * All instantiable subclasses must be final, immutable and thread-safe.
 * It is recommended that subclasses implement <code>Serializable</code>
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
public abstract class DateTimeFieldRule implements Comparable<DateTimeFieldRule> {

    /** The name of the rule, not null. */
    private final Chronology chronology;
    /** The id of the rule, not null. */
    private final String id;
    /** The name of the rule, not null. */
    private final String name;
    /** The period unit, not null. */
    private final PeriodUnit periodUnit;
    /** The period range, not null. */
    private final PeriodUnit periodRange;
    /** The minimum value for the field. */
    private final int minimumValue;
    /** The maximum value for the field. */
    private final int maximumValue;
    /** True if this is a date field, false for a time field. */
    private final boolean isDate;

    /**
     * Constructor.
     *
     * @param chronology  the chronology, not null
     * @param name  the name of the type, not null
     * @param periodUnit  the period unit, not null
     * @param periodRange  the period range, not null
     * @param minimumValue  the minimum value
     * @param maximumValue  the minimum value
     */
    protected DateTimeFieldRule(
            Chronology chronology,
            String name,
            PeriodUnit periodUnit,
            PeriodUnit periodRange,
            int minimumValue,
            int maximumValue) {
        if (chronology == null) {
            throw new NullPointerException("The chronology must not be null");
        }
        if (name == null) {
            throw new NullPointerException("The name must not be null");
        }
//        if (periodUnit == null) {
//            throw new NullPointerException("periodUnit must not be null");
//        }
//        if (periodRange == null) {
//            throw new NullPointerException("periodRange must not be null");
//        }
        this.chronology = chronology;
        this.id = chronology.getName() + '.' + name;
        this.name = name;
        this.periodUnit = periodUnit;
        this.periodRange = periodRange;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.isDate = true;  // TODO pass in isDate
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the id of the field.
     * <p>
     * The id is of the form 'ChronologyName.FieldName'.
     * No two fields should have the same id.
     *
     * @return the id of the field, never null
     */
    public final String getID() {
        return id;
    }

    /**
     * Gets the name of the field.
     * <p>
     * Subclasses should use the form 'UnitOfRange' whenever possible.
     *
     * @return the name of the field, never null
     */
    public String getName() {
        return name;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the field represents part of the date, as opposed to part
     * of the time.
     *
     * @return true if this is a date field, false if this is a time field
     * @see #isTimeField()
     */
    public boolean isDateField() {
        return isDate;
    }

    /**
     * Checks if the field represents part of the time, as opposed to part
     * of the date.
     *
     * @return true if this is a time field, false if this is a date field
     * @see #isDateField()
     */
    public boolean isTimeField() {
        return !isDate;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the period unit, which the element which alters within the range.
     * <p>
     * In the phrase 'hour of day', the unit is the hour.
     *
     * @return the rule for the unit period, never null
     */
    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    /**
     * Gets the period range, which the field is bound by.
     * <p>
     * In the phrase 'hour of day', the range is the day.
     *
     * @return the rule for the range period, never null
     */
    public PeriodUnit getPeriodRange() {
        return periodRange;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the this field is supported using calendrical data that is
     * completely specified by the unit and range.
     * <p>
     * For example, a date object has a unit of days and a range of forever.
     * If this field is for hour of day, then that cannot be supported by the
     * unit and range from a date object.
     *
     * @param unit  the unit to check, not null
     * @param range  the range to check, not null
     * @return true if the field is supported
     */
    public boolean isSupported(PeriodUnit unit, PeriodUnit range) {
        return (periodUnit.compareTo(unit) >= 0) &&
               (periodRange.compareTo(range) < 0);
    }

    /**
     * Gets the value for this field throwing an exception if the field cannot be obtained.
     * <p>
     * The value will be checked for basic validity.
     * The value returned will be within the valid range for the field.
     * Also, if the value is present in both the date/time and the field-value
     * map then the two values must be the same.
     *
     * @param dateTime  the date-time, not null
     * @return the value of the field
     * @throws UnsupportedCalendarFieldException if the value cannot be extracted
     */
    public int getValue(FlexiDateTime dateTime) {
        return dateTime.getValue(this);
    }

    /**
     * Extracts the value of this field from the date or time on the specified
     * FlexiDateTime.
     * <p>
     * The implementation of this method is only responsible for querying the
     * date and time fields. The field-value map will be queried separately.
     *
     * @param dateTime  the date-time, not null
     * @return the value of the field, null if unable to extract field
     */
    protected Integer extractValue(FlexiDateTime dateTime) {
        return null;
    }

    /**
     * Merges this field with other fields in the given date-time to create
     * more complete and meaningful date-time objects.
     * <p>
     * This method is used to combine individual date-time fields into more
     * significant concepts such as date and time. The full merge process
     * requires this method to be called repeatedly on each of the fields until
     * no more changes occur. This method will only be called if the field is
     * present in the field-value map of the input FlexiDateTime.
     * <p>
     * Since the merge process is cooperative, it is not necessary to include
     * the full merge code in each field. Thus, the code to merge a year, month
     * and day of month into a date only needs to be in one of the three rules.
     * In this example it will be the day of month rule. The merged fields are
     * removed from the field map.
     * <p>
     * The merge process will also merge minor fields into major ones. For example,
     * the month of quarter and quarter of year fields would be merged to form the
     * month of year. In this case, the original two fields are removed from the
     * field map and the month of year added. If the field map already contained
     * the month of year then the value must match.
     * <p>
     * If this method is called with the date or time already created then the
     * implementation must validate the value for this rule against the date/time.
     * <p>
     * Implementors must ensure that the input parameter is returned if there is
     * no change to the date-time.
     *
     * @param dateTime  the date-time to merge, not null
     * @return the merged date-time with the processed fields removed, never null,
     *  the input date-time must be retuned if no change is made
     * @throws CalendarFieldException if the values cannot be merged
     */
    protected FlexiDateTime mergeFields(FlexiDateTime dateTime) {
        dateTime.getValue(this);  // validates the value of this field
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the value is invalid and throws an exception if it is.
     * <p>
     * This method has no knowledge of other date-time fields, thus only the
     * outer minimum and maximum range for the field is validated.
     *
     * @param value  the value to check
     * @throws IllegalCalendarFieldValueException if the value is invalid
     */
    public void checkValue(int value) {
        if (value < getMinimumValue() || value > getMaximumValue()) {
            throw new IllegalCalendarFieldValueException(this, value, getMinimumValue(), getMaximumValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Is the set of values, from the minimum value to the maximum, a fixed
     * set, or does it vary according to other fields.
     *
     * @return true if the set of values is fixed
     */
    public boolean isFixedValueSet() {
        return getMaximumValue() == getSmallestMaximumValue() &&
                getMinimumValue() == getLargestMinimumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the minimum value that the field can take.
     *
     * @return the minimum value for this field
     */
    public int getMinimumValue() {
        return minimumValue;
    }

    /**
     * Gets the largest possible minimum value that the field can take.
     *
     * @return the largest possible minimum value for this field
     */
    public int getLargestMinimumValue() {
        return getMinimumValue();
    }

//    /**
//     * Gets the minimum value that the field can take using the specified
//     * calendrical information to refine the accuracy of the response.
//     *
//     * @param calendricalContext  context datetime, null returns getMinimumValue()
//     * @return the minimum value of the field given the context
//     */
//    public int getMinimumValue(Calendrical calendricalContext) {
//        return getMinimumValue();
//    }
//
//    /**
//     * Gets the largest possible minimum value that the field can take using
//     * the specified calendrical information to refine the accuracy of the response.
//     *
//     * @param calendricalContext  context datetime, null returns getLargestMinimumValue()
//     * @return the largest possible minimum value of the field given the context
//     */
//    public int getLargestMinimumValue(Calendrical calendricalContext) {
//        if (calendricalContext == null) {
//            return getLargestMinimumValue();
//        }
//        return getMinimumValue(calendricalContext);
//    }

    //-----------------------------------------------------------------------
    /**
     * Gets the maximum value that the field can take.
     *
     * @return the maximum value for this field
     */
    public int getMaximumValue() {
        return maximumValue;
    }

    /**
     * Gets the smallest possible maximum value that the field can take.
     *
     * @return the smallest possible maximum value for this field
     */
    public int getSmallestMaximumValue() {
        return getMaximumValue();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the text for this field throwing an exception if the field cannot
     * be obtained.
     * <p>
     * The value is queried using {@link #getValue(FlexiDateTime)}. The text
     * is then obtained for that value. If there is no textual mapping, then
     * the value is returned as per {@link Integer#toString()}.
     *
     * @param dateTime  the date-time, not null
     * @param locale  the locale to use, not null
     * @param textStyle  the text style, not null
     * @return the text of the field, never null
     * @throws UnsupportedCalendarFieldException if the value cannot be extracted
     */
    public String getText(FlexiDateTime dateTime, Locale locale, TextStyle textStyle) {
        int value = dateTime.getValue(this);
        DateTimeFormatSymbols symbols = DateTimeFormatSymbols.getInstance(locale);
        String text = symbols.getFieldValueText(this, textStyle, value);
        return text == null ? Integer.toString(value) : text;
    }

//    /**
//     * Gets the maximum value that the field can take using the specified
//     * calendrical information to refine the accuracy of the response.
//     *
//     * @param calendricalContext  context datetime, null returns getMaximumValue()
//     * @return the maximum value of the field given the context
//     */
//    public int getMaximumValue(Calendrical calendricalContext) {
//        return getMaximumValue();
//    }
//
//    /**
//     * Gets the smallest possible maximum value that the field can take using
//     * the specified calendrical information to refine the accuracy of the response.
//     *
//     * @param calendricalContext  context datetime, null returns getSmallestMaximumValue()
//     * @return the smallest possible maximum value of the field given the context
//     */
//    public int getSmallestMaximumValue(Calendrical calendricalContext) {
//        if (calendricalContext == null) {
//            return getSmallestMaximumValue();
//        }
//        return getMaximumValue(calendricalContext);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * Checks whether a given calendrical is supported or not.
//     *
//     * @param calState  the calendar state to check, not null
//     * @throws UnsupportedCalendarFieldException if the field is unsupported
//     */
//    protected void checkSupported(CalendricalState calState) {
//        if (calState.getPeriodUnit().compareTo(getPeriodUnit()) > 0 ||
//                calState.getPeriodRange().compareTo(getPeriodRange()) < 0) {
//            throw new UnsupportedCalendarFieldException("Calendar field " + getName() + " cannot be queried");
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Compares this DateTimeFieldRule to another based on the period unit
     * followed by the period range.
     * <p>
     * The period unit is compared first, so MinuteOfHour will be less than
     * HourOfDay, which will be less than DayOfWeek. When the period unit is
     * the same, the period range is compared, so DayOfWeek is less than
     * DayOfMonth, which is less than DayOfYear.
     *
     * @param other  the other type to compare to, not null
     * @return the comparator result, negative if less, postive if greater, zero if equal
     * @throws NullPointerException if other is null
     */
    public int compareTo(DateTimeFieldRule other) {
        int cmp = this.getPeriodUnit().compareTo(other.getPeriodUnit());
        if (cmp != 0) {
            return cmp;
        }
        return this.getPeriodRange().compareTo(other.getPeriodRange());
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the rule.
     *
     * @return a description of the rule
     */
    @Override
    public String toString() {
        return getName();
    }

}
