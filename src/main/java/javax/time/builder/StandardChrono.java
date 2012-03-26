package javax.time.builder;

import static javax.time.MathUtils.checkNotNull;

import javax.time.CalendricalException;
import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.LocalTime;
import javax.time.MathUtils;
import javax.time.calendrical.DateTimeRuleRange;

/**
 * 
 * StandardChrono abstracts away Common functionality for Standard Chronologies
 * in order to provide some code reuse and make it easier to author a custom
 * chronology.
 * <p>
 * All StandardChrono instances use StandardDateTimeField to define their fields
 * and have days that begin at midnight/sunrise.
 * 
 * @author Richard Warburton
 */
public abstract class StandardChrono implements Chrono {

    private static final DateTimeRuleRange RANGE_NOS = DateTimeRuleRange.of(0, 999999999);
    private static final DateTimeRuleRange RANGE_NOD = DateTimeRuleRange.of(0, 86400L * 1000000000L - 1);
    private static final DateTimeRuleRange RANGE_MCOS = DateTimeRuleRange.of(0, 999999);
    private static final DateTimeRuleRange RANGE_MCOD = DateTimeRuleRange.of(0, 0, 86400L * 1000000L - 1);
    private static final DateTimeRuleRange RANGE_MLOS = DateTimeRuleRange.of(0, 999);
    private static final DateTimeRuleRange RANGE_MLOD = DateTimeRuleRange.of(0, 0, 86400L * 1000L - 1);
    private static final DateTimeRuleRange RANGE_SOM = DateTimeRuleRange.of(0, 59);
    private static final DateTimeRuleRange RANGE_SOD = DateTimeRuleRange.of(0, 86400L - 1);
    private static final DateTimeRuleRange RANGE_MOH = RANGE_SOM; //DateTimeRuleRange.of(0, 59);
    private static final DateTimeRuleRange RANGE_MOD = DateTimeRuleRange.of(0, 24 * 60 - 1);
    private static final DateTimeRuleRange RANGE_HOD = DateTimeRuleRange.of(0, 23);
    private static final DateTimeRuleRange RANGE_DOW = DateTimeRuleRange.of(1, 7);
    
    //-----------------------------------------------------------------------
    protected DateTimeRuleRange getTimeRange(StandardDateTimeField field) {
        switch(field) {
            case NANO_OF_SECOND: return RANGE_NOS;
            case NANO_OF_DAY: return RANGE_NOD;
            case MICRO_OF_SECOND: return RANGE_MCOS;
            case MICRO_OF_DAY: return RANGE_MCOD;
            case MILLI_OF_SECOND: return RANGE_MLOS;
            case MILLI_OF_DAY: return RANGE_MLOD;
            case SECOND_OF_MINUTE: return RANGE_SOM;
            case SECOND_OF_DAY: return RANGE_SOD;
            case MINUTE_OF_HOUR: return RANGE_MOH;
            case MINUTE_OF_DAY: return RANGE_MOD;
            case HOUR_OF_DAY: return RANGE_HOD;
            case DAY_OF_WEEK: return RANGE_DOW;
        }
        throw new CalendricalException("Unsupported field");
    }

    //-----------------------------------------------------------------------
    @Override
    public long getTimeValue(LocalTime time, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            switch ((StandardDateTimeField) field) {
                case NANO_OF_DAY: return time.toNanoOfDay();
                case NANO_OF_SECOND: return time.getNanoOfSecond();
                case MICRO_OF_SECOND: return time.getNanoOfSecond() / 1000;
                case MICRO_OF_DAY: return time.toNanoOfDay() / 1000;
                case MILLI_OF_SECOND: return time.getNanoOfSecond() / 1000000;
                case MILLI_OF_DAY: return time.toNanoOfDay() / 1000000;
                case SECOND_OF_MINUTE: return time.getSecondOfMinute();
                case SECOND_OF_DAY: return time.toSecondOfDay();
                case MINUTE_OF_HOUR: return time.getMinuteOfHour();
                case MINUTE_OF_DAY: return time.getHourOfDay() * 60 + time.getMinuteOfHour();
                case HOUR_OF_DAY: return time.getHourOfDay();
            }
            throw new CalendricalException("Unsupported field");
        }
        return field.implementationRules(this).getTimeValue(time, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public long getDateTimeValue(LocalDateTime dateTime, DateTimeField field) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return getDateValue(dateTime.toLocalDate(), field);
            } else {
                return getTimeValue(dateTime.toLocalTime(), field);
            }
        }
        return field.implementationRules(this).getDateTimeValue(dateTime, field);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalTime setTime(LocalTime time, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            if (getRange(field, null, time).isValidValue(newValue) == false) {
                throw new IllegalArgumentException();  // TODO
            }
            switch ((StandardDateTimeField) field) {
                case HOUR_OF_DAY: return time.withHourOfDay((int) newValue);
                case MINUTE_OF_HOUR: return time.withMinuteOfHour((int) newValue);
                case SECOND_OF_MINUTE: return time.withSecondOfMinute((int) newValue);
                case MILLI_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000000);
                case MICRO_OF_SECOND: return time.withNanoOfSecond((int) newValue * 1000);
                case NANO_OF_SECOND: return time.withNanoOfSecond((int) newValue);
            }
            throw new CalendricalException("Unsupported field on LocalTime: " + field);
        }
        return field.implementationRules(this).setTime(time, field, newValue);
    }

    @Override
    public LocalDateTime setDateTime(LocalDateTime dateTime, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return dateTime.with(setDate(dateTime.toLocalDate(), field, newValue));
            } else {
                return dateTime.with(setTime(dateTime.toLocalTime(), field, newValue));
            }
        }
        return field.implementationRules(this).setDateTime(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalTime setTimeLenient(LocalTime time, DateTimeField field, long newValue) {
        // TODO
        return field.implementationRules(this).setTimeLenient(time, field, newValue);
    }

    @Override
    public LocalDateTime setDateTimeLenient(LocalDateTime dateTime, DateTimeField field, long newValue) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField std = (StandardDateTimeField) field;
            if (std.isDateField()) {
                return dateTime.with(setDateLenient(dateTime.toLocalDate(), field, newValue));
            } else {
                return dateTime.with(setTimeLenient(dateTime.toLocalTime(), field, newValue));
            }
        }
        return field.implementationRules(this).setDateTimeLenient(dateTime, field, newValue);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDate rollDate(LocalDate date, DateTimeField field, long roll) {
        DateTimeRuleRange range = getRange(field, date, null);
        long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
        long currentValue = getDateValue(date, field);
        long newValue = roll % valueRange; // TODO
        return addToDate(date, field.getBaseUnit(), newValue - currentValue);
    }
    
    @Override
    public LocalTime rollTime(LocalTime time, DateTimeField field, long roll) {
        DateTimeRuleRange range = getRange(field, null, time);
        long valueRange = (range.getMaximum() - range.getMinimum()) + 1;
        long currentValue = getTimeValue(time, field);
        long newValue = roll % valueRange;
        return addToTime(time, field.getBaseUnit(), newValue - currentValue);
    }

    @Override
    public LocalDateTime rollDateTime(LocalDateTime dateTime, DateTimeField field, long roll) {
        if (field instanceof StandardDateTimeField) {
            StandardDateTimeField standardField = (StandardDateTimeField) field;
            if (standardField.isDateField()) {
                return dateTime.with(rollDate(dateTime.toLocalDate(), field, roll));
            } else {
                return dateTime.with(rollTime(dateTime.toLocalTime(), field, roll));
            }
        } else {
            return field.implementationRules(this).rollDateTime(dateTime, field, roll);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalTime addToTime(LocalTime time, PeriodUnit unit, long amount) {
        if (unit instanceof StandardPeriodUnit) {
            switch ((StandardPeriodUnit) unit) {
                case NANOS: return time.plusNanos(amount);
                case MICROS: return time.plusNanos(MathUtils.safeMultiply(amount, 1000));
                case MILLIS: return time.plusNanos(MathUtils.safeMultiply(amount, 1000000));
                case SECONDS: return time.plusSeconds(amount);
                case MINUTES: return time.plusMinutes(amount);
                case HOURS: return time.plusHours(amount);
                case HALF_DAYS: return time.plusHours(MathUtils.safeMultiply(amount, 12));
            }
            throw new CalendricalException("Unsupported unit on LocalTime: " + unit);
        }
        return unit.implementationRules(this).addToTime(time, unit, amount);
    }

    @Override
    public LocalDateTime addToDateTime(LocalDateTime dateTime, PeriodUnit unit, long amount) {
        if (unit instanceof StandardPeriodUnit) {
            StandardPeriodUnit std = (StandardPeriodUnit) unit;
            if (std.ordinal() >= StandardPeriodUnit.DAYS.ordinal()) {
                return dateTime.with(addToDate(dateTime.toLocalDate(), unit, amount));
            } else {
                return dateTime.with(addToTime(dateTime.toLocalTime(), unit, amount));
            }
        }
        return unit.implementationRules(this).addToDateTime(dateTime, unit, amount);
    }

    //-----------------------------------------------------------------------
    @Override
    public LocalDateTime buildDateTime(DateTimeBuilder builder) {
        checkNotNull(builder, "builder cannot be null");
        return LocalDateTime.of(buildDate(builder), builder.buildLocalTime());
    }

    @Override
    public DateChronoView<?> buildDateChronoView(DateTimeBuilder builder) {
        checkNotNull(builder, "builder cannot be null");
        return DateChronoView.of(buildDate(builder), this);
    }

}
