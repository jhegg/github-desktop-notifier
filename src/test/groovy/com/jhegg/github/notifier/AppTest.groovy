package com.jhegg.github.notifier

import spock.lang.Specification
import spock.lang.Unroll

class AppTest extends Specification {
    App app = new App()

    @Unroll
    def "parseArguments invoked with '#arguments'"() {
        setup:
        App.metaClass.stop = {} // Make sure we don't actually try to exit

        when:
        app.parseArguments(arguments)

        then:
        app.token == expected.token
        app.userName == expected.userName
        app.githubEnterpriseHostname == expected.hostname

        where:
        arguments || expected
        [] as List<String> || [token: GString.EMPTY, userName: GString.EMPTY, hostname: GString.EMPTY]
        ['-u', 'josh'] || [token: GString.EMPTY, userName: 'josh', hostname: GString.EMPTY]
        ['-t', '12345'] || [token: '12345', userName: GString.EMPTY, hostname: GString.EMPTY]
        ['-n', 'example.com'] || [token: GString.EMPTY, userName: GString.EMPTY, hostname: 'example.com']
        ['-u', 'josh', '-t', '12345', '-n', 'example.com'] || [token: '12345', userName: 'josh', hostname: 'example.com']
        ['-h'] as List<String> || [token: GString.EMPTY, userName: GString.EMPTY, hostname: GString.EMPTY]
    }
}
