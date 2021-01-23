[![Build Status](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-java-time/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-java-time?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![J2CL compatible](https://img.shields.io/badge/J2CL-compatible-brightgreen.svg)](https://github.com/mP1/j2cl-central)



# java.time

This project aims to provide an most of `java.time` by taking a fork of [threetenbp](https://github.com/ThreeTen/threetenbp).

- Only `java.time` imports should be used, the `org.threeten.bp` source is shaded to `java.time` during the build and not available in transpiled code.
- The public APIs for both `java.time` and `org.threetenbp` are almost identical in the vast majority of class names, methods and fields.
- Many other java packages are required by `java.time` and are not currently emulated by the JRE provided by [j2cl](https://github.com/google/j2cl).
- The missing JRE packages are provided and listed at [additional java packages](https://github.com/mP1/j2cl-central#jre-emulation).



## org.threeten.bp

Changes have been kept to a minimum when porting `org.threeten.bp`. It should be relatively straightforward to pull
updates from the original when necessary. 

- Unnecessary code has been commented out as much as possible.
- Replacing `java.util.regex.Pattern` with a pure java equivalent
- Eliminating some reflection to call a new JDK8 `java.util.Locale` method.
- Serialization support has been removed, but the deserializations remain and are used to recreate the tzdb data and rules.
- New previously un emulated APIs that are used internally are now emulated completely (`java.util.Calendar`).

Most features such as parsing and formatting all of the dates, times and related classes should work using the `java.util.Locale` 
and `java.util.TimeZone` selected for bundling.



## Missing/Unsupported APIs

- `java.time.chrono.Chronology` support for `java.util.ServiceLoader` has been removed rendering the manifest entries ignored.
- `java.time.chrono.DayOfWeek#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/99).
- `java.time.chrono.Era#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/98).
- `java.time.chrono.HijrahDate` support for loading a deviation file given by a system property has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/96)
- `java.time.chrono.Month#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/100).
- `java.time.zone.ZoneId#getDisplay` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/101).
- Serialization of any class is not supported.



## Annotation processor arguments

- See [j2cl-java-util-currency-annotation-processor](https://github.com/mP1/j2cl-java-util-locale-annotation-processor) for more info about selecting which currencies get bundled. 
This is required by `java.text.DateFormat` which is used internally by org.threeten.bp.
- See [j2cl-java-util-locale-annotation-processor](https://github.com/mP1/j2cl-java-util-locale-annotation-processor) for more info about selecting which locales get bundled.
- See [j2cl-java-util-TimeZone-annotation-processor](https://github.com/mP1/j2cl-java-util-TimeZone-annotation-processor) for more info about selecting which locales get bundled.
- See [j2cl-locale](https://github.com/mP1/j2cl-locale) for more info about logging.

The currency code `XXX` must be selected as it is used by various `java.text` classes as a source of defaults.



## File sizes

Builds were repeated on [JunitTest](https://github.com/mP1/j2cl-java-time/blob/master/src/it/junit-test/src/test/java/test/JunitTest.java) 
with only the selection of Locale and Timezone changed. Building has been done with `compilationLevel=ADVANCED` so that
dead code is pruned and everything is obsfucated and minified. 

Each sample includes the selected locales and timezone along with some logging that reports the actual size of data for
a JRE component such as `java.text.DateFormatSymbols` and so on. The selected logging text is taken from the log generated
during the build by [j2cl-maven-plugin](https://github.com/mP1/j2cl-maven-plugin).



### EN-NZ, Australia/* 287k, 89k compressed

TimeZones: 23 All Australian.

Locale: 2

```text
LocaleProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.locale.LocaleProvider.java, 1 Locale(s) selected by "EN-NZ", data: 15 char(s), utf-8: 15 byte(s), gzipped 33 byte(s)
DateFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatSymbolsProvider.java, 1 Locale(s) selected by "EN-NZ", data: 256 char(s), utf-8: 256 byte(s), gzipped 177 byte(s)
DateFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatProvider.java, 1 Locale(s) selected by "EN-NZ", data: 473 char(s), utf-8: 473 byte(s), gzipped 136 byte(s)
DecimalFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatProvider.java, 1 Locale(s) selected by "EN-NZ", data: 263 char(s), utf-8: 264 byte(s), gzipped 120 byte(s)
DecimalFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatSymbolsProvider.java, 1 Locale(s) selected by "EN-NZ", data: 31 char(s), utf-8: 35 byte(s), gzipped 55 byte(s)
TimeZoneProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.timezone.support.TimeZoneProvider.java, 1 Locale(s) selected by "EN-NZ", 23 TimeZone(s) selected by "Australia/*", data: 22650 char(s), utf-8: 22650 byte(s), gzipped 2017 byte(s)
CurrencyProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.currency.CurrencyProvider.java, 1 Locale(s) selected by "EN-NZ", 1 Currency(s) selected by "XXX", data: 54 char(s), utf-8: 54 byte(s), gzipped 60 byte(s)
```



### EN-NZ, EN-AU, Australia/* 288k, 89k compressed

TimeZones: 23 All Australian.

Locale: 2

```text
LocaleProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.locale.LocaleProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", data: 29 char(s), utf-8: 29 byte(s), gzipped 41 byte(s)
DateFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatSymbolsProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", data: 529 char(s), utf-8: 529 byte(s), gzipped 229 byte(s)
DateFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", data: 945 char(s), utf-8: 945 byte(s), gzipped 174 byte(s)
DecimalFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", data: 269 char(s), utf-8: 270 byte(s), gzipped 124 byte(s)
DecimalFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatSymbolsProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", data: 60 char(s), utf-8: 68 byte(s), gzipped 68 byte(s)
TimeZoneProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.timezone.support.TimeZoneProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", 23 TimeZone(s) selected by "Australia/*", data: 22926 char(s), utf-8: 22926 byte(s), gzipped 2032 byte(s)
CurrencyProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.currency.CurrencyProvider.java, 2 Locale(s) selected by "EN-NZ,EN-AU", 1 Currency(s) selected by "XXX", data: 98 char(s), utf-8: 98 byte(s), gzipped 79 byte(s)
```



### EN-*, Australia/* 326k 94k compressed
TimeZones: 23 All Australian.

Locales: "en-001,en-150,en-AG,en-AI,en-AS,en-AT,en-AU,en-BB,en-BE,en-BI,en-BM,en-BS,en-BW,en-BZ,en-CA,en-CC,en-CH,en-CK,en-CM,en-CX,en-CY,en-DE,en-DG,en-DK,en-DM,en-ER,en-FI,en-FJ,en-FK,en-FM,en-GB,en-GD,en-GG,en-GH,en-GI,en-GM,en-GU,en-GY,en-HK,en-IE,en-IL,en-IM,en-IN,en-IO,en-JE,en-JM,en-KE,en-KI,en-KN,en-KY,en-LC,en-LR,en-LS,en-MG,en-MH,en-MO,en-MP,en-MS,en-MT,en-MU,en-MW,en-MY,en-NA,en-NF,en-NG,en-NL,en-NR,en-NU,en-NZ,en-PG,en-PH,en-PK,en-PN,en-PR,en-PW,en-RW,en-SB,en-SC,en-SD,en-SE,en-SG,en-SH,en-SI,en-SL,en-SS,en-SX,en-SZ,en-TC,en-TK,en-TO,en-TT,en-TV,en-TZ,en-UG,en-UM,en-US,en-US-POSIX,en-VC,en-VG,en-VI,en-VU,en-WS,en-ZA,en-ZM,en-ZW"

```text
en-001,en-150,en-AG,en-AI,en-AS,en-AT,en-AU,en-BB,en-BE,en-BI,en-BM,en-BS,en-BW,en-BZ,en-CA,en-CC,en-CH,en-CK,en-CM,en-CX,en-CY,en-DE,en-DG,en-DK,en-DM,en-ER,en-FI,en-FJ,en-FK,en-FM,en-GB,en-GD,en-GG,en-GH,en-GI,en-GM,en-GU,en-GY,en-HK,en-IE,en-IL,en-IM,en-IN,en-IO,en-JE,en-JM,en-KE,en-KI,en-KN,en-KY,en-LC,en-LR,en-LS,en-MG,en-MH,en-MO,en-MP,en-MS,en-MT,en-MU,en-MW,en-MY,en-NA,en-NF,en-NG,en-NL,en-NR,en-NU,en-NZ,en-PG,en-PH,en-PK,en-PN,en-PR,en-PW,en-RW,en-SB,en-SC,en-SD,en-SE,en-SG,en-SH,en-SI,en-SL,en-SS,en-SX,en-SZ,en-TC,en-TK,en-TO,en-TT,en-TV,en-TZ,en-UG,en-UM,en-US,en-US-POSIX,en-VC,en-VG,en-VI,en-VU,en-WS,en-ZA,en-ZM,en-ZW
LocaleProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.locale.LocaleProvider.java, 105 Locale(s) selected by "EN-*", data: 1490 char(s), utf-8: 1490 byte(s), gzipped 512 byte(s)
DateFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatSymbolsProvider.java, 105 Locale(s) selected by "EN-*", data: 1930 char(s), utf-8: 1930 byte(s), gzipped 495 byte(s)
DateFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DateFormatProvider.java, 105 Locale(s) selected by "EN-*", data: 9918 char(s), utf-8: 9918 byte(s), gzipped 1108 byte(s)
DecimalFormatProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatProvider.java, 105 Locale(s) selected by "EN-*", data: 12548 char(s), utf-8: 12684 byte(s), gzipped 916 byte(s)
DecimalFormatSymbolsProviderAnnotationProcessor generated walkingkooka.j2cl.java.text.DecimalFormatSymbolsProvider.java, 105 Locale(s) selected by "EN-*", data: 2211 char(s), utf-8: 2483 byte(s), gzipped 642 byte(s)
TimeZoneProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.timezone.support.TimeZoneProvider.java, 105 Locale(s) selected by "EN-*", 23 TimeZone(s) selected by "Australia/*", data: 29596 char(s), utf-8: 29596 byte(s), gzipped 2276 byte(s)
CurrencyProviderAnnotationProcessor generated walkingkooka.j2cl.java.util.currency.CurrencyProvider.java, 105 Locale(s) selected by "EN-*", 1 Currency(s) selected by "XXX", data: 2602 char(s), utf-8: 2620 byte(s), gzipped 1045 byte(s)
```

Cost of the same junit test with an empty test is about 50k.

Examining the total size of the single javascript file and the size of the embedded data most of the cost is for code and not data.
The `java.time` uses many other java packages, both as utilities and as the source for locale sensitive text and symbols,
such as week days and more. The test using all EN locales is not considerably bigger and thats because many EN locales actually
share exactly the same data, with EN-AU and EN-NZ everything from week names, number symbols and more, the only real difference
are symbols relating to currency.

There are many other opportunites to share data and reduce code bloat which are not mentioned here and some have issues
already created in their respective projects, such as only bundling current or current + future timezone data and more.


