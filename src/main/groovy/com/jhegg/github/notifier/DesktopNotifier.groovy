package com.jhegg.github.notifier

import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import org.apache.commons.lang.SystemUtils
import org.controlsfx.control.Notifications

class DesktopNotifier {
    static final String notifySendPath = '/usr/bin/notify-send'

    boolean isPlatformLinux = SystemUtils.IS_OS_LINUX

    Stage hiddenStage

    void send(GitHubEvent event) {
        def parsedEvent = event.parse()
        if (isPlatformLinux && hasLibNotify()) {
            sendLibNotifyMessage(parsedEvent.title, parsedEvent.message)
        } else {
            sendJavaFxMessage(parsedEvent.title, parsedEvent.message)
        }
    }

    void sendLibNotifyMessage(String title, String message) {
        def commandAndArguments = [notifySendPath, title, message]
        def libNotifyTask = new LibNotifyTask(commandAndArguments)
        Thread thread = new Thread(libNotifyTask)
        thread.setDaemon(true)
        thread.start()
    }

    boolean hasLibNotify() {
        return new File(notifySendPath).exists()
    }

    void sendJavaFxMessage(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(5d))
                .show()
    }

    /**
     * This is a hack, because JavaFX does not have support yet for the system tray. When the application window is
     * hidden, then the Notifications don't have a stage upon which to be displayed. So, we create a second hidden
     * stage that lives off-screen, and does not show up on the task bar (so it can't be hidden by the user).
     *
     * Note: this does NOT work well on Linux platforms. It seems to be fine for Windows, though.
     */
    void initializeHiddenStage() {
        if (isPlatformLinux) return

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
