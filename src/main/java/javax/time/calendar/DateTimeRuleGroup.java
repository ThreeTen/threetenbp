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
package javax.time.calendar;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Stephen Colebourne
 */
public final class DateTimeRuleGroup {

    /** A serialization identifier for this class. */
    private static final long serialVersionUID = 1L;
    /** The rule groups. */
    private static final ConcurrentMap<DateTimeRule, DateTimeRuleGroup> GROUPS =
        new ConcurrentHashMap<DateTimeRule, DateTimeRuleGroup>(16, 0.75f, 2);

    /** The base rule. */
    private final DateTimeRule baseRule;
    /** The related rules. */
    private final ConcurrentMap<Map.Entry<PeriodUnit, PeriodUnit>, DateTimeRule> rules =
        new ConcurrentHashMap<Map.Entry<PeriodUnit, PeriodUnit>, DateTimeRule>(16, 0.75f, 2);

    /**
     *
     * @param baseRule  the base rule that this rule relates to, null
     *  if this rule does not relate to another rule
     */
    public static DateTimeRuleGroup of(DateTimeRule baseRule) {
        ISOChronology.checkNotNull(baseRule, "DateTimeRule must not be null");
        DateTimeRuleGroup group = GROUPS.get(baseRule);
        if (group == null) {
            group = new DateTimeRuleGroup(baseRule);
            GROUPS.putIfAbsent(baseRule, group);
            group = GROUPS.get(baseRule);
        }
        return group;
    }

    /**
     * Creates an instance specifying the base rule.
     *
     * @param baseRule  the base rule that this rule relates to, null
     *  if this rule does not relate to another rule
     */
    private DateTimeRuleGroup(DateTimeRule baseRule) {
        ISOChronology.checkNotNull(baseRule, "DateTimeRule must not be null");
        this.baseRule = baseRule;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the base rule that all the rules in the group can be derived from.
     * <p>
     * All the rules in a group can be derived from the base rule.
     * For example, 'HourOfDay', 'MinuteOfHour', 'SecondOfMinute' and 'NanoOfSecond'
     * can be derived from 'NanoOfDay'.
     *
     * @return the base rule, not null
     */
    public DateTimeRule getBaseRule() {
        return baseRule;
    }

    //-----------------------------------------------------------------------
    /**
     */
    public Set<DateTimeRule> getRelatedRules() {
        return new HashSet<DateTimeRule>(rules.values());
    }

    /**
     */
    public DateTimeRule getRelatedRule(DateTimeRule rule1, DateTimeRule rule2) {
        if (rule1.compareTo(rule2) > 0) {
            DateTimeRule temp = rule1;
            rule1 = rule2;
            rule2 = temp;
        }
        PeriodUnit requiredUnit = rule1.getPeriodUnit();
        PeriodUnit requiredRange = rule2.getPeriodRange();
        Map.Entry<PeriodUnit, PeriodUnit> entry = createEntry(requiredUnit, requiredRange);
        if (entry.equals(createEntry(baseRule.getPeriodUnit(), baseRule.getPeriodRange()))) {
            return baseRule;
        }
        return rules.get(entry);
    }

    //-----------------------------------------------------------------------
    /**
     */
    public void registerRelatedRule(DateTimeRule rule) {
        // TODO validate rule isn't from javax.time (use parallel private/package method)
        ISOChronology.checkNotNull(rule, "DateTimeRule must not be null");
        if (rule.getPeriodUnit().compareTo(baseRule.getPeriodUnit()) < 0) {
            throw new IllegalArgumentException("Rule includes information outside the boundaries of base rule " + baseRule.getName());
        }
        if ((rule.getPeriodRange() == null && baseRule.getPeriodRange() != null) ||
                (rule.getPeriodRange() != null && baseRule.getPeriodRange() != null && rule.getPeriodRange().compareTo(baseRule.getPeriodRange()) > 0)) {
            throw new IllegalArgumentException("Rule includes information outside the boundaries of base rule " + baseRule.getName());
        }
        registerRelatedRule0(rule);
    }

    /**
     */
    void registerRelatedRule0(DateTimeRule rule) {
        Map.Entry<PeriodUnit, PeriodUnit> entry = createEntry(rule.getPeriodUnit(), rule.getPeriodRange());
        DateTimeRule previous = rules.putIfAbsent(entry, rule);
        if (previous != null) {
            throw new IllegalArgumentException("Rule already defined for " + rule.getPeriodUnit() + " of " + rule.getPeriodRange());
        }
    }

    /**
     * @param rule
     * @return
     */
    private SimpleImmutableEntry<PeriodUnit, PeriodUnit> createEntry(PeriodUnit unit, PeriodUnit range) {
        return new AbstractMap.SimpleImmutableEntry<PeriodUnit, PeriodUnit>(unit, range);
    }

}
