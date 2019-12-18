[![Build Status](https://travis-ci.com/mP1/java-time-j2cl.svg?branch=master)](https://travis-ci.com/mP1/java-time-j2cl.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/java-time-j2cl/badge.svg?branch=master)](https://coveralls.io/github/mP1/java-time-j2cl?branch=master)

# java.time j2cl

This project aims to provide an emulated subset of `java.time` by taking a fork of [threetenbp](https://github.com/ThreeTen/threetenbp).

The end goal is to provide all value types such as `Instance`, `LocalDate`, minus offset and zone support, along with
`DateTimeFormat` and `DateTimeFormatBuilder`. 


## Classes

The following section describes in general form the following groups of classes and what is and what is not suppored.



### Chronology

The following chronologies less `IsoChronology` are not supported:

- Hijrah
- Japanese
- Minguo
- ThaiBuddhist



### Era

All eras are removed except for `IsoEra`.



### Format

The `DateTimeFormat` and `DateTimeFormatBuilder` along with support Style classes are supported with reductions. Patterns
that represent offset, timezones and similar concepts are not supported.



### Locale

Locale support is not available. All methods that take a Locale are removed and the symbols must be sourced and provided
in an alternate manner



### Values

The following value types are supported, others are removed.

- Clock
- DayOfWeek
- Duration
- Instance
- LocalDate
- LocalDateTime
- LocalTime
- Month
- MonthDay
- Period
- Year
- YearMonth



### Zone

Zone and offset support is removed, including classes and methods that take a Zone or offset.

- ZonedDateTime
- ZoneId
- ZoneOffset
- ZoneRegion
- the zone package.

This reduction greatly simplifies the total number of classes and methods.



### Serialization

Serialization is not supported, and all support classes and forms including magic methods such as `writeReplace` are removed.



## Project

A goal is to leave source in their original package `org.threeten.bp` and correct this in the distributed zip containing 
the javascript. Another goal is to support executing as many as possible of the original backport tests.


## JRE Differences

### Clock

- Clock.systemUTC() & Clock.systemDefaultZone() are equivalent, both without a ZoneId.
- Clock.getZone() is unavailable.
