package com.jhegg.github.notifier

import spock.lang.Specification
import spock.lang.Unroll

class GithubServiceTest extends Specification {
    GithubService service = new GithubService()
    def layoutController = Mock(CenterLayoutController)
    def app = Mock(App)

    def setup() {
        service.layoutController = layoutController
        service.app = app
    }

    @Unroll
    def "getResolvedUrl stuff"() {
        setup:
        app.getUserName() >> user
        app.getGithubEnterpriseHostname() >> enterpriseHost

        expect:
        service.getResolvedUrl() == result

        where:
        user   | enterpriseHost       || result
        "josh" | null                 || "https://api.github.com/users/josh/received_events"
        "me"   | null                 || "https://api.github.com/users/me/received_events"
        "josh" | "example.com"        || "https://example.com/api/v3/users/josh/received_events"
        "you"  | "github.example.com" || "https://github.example.com/api/v3/users/you/received_events"
    }

    def "failure handler sets error message"() {
        setup:
        app.getGithubUrlSuffixWithPlaceholder() >> "https://www.example.com"
        app.getUserName() >> "josh"

        when:
        service.failed()

        then:
        1 * layoutController.displayError(_ as String)
    }

    def "success handler parses empty events"() {
        when:
        service.metaClass.getValue = { "{}" }
        service.succeeded()

        then:
        1 * layoutController.updateEvents({it.isEmpty()})
    }

    def "success handler parses single push event"() {
        when:
        service.metaClass.getValue = { "[${getExampleSinglePushPayload()}]" }
        service.succeeded()

        then:
        1 * layoutController.updateEvents({ List<GithubEvent> events ->
            events.size() == 1
            events[0].id == "2671420212"
            events[0].type == "PushEvent"
            events[0].login == "SomeUser"
            events[0].created_at == "2015-02-01T01:02:03Z"
            !events[0].json.isEmpty()
        })
    }

    String getExampleSinglePushPayload() {
        """{
    "actor": {
        "avatar_url": "https://avatars.githubusercontent.com/u/12345?",
        "gravatar_id": "",
        "id": 12345,
        "login": "SomeUser",
        "url": "https://api.github.com/users/SomeUser"
    },
    "created_at": "2015-02-01T01:02:03Z",
    "id": "2671420212",
    "org": {
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
        "gravatar_id": "",
        "id": 123456,
        "login": "SomeOrg",
        "url": "https://api.github.com/orgs/SomeOrg"
    },
    "payload": {
        "before": "8aeb1085cf37920495bac0f0c0ea00d7cd6d2105",
        "commits": [
            {
                "author": {
                    "email": "someuser@example.com",
                    "name": "Some User"
                },
                "distinct": true,
                "message": "I made this thing",
                "sha": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
                "url": "https://api.github.com/repos/SomeOrg/i-made-this/commits/05351301f9400ddaf5d7aaec4f55ab13a06986c3"
            }
        ],
        "distinct_size": 1,
        "head": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
        "push_id": 551032016,
        "ref": "refs/heads/master",
        "size": 1
    },
    "public": true,
    "repo": {
        "id": 1234567,
        "name": "SomeOrg/i-made-this",
        "url": "https://api.github.com/repos/SomeOrg/i-made-this"
    },
    "type": "PushEvent"
}"""
    }
}
