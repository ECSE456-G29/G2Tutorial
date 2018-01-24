# G2Tutorial
Tool that automates the simultaneous synchronization of tutorials and commits


|branch         | status                                                                      |
|:-------------:|:---------------------------------------------------------------------------:|
|master         |![master build](https://travis-ci.org/ECSE456-G29/Untitled.svg?branch=master)|
|dev            |![dev build](https://travis-ci.org/ECSE456-G29/Untitled.svg?branch=dev)      |

## Build
To build the project run:
```sh
$ ./gradlew build
```

## Tests
To run the test suite:
```sh
$ ./gradlew test
```
You can then view the results in HTML format at `build/reports/tests/test/index.html`

## Distribute
To build a .jar file with dependencies:
```sh
$ ./gradlew fatJar
```

The resulting jar will then be available at build/libs/G2Tutorial-all.jar

## Run
To run the main application:
```sh
$ ./gradlew run
```

To run using the jar:
```sh
$ java -jar build/libs/G2Tutorial-all.jar
```

# Contributing
1. New work should be commited to a new branch
2. When ready, submit for a pull request to `dev`
3. When tests pass and one or more peers have succesfully reviewed the PR, merge the branch 
