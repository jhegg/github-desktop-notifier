package com.jhegg.github.notifier

import spock.lang.Specification
import spock.lang.Unroll

import static com.jhegg.github.notifier.GitHubJsonPayloadExamples.*

class DesktopNotifierTest extends Specification {
    DesktopNotifier desktopNotifier = new DesktopNotifier()

    @Unroll
    def "send event where isPlatformLinux==#isPlatformLinux and hasLibNotify==#hasLibNotify"() {
        setup:
        boolean sentJavaFxMessage = false
        boolean sentLibNotifyMessage = false
        desktopNotifier.metaClass.sendJavaFxMessage = { String title, String message -> sentJavaFxMessage = true }
        desktopNotifier.metaClass.sendLibNotifyMessage = { String title, String message -> sentLibNotifyMessage = true }
        desktopNotifier.metaClass.hasLibNotify = { return hasLibNotify }
        desktopNotifier.isPlatformLinux = isPlatformLinux

        when:
        desktopNotifier.send(new GitHubEvent(id: 1, json: exampleCreateRepoEventJson))

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
