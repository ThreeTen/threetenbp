## <i></i> About

**ThreeTen-Backport** provides a backport of the
[Java SE 8](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html) date-time classes to Java SE 6 and 7.

The backport is NOT an implementation of JSR-310, as that would require
jumping through lots of unnecessary hoops.
Instead, this is a simple backport intended to allow users to quickly
use the JSR-310 API on Java SE 6 and 7.
The backport should be referred to using the "ThreeTen" name.
The backport is curated by the primary author of the Java 8 date and time library, [Stephen Colebourne](https://www.joda.org/).

ThreeTen-Backport is licensed under the business-friendly [BSD 3-clause license](license.html).


## <i></i> Features

The features of the backport match those of Java SE 8:

* Date and time value types
* Formatting
* Alternate calendar systems
* Utilities

The API of ThreeTen-Extra matches that of Java SE 8 as far as practicable.
Default and static methods on interfaces are simulated in the backport
by using an abstract class rather than an interface.
In addition, static query constants are used to simulate method references, for example
use <code>LocalDate.FROM</code> rather than <code>LocalDate::from</code>.


## <i></i> Documentation

Various documentation is available:

* The [Javadoc](apidocs/index.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/ThreeTen/threetenbp) source repository
* The mechanism to [update](update-tzdb.html) the time-zone information

---

## <i></i> Releases

Release 1.4.5 is the latest release.
It is considered to be stable and usable in production.

The project runs on Java SE 6 (or later) and has no [dependencies](dependencies.html).

There are some known issues.
The Hijrah calendar system does not work.
Formatting and parsing often depends on data only available in Java SE 8.
Zone id and text parsing is significantly less powerful.

Available in [Maven Central](https://search.maven.org/search?q=g:org.threeten%20AND%20a:threetenbp&core=gav).

```xml
<dependency>
  <groupId>org.threeten</groupId>
  <artifactId>threetenbp</artifactId>
  <version>1.4.5</version>
</dependency>
```

---

### Support

Please use [Stack Overflow](https://stackoverflow.com/questions/tagged/threetenbp) for general usage questions.
GitHub [issues](https://github.com/ThreeTen/threetenbp/issues) and [pull requests](https://github.com/ThreeTen/threetenbp/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-threeten-threetenbp?utm_source=maven-org-threeten-threetenbp&utm_medium=referral&utm_campaign=website).

Note that pull requests and issues will only be considered so far as matching the behaviour of Java SE releases.
Additional requested features will be rejected.

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.
