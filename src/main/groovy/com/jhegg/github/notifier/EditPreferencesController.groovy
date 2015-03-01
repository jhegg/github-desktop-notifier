package com.jhegg.github.notifier

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage

class EditPreferencesController {
    @FXML
    TextField token
    @FXML
    TextField userName

    App app
    Stage dialogStage
    boolean wasOkClicked

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    void initialize() {
        pressingEnterKeyClicksOk()
    }

    private Iterable<TextField> pressingEnterKeyClicksOk() {
        [token, userName].each {
            it.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                void handle(KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ENTER))
                        clickedOk()
                    if (event.getCode().equals(KeyCode.ESCAPE))
                        clickedCancel()
                }
            })
        }
    }

    @FXML
    void clickedOk() {
        app.userName = userName.getText()
        app.token = token.getText()
        wasOkClicked = true
        closeDialog()
    }

    @FXML
    void clickedCancel() {
        wasOkClicked = false
        closeDialog()
    }

    void setDisplayedPreferences(String token, String userName) {
        this.token.setText(token)
        this.userName.setText(userName)
    }

    void configure(App app, Pane pane) {
        this.app = app
        buildDialogStage(pane)
        setDisplayedPreferences(app.token, app.userName)
    }

    private void buildDialogStage(Pane pane) {
        dialogStage = new Stage()
        dialogStage.setTitle("Edit Preferences")
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(app.primaryStage)
        dialogStage.setScene(new Scene(pane))
    }

    void showDialog() {
        setDisplayedPreferences(app.token, app.userName)
        dialogStage.showAndWait()
    }

    void closeDialog() {
        dialogStage.close()
    }
}
