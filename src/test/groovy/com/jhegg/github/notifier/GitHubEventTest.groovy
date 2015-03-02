package com.jhegg.github.notifier

import spock.lang.Shared
import spock.lang.Specification

class GitHubEventTest extends Specification {
    @Shared def event = new GitHubEvent(id: 1)
    @Shared def eventTwo = new GitHubEvent(id: 2)

    def "CompareTo greater than"() {
        expect:
        eventTwo > event
    }

    def "CompareTo less than"() {
        expect:
        event < eventTwo
    }

    def "CompareTo equals"() {
        expect:
        event == event
        eventTwo == eventTwo
    }

    def "CompareTo not equals"() {
        expect:
        event != eventTwo
        eventTwo != event
    }
}
