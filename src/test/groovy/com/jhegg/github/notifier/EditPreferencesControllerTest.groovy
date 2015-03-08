package com.jhegg.github.notifier

import javafx.embed.swing.JFXPanel
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class EditPreferencesControllerTest extends Specification {
    static {
        new JFXPanel() // we need the JavaFX Toolkit to be initialized, or an IllegalStateException is thrown.
    }

    EditPreferencesController controller = new EditPreferencesController()
    TextField token = new TextField()
    TextField userName = new TextField()
    TextField gitHubEnterpriseHostname = new TextField()
    CheckBox systemTrayIcon = new CheckBox()
    @Shared KeyEvent enterKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.ENTER, false, false, false, false)
    @Shared KeyEvent escapeKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.ESCAPE, false, false, false, false)
    @Shared KeyEvent spaceKey = new KeyEvent(KeyEvent.KEY_PRESSED, "enter", "enter", KeyCode.SPACE, false, false, false, false)
    App app = new App()

    @Unroll
    def "key codes with event: '#event.getCode()'"() {
        setup:
        controller.app = app
        userName.setText('josh')
        controller.token = token
        controller.userName = userName
        controller.gitHubEnterpriseHostname = gitHubEnterpriseHostname
        controller.systemTrayIcon = systemTrayIcon
        controller.initialize()
        controller.metaClass.wasCloseDialogCalled = false
        controller.metaClass.closeDialog = { wasCloseDialogCalled = true }

        when:
        token.onKeyPressed.handle(event)

        then:
        app.userName == result.userName
        controller.wasOkClicked == wasOkClicked
        controller.wasCloseDialogCalled == closed

        where:
        event | wasOkClicked | closed || result
        enterKey | true | true || [userName: 'josh']
        escapeKey | false | true  || [userName: '']
        spaceKey | false | false || [userName: '']
    }

    @Unroll
    def "clicked ok with user=#testUser, token=#testToken, hostname=#testHostname, icon=#testIcon"() {
        setup:
        controller.app = app
        controller.token = token
        token.setText(testToken)
        controller.userName = userName
        userName.setText(testUser)
        controller.gitHubEnterpriseHostname = gitHubEnterpriseHostname
        gitHubEnterpriseHostname.setText(testHostname)
        controller.systemTrayIcon = systemTrayIcon
        systemTrayIcon.selected = testIcon
        controller.metaClass.closeDialog = {}

        when:
        controller.clickedOk()

        then:
        app.userName == testUser
        app.token == testToken
        app.gitHubEnterpriseHostname == testHostname
        // todo app.systemTrayIconEnabled == testIcon

        where:
        testUser | testToken | testHostname | testIcon
        '' | '' | '' | false
        'josh' | '' | '' | false
        'josh' | '12345' | '' | false
        'josh' | '12345' | 'localhost' | false
        'josh' | '12345' | 'localhost' | true
    }

    def "setDisplayedPreferences updates fields"() {
        setup:
        controller.token = token
        controller.userName = userName

        when:
        controller.setDisplayedPreferences("12345", "someUser")

        then:
        token.getText() == "12345"
        userName.getText() == "someUser"
    }
}
