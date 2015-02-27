package com.jhegg.github.notifier

import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.control.ListView
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.TextArea
import spock.lang.Specification
import spock.lang.Unroll

class CenterLayoutControllerTest extends Specification {
    static {
        new JFXPanel() // we need the JavaFX Toolkit to be initialized, or an IllegalStateException is thrown.
    }

    CenterLayoutController centerLayoutController = new CenterLayoutController()
    def observableList = Mock(ObservableList)
    def listView = new ListView<GithubEvent>()
    def selectionModel = Mock(MultipleSelectionModel)
    def gitHubService = Mock(GitHubService)
    def app = Mock(App)

    def setup() {
        listView.selectionModel = selectionModel
        centerLayoutController.observableList = observableList
        centerLayoutController.listView = listView
        centerLayoutController.gitHubService = gitHubService
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

    @Unroll
    def "refreshDisplay with username: '#userName'"() {
        setup:
        app.getUserName() >> userName

        when:
        centerLayoutController.refreshDisplay()

        then:
        times * gitHubService.restart()

        where:
        userName || times
        GString.EMPTY || 0
        "josh" || 1
    }

    @Unroll
    def "initializeGithubService with username: '#userName'"() {
        setup:
        app.getUserName() >> userName
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.initializeGithubService()

        then:
        1 * gitHubService.setController(centerLayoutController)
        1 * gitHubService.setApp(app)
        times * gitHubService.start()
        centerLayoutController.textArea.getText().isEmpty() == doesNotShowPlaceholderMessage

        where:
        userName || times | doesNotShowPlaceholderMessage
        GString.EMPTY || 0 | false
        "josh" || 1 | true
    }

    def "displayTextArea with an event that has json"() {
        setup:
        def gitHubEvent = new GithubEvent(id: "123", json: "{\"id\": \"123\"}")
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.displayTextArea(gitHubEvent)

        then:
        centerLayoutController.textArea.getText() == "{\n    \"id\": \"123\"\n}"
    }
}
