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
package javax.time.calendrical;

import static org.testng.Assert.assertTrue;

import javax.time.LocalDate;
import javax.time.LocalDateTime;
import javax.time.OffsetDateTime;
import javax.time.zone.ZoneOffset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test Year.
 */
@Test
public class TestDateTimeBuilder {

    //-----------------------------------------------------------------------
    @DataProvider(name="DateTimes")
    Object[][] provider_sampleDateTimes() {
        return new Object[][] {
            {LocalDateTime.of(2008, 7, 5, 12, 30)},
            {LocalDate.of(2008, 7, 5)},
            {OffsetDateTime.of(2008, 7, 5, 12, 30, ZoneOffset.ofHours(5))},
        };
    }

    //-----------------------------------------------------------------------
    @Test(groups={"implementation"})
    public void test_interfaces() {
        assertTrue(Cloneable.class.isAssignableFrom(DateTimeBuilder.class));
        assertTrue(DateTimeAccessor.class.isAssignableFrom(DateTimeBuilder.class));
    }

//    //-----------------------------------------------------------------------
//    @Test(dataProvider="DateTimes", groups={"tck"})
//    public void testFrom(DateTime datetime) {
//        // Test that invoking DateTimeBuilder.from is the same as invoking
//        // the from method directly.
//
//        assertTest(testFromLocalDate(datetime),
//                testBuilderFrom(LocalDate.class, datetime));
//
//        assertTest(testFromLocalTime(datetime),
//                testBuilderFrom(LocalTime.class, datetime));
//
//        assertTest(testFromLocalDateTime(datetime),
//                testBuilderFrom(LocalDateTime.class, datetime));
//
//    }
//
//    private void assertTest(Object o1, Object o2){
//        if (o1 != null & o2 != null &&
//                o1.getClass() == o2.getClass() &&
//                o1 instanceof DateTimeException) {
//            Exception e1 = (Exception)o1;
//            Exception e2 = (Exception)o2;
//            assertEquals(e1.getMessage(), e2.getMessage());
//            assertTest(e1.getCause(), e2.getCause());
//        } else {
//            assertEquals(o1, o2);
//        }
//    }
//
//    private Object testFromLocalTime(DateTime datetime) {
//        try {
//            return LocalTime.from(datetime);
//        } catch (Exception e) {
//            return e;
//        }
//    }
//
//    private Object testFromLocalDate(DateTime datetime) {
//        try {
//            return LocalDate.from(datetime);
//        } catch (Exception e) {
//            return e;
//        }
//    }
//
//    private Object testFromLocalDateTime(DateTime datetime) {
//        try {
//            return LocalDateTime.from(datetime);
//        } catch (Exception e) {
//            return e;
//        }
//    }
//
//    private Object testBuilderFrom(Class type, DateTime datetime) {
//        try {
//            return DateTimeBuilder.invokeFrom(type, datetime);
//        } catch (Exception e) {
//            return e;
//        }
//    }

}
