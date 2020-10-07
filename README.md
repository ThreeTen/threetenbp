
## ThreeTen backport project
JSR-310 provides a new date and time library for Java SE 8.
This project is the backport to Java SE 6 and 7.

See the [main home page](https://www.threeten.org/threetenbp/) of the project.

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
An automated CI job should help keep the time-zone data up to date.

#### FAQs

1. What version of Java SE 8 does this project map to?
This project currently maps to the contents of release Java SE 8u20.

2. Will the backport be kept up to date?
There are no plans for further releases.
However if security issues or bugs are found, or pull requests received then a release may occur.

3. Is this project derived from OpenJDK?
No. This project is derived from the Reference Implementation previously hosted on GitHub.
That project had a BSD license, which has been preserved here.
Thus, this project is a fork of the original code before entry to OpenJDK.

### Releases
Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threetenbp&core=gav)

![Tidelift dependency check](https://tidelift.com/badges/github/ThreeTen/threetenbp)


### Support
Please use [Stack Overflow](https://stackoverflow.com/questions/tagged/threetenbp) for general usage questions.
GitHub [issues](https://github.com/ThreeTen/threetenbp/issues) and [pull requests](https://github.com/ThreeTen/threetenbp/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-threeten-threetenbp?utm_source=maven-org-threeten-threetenbp&utm_medium=referral&utm_campaign=readme).

Note that pull requests and issues will only be considered so far as matching the behaviour of Java SE releases.
Additional requested features will be rejected.

Pull requests must _not_ be copied from the JDK, because the GPL license is incompatible with the BSD license used here.

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.


### Release process

* Update version (index.md, changes.xml - checking tzdb version)
* Commit and push
* Run `mvn clean release:clean release:prepare release:perform` on Java 11
* Website will be built and released by GitHub Actions
