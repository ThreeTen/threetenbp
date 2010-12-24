/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
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

README for ThreeTen, the JSR-310 Reference Implementation
2010-12-24

Welcome to the ThreeTen project, the reference implementation for JSR-310.
This is a quick guide to help you getting started.

The subversion repository for this codebase is at:
https://threeten.svn.sourceforge.net/svnroot/threeten/trunk/threeten

The main build process uses Apache Ant - http://ant.apache.org
From the command line the following options will get you started:
  ant                 # downloads libraries and compiles code to a jar file
  ant examples        # runs a supplied java program to print some examples
  ant javadoc         # creates the javadoc
  ant test            # runs the main test suite (excluding OpenJDK classes)
  ant coverage        # calculates test coverage (excluding OpenJDK classes)
Oracle JDK 1.6 (or OpenJDK) is required to build the codebase.


ThreeTen divides time into two categories - continuous and human.

Continuous time is based around a single incrementing number from a single epoch.
ThreeTen counts in nanoseconds from 1970-01-01T00:00:00.000000000Z
Continuous time is represented as follows:
 - Instant - a point on the time-line to nanosecond precision
 - Duration - an amount of time measured in nanoseconds

Human time is based around fields, such as year, month, day and hour.
ThreeTen supports precision of nanoseconds.
The year range roughly equal to the range of a 32-bit int.
Human time is represented via a group of classes:
 - LocalDate - a date, without time of day, offset or zone
 - LocalTime - the time of day, without date, offset or zone
 - LocalDateTime - the date and time, without offset or zone
 - OffsetDate - a date with an offset such as +02:00, without time of day or zone
 - OffsetTime - the time of day with an offset such as +02:00, without date or zone
 - OffsetDateTime - the date and time with an offset such as +02:00, without a zone
 - ZonedDateTime - the date and time with a time zone and offset
 - YearMonth - a year and month
 - MonthDay - month and day
 - Year/MonthOfDay/DayOfWeek/... - classes for the important fields
 - DateTimeFields - stores a map of field-value pairs which may be invalid
 - Calendrical - access to the low-level API
 - Period - a descriptive amount of time, such as "2 months and 3 days"

Support classes include:
 - Clock - wraps the current date and time
 - ZoneOffset - the offset from UTC, such as -05:00
 - TimeZone - the time zone, such as Europe/London
 - Numerous small interfaces - these link the main classes together

Additional packages provide for:
 - formatting and parsing
 - alternate calendar systems
 - single field periods
 - integration with existing Java classes

The ThreeTen project is used to drive the reference implementation of JSR-310.
The API is currently considered usable and accurate, yet incomplete and subject to change.
If you use this API you must be able to handle incompatible changes in later versions.

Special efforts with the boot classpath are necessary to test and use
the integration with the existing Java classes.

Test coverage is at about 95% and all current tests pass.

Feedback is welcomed!
Home page: https://threeten.sourceforge.net/
Mailing list: http://sourceforge.net/mailarchive/forum.php?forum_name=threeten-develop (develop)
Wiki: http://wiki.java.net/bin/view/Projects/DateTimeAPI

ThreeTen/JSR-310 team


(See the license files for detail on licensing, warranty and disclaimers)
(All trademarks are hereby granted to their respective owners)
