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

    Stage dialogStage

    boolean wasOkClicked

    @SuppressWarnings("GroovyUnusedDeclaration")
    @FXML
    private void initialize() {
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
        App.userName = userName.getText()
        App.token = token.getText()
        wasOkClicked = true
        dialogStage.close()
    }

    @FXML
    void clickedCancel() {
        dialogStage.close()
    }

    void setDisplayedPreferences(String token, String userName) {
        this.token.setText(token)
        this.userName.setText(userName)
    }
}
