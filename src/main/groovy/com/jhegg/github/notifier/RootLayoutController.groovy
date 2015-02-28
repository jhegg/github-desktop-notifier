package com.jhegg.github.notifier

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.StageStyle

class RootLayoutController {
    App app
    EditPreferencesController editPreferencesController
    Alert aboutBox

    @FXML
    Label address

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        aboutBox = new Alert(Alert.AlertType.INFORMATION)
        aboutBox.setTitle("About")
        aboutBox.setHeaderText(null)
        def hyperlink = new Hyperlink("GitHub Desktop Notifier")
        hyperlink.setOnAction { ActionEvent event ->
            app.getHostServices().showDocument('https://github.com/jhegg/github-desktop-notifier/')
        }
        aboutBox.getDialogPane().setContent(new TextFlow(hyperlink, new Text(" by Josh Hegg")))
        aboutBox.initStyle(StageStyle.UTILITY)
    }

    @FXML
    void exit() {
        app.exitApp()
    }

    @FXML
    void editPreferences() {
        editPreferencesController.showDialog()

        if (editPreferencesController.wasOkClicked) {
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
        updateGitHubAddress()
        buildEditPreferencesController()
    }

    void buildEditPreferencesController() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('EditPreferences.fxml') as URL)
        Pane pane = loader.load()
        editPreferencesController = loader.getController()
        editPreferencesController.configure(app, pane)
    }
}
