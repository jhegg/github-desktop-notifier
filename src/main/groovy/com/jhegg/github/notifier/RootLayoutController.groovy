package com.jhegg.github.notifier

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

class RootLayoutController {
    App app

    @FXML
    void exit() {
        app.exitApp()
    }

    @FXML
    void editPreferences() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('EditPreferences.fxml') as URL)
        Pane pane = loader.load()
        Stage dialogStage = buildDialogStage(pane)
        EditPreferencesController controller = loader.getController()
        controller.dialogStage = dialogStage
        controller.setDisplayedPreferences(app.token, app.userName)
        dialogStage.showAndWait()

        if (controller.wasOkClicked) {
            app.centerLayoutController.refreshDisplay()
        }
    }

    private Stage buildDialogStage(Pane pane) {
        Stage dialogStage = new Stage()
        dialogStage.setTitle("Edit Preferences")
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(app.primaryStage)
        dialogStage.setScene(new Scene(pane))
        dialogStage
    }

    @FXML
    void about() {
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION)
        aboutBox.setTitle("About")
        aboutBox.setHeaderText(null)
        aboutBox.setContentText("This example app illustrates using Groovy and JavaFX to interact with the GitHub API.")
        aboutBox.initStyle(StageStyle.UTILITY)
        aboutBox.showAndWait()
    }
}
