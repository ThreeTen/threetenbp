/*
 * Copyright (c) 2011-2012 Stephen Colebourne & Michael Nascimento Santos
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

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.DateTimes;


/**
 *
 * @author Stephen Colebourne
 */
public final class DateTimeRuleGroup {
    // TODO: tidy up, or delete, or merge into DTRule

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
        DateTimes.checkNotNull(baseRule, "DateTimeRule must not be null");
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
        DateTimes.checkNotNull(baseRule, "DateTimeRule must not be null");
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
//    /**
//     */
//    public Set<DateTimeRule> getRelatedRules() {
//        return new HashSet<DateTimeRule>(rules.values());
//    }

    public DateTimeRule getRelatedRule(PeriodUnit requiredUnit, PeriodUnit requiredRange) {
        Map.Entry<PeriodUnit, PeriodUnit> entry = createEntry(requiredUnit, requiredRange);
        if (entry.equals(createEntry(baseRule.getPeriodUnit(), baseRule.getPeriodRange()))) {
            return baseRule;
        }
        return rules.get(entry);
    }

    //-----------------------------------------------------------------------
    /**
     */
    void registerRelatedRule(DateTimeRule rule) {
        if (rule.comparePeriodUnit(baseRule) < 0) {
            throw new IllegalArgumentException("Rule includes information outside the boundaries of base rule " + baseRule.getName());
        }
        if (rule.comparePeriodRange(baseRule) > 0) {
            throw new IllegalArgumentException("Rule includes information outside the boundaries of base rule " + baseRule.getName());
        }
        Map.Entry<PeriodUnit, PeriodUnit> entry = createEntry(rule.getPeriodUnit(), rule.getPeriodRange());
        DateTimeRule previous = rules.putIfAbsent(entry, rule);
        if (previous != null) {
            throw new IllegalArgumentException("Rule already defined for " + rule.getPeriodUnit() + " of " + rule.getPeriodRange());
        }
    }

    private AbstractMap.SimpleImmutableEntry<PeriodUnit, PeriodUnit> createEntry(PeriodUnit unit, PeriodUnit range) {
        return new AbstractMap.SimpleImmutableEntry<PeriodUnit, PeriodUnit>(unit, range);
    }

}
