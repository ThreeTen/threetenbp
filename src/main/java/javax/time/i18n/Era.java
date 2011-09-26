/*
 * Copyright (c) 2011, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.i18n;

/**
 * An era of the time-line.
 * <p>
 * Most calendar systems have a single epoch dividing the time-line into two eras.
 * However, some calendar systems, have multiple eras, such as one for the reign
 * of each leader.
 * In all cases, the era is conceptually the largest division of the time-line.
 * <p>
 * For example, the Gregorian calendar system divides time into AD and BC.
 * By contrast, the Japanese calendar system has one era per Emperor.
 * <p>
 * This interface provides basic type safety on top of eras in different calendar systems.
 * It is normally implemented by an enum.
 * An implementation of {@code Era} may be shared between different calendar systems
 * if appropriate.
 * <p>
 * Instances of this class may be created from other date objects that implement {@code Calendrical}.
 * Notably this includes {@code LocalDate} and all other date classes from other calendar systems.
 * <p>
 * This interface must be implemented with care to ensure other classes in
 * the framework operate correctly.
 * All implementations that can be instantiated must be final, immutable, thread-safe and singleton.
 * It is strongly recommended to use an enum.
 *
 * @author Stephen Colebourne
 */
public interface Era {

    /**
     * Gets the numeric value associated with the era as defined by the chronology.
     * <p>
     * Within the Time Framework for Java, all fields are allocated a numerical value.
     * The meaning of the value for era is determined by the chronology according
     * to these principles:
     * <p>
     * The era in use at 1970-01-01 has the value 1.
     * Later eras have sequentially higher values.
     * Earlier eras have sequentially lower values.
     * Each chronology should have constants providing meaning to the era value.
     * <p>
     * For example, the Gregorian chronology uses AD/BC, with AD being 1 and BC being 0.
     *
     * @return the numerical era value, within the valid range for the chronology
     */
    int getValue();

}
