package com.jhegg.github.notifier

import javafx.embed.swing.JFXPanel
import javafx.scene.control.Label
import spock.lang.Specification
import spock.lang.Unroll

class RootLayoutControllerTest extends Specification {
    static {
        new JFXPanel() // we need the JavaFX Toolkit to be initialized, or an IllegalStateException is thrown.
    }

    RootLayoutController rootLayoutController = new RootLayoutController()
    App app = new App()
    CenterLayoutController centerLayoutController = Mock(CenterLayoutController)
    EditPreferencesController editPreferencesController = Mock(EditPreferencesController)

    @Unroll
    def "EditPreferences when OK was not clicked"() {
        setup:
        app.userName = 'josh'
        app.centerLayoutController = centerLayoutController
        rootLayoutController.app = app
        rootLayoutController.address = new Label()
        rootLayoutController.editPreferencesController = editPreferencesController
        editPreferencesController.wasOkClicked >> wasOkClicked

        when:
        rootLayoutController.editPreferences()

        then:
        rootLayoutController.address.getText() == result

        where:
        wasOkClicked || result
        false || ""
        true || "https://api.github.com/users/josh/received_events"
    }

    def "exit invokes exitApp"() {
        setup:
        app = Mock(App)
        rootLayoutController.app = app

        when:
        rootLayoutController.exit()

        then:
        1 * app.exitApp()
    }
}
