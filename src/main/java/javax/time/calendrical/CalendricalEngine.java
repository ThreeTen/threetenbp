/*
 * Copyright (c) 2011 Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendrical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.time.CalendricalException;
import javax.time.MathUtils;
import javax.time.calendar.Chronology;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.ZoneId;
import javax.time.calendar.ZoneOffset;

/**
 * Main processing engine to merge and interpret calendrical information.
 * <p>
 * This stateful class is a tool for manipulating a set of calendrical information.
 * The engine typically takes some calendrical information as input and derives
 * other information from it.
 * For example, a date can be derived from separate year, month and day fields.
 * <p>
 * This class is mutable and not thread-safe.
 * It must only be used from a single thread and must not be passed between threads.
 *
 * @author Stephen Colebourne
 */
public final class CalendricalEngine {

    /**
     * The original input.
     */
    private List<Calendrical> input;
    /**
     * The rule of the calendrical supplying the normalized fields.
     */
    private final CalendricalRule<?> rule;
    /**
     * The date.
     */
    private LocalDate date;
    /**
     * The time.
     */
    private LocalTime time;
    /**
     * The offset.
     */
    private ZoneOffset offset;
    /**
     * The zone.
     */
    private ZoneId zone;
    /**
     * The chronloogy.
     */
    private Chronology chronology;
    /**
     * The map of fields.
     */
    private Map<DateTimeRule, DateTimeField> fields;
    /**
     * The errors that occur during normalization.
     */
    private final Set<String> errors = new LinkedHashSet<String>();

    //-----------------------------------------------------------------------
    /**
     * Gets the rule for {@code CalendricalEngine}.
     *
     * @return the rule for the engine, not null
     */
    public static CalendricalRule<CalendricalEngine> rule() {
        return Rule.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a set of calendricals.
     * <p>
     * This method is used to combine any number of calendrical objects into
     * a more meaningful form. For example, a {@code Year}, {@code MonthDay}
     * and {@code LocalTime} could be combined into a {@code LocalDateTime}.
     * <p>
     * The returned merger will contain the combined information of the input
     * in a normalized form. Use {@link #derive(CalendricalRule)} or
     * {@link #deriveChecked(CalendricalRule)} to derive the desired values or values.
     * <p>
     * If the input cannot be combined into any sensible normalized output then
     * an exception is thrown. For example, passing in the month January and the
     * date 2011-06-30 will fail as the months are in conflict.
     * 
     * @param calendricals  the calendricals to merge, not null, no nulls
     * @return the normalized merger to query, not null
     * @throws CalendricalException if the calendricals cannot be successfully merged
     */
    public static CalendricalEngine merge(Calendrical... calendricals) {
        ISOChronology.checkNotNull(calendricals, "Calendricals must not be null");
        CalendricalEngine target;
        try {
            List<CalendricalEngine> semiNormalized = new ArrayList<CalendricalEngine>(calendricals.length);
            for (Calendrical calendrical : calendricals) {
                CalendricalEngine engine = rule().getValue(calendrical);
                if (engine != null) {  // ignore anything with no normalized form
                    semiNormalized.add(engine);
                }
            }
            semiNormalized = Collections.unmodifiableList(semiNormalized);  // make list safe for external use
            for (CalendricalEngine engine : semiNormalized) {
                if (engine.getRule() != null) {
                    engine.getRule().merge(engine, semiNormalized);
                }
            }
            target = new CalendricalEngine(calendricals, semiNormalized);
            target.validate();
            target.normalize();
        } catch (NullPointerException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            // provide more useful error message
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + ex.getMessage(), ex);
        }
        // normalization is quiet, so need to check it was successful
        if (target.errors.size() > 0) {
            throw new CalendricalException("Unable to merge " + Arrays.toString(calendricals) + ": " + target.errors);
        }
        return target;
    }

    //-----------------------------------------------------------------------
    /**
     * Derives the specified rule from a the normalized set of objects.
     * <p>
     * This method is designed to be called from {@link Calendrical#get(CalendricalRule)}.
     * The class implementing the interface must call this method passing in
     * parameters to fully describe the state of the object to be derived from.
     * Avoid duplicating information between the date, time and fields if possible.
     * 
     * @param <R>  the type of the desired rule
     * @param ruleToDerive  the rule to derive, not null
     * @param ruleOfData  the rule of the data to derive from, may be null
     * @param date  the date to derive from, may be null
     * @param time  the time to derive from, may be null
     * @param offset  the zone offset to derive from, may be null
     * @param zone  the zone ID to derive from, may be null
     * @param chronology  the chronology to derive from, may be null
     * @param fields  the fields to derive from, may be null
     * @return the derived value for the rule, null if unable to derive
     */
    @SuppressWarnings("unchecked")
    public static <R> R derive(CalendricalRule<R> ruleToDerive, CalendricalRule<?> ruleOfData,
            LocalDate date, LocalTime time, ZoneOffset offset, ZoneId zone, Chronology chronology, DateTimeFields fields) {
        ISOChronology.checkNotNull(ruleToDerive, "CalendricalRule must not be null");
        if (fields == null) {
            // optimize simple cases
//            if (ruleToDerive instanceof ISOCalendricalRule<?>) {
//                switch (((ISOCalendricalRule<?>) ruleToDerive).ordinal) {
//                    case ISOCalendricalRule.LOCAL_DATE_ORDINAL: return (R) date;
//                    case ISOCalendricalRule.LOCAL_TIME_ORDINAL: return (R) time;
//                    case ISOCalendricalRule.ZONE_OFFSET_ORDINAL: return (R) offset;
//                    case ISOCalendricalRule.ZONE_ID_ORDINAL: return (R) zone;
//                    case ISOCalendricalRule.CHRONOLOGY_ORDINAL: return (R) chronology;
//                    // other cases are not so simple, so drop through
//                }
//            } else 
            if (ruleToDerive instanceof ISODateTimeRule) {
                return (R) ((ISODateTimeRule) ruleToDerive).deriveFrom(date, time, offset);
            }
        }
        CalendricalEngine engine = new CalendricalEngine(ruleOfData, date, time, offset, zone, chronology, fields);
        engine.normalize();
        return engine.derive(ruleToDerive);
    }

    /**
     * Derives the specified rule from a the normalized set of objects.
     * <p>
     * This method is designed to be called from {@link Calendrical#get(CalendricalRule)}.
     * The class implementing the interface must call this method passing in
     * parameters to fully describe the state of the object to be derived from.
     * 
     * @param <R>  the type of the desired rule
     * @param ruleToDerive  the rule to derive, not null
     * @param ruleOfData  the rule of the data to derive from, may be null
     * @param chronology  the chronology to derive from, may be null
     * @param field  the field to derive from, not null
     * @return the derived value for the rule, null if unable to derive
     */
    public static <R> R derive(CalendricalRule<R> ruleToDerive, CalendricalRule<?> ruleOfData, Chronology chronology, DateTimeField field) {
        ISOChronology.checkNotNull(ruleToDerive, "CalendricalRule must not be null");
        ISOChronology.checkNotNull(field, "DateTimeField must not be null");
        CalendricalEngine engine = new CalendricalEngine(ruleOfData, null, null, null, null, chronology, Collections.singleton(field));
        engine.normalize();
        return engine.derive(ruleToDerive);
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from a normalized list of mergers.
     * 
     * @param calendricals  the original calendricals prior to merging, not null
     * @param engines  the engine form of the calendricals, not null
     */
    private CalendricalEngine(Calendrical[] calendricals, List<CalendricalEngine> engines) {
        this.input = Collections.unmodifiableList(Arrays.asList(calendricals));
        this.rule = null;
        for (CalendricalEngine engine : engines) {
            if (engine.date != null) {
                setDate(engine.date, true);
            }
            if (engine.time != null) {
                setTime(engine.time, true);
            }
            if (engine.offset != null) {
                setOffset(engine.offset, true);
            }
            if (engine.zone != null) {
                setZone(engine.zone, true);
            }
            if (engine.chronology != null) {
                setChronology(engine.chronology, true);
            }
            if (engine.fields != null) {
                for (DateTimeField field : engine.fields.values()) {
                    setField(field, true);
                }
            }
        }
    }

    /**
     * Creates an instance from a normalized set of objects.
     * 
     * @param ruleOfData  the rule of the calendrical that these objects represent, may be null
     * @param date  the date, may be null
     * @param time  the time, may be null
     * @param offset  the zone offset, may be null
     * @param zone  the zone ID, may be null
     * @param chronology  the chronology, may be null
     * @param fields  the fields, may be null
     */
    private CalendricalEngine(
            CalendricalRule<?> ruleOfData, LocalDate date, LocalTime time, ZoneOffset offset,
            ZoneId zone, Chronology chronology, Iterable<DateTimeField> fields) {
        this.rule = ruleOfData;
        this.date = date;
        this.time = time;
        this.offset = offset;
        this.zone = zone;
        this.chronology = chronology;
        if (fields != null) {
            for (DateTimeField field : fields) {
                setField(field, false);  // can use false here as DateTimeFields ensure no rule clashes
            }
            getInput();  // field map is modifiable, so lock input now
        }
    }

    //-----------------------------------------------------------------------
    /**
     * The original input provided to the merger.
     * 
     * @return the unmodifiable original input, not null
     */
    public List<Calendrical> getInput() {
        if (input == null) {
            List<Calendrical> list = new ArrayList<Calendrical>();
            if (date != null) {
                list.add(date);
            }
            if (time != null) {
                list.add(time);
            }
            if (offset != null) {
                list.add(offset);
            }
            if (zone != null) {
                list.add(zone);
            }
            if (chronology != null) {
                list.add(chronology);
            }
            if (fields != null) {
                list.addAll(fields.values());
            }
            input = Collections.unmodifiableList(list);
        }
        return input;
    }

    /**
     * The single rule that defines the data in this merger.
     * <p>
     * This will only be present if this merger is created from a single
     * conceptual object as opposed to being merged.
     * 
     * @return the rule of the data, may be null
     */
    public CalendricalRule<?> getRule() {
        return rule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the date.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalDate getDate(boolean storeErrorIfNull) {
        if (storeErrorIfNull && date == null) {
            addError("Missing LocalDate");
        }
        return date;
    }

    /**
     * Gets the time.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public LocalTime getTime(boolean storeErrorIfNull) {
        if (storeErrorIfNull && time == null) {
            addError("Missing LocalTime");
        }
        return time;
    }

    /**
     * Gets the offset.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneOffset getOffset(boolean storeErrorIfNull) {
        if (storeErrorIfNull && offset == null) {
            addError("Missing ZoneOffset");
        }
        return offset;
    }

    /**
     * Gets the zone.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public ZoneId getZone(boolean storeErrorIfNull) {
        if (storeErrorIfNull && zone == null) {
            addError("Missing ZoneId");
        }
        return zone;
    }

    /**
     * Gets the chronology.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param storeErrorIfNull  true to store an error if the value is null
     * @return the date, may be null
     */
    public Chronology getChronology(boolean storeErrorIfNull) {
        if (storeErrorIfNull && chronology == null) {
            addError("Missing Chronology");
        }
        return chronology;
    }

    /**
     * Gets a field by rule.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param rule  the rule to retrieve, null returns null
     * @return the field, may be null
     */
    public DateTimeField getField(DateTimeRule rule, boolean storeErrorIfNull) {
        DateTimeField field = (fields == null ? null : fields.get(rule));
        if (storeErrorIfNull && field == null) {
            addError("Missing field " + rule.getName());
        }
        return field;
    }

    /**
     * Gets a field by rule, deriving the value.
     * <p>
     * The flag is set to <code>true</code> if the value is required and an error
     * should be stored if it is not available. Note that the value is returned
     * whether it is null or not and no exception is thrown.
     * 
     * @param ruleToDerive  the rule to retrieve, null returns null
     * @return the field, may be null
     */
    public DateTimeField getFieldDerived(DateTimeRule ruleToDerive, boolean storeErrorIfNull) {
        DateTimeField result = ruleToDerive.deriveFrom(this);
        if (result == null) {
            result = deriveField(ruleToDerive);
        }
        if (storeErrorIfNull && result == null) {
            addError("Missing field " + ruleToDerive.getName());
        }
        return result;
    }

    private DateTimeField deriveField(DateTimeRule ruleToDerive) {
        if (fields != null) {
            DateTimeRule baseRule = ruleToDerive.getBaseRule();
            for (DateTimeField field : fields.values()) {
                if (field.getRule().getBaseRule().equals(baseRule)) {
                    DateTimeField result = field.derive(ruleToDerive);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the date.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param date  the date to store, may be null
     * @param storeErrorIfClash  true to store an error if the date clashes with the stored value
     */
    public void setDate(LocalDate date, boolean storeErrorIfClash) {
        this.date = set(this.date, date, storeErrorIfClash);
    }

    /**
     * Sets the time.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param time  the time to store, may be null
     * @param storeErrorIfClash  true to store an error if the time clashes with the stored value
     */
    public void setTime(LocalTime time, boolean storeErrorIfClash) {
        this.time = set(this.time, time, storeErrorIfClash);
    }

    /**
     * Sets the offset.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param offset  the offset to store, may be null
     * @param storeErrorIfClash  true to store an error if the offset clashes with the stored value
     */
    public void setOffset(ZoneOffset offset, boolean storeErrorIfClash) {
        this.offset = set(this.offset, offset, storeErrorIfClash);
    }

    /**
     * Sets the zone.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param zone  the zone to store, may be null
     * @param storeErrorIfClash  true to store an error if the zone clashes with the stored value
     */
    public void setZone(ZoneId zone, boolean storeErrorIfClash) {
        this.zone = set(this.zone, zone, storeErrorIfClash);
    }

    /**
     * Sets the chronology.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * @param chronology  the chronology to store, may be null
     * @param storeErrorIfClash  true to store an error if the chronology clashes with the stored value
     */
    public void setChronology(Chronology chronology, boolean storeErrorIfClash) {
        this.chronology = set(this.chronology, chronology, storeErrorIfClash);
    }

    /**
     * Sets the specified field.
     * <p>
     * If the flag is true, then a check is performed to see if the specified object
     * clashes with the stored object, storing an error if it does.
     * If the flag is false, then the current value is overwritten without further checks.
     * 
     * The flag should be set to <code>true</code> to cause the method to check
     * if the value for the field clashes with an existing value.
     * A <code>false</code> value simply overwrites any previous value.
     * 
     * @param field  the field to store, null ignored
     * @param storeErrorIfClash  true to store an error if the field clashes with an existing field
     */
    public void setField(DateTimeField field, boolean storeErrorIfClash) {
        if (field != null) {
            if (fields == null) {
                fields = new HashMap<DateTimeRule, DateTimeField>();
            }
            DateTimeField curField = fields.get(field.getRule());
            DateTimeField newField = set(curField, field, storeErrorIfClash);
            fields.put(newField.getRule(), newField);
        }
    }

    private <T> T set(T curObj, T newObj, boolean storeErrorIfClash) {
        if (storeErrorIfClash) {
            if (curObj != null && curObj.equals(newObj) == false) {
                addError("Clash: " + curObj + " and " + newObj);
                return curObj;
            }
        }
        return newObj;
    }

    // phase 1
    //-----------------------------------------------------------------------
    private void validate() {
        if (fields != null) {
            for (DateTimeField field : fields.values()) {
                if (field.isValidValue() == false) {
                    addError("Value out of range: " + field);
                }
            }
        }
    }

    // phase 2
    //-----------------------------------------------------------------------
    /**
     * Normalize the fields, converting to LocalDate/LocalTime if possible.
     */
    private void normalize() {
        // do not call from the constructor
        if (fields != null && fields.size() > 0) {
            normalizeSeparately();
            if (errors.size() == 0) {
                if (fields.size() > 1) {
                    normalizeAuto();
                }
                if (errors.size() == 0) {
                    normalizeManual();
                    if (errors.size() == 0) {
                        normalizeCrossCheck();
                    }
                }
                if (fields.size() == 0) {
                    fields = null;
                }
            }
        }
    }

    private void normalizeSeparately() {
        for (DateTimeField field : new ArrayList<DateTimeField>(fields.values())) {
            DateTimeRule fieldRule = field.getRule();
            DateTimeRule normalizationRule = fieldRule.getNormalizationRule();
            if (fieldRule.equals(normalizationRule) == false) {
                long newValue = normalizationRule.convertFromPeriod(fieldRule.convertToPeriod(field.getValue()));
                setField(normalizationRule.field(newValue), true);
                fields.remove(fieldRule);
            }
        }
    }

    private void normalizeAuto() {
        // group according to base rule
        Map<DateTimeRule, List<DateTimeField>> grouped = new HashMap<DateTimeRule, List<DateTimeField>>();
        for (DateTimeField field : fields.values()) {
            DateTimeRule baseRule = field.getRule().getBaseRule();
            List<DateTimeField> groupedList = grouped.get(baseRule);
            if (groupedList == null) {
                groupedList = new ArrayList<DateTimeField>();
                grouped.put(baseRule, groupedList);
            }
            groupedList.add(field);
        }
        
        // normalize groups
        // TODO: loop again (group again) if register on group is public
        fields.clear();
        for (Map.Entry<DateTimeRule, List<DateTimeField>> entry : grouped.entrySet()) {
            List<DateTimeField> group = entry.getValue();
            if (group.size() >= 2) {
                mergeGroup(entry.getKey(), group);
            }
            for (DateTimeField field : group) {
                fields.put(field.getRule(), field);  // should be no clashes here
            }
        }
    }

    private void mergeGroup(DateTimeRule baseRule, List<DateTimeField> group) {
        sort(group);
        DateTimeRuleGroup ruleGroup = DateTimeRuleGroup.of(baseRule);
        for (int i = 0; i < group.size() - 1; i++) {
            final DateTimeField fieldLge = group.get(i);
            for (int j = i + 1; j < group.size(); j++) {
                final DateTimeField fieldSml = group.get(j);
                final DateTimeRule ruleLge = fieldLge.getRule();
                final DateTimeRule ruleSml = fieldSml.getRule();
                // remove if derived
                // the fields share a base rule, so this must succeed if there fieldLge surrounds fieldSml
                final DateTimeField derivedSml = fieldLge.derive(ruleSml);
                if (derivedSml != null) {
                    if (derivedSml.equals(fieldSml)) {
                        group.remove(j--);
                        continue;
                    } else {
                        addError("Clash: " + fieldSml + " and " + derivedSml);
                        return;
                    }
                }
                // merge overlap or adjacent
                if (DateTimeRule.comparePeriodUnits(ruleSml.getPeriodRange(), ruleLge.getPeriodUnit()) >= 0 &&
                        DateTimeRule.comparePeriodUnits(ruleSml.getPeriodUnit(), ruleLge.getPeriodUnit()) < 0) {
                    final long periodLge = ruleLge.convertToPeriod(fieldLge.getValue());
                    final long periodSml = ruleSml.convertToPeriod(fieldSml.getValue());
                    final long conversion1 = ruleSml.getPeriodRange().toEquivalent(ruleLge.getPeriodUnit());
                    if (conversion1 >= 0) {
                        // if was an overlap, then check it is valid
                        // this must be done before the combined rule check to ensure that the final derivation is OK
                        if (DateTimeRule.comparePeriodUnits(ruleSml.getPeriodRange(), ruleLge.getPeriodUnit()) > 0) {
                            long periodMidLge = MathUtils.floorMod(periodLge, conversion1);
                            final long conversion2 = ruleSml.getPeriodRange().toEquivalent(ruleSml.getPeriodUnit());
                            final long conversion3 = ruleLge.getPeriodUnit().toEquivalent(ruleSml.getPeriodUnit());
                            if (conversion2 >= 0 && conversion3 >= 0) {
                                long periodMidSml = MathUtils.floorMod(periodSml, conversion2);
                                periodMidSml = MathUtils.floorDiv(periodMidSml, conversion3);
                                if (periodMidLge != periodMidSml) {
                                    addError("Clash: " + fieldLge + " and " + fieldSml);
                                    return;
                                }
                            }
                        }
                        // merge if possible
                        DateTimeRule ruleCombined = ruleGroup.getRelatedRule(ruleSml.getPeriodUnit(), ruleLge.getPeriodRange());
                        if (ruleCombined != null) {
                            long period = MathUtils.floorDiv(periodLge, conversion1);
                            final long conversion2 = ruleSml.getPeriodRange().toEquivalent(ruleSml.getPeriodUnit());
                            if (conversion2 >= 0) {
                                period = MathUtils.safeMultiply(period, conversion2);
                                period = MathUtils.safeAdd(period, periodSml);
                                DateTimeField fieldCombined = ruleCombined.field(ruleCombined.convertFromPeriod(period));
                                group.set(i, fieldCombined);
                                group.remove(j);
                                i = -1;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void sort(List<DateTimeField> group) {
        Collections.sort(group, new Comparator<DateTimeField>() {
            public int compare(DateTimeField dtf1, DateTimeField dtf2) {
                int cmp = -dtf1.getRule().comparePeriodRange(dtf2.getRule());
                if (cmp == 0) {
                    cmp = dtf1.getRule().comparePeriodUnit(dtf2.getRule());
                }
                return cmp;
            }
        });
    }

    private void normalizeManual() {
        for (DateTimeField field : fields.values()) {
            field.getRule().normalize(this);
        }
    }

    private void normalizeCrossCheck() {
        for (Iterator<DateTimeField> it = fields.values().iterator(); it.hasNext(); ) {
            DateTimeField field = it.next();
            DateTimeField derived = field.getRule().deriveFrom(this);
            if (derived != null) {
                if (derived.equals(field) == false) {
                    addError("Cross-check clash: " + field + " and " + derived);
                } else {
                    it.remove();
                }
            }
        }
    }

    // phase 3
    //-----------------------------------------------------------------------
    public <R> R derive(CalendricalRule<R> ruleToDerive) {
        if (errors.size() > 0) {
            return null;  // quiet
        }
        R result = doDerive(ruleToDerive);
        if (errors.size() > 0) {
            errors.clear();
            return null;  // quiet
        }
        return result;
    }

    public <R> R deriveChecked(CalendricalRule<R> ruleToDerive) {
        R result = doDerive(ruleToDerive);
        if (result == null) {
            if (errors.size() > 0) {
                throw new CalendricalException("Unable to derive " + ruleToDerive + " from " + this + ": " + errors);
            }
            throw new CalendricalException("Unable to derive " + ruleToDerive + " from " + this);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <R> R doDerive(CalendricalRule<R> ruleToDerive) {
        if (errors.size() > 0) {
            return null;  // quiet
        }
        if (ruleToDerive == rule()) {
            return (R) this;
        }
        try {
            R result = ruleToDerive.deriveFrom(this);
            if (result == null && ruleToDerive instanceof DateTimeRule) {
                result = (R) deriveField((DateTimeRule) ruleToDerive);
            }
            if (errors.size() > 0) {
                return null;
            }
            return result;
        } catch (RuntimeException ex) {
            addError(ex.getMessage());
            return null;
        }
    }

    /**
     * Adds an error to those tracked by the normalizer.
     * 
     * @param error  the error text, not null
     */
    public void addError(String error) {
        if (error != null) {
            errors.add(error);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
        return getInput().toString() + (errors.size() > 0 ? " " + errors : "");
    }

    //-----------------------------------------------------------------------
    /**
     * Rule class.
     */
    static final class Rule extends CalendricalRule<CalendricalEngine> implements Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Singleton instance. */
        static final Rule INSTANCE = new Rule();

        private Rule() {
            super(CalendricalEngine.class, "CalendricalEngine");  // TODO
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

}
