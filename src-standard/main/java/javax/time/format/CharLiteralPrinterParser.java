/*
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.format;

/**
 * Prints or parses a character literal.
 * 
 * <h4>Implementation notes</h4>
 * This class is immutable and thread-safe.
 */
final class CharLiteralPrinterParser implements DateTimePrinter, DateTimeParser {

    /**
     * The literal to print or parse.
     */
    private final char literal;

    /**
     * Constructor.
     *
     * @param literal  the literal to print or parse, not null
     */
    CharLiteralPrinterParser(char literal) {
        this.literal = literal;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean print(DateTimePrintContext context, StringBuilder buf) {
        buf.append(literal);
        return true;
    }

    @Override
    public int parse(DateTimeParseContext context, CharSequence text, int position) {
        int length = text.length();
        if (position == length) {
            return ~position;
        }
        char ch = text.charAt(position);
        if (ch != literal) {
            if (context.isCaseSensitive() ||
                    (Character.toUpperCase(ch) != Character.toUpperCase(literal) &&
                     Character.toLowerCase(ch) != Character.toLowerCase(literal))) {
                return ~position;
            }
        }
        return position + 1;
    }

    @Override
    public String toString() {
        if (literal == '\'') {
            return "''";
        }
        return "'" + literal + "'";
    }

}
