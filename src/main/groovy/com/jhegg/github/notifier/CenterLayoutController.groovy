package com.jhegg.github.notifier

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.transformation.SortedList
import javafx.concurrent.WorkerStateEvent
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextArea
import javafx.util.Duration

class CenterLayoutController {
    @FXML
    ListView<GitHubEvent> listView

    @FXML
    TextArea textArea;

    def observableList = FXCollections.<GitHubEvent>observableArrayList()

    SortedList<GitHubEvent> sortedList

    App app

    RootLayoutController rootLayoutController

    GitHubService gitHubService = new GitHubService(Duration.seconds(10))

    DesktopNotifier desktopNotifier = new DesktopNotifier()

    def pleaseSetUserNameMessage = "Please click on Edit->Preferences and set a GitHub User Name."

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        def comparison = [compare: { a, b -> a.equals(b) ? 0 : a < b ? 1 : -1}] as Comparator
        sortedList = new SortedList<>(observableList, comparison)
        listView.setItems(sortedList)
        textArea.setEditable(false)
        listView.getSelectionModel().selectedItemProperty().addListener(
                {observableValue, oldValue, newValue ->
                    displayTextArea(newValue)} as ChangeListener)
        desktopNotifier.initializeHiddenStage()
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
            textArea.setText(pleaseSetUserNameMessage)
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
        desktopNotifier.send(gitHubEvent)
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
        } else {
            textArea.setText(pleaseSetUserNameMessage)
        }
    }

    void displayError(String errorMessage) {
        textArea.setText(errorMessage)
    }
}
