# github-desktop-notifier

[![Build Status](https://travis-ci.org/jhegg/github-desktop-notifier.svg)](https://travis-ci.org/jhegg/github-desktop-notifier)
[![Coverage Status](https://coveralls.io/repos/jhegg/github-desktop-notifier/badge.svg)](https://coveralls.io/r/jhegg/github-desktop-notifier)

A cross-platform desktop notifier of GitHub activity, built using JavaFX and Groovy.

## What does it do?

This app periodically queries the GitHub API for new notifications that your GitHub user has received. For example, you can receive notifications of pushes, branch creation, pull requests, issues, etc.

The app is not very pretty right now, it's just functional. I'm open to suggestions for the UI, just open an issue to let me know.

## System Requirements

* JDK 1.8.0_31+ (to build from source, until I do an actual release)
* Linux, Mac, or Windows
  * If Linux and using Gnome or Cinnamon, the tray icon does not work. Please disable it in the preferences.

## Running it quickly from Gradle

If you just want to fire it up to play around with it, use the `run` task.

1. Clone/update the repo.
2. `./gradlew clean run`
  * By default, the user and token are not set. Click on Edit -> Preferences to do so. Note that the preferences will not be stored until issue #6 is resolved.
  * Warning: if you do not set [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/), then GitHub will start [rejecting requests above 60 per hour](https://developer.github.com/v3/#rate-limiting).

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

1. Get [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) for your GitHub.com account. It does not require any special permissions/scopes, so it is recommended that you uncheck all of the scopes.
2. Open `GitHub Desktop Notifier`, click on `Edit -> Preferences`, and put in the user name and OAuth token, then click OK.
  * Or, `./gradlew run -Parguments=-u,jhegg,-t,12345`

## How to use with GitHub Enterprise

1. Get [an OAuth token](https://help.github.com/articles/creating-an-access-token-for-command-line-use/) for your GitHub Enterprise account. It does not require any special permissions/scopes, so it is recommended that you uncheck all of the scopes.
2. Open `GitHub Desktop Notifier`, click on `Edit -> Preferences`, and put in the user name and OAuth token and GitHub Enterprise hostname, then click OK.
  * Or, `./gradlew run -Parguments=-u,jhegg,-t,12345,-n,localhost`
3. If you get an `SSLPeerUnverifiedException`, then the most likely cause is that the SSL certificate presented by the GitHub Enterprise server is not trusted by the JRE's `cacerts` keystore. The keystore of the JRE needs to have the certificate imported. If you are using `./gradlew run`, then the keystore is `$JAVA_HOME/jre/lib/security/cacerts`; but if you use the `jfxDeploy` task, it will be the `build/distributions/github-desktop-notifier/runtime/jre/lib/security/cacerts` file. If you don't know what to do, look up the documentation for Java's `keytool` regarding importing certificates into a keystore.


