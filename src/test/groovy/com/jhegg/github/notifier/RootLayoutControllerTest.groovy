package com.jhegg.github.notifier

import javafx.scene.control.Label
import spock.lang.Specification
import spock.lang.Unroll

class RootLayoutControllerTest extends Specification {
    RootLayoutController rootLayoutController = new RootLayoutController()
    App app = new App()
    CenterLayoutController centerLayoutController = Mock(CenterLayoutController)
    EditPreferencesView editPreferencesView = Mock(EditPreferencesView)
    EditPreferencesController editPreferencesController = Mock(EditPreferencesController)

    @Unroll
    def "EditPreferences when OK was not clicked"() {
        setup:
        app.userName = 'josh'
        app.centerLayoutController = centerLayoutController
        rootLayoutController.app = app
        rootLayoutController.address = new Label()
        rootLayoutController.editPreferencesView = editPreferencesView
        editPreferencesView.controller >> editPreferencesController
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
}
