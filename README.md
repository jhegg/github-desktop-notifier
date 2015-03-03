# github-desktop-notifier

[![Build Status](https://travis-ci.org/jhegg/github-desktop-notifier.svg)](https://travis-ci.org/jhegg/github-desktop-notifier)
[![Coverage Status](https://coveralls.io/repos/jhegg/github-desktop-notifier/badge.svg)](https://coveralls.io/r/jhegg/github-desktop-notifier)

A cross-platform desktop notifier of GitHub activity, built using JavaFX and Groovy.

This app periodically queries the GitHub API for new notifications that your GitHub user has received. For example, you can receive notifications of pushes, branch creation, pull requests, issues, etc.

The app is not very pretty right now, it's just functional. I'm open to suggestions for the UI, just open an issue to let me know.

## System Requirements

* JDK 1.8.0_31 (to build from source, until I do an actual release)
* Linux, Mac, or Windows

## Running it quickly from Gradle

If you just want to fire it up to play around with it, use the `run` task.

1. Clone/update the repo.
2. `./gradlew clean run`
  * By default, the user and token are not set. Click on Edit -> Preferences to do so. Note that the preferences will not be stored.
  * Warning: if you do not set [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/), then GitHub will start [rejecting requests above 60 per hour](https://developer.github.com/v3/#rate-limiting).
  * Also until issue #5 is fixed, you can't set the GitHub Enterprise hostname from the UI, only from the command line parameter. See below for an example.

## Building the self-contained app

1. Clone/update the repo.
2. `./gradlew clean jfxDeploy`
  * This builds a self-contained application, with an executable for your platform.
  * On Ubuntu, the `fakeroot` package is required to build the `.deb`
3. The executable is located in the directory: `./build/distributions/github-desktop-notifier/`
  * There are also two subdirectories: `runtime` which holds the JRE, and `app` which holds the jars.
4. Either run the application directly from this directory, or copy the `github-desktop-notifier` directory tree somewhere else and run it from there.
  * Note: on Windows, the executable is ignoring all command line arguments, for some reason.

## Command-line arguments

Run with `-h` to see the available arguments. Currently they are:
* -h,--help             Show usage information
* -n,--hostname <arg>   GitHub Enterprise hostname (Optional)
* -t,--token <arg>      GitHub OAuth token (Optional)
* -u,--user <arg>       GitHub username to be queried (Required)

## How to use with GitHub.com

The app is setup to talk to GitHub.com, as long as the username is entered.

1. Get [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) for your GitHub.com account. It does not require any special permissions.
2. Open `GitHub Desktop Notifier`, click on `Edit -> Preferences`, and put in the user name and OAuth token, then click OK.
  * Or, `./gradlew run -Parguments=-u,jhegg,-t,12345`

## How to use with GitHub Enterprise

Note: Until issue #5 is fixed, you can't set the GitHub Enterprise hostname from the UI, only from the command line parameter.

1. Get [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) for your GitHub.com account. It does not require any special permissions.
2. Start the app with `-u <user> -t <token> -n <github_enterprise_hostname>`.
  * Or, `./gradlew run -Parguments=-u,jhegg,-t,12345,-n,localhost`
