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

    def "failure handler sets error message"() {
        setup:
        app.getGitHubUrlSuffixWithPlaceholder() >> "https://www.example.com"
        app.getUserName() >> "josh"
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.onFailure(new Throwable("some unique error"))

        then:
        centerLayoutController.textArea.getText().contains("some unique error")
    }

    def "success handler parses empty events"() {
        setup:
        centerLayoutController.rootLayoutController = Mock(RootLayoutController)

        when:
        centerLayoutController.onSuccess("{}")

        then:
        1 * observableList.setAll({it.isEmpty()})
    }

    def "success handler parses single push event"() {
        setup:
        centerLayoutController.rootLayoutController = Mock(RootLayoutController)

        when:
        centerLayoutController.onSuccess("[${getExampleSinglePushPayload()}]")

        then:
        1 * observableList.setAll({ List<GitHubEvent> events ->
            events.size() == 1
            events[0].id == "2671420212"
            events[0].type == "PushEvent"
            events[0].login == "SomeUser"
            events[0].created_at == "2015-02-01T01:02:03Z"
            !events[0].json.isEmpty()
        })
    }

    String getExampleSinglePushPayload() {
        """{
    "actor": {
        "avatar_url": "https://avatars.githubusercontent.com/u/12345?",
        "gravatar_id": "",
        "id": 12345,
        "login": "SomeUser",
        "url": "https://api.github.com/users/SomeUser"
    },
    "created_at": "2015-02-01T01:02:03Z",
    "id": "2671420212",
    "org": {
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
        "gravatar_id": "",
        "id": 123456,
        "login": "SomeOrg",
        "url": "https://api.github.com/orgs/SomeOrg"
    },
    "payload": {
        "before": "8aeb1085cf37920495bac0f0c0ea00d7cd6d2105",
        "commits": [
            {
                "author": {
                    "email": "someuser@example.com",
                    "name": "Some User"
                },
                "distinct": true,
                "message": "I made this thing",
                "sha": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
                "url": "https://api.github.com/repos/SomeOrg/i-made-this/commits/05351301f9400ddaf5d7aaec4f55ab13a06986c3"
            }
        ],
        "distinct_size": 1,
        "head": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
        "push_id": 551032016,
        "ref": "refs/heads/master",
        "size": 1
    },
    "public": true,
    "repo": {
        "id": 1234567,
        "name": "SomeOrg/i-made-this",
        "url": "https://api.github.com/repos/SomeOrg/i-made-this"
    },
    "type": "PushEvent"
}"""
    }
}
