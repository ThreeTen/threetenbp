
## ThreeTen backport project
JSR-310 provides a new date and time library for Java SE 8.
This project is the backport to Java SE 6 and 7.

See the [main home page](http://www.threeten.org/threetenbp/) of the project.

The backport is NOT an implementation of JSR-310, as that would require
jumping through lots of unnecessary hoops.
Instead, this is a simple backport intended to allow users to quickly
use the JSR-310 API on Java SE 6 and 7.
The backport should be referred to using the "ThreeTen" name.

Active development on JSR-310 is at [OpenJDK](http://openjdk.java.net/):

This GitHub repository is a fork of that originally used to create JSR-310.
That repository used the same BSD 3-clause license as this repository.

Issues about the backport should be reported here at GitHub.
Pull requests and issues will only be considered so far as matching the behaviour
of the real Java SE 8. Additional requested features will be rejected.

#### Building
This project builds using maven.

#### Time-zone data
The time-zone database is stored as a pre-compiled dat file that is included in the built jar.
The version of the time-zone data used is stored within the dat file (near the start).
Updating the time-zone database involves using the `TzdbZoneRulesCompiler` class
and re-compiling the jar file.
Pull requests with later versions of the dat file will be accepted.

#### FAQs

1. What version of Java SE 8 does this project map to?
This project currently maps to the expected contents of release Java SE 8u20.

2. Will the backport be kept up to date?
There are no plans for further releases.
However if bugs are found, or pull requests received then a release may occur.

3. Is this project derived from OpenJDK?
No. This project is derived from the Reference Implementation previously hosted on GitHub.
That project had a BSD license, which has been preserved here.
Thus, this project is a fork of the original code before entry to OpenJDK.
