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
package javax.time.builder;

/**
 * How to resolve invalid combinations of date or time fields.
 * 
 * @author Stephen Colebourne
 */
public enum ResolutionMode {

    /**
     * Default option to choose the most sensible resolution.
     */
    SMART,
    /**
     * Resolves the invalid combination by choosing the previous valid date or time.
     * <p>
     * For example consider invalid dates in the ISO chronology:<br />
     * The invalid input 2011-04-31 will result in 2011-04-30.<br />
     * The invalid input 2011-02-30 will result in 2011-02-28.<br />
     * The invalid input 2012-02-30 will result in 2012-02-29 (leap year).<br />
     */
    PREVIOUS_VALID,
    /**
     * Resolves the invalid combination by choosing the next valid date or time.
     * <p>
     * For example consider invalid dates in the ISO chronology:<br />
     * The invalid input 2011-04-31 will result in 2011-05-01.<br />
     * The invalid input 2011-02-30 will result in 2011-03-01.<br />
     * The invalid input 2012-02-30 will result in 2012-03-01 (leap year).<br />
     */
    NEXT_VALID,
    /**
     * Resolves the invalid combination by moving the date or time forward by the amount
     * that the combination is invalid relative to the previous valid combination.
     * <p>
     * For example consider invalid dates in the ISO chronology:<br />
     * The invalid input 2011-04-31 will result in 2011-05-01.<br />
     * The invalid input 2011-02-29 will result in 2011-03-01.<br />
     * The invalid input 2011-02-30 will result in 2011-03-02.<br />
     * The invalid input 2011-02-31 will result in 2011-03-03.<br />
     * The invalid input 2012-02-30 will result in 2012-03-01 (leap year).<br />
     * The invalid input 2012-02-31 will result in 2012-03-02 (leap year).<br />
     */
    PUSH_FORWARD,
    /**
     * Resolves the invalid combination by moving the date or time backward by the amount
     * that the combination is invalid relative to the next valid combination.
     * <p>
     * For example consider invalid dates in the ISO chronology:<br />
     * The invalid input 2011-04-31 will result in 2011-04-30.<br />
     * The invalid input 2011-02-29 will result in 2011-02-26.<br />
     * The invalid input 2011-02-30 will result in 2011-02-27.<br />
     * The invalid input 2011-02-31 will result in 2011-02-28.<br />
     * The invalid input 2012-02-30 will result in 2012-02-28 (leap year).<br />
     * The invalid input 2012-02-31 will result in 2012-02-29 (leap year).<br />
     */
    PUSH_BACKWARD,
    /**
     * Resolves the invalid combination by throwing an exception.
     * <p>
     * All invalid dates and times will cause an exception to be thrown.
     */
    STRICT;

}
