package com.jhegg.github.notifier

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
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

    App app

    GitHubService gitHubService = new GitHubService(Duration.minutes(1))

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        listView.setItems(observableList)
        textArea.setEditable(false)
        listView.getSelectionModel().selectedItemProperty().addListener(
                {observableValue, oldValue, newValue ->
                    displayTextArea(newValue)} as ChangeListener)
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

        updateEvents(events)
    }

    void onFailure(Throwable throwable) {
        displayError("Failed retrieving results from ${GitHubAddress.getResolvedUrl(app)} due to:\n $throwable")
    }

    void displayTextArea(GitHubEvent event) {
        if (event != null )
            textArea.setText(JsonOutput.prettyPrint(event.json))
    }

    void updateEvents(List<GitHubEvent> githubEvents) {
        observableList.setAll(githubEvents)
        if (!githubEvents.isEmpty()) {
            listView.getSelectionModel().selectFirst()
        }
    }

    void refreshDisplay() {
        if (app.userName) {
            gitHubService.restart() // todo This is not an ideal usage for proper error handling
        }
    }

    void displayError(String errorMessage) {
        textArea.setText(errorMessage)
    }
}
