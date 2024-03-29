## Update tzdb

ThreeTen-Backport contains a set of time-zone information independent of the JDK.
This can be updated when time-zone rules change.
There are two ways to update the data.


## Build separate tzdb.jar

The first option is to build a standalone tzdb file.
The generated jar file is added to the classpath in addition to the standard threetenbp.jar file.

1. Clone the [source repository](https://github.com/ThreeTen/threetenbp) from GitHub
2. [Download](https://github.com/JodaOrg/global-tz/releases) the latest rearguard time zone data
3. Extract and copy the rearguard files from `tzdata{tzdb-version}-rearguard.dir` to the folder `src/tzdb/{tzdb-version}` inside the threetenbp source
4. Run the maven command `mvn clean package -Dtzdb-jar`
5. Add the resulting tzdb-{version}.jar file in the `target` folder to your classpath
(ignore the tadb-all.jar file)

This is the preferred approach as it separates the tzdb data from released code.


## Rebuild threetenbp.jar

The second option is to rebuild the main threetenbp.jar file.
The generated jar file will replace the standard threetenbp.jar file.

1. Clone the [source repository](https://github.com/ThreeTen/threetenbp) from GitHub
2. [Download](https://github.com/JodaOrg/global-tz/releases) the latest rearguard time zone data
3. Extract and copy the rearguard files from `tzdata{tzdb-version}-rearguard.dir` to the folder `src/tzdb/{tzdb-version}` inside the threetenbp source
4. Change the version number in the maven pom to indicate it is your unoffical build
5. Run the maven command `mvn clean compile`
6. Run the maven command `mvn package -Dtzdb-update`
7. Use the resulting threetenbp.jar file in the `target` folder
