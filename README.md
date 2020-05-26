[![Build Status](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)](https://travis-ci.com/mP1/j2cl-java-time.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/j2cl-java-time/badge.svg?branch=master)](https://coveralls.io/github/mP1/j2cl-java-time?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# java.time

This project aims to provide an emulated subset of `java.time` by taking a fork of [threetenbp](https://github.com/ThreeTen/threetenbp)
and minimal changes primarily due to j2cl and browser limitations such as reading files.



## Missing/Unsupported APIs

- `java.time.chrono.Chronology` support for `java.util.ServiceLoader` has been removed rendering the manifest entries ignored.
- `java.time.chrono.DayOfWeek#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/99).
- `java.time.chrono.Era#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/98).
- `java.time.chrono.HijrahDate` support for loading a deviation file given by a system property has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/96)
- `java.time.chrono.Month#getDisplayName` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/100).
- `java.time.format.DecimalStyle`, APIs using `java.util.Locale` are not available and was removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/97)
- Serialization of any class is not supported.
- `java.time.zone.ZoneId#getDisplay` has been removed. [Ticket](https://github.com/mP1/j2cl-java-time/issues/101).



## Project

A goal is to leave source in their original package `org.threeten.bp` and correct this in the distributed zip containing 
the javascript. Another goal is to support executing as many as possible of the original backport tests.
