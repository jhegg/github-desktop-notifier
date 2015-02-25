package com.jhegg.github.notifier

import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage

class EditPreferencesView {
    App app
    EditPreferencesController controller
    Stage dialogStage

    EditPreferencesView(App app) {
        this.app = app
    }

    void configure() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('EditPreferences.fxml') as URL)
        Pane pane = loader.load()
        dialogStage = buildDialogStage(pane)
        configureController(loader, app)
    }

    private void configureController(FXMLLoader loader, App app) {
        controller = loader.getController()
        controller.app = app
        controller.setDisplayedPreferences(app.token, app.userName)
        controller.editPreferencesView = this
    }

    private Stage buildDialogStage(Pane pane) {
        Stage dialogStage = new Stage()
        dialogStage.setTitle("Edit Preferences")
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(app.primaryStage)
        dialogStage.setScene(new Scene(pane))
        dialogStage
    }

    void showDialog() {
        dialogStage.showAndWait()
    }
}
