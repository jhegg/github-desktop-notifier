package com.jhegg.github.notifier

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import org.apache.commons.lang.SystemUtils
import org.controlsfx.control.Notifications

class CenterLayoutController {
    @FXML
    ListView<GitHubEvent> listView

    @FXML
    TextArea textArea;

    def observableList = FXCollections.<GitHubEvent>observableArrayList()

    App app

    RootLayoutController rootLayoutController

    GitHubService gitHubService = new GitHubService(Duration.seconds(10))

    Stage hiddenStage

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        listView.setItems(observableList)
        textArea.setEditable(false)
        listView.getSelectionModel().selectedItemProperty().addListener(
                {observableValue, oldValue, newValue ->
                    displayTextArea(newValue)} as ChangeListener)

        initializeHiddenStage()
    }

    public void initializeGithubService() {
        gitHubService.setApp(app)
        gitHubService.setOnSucceeded { WorkerStateEvent event ->
            onSuccess((String) event.getSource().getValue())
        }

        gitHubService.setOnFailed { WorkerStateEvent event ->
            onFailure(event.getSource().getException())
        }

        if (app.userName) {
            gitHubService.start()
        } else {
            textArea.setText("Please click on Edit->Preferences and set a GitHub User Name.")
        }
    }

    void onSuccess(String value) {
        def result = new JsonSlurper().parseText(value)
        def events = result.collect {
            // todo determine which events we care about, and then build in support for parsing them
            new GitHubEvent(id: it.id, type: it.type, login: it.actor.login, created_at: it.created_at, json: JsonOutput.toJson(it))
        }

        notifyEvents(events)
        updateEvents(events)
        rootLayoutController.updateLastFetchTime()
    }

    void notifyEvents(List<GitHubEvent> gitHubEvents) {
        if (gitHubEvents.isEmpty()) { return }

        getNewEventsForNotification(gitHubEvents).eachWithIndex { GitHubEvent event, index ->
            if (index < 3) { notifyEvent(event) } // We don't want to open too many notifications
        }
    }

    List<GitHubEvent> getNewEventsForNotification(List<GitHubEvent> gitHubEvents) {
        def newEvents = gitHubEvents.collect()
        newEvents.removeAll(observableList)
        return newEvents
    }

    void notifyEvent(GitHubEvent gitHubEvent) {
        Notifications.create()
                .title(gitHubEvent.type ?: "Unknown event")
                .text(getNotificationText(gitHubEvent))
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
     * todo This could be a clue that I should look for alternative mechanisms for doing the notifications...
     */
    private void initializeHiddenStage() {
        if (!hiddenStage) {
            Stage stage = new Stage()
            stage.initStyle(StageStyle.UTILITY)
            stage.setMaxHeight(0)
            stage.setMaxWidth(0)
            stage.setHeight(0)
            stage.setWidth(0)
            if (SystemUtils.IS_OS_LINUX) {
                stage.centerOnScreen() // try to hide it behind the main window
                stage.toBack()
                stage.setOpacity(0d) // try to make it translucent so it's less obvious
            } else {
                stage.setX(Double.MAX_VALUE) // move the window off-screen (does not work on Linux)
            }
            stage.setScene(new Scene(new Group()))
            stage.show()
            if (SystemUtils.IS_OS_LINUX) {
                stage.toBack()
                stage.centerOnScreen()
                stage.setX(20000d) // move the window off-screen, workaround a bug on Linux
            }
            hiddenStage = stage
        }
    }

    void onFailure(Throwable throwable) {
        displayError("Failed retrieving results from ${GitHubAddress.getResolvedUrl(app)} due to:\n $throwable")
    }

    void displayTextArea(GitHubEvent event) {
        if (event != null )
            textArea.setText(JsonOutput.prettyPrint(event.json))
    }

    void updateEvents(List<GitHubEvent> githubEvents) {
        def newEvents = getNewEventsForNotification(githubEvents)
        observableList.addAll(newEvents)
        if (!newEvents.isEmpty() && listView.getSelectionModel().getSelectedItem() == null) {
            listView.getSelectionModel().selectFirst()
        }

        purgeEventsOver200()
    }

    void purgeEventsOver200() {
        def maxEvents = 200
        if (observableList.size() > maxEvents) {
            (maxEvents..(observableList.size() - 1)).each {
                observableList.remove(maxEvents) // always remove the 201st entry in the list
            }
        }
    }

    void refreshDisplay() {
        observableList.clear()
        textArea.setText("")
        if (app.userName) {
            textArea.setText("Loading...")
            gitHubService.restart() // todo This is not an ideal usage for proper error handling
        }
    }

    void displayError(String errorMessage) {
        textArea.setText(errorMessage)
    }
}
