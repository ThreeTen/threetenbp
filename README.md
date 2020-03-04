[![Build Status](https://travis-ci.com/mP1/java-time-j2cl.svg?branch=master)](https://travis-ci.com/mP1/java-time-j2cl.svg?branch=master)
[![Coverage Status](https://coveralls.io/repos/github/mP1/java-time-j2cl/badge.svg?branch=master)](https://coveralls.io/github/mP1/java-time-j2cl?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# java.time j2cl

This project aims to provide an emulated subset of `java.time` by taking a fork of [threetenbp](https://github.com/ThreeTen/threetenbp).

The end goal is to provide all value types such as `Instance`, `LocalDate`, minus offset and zone support, along with
`DateTimeFormat` and `DateTimeFormatBuilder`. 


## Classes

The following section describes in general form the following groups of classes and what is and what is not suppored.



### Serialization

Serialization is not supported, and all support classes and forms including magic methods such as `writeReplace` are removed.



## Project

A goal is to leave source in their original package `org.threeten.bp` and correct this in the distributed zip containing 
the javascript. Another goal is to support executing as many as possible of the original backport tests.
