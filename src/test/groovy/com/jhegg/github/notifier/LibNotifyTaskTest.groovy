package com.jhegg.github.notifier

import spock.lang.Specification

import static org.hamcrest.CoreMatchers.hasItems
import static spock.util.matcher.HamcrestSupport.that

class LibNotifyTaskTest extends Specification {
    def "task construction creates ProcessBuilder"() {
        given:
        def command = ["/bin/bash", "-c", "echo hello"] as List<String>

        expect:
        that new LibNotifyTask(command).processBuilder.command(), hasItems("/bin/bash", "-c", "echo hello")
    }
}
