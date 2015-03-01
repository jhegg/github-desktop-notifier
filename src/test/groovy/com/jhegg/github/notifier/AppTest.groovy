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
        app.gitHubEnterpriseHostname == expected.hostname

        where:
        arguments || expected
        [] as List<String> || [token: GString.EMPTY, userName: GString.EMPTY, hostname: GString.EMPTY]
        ['-u', 'josh'] || [token: GString.EMPTY, userName: 'josh', hostname: GString.EMPTY]
        ['-t', '12345'] || [token: '12345', userName: GString.EMPTY, hostname: GString.EMPTY]
        ['-n', 'example.com'] || [token: GString.EMPTY, userName: GString.EMPTY, hostname: 'example.com']
        ['-u', 'josh', '-t', '12345', '-n', 'example.com'] || [token: '12345', userName: 'josh', hostname: 'example.com']
        ['-h'] as List<String> || [token: GString.EMPTY, userName: GString.EMPTY, hostname: GString.EMPTY]
    }

    @Unroll
    def "getIconResourcePath on #system"() {
        setup:
        app.metaClass.isOsLinux = { is_os_linux }

        expect:
        app.getIconResourcePath() == result

        where:
        system | is_os_linux || result
        "Linux" | true || "/github-notifier-black-background-256.png"
        "Windows" | false || "/github-notifier.png"
    }
}
