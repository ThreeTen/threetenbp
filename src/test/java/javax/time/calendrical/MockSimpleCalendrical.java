/*
 * Copyright (c) 2009-2011, Stephen Colebourne & Michael Nascimento Santos
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.time.calendrical.Calendrical;
import javax.time.calendrical.CalendricalRule;

/**
 * Simple Mock Calendrical.
 *
 * @author Stephen Colebourne
 */
public class MockSimpleCalendrical implements Calendrical {

    private Map<CalendricalRule<?>, Calendrical> map = new HashMap<CalendricalRule<?>, Calendrical>();

    public MockSimpleCalendrical() {
    }

    public <T> MockSimpleCalendrical(CalendricalRule<T> rule, T value) {
        map.put(rule, (Calendrical) value);
    }

    public <T, U> MockSimpleCalendrical(CalendricalRule<T> rule1, T value1, CalendricalRule<U> rule2, U value2) {
        map.put(rule1, (Calendrical) value1);
        map.put(rule2, (Calendrical) value2);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(CalendricalRule<T> rule) {
        Calendrical value = map.get(rule);
        return (T) value;
        // don't derive anything in this mock class
        
//        return CalendricalNormalizer.merge(map.values().toArray(new Calendrical[map.size()])).derive(rule);
    }

    public <T> void put(CalendricalRule<T> rule, T value) {
        map.put(rule, (Calendrical) value);
    }

    public Set<CalendricalRule<?>> rules() {
        return map.keySet();
    }

}
