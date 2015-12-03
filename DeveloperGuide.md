**Table of Content**


# 1.0 Prerequisites #

You must have Ant 1.8.1 and Java 1.6 installed.  We also provide .project and .classpath files for Eclipse, although that IDE is not mandatory for development.

# 2.0 Installation #

## 2.1 Run Ant to install libraries required for compilation ##

After checkout out the sources from SVN, or else downloading the distribution, you must start by invoking "ant" at the top level of the project:

```
% ant
```

> This will do the following:

  * Download Ivy.
  * Use Ivy to download packages required for compilation, including JUnit and Robocode.
  * Compile the system.

## 2.2 Verify installation ##

Once compilation is successful, perform a "verification" of the system as follows:

```
% ant -f verify.build.xml
```

This command will run JUnit, Checkstyle, PMD, JavaDoc, and FindBugs, and fail the build if any warnings are issued by these tools.  It will also download these packages using Ivy.

## 3.0 Development Guidelines ##

Once you have verified your local installation of robocode-sht-michelangelo, you are ready for development.    We follow these development guidelines:

  * We use Eclipse for development, and format the code using [this format](http://ics-software-engineering.googlecode.com/svn/trunk/configfiles/eclipse.format.xml).

  * For new features, please add an Issue that specifies what you plan to do.

  * Please develop a JUnit test case (either before, during, or after) development of a new feature to ensure it works.

  * Before committing changes, please make sure that "ant -f verify.build.xml" runs successfully.