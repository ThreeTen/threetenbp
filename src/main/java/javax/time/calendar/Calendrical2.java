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
package javax.time.calendar;

/**
 * Provides access in a uniform way to calendrical information.
 * <p>
 * All classes that can provide calendrical information should implement this interface.
 * It allows one of the standard calendrical concepts to be extracted.
 * <p>
 * This interface makes no guarantees about the thread-safety or immutability of implementations.
 * 
 * @author Stephen Colebourne
 */
public interface Calendrical2 {

    /**
     * Extracts the value of a calendrical concept.
     * <p>
     * This is a low-level method used to unify the API between different calendricals.
     * Applications should use factory methods taking a {@code Calendrical}.
     * <p>
     * When implementing a class providing calendrical information, this method
     * allows the implementor to provide conversions to the standard known types.
     * The implementation must return a value for as many concepts as it can.
     *
     * @param concept  the concept to extract, not null
     * @return the extracted value, null if the value cannot be returned
     */
    Object extractCalendrical(CalendricalConcept concept);

}
