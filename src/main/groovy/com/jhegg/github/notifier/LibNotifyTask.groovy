package com.jhegg.github.notifier

import javafx.concurrent.Task

class LibNotifyTask extends Task {
    ProcessBuilder processBuilder

    LibNotifyTask(def commandAndArguments) {
        processBuilder = new ProcessBuilder(commandAndArguments as List<String>)
    }

    @Override
    protected call() throws Exception {
        def process = processBuilder.start()
        def out = new StringBuffer()
        def err = new StringBuffer()
        process.waitForProcessOutput(out, err)
        if (process.exitValue() != 0) {
            println "Attempting to run the following command resulted in an error: $commandAndArguments"
            println "LibNotify stdout: $out"
            println "LibNotify stderr: $err"
        }
    }
}
