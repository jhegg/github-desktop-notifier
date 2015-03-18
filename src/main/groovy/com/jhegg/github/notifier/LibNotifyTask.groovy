package com.jhegg.github.notifier

import javafx.concurrent.Task
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class LibNotifyTask extends Task {
    ProcessBuilder processBuilder

    Log log = LogFactory.getLog(LibNotifyTask.class)

    LibNotifyTask(def commandAndArguments) {
        processBuilder = new ProcessBuilder(commandAndArguments as List<String>)
    }

    @Override
    protected call() throws Exception {
        def process = processBuilder.start()
        def out = new StringBuffer()
        def err = new StringBuffer()
        process.waitForProcessOutput(out, err)
        handleErrors(process, out, err)
    }

    void handleErrors(def process, def out, def err) {
        if (process.exitValue() != 0) {
            log.error("Attempting to run the following command resulted in an error: ${processBuilder.command()}\n" +
                    "stdout: $out\n" +
                    "stderr: $err")
        }
    }
}
