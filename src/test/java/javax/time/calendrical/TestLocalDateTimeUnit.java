package javax.time.calendrical;

import static org.testng.Assert.assertEquals;

import static javax.time.calendrical.LocalDateTimeUnit.YEARS;

import javax.time.LocalDate;
import javax.time.Month;

import org.testng.annotations.Test;

@Test
public class TestLocalDateTimeUnit {

	public void testYearsBetweenInSameMonth() {
		LocalDate begin = LocalDate.of(1939, Month.SEPTEMBER, 1);
		LocalDate end = LocalDate.of(1945, Month.SEPTEMBER, 2);
		
		assertEquals( 6 ,YEARS.between(begin, end).getAmountInt());
	}
	
	public void testYearsBetweenInMonthAfter() {
		LocalDate begin = LocalDate.of(1939, Month.SEPTEMBER, 1);
		LocalDate end = LocalDate.of(1945, Month.OCTOBER, 2);
		
		assertEquals( 6 ,YEARS.between(begin, end).getAmountInt());
	}
	
	public void testYearsBetweenInMonthBefore() {
		LocalDate begin = LocalDate.of(1939, Month.SEPTEMBER, 1);
		LocalDate end = LocalDate.of(1945, Month.AUGUST, 2);
		
		assertEquals( 5 ,YEARS.between(begin, end).getAmountInt());
	}
}
