package com.jhegg.github.notifier

import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import spock.lang.Shared
import spock.lang.Specification

class EditPreferencesControllerTest extends Specification {
    EditPreferencesController controller = new EditPreferencesController()
    EditPreferencesView view = Mock(EditPreferencesView)
    TextField token = new TextField()
    TextField userName = new TextField()
    @Shared KeyEvent enterKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.ENTER, false, false, false, false)
    @Shared KeyEvent escapeKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.ESCAPE, false, false, false, false)
    @Shared KeyEvent spaceKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.SPACE, false, false, false, false)
    App app = new App()

    def "key codes"() {
        setup:
        controller.app = app
        userName.setText('josh')
        controller.token = token
        controller.userName = userName
        controller.editPreferencesView = view
        controller.initialize()

        when:
        token.onKeyPressed.handle(event)

        then:
        app.userName == result.userName
        controller.wasOkClicked == wasOkClicked
        times * view.closeDialog()

        where:
        event | wasOkClicked | times || result
        enterKey | true | 1 || [userName: 'josh']
        escapeKey | false | 1  || [userName: '']
        spaceKey | false | 0 || [userName: '']
    }
}
