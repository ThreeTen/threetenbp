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

import java.io.Serializable;

import javax.time.CalendricalException;
import javax.time.LocalDate;

/**
 * Stores a combination of LocalDate and Chronology, providing a view on the
 * combination.
 * 
 * DateChronoView is immutable and thread-safe.
 * 
 * @author Richard Warburton
 */
public final class DateChronoView implements Comparable<DateChronoView>,
		Serializable {

	private static final long serialVersionUID = -762151706343590704L;

	private final LocalDate date;
	private final Chrono chronology;

	public static DateChronoView of(LocalDate date, Chrono chronology) {
		return new DateChronoView(date, chronology);
	}

	public static DateChronoView now(Chrono chronology) {
		return DateChronoView.of(LocalDate.now(), chronology);
	}

	private DateChronoView(LocalDate date, Chrono chronology) {
		super();
		this.date = date;
		this.chronology = chronology;
	}

	@Override
	public int compareTo(DateChronoView o) {
		return getDate().compareTo(o.getDate());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DateChronoView) {
			DateChronoView otherView = (DateChronoView) obj;
			return getDate().equals(otherView.getDate());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getDate().hashCode();
	}

	public int getYear() {
		return (int) getValue(StandardDateTimeField.YEAR);
	}

	public int getMonthOfYear() {
		return (int) getValue(StandardDateTimeField.MONTH_OF_YEAR);
	}

	public int getDayOfWeek() {
		return (int) getValue(StandardDateTimeField.DAY_OF_WEEK);
	}

	public int getDayOfMonth() {
		return (int) getValue(StandardDateTimeField.DAY_OF_MONTH);
	}

	public int getDayOfYear() {
		return (int) getValue(StandardDateTimeField.DAY_OF_YEAR);
	}

	/**
	 * Gets the value of the specified field using the chronology.
	 * 
	 * @param field  the field to query, not null
	 * @return the value of the field in the stored chronology
	 * @throws CalendricalException if the field is not supported on the chronology
	 */
	public long getValue(DateTimeField field) {
		return chronology.getValue(field, date, null);
	}

	public DateChronoView withYear(int newValue) {
		return withValue(StandardDateTimeField.YEAR, newValue);
	}

	public DateChronoView withMonthOfYear(int newValue) {
		return withValue(StandardDateTimeField.MONTH_OF_YEAR, newValue);
	}

	public DateChronoView withDayOfWeek(int newValue) {
		return withValue(StandardDateTimeField.DAY_OF_WEEK, newValue);
	}

	public DateChronoView withDayOfMonth(int newValue) {
		return withValue(StandardDateTimeField.DAY_OF_MONTH, newValue);
	}

	public DateChronoView withDayOfYear(int newValue) {
		return withValue(StandardDateTimeField.DAY_OF_YEAR, newValue);
	}

	/**
	 * Returns a copy of this date view with the field set to a new value.
     * 
     * @param field  the field to set, not null
     * @param newValue  the new value of the field
     * @return the value of the field in the stored chronology
     * @throws CalendricalException if the field is not supported on the chronology
	 */
	public DateChronoView withValue(DateTimeField field, long newValue) {
		LocalDate newDate = chronology.setDate(field, date, newValue);
        return (newDate == date ? this : DateChronoView.of(newDate, chronology));
	}

	/**
	 * @return the underlying date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @return the underlying chronology
	 */
	public Chrono getChronology() {
		return chronology;
	}

}
