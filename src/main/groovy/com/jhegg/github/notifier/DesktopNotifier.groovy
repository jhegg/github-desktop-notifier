package com.jhegg.github.notifier

import groovy.json.JsonSlurper
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import org.apache.commons.lang.SystemUtils
import org.controlsfx.control.Notifications

class DesktopNotifier {
    boolean isPlatformLinux = SystemUtils.IS_OS_LINUX

    Stage hiddenStage

    void send(GitHubEvent event) {
        if (isPlatformLinux && hasLibNotify()) {
            sendLibNotifyMessage(event)
        } else {
            sendJavaFxMessage(event)
        }
    }

    void sendLibNotifyMessage(GitHubEvent event) {

    }

    boolean hasLibNotify() {
        return new File('/usr/bin/notify-send').exists()
    }

    void sendJavaFxMessage(GitHubEvent event) {
        Notifications.create()
                .title(event.type ?: "Unknown event")
                .text(getNotificationText(event))
                .hideAfter(Duration.seconds(5d))
                .show()
    }

    String getNotificationText(GitHubEvent gitHubEvent) {
        def json = new JsonSlurper().parseText(gitHubEvent.json)

        if (gitHubEvent.type == "PushEvent") {
            String truncatedMessage = json.payload.commits[0].message.tokenize('\n\r').get(0)
            return "${gitHubEvent.login} pushed ${json.payload.size} commit(s) to repo ${json.repo.name}\n\n" +
                    "\"${truncatedMessage.take(85)} ...\""
        } else {
            "${gitHubEvent.login} acted on repo ${json.repo.name}"
        }
    }

    /**
     * This is a hack, because JavaFX does not have support yet for the system tray. When the application window is
     * hidden, then the Notifications don't have a stage upon which to be displayed. So, we create a second hidden
     * stage that lives off-screen, and does not show up on the task bar (so it can't be hidden by the user).
     *
     * Note: this does NOT work well on Linux platforms. It seems to be fine for Windows, though.
     */
    void initializeHiddenStage() {
        if (!hiddenStage) {
            Stage stage = new Stage()
            stage.initStyle(StageStyle.UTILITY)
            stage.setMaxHeight(1)
            stage.setMaxWidth(1)
            stage.setHeight(1)
            stage.setWidth(1)
            stage.setX(Double.MAX_VALUE) // move the window off-screen (does not work on Linux)
            stage.setScene(new Scene(new Group()))
            stage.show()
            hiddenStage = stage
        }
    }
}
