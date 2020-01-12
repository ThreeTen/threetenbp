## Update tzdb

ThreeTen-Backport contains a set of time-zone information independent of the JDK.
This can be updated when time-zone rules change.
There are two ways to update the data.


## Build separate tzdb.jar

The first option is to build a standalone tzdb file.
The generated jar file is added to the classpath in addition to the standard threetenbp.jar file.

1. Clone the [source repository](https://github.com/ThreeTen/threetenbp) from GitHub
2. [Download](https://www.iana.org/time-zones) the latest tzdb from IANA
3. Run make `rearguard_tarballs` to obtain the corrected input data files
4. Copy the corrected rearguard files from `tzdata{tzdb-version}-rearguard.dir` to the folder `src/tzdb/{tzdb-version}` inside the cloned source
5. Run the maven command `mvn clean package -Dtzdb-jar`
6. Add the resulting tzdb-{version}.jar file in the `target` folder to your classpath
(ignore the tadb-all.jar file)

This is the preferred approach as it separates the tzdb data from released code.


## Rebuild threetenbp.jar

The second option is to rebuild the main threetenbp.jar file.
The generated jar file will replace the standard threetenbp.jar file.

1. Clone the [source repository](https://github.com/ThreeTen/threetenbp) from GitHub
2. [Download](https://www.iana.org/time-zones) the latest tzdb from IANA
3. Run make `rearguard_tarballs` to obtain the corrected input data files
4. Copy the corrected rearguard files from `tzdata{tzdb-version}-rearguard.dir` to the folder `src/tzdb/{tzdb-version}` inside the cloned source
5. Change the version number in the maven pom to indicate it is your unoffical build
6. Run the maven command `mvn clean compile`
7. Run the maven command `mvn package -Dtzdb-update`
8. Use the resulting threetenbp.jar file in the `target` folder
