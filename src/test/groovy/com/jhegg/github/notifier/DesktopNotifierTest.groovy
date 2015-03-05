package com.jhegg.github.notifier

import spock.lang.Specification
import spock.lang.Unroll

class DesktopNotifierTest extends Specification {
    DesktopNotifier desktopNotifier = new DesktopNotifier()

    @Unroll
    def "getNotificationText for type #type and login #login"() {
        expect:
        desktopNotifier.getNotificationText(new GitHubEvent(type: type, login: login, json: json)) == result

        where:
        type          | login          | json                                               || result
        "PushEvent"   | "SomeUser"     | GitHubJsonPayloadExamples.exampleSinglePushPayload || "SomeUser pushed 1 commit(s) to repo SomeOrg/i-made-this\n\n\"I made this thing ...\""
        "CreateEvent" | "creatorLogin" | GitHubJsonPayloadExamples.exampleCreateEventJson   || "creatorLogin acted on repo SomeOrg/some-new-repo"
    }

    @Unroll
    def "send event where isPlatformLinux==#isPlatformLinux and hasLibNotify==#hasLibNotify"() {
        setup:
        boolean sentJavaFxMessage = false
        boolean sentLibNotifyMessage = false
        desktopNotifier.metaClass.sendJavaFxMessage = { GitHubEvent event -> sentJavaFxMessage = true }
        desktopNotifier.metaClass.sendLibNotifyMessage = { GitHubEvent event -> sentLibNotifyMessage = true }
        desktopNotifier.metaClass.hasLibNotify = { return hasLibNotify }
        desktopNotifier.isPlatformLinux = isPlatformLinux

        when:
        desktopNotifier.send(new GitHubEvent(id: 1))

        then:
        sentJavaFxMessage == wasJavaFxMessageSent
        sentLibNotifyMessage == wasLibNotifyMessageSent

        where:
        isPlatformLinux | hasLibNotify || wasJavaFxMessageSent | wasLibNotifyMessageSent
        true            | true         || false | true
        true            | false        || true | false
        false           | false        || true | false
        false           | true         || true | false
    }
}
