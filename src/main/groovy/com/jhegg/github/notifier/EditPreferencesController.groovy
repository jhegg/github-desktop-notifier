package com.jhegg.github.notifier

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage

class EditPreferencesController {
    @FXML
    private TextField token
    @FXML
    private TextField userName

    App app
    EditPreferencesView editPreferencesView
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
        editPreferencesView.closeDialog()
    }

    @FXML
    void clickedCancel() {
        wasOkClicked = false
        editPreferencesView.closeDialog()
    }

    void setDisplayedPreferences(String token, String userName) {
        this.token.setText(token)
        this.userName.setText(userName)
    }
}
