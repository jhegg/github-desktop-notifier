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
    def listView = new ListView<GitHubEvent>()
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

    @Unroll
    def "updateEvents with list: #list"() {
        when:
        centerLayoutController.updateEvents(events as List<GitHubEvent>)

        then:
        1 * observableList.setAll({it.size() == resultSize})
        timesSelected * selectionModel.selectFirst()

        where:
        events || resultSize | timesSelected
        [] || 0 | 0
        [new GitHubEvent(id: "1")] || 1 | 1
        [new GitHubEvent(id: "1"), new GitHubEvent(id: "2")] || 2 | 1
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

    @Unroll
    def "displayTextArea with event: #event"() {
        setup:
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.displayTextArea(event)

        then:
        centerLayoutController.textArea.getText() == result

        where:
        event || result
        null || GString.EMPTY
        new GitHubEvent(id: "123", login: "josh", created_at: "now", json: "{\"id\": \"123\"}") || "{\n    \"id\": \"123\"\n}"
    }

    def "initialize"() {
        setup:
        centerLayoutController.listView = new ListView<GitHubEvent>()
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.initialize()

        then:
        listView.getItems() == observableList
        !centerLayoutController.textArea.isEditable()
    }
}
