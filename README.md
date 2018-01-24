# G2Tutorial
Tool that automates the simultaneous synchronization of tutorials and commits


|branch         | status                                                                      |
|:-------------:|:---------------------------------------------------------------------------:|
|master         |![master build](https://travis-ci.org/ECSE456-G29/G2Tutorial.svg?branch=master)|
|dev            |![dev build](https://travis-ci.org/ECSE456-G29/G2Tutorial.svg?branch=dev)      |

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

To run the clean code linter:
```sh
$ ./gradlew check
```
You can then view the results in HTML format at `build/reports/checkstyle/`

## Installation
To create distributions run:
```sh
$ ./gradlew installDist
```

Then to install:
```sh
$ tar -xvf build/distributions/G2Tutorial.tar
$ chmod +x G2Tutorial/bin/G2Tutorial
$ ln -sf `pwd`/G2Tutorial/bin/G2Tutorial /usr/local/bin/g2t
```

G2Tutorial is then available anywhere as the command `g2t`!

# Contributing
1. New work should be commited to a new branch
2. When ready, submit for a pull request to `dev`
3. When tests pass and one or more peers have succesfully reviewed the PR, merge the branch 
