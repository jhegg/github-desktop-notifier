package com.jhegg.github.notifier

import org.apache.commons.logging.Log
import spock.lang.Specification
import spock.lang.Unroll

import static org.hamcrest.CoreMatchers.hasItems
import static spock.util.matcher.HamcrestSupport.that

class LibNotifyTaskTest extends Specification {
    def "task construction creates ProcessBuilder"() {
        given:
        def command = ["/bin/bash", "-c", "echo hello"] as List<String>

        expect:
        that new LibNotifyTask(command).processBuilder.command(), hasItems("/bin/bash", "-c", "echo hello")
    }

    @Unroll
    def "test error handling with exitValue: #exitValue"() {
        given:
        LibNotifyTask libNotifyTask = new LibNotifyTask(["date"])
        def log = Mock(Log)
        libNotifyTask.log = log
        def process = Mock(Process)
        process.exitValue() >> exitValue

        when:
        libNotifyTask.handleErrors(process, standardOutput, errorOutput)

        then:
        times * log.error("Attempting to run the following command resulted in an error: [date]\n" +
                "stdout: $standardOutput\n" +
                "stderr: $errorOutput")

        where:
        exitValue | standardOutput | errorOutput || times
        1 | "some output" | "some error output" || 1
        0 | null | null | 0
    }
}
