[![Build Status](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-java-time/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-java-time?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# java.time

This project aims to provide an emulated subset of `java.time` by taking a fork of [threetenbp](https://github.com/ThreeTen/threetenbp)
and minimal changes primarily due to j2cl and browser limitations such as reading files. 

- Only `java.time` imports should be used.
- All `org.threeten.bp` source is shaded to `java.time` during the build and not available in transpiled code.
- The public APIs for both are almost identical from class names, fields and method signatures.


## org.threeten.bp changes

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
- `java.time.format.DecimalStyle`, APIs using `java.util.Locale` are not available and was removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/97)
- Serialization of any class is not supported.
- `java.time.zone.ZoneId#getDisplay` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/101).



## Annotation processor arguments

- See [j2cl-java-util-currency-annotation-processor](https://github.com/mP1/j2cl-java-util-locale-annotation-processor) for more info about selecting which currencies get bundled. 
This is required by `java.text.DateFormat` which is used internally by org.threeten.bp.
- See [j2cl-java-util-locale-annotation-processor](https://github.com/mP1/j2cl-java-util-locale-annotation-processor) for more info about selecting which locales get bundled.
- See [j2cl-java-util-TimeZone-annotation-processor](https://github.com/mP1/j2cl-java-util-TimeZone-annotation-processor) for more info about selecting which locales get bundled.
- See [j2cl-locale](https://github.com/mP1/j2cl-locale) for more info about logging.

The currency code `XXX` must be selected as it is used by various `java.text` classes as a source of defaults.



## Project

A goal is to leave source in their original package `org.threeten.bp` and correct this in the distributed zip containing 
the javascript. Another goal is to support executing as many as possible of the original backport tests.



## Getting the source

You can either download the source using the "ZIP" button at the top
of the github page, or you can make a clone using git:

```
git clone git://github.com/mP1/j2cl-java-text.git