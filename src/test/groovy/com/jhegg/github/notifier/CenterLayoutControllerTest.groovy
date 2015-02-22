package com.jhegg.github.notifier

import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.control.ListView
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.TextArea
import spock.lang.Specification

class CenterLayoutControllerTest extends Specification {
    static {
        new JFXPanel() // we need the JavaFX Toolkit to be initialized, or an IllegalStateException is thrown.
    }

    CenterLayoutController centerLayoutController = new CenterLayoutController()
    def observableList = Mock(ObservableList)
    def listView = Mock(ListView)
    def selectionModel = Mock(MultipleSelectionModel)
    def githubService = Mock(GithubService)
    def app = Mock(App)

    def setup() {
        listView.selectionModel = selectionModel
        centerLayoutController.observableList = observableList
        centerLayoutController.listView = listView
        centerLayoutController.githubService = githubService
        centerLayoutController.app = app
    }

    def "updateEvents with empty list"() {
        when:
        centerLayoutController.updateEvents([] as List<GithubEvent>)

        then:
        1 * observableList.setAll({it.isEmpty()})
        0 * selectionModel.selectFirst()
    }

    def "updateEvents with single event in list"() {
        when:
        def events = [new GithubEvent(id: "1")] as List<GithubEvent>
        centerLayoutController.updateEvents(events)

        then:
        1 * observableList.setAll({it.size() == 1})
        1 * selectionModel.selectFirst()
    }

    def "displayError"() {
        setup:
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.displayError("error message")

        then:
        centerLayoutController.textArea.getText() == "error message"
    }

    def "refreshDisplay without username"() {
        setup:
        app.getUserName() >> GString.EMPTY

        when:
        centerLayoutController.refreshDisplay()

        then:
        0 * githubService.restart()
    }

    def "refreshDisplay with username"() {
        setup:
        app.getUserName() >> "josh"

        when:
        centerLayoutController.refreshDisplay()

        then:
        1 * githubService.restart()
    }

    def "initializeGithubService without userName"() {
        setup:
        app.getUserName() >> GString.EMPTY
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.initializeGithubService()

        then:
        1 * githubService.setController(centerLayoutController)
        1 * githubService.setApp(app)
        0 * githubService.start()
        !centerLayoutController.textArea.getText().isEmpty()
    }

    def "initializeGithubService with userName"() {
        setup:
        app.getUserName() >> "josh"

        when:
        centerLayoutController.initializeGithubService()

        then:
        1 * githubService.setController(centerLayoutController)
        1 * githubService.setApp(app)
        1 * githubService.start()
    }

    def "displayTextArea with an event that has json"() {
        setup:
        def githubEvent = new GithubEvent(id: "123", json: "{\"id\": \"123\"}")
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.displayTextArea(githubEvent)

        then:
        centerLayoutController.textArea.getText() == "{\n    \"id\": \"123\"\n}"
    }
}
