# FeignX 

[![CircleCI](https://circleci.com/gh/OpenFeign/feignx/tree/master.svg?style=svg)](https://circleci.com/gh/OpenFeign/feignx/tree/master) ![codecov](https://codecov.io/gh/OpenFeign/feignx/branch/master/graph/badge.svg)
[![Known Vulnerabilities](https://snyk.io/test/github/openfeign/feignx/badge.svg)](https://snyk.io/test/github/openfeign/feignx)

FeignX, an experimental version of [Feign](https://github.com/OpenFeign/feign), extending the
core concepts of Feign beyond HTTP and REST including support for pooled, asynchronous, non-blocking,
and reactive execution modes.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for 
development and testing purposes.

### Prerequisites

What things you need:

* [Recent Java JDK: (1.8 or higher)](https://adoptopenjdk.net/)
* A clone of this repository:

```
git clone https://github.com/openfeign/feignx.git
cd feignx
```

### Building from the Command Line

To compile, test, and build all artifacts, use:

```
./mvnw install
```

The first time you run the build, it may take some time to download Maven and all of the required
dependencies, compile, and run all of the tests.  Dependencies will be cached in your `$HOME/.m2`
directory.

### Importing into your IDE

Most popular IDE environments support reading project information from a Maven `pom.xml` file.  
Follow the instructions for your preferred IDE:

* [JetBrains IntelliJ](https://www.jetbrains.com/help/idea/maven-support.html)
* [Eclipse](https://books.sonatype.com/m2eclipse-book/reference/creating-sect-importing-projects.html)
* [Visual Studio Code](https://code.visualstudio.com/docs/java/java-project)

## Running the tests

To test your changes, run the test suite using:

```
./mvnw clean test
```

This will run the entire test suite, verifying your changes including formatting, licensing, and 
code coverage.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Snyk](https://snyk.io/) - Security and Dependency Scanning

## Issues

To report an issue, or check on the status of an issue, see the [Issues](https://github.com/openfeign/feignx/issues) 
section of this repository. 

## Contributing

Please read [CONTRIBUTING](CONTRIBUTING.md) for details on our [Code of Conduct](CODE_OF_CONDUCT.md), 
and the process for submitting [Pull Requests](https://github.com/openfeign/feignx/pulls) to us.

## Releases

For the versions available, see the [tags on this repository](https://github.com/openfeign/feignx/tags)
or [Maven Central](https://search.maven.org/search?q=g:io.github.openfeign) 

## Contributors

See also the list of [contributors](https://github.com/openfeign/contributors) who participated in this project.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE.md) file for details

## Acknowledgments

* [Netflix OSS](https://netflix.github.io/)
