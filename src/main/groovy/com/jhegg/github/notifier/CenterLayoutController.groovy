package com.jhegg.github.notifier

import groovy.json.JsonOutput
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.TextArea

class CenterLayoutController {
    @FXML
    ListView<GithubEvent> listView

    @FXML
    TextArea textArea;

    def observableList = FXCollections.<GithubEvent>observableArrayList()

    App app

    GitHubService gitHubService = new GitHubService()

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    private void initialize() {
        listView.setItems(observableList)
        textArea.setEditable(false)
        listView.getSelectionModel().selectedItemProperty().addListener(
                {observableValue, oldValue, newValue ->
                    displayTextArea(newValue)} as ChangeListener)
    }

    public void initializeGithubService() {
        gitHubService.setController(this)
        gitHubService.setApp(app)

        if (app.userName) {
            gitHubService.start()
        } else {
            textArea.setText("Please click on Edit->Preferences and set a GitHub User Name.")
        }
    }

    void displayTextArea(GithubEvent event) {
        if (event != null )
            textArea.setText(JsonOutput.prettyPrint(event.json))
    }

    void updateEvents(List<GithubEvent> githubEvents) {
        observableList.setAll(githubEvents)
        if (!githubEvents.isEmpty()) {
            listView.selectionModel.selectFirst()
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
