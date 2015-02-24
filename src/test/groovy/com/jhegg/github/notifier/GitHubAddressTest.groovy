package com.jhegg.github.notifier

import spock.lang.Specification
import spock.lang.Unroll

class GitHubAddressTest extends Specification {
    def app = Mock(App)

    @Unroll
    def "getResolvedUrl with user '#user' and enterpriseHost '#enterpriseHost'"() {
        setup:
        app.getUserName() >> user
        app.getGitHubEnterpriseHostname() >> enterpriseHost

        expect:
        GitHubAddress.getResolvedUrl(app) == result

        where:
        user   | enterpriseHost       || result
        "josh" | null                 || "https://api.github.com/users/josh/received_events"
        "me"   | null                 || "https://api.github.com/users/me/received_events"
        "josh" | "example.com"        || "https://example.com/api/v3/users/josh/received_events"
        "you"  | "github.example.com" || "https://github.example.com/api/v3/users/you/received_events"
    }
}
