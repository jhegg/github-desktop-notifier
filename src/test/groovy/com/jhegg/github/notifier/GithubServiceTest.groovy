package com.jhegg.github.notifier

import groovyx.net.http.HTTPBuilder
import spock.lang.Specification
import spock.lang.Unroll

class GitHubServiceTest extends Specification {
    GitHubService service = new GitHubService()
    def app = Mock(App)

    def setup() {
        service.app = app
        service.restartOnFailure = false
        service.maximumFailureCount = 0
    }

    @Unroll
    def "getHeaders with token '#token'"() {
        setup:
        app.getToken() >> token

        expect:
        service.getHeaders() == result

        where:
        token | result
        null | ['User-Agent': 'Apache HTTPClient',
                'Accept'    : 'application/vnd.github.v3+json',]
        "1234" | ['User-Agent': 'Apache HTTPClient',
                  'Accept'    : 'application/vnd.github.v3+json',
                  'Authorization' : "token 1234"]
    }

    def "service creation"() {
        setup:
        service.gitHub = Mock(HTTPBuilder)

        when:
        service.createTask().call()

        then:
        1 * service.gitHub.setUri(_)
        1 * service.gitHub.get(*_)
    }
}
