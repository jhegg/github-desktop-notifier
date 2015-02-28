package com.jhegg.github.notifier

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.stage.StageStyle

class RootLayoutController {
    App app
    EditPreferencesView editPreferencesView
    Alert aboutBox

    @FXML
    Label address

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        aboutBox = new Alert(Alert.AlertType.INFORMATION)
        aboutBox.setTitle("About")
        aboutBox.setHeaderText(null)
        aboutBox.setContentText("This example app illustrates using Groovy and JavaFX to interact with the GitHub API.")
        aboutBox.initStyle(StageStyle.UTILITY)
    }

    @FXML
    void exit() {
        app.exitApp()
    }

    @FXML
    void editPreferences() {
        editPreferencesView.showDialog()

        if (editPreferencesView.controller.wasOkClicked) {
            app.centerLayoutController.refreshDisplay()
            updateGitHubAddress()
        }
    }

    void updateGitHubAddress() {
        address.setText(GitHubAddress.getResolvedUrl(app))
    }

    @FXML
    void about() {
        aboutBox.showAndWait()
    }

    void configure(App app) {
        this.app = app
        editPreferencesView = new EditPreferencesView(app)
        editPreferencesView.configure()
        updateGitHubAddress()
    }
}
