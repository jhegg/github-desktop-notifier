package com.jhegg.github.notifier

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.control.ListView
import javafx.scene.control.MultipleSelectionModel
import javafx.scene.control.TextArea
import spock.lang.Shared
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

    @Shared GitHubEvent genericEvent = new GitHubEvent(login: "josh", created_at: "now")
    @Shared GitHubEvent newEvent = new GitHubEvent(login: "josh2", created_at: "now2")

    def setup() {
        listView.selectionModel = selectionModel
        centerLayoutController.observableList = observableList
        centerLayoutController.listView = listView
        centerLayoutController.gitHubService = gitHubService
        centerLayoutController.app = app
    }

    @Unroll
    def "updateEvents with #resultSize events"() {
        setup:
        centerLayoutController.observableList = FXCollections.<GitHubEvent>observableArrayList()
        centerLayoutController.listView = new ListView<>(centerLayoutController.observableList)

        when:
        centerLayoutController.updateEvents(events as List<GitHubEvent>)

        then:
        centerLayoutController.observableList.size() == resultSize

        where:
        events || resultSize
        [] || 0
        [new GitHubEvent(id: "1")] || 1
        [new GitHubEvent(id: "1"), new GitHubEvent(id: "2")] || 2
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
        centerLayoutController.textArea = new TextArea()

        when:
        centerLayoutController.refreshDisplay()

        then:
        times * gitHubService.restart()
        centerLayoutController.textArea.getText() == text

        where:
        userName || times | text
        GString.EMPTY || 0 | "Please click on Edit->Preferences and set a GitHub User Name."
        "josh" || 1 | "Loading..."
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

    @Unroll
    def "getNewEventsForNotification with #existing.size() oldEvents and #input.size() new, expected #output.size() result(s)"() {
        setup:
        centerLayoutController.observableList = FXCollections.<GitHubEvent>observableArrayList()
        centerLayoutController.observableList.addAll(existing)

        expect:
        centerLayoutController.getNewEventsForNotification(input) == output

        where:
        existing | input || output
        [] | [] || []
        [genericEvent] | [] || []
        [] | [genericEvent] || [genericEvent]
        [] | [genericEvent, newEvent] || [genericEvent, newEvent]
        [genericEvent] | [newEvent] || [newEvent]
        [genericEvent, newEvent] | [] || []
        [genericEvent, newEvent] | [newEvent] || []
    }

    @Unroll
    def "purgeEventsOver200 where size = #size"() {
        setup:
        observableList.size() >> size

        when:
        centerLayoutController.purgeEventsOver200()

        then:
        times * observableList.remove(!null)

        where:
        size | times
        0 | 0
        1 | 0
        200 | 0
        201 | 1
        202 | 2
        300 | 100
    }

    @Unroll
    def "purgeEventsOver200 using real data where size = #size"() {
        setup:
        centerLayoutController.observableList = FXCollections.<GitHubEvent>observableArrayList()
        def events = []
        (1..size).each { int i ->
            events << new GitHubEvent(id: i)
        }
        centerLayoutController.observableList.addAll(events)

        when:
        centerLayoutController.purgeEventsOver200()

        then:
        centerLayoutController.observableList.size() == result

        where:
        size | result
        200 | 200
        201 | 200
        202 | 200
        300 | 200
    }

    def "initialize"() {
        setup:
        centerLayoutController.listView = new ListView<GitHubEvent>()
        centerLayoutController.textArea = new TextArea()
        centerLayoutController.desktopNotifier = Mock(DesktopNotifier)

        when:
        centerLayoutController.initialize()

        then:
        listView.getItems() == observableList
        !centerLayoutController.textArea.isEditable()
        1 * centerLayoutController.desktopNotifier.initializeHiddenStage()
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
        1 * observableList.addAll({it.isEmpty()})
    }

    def "success handler parses single push event"() {
        setup:
        centerLayoutController.rootLayoutController = Mock(RootLayoutController)
        centerLayoutController.desktopNotifier = Mock(DesktopNotifier)
        centerLayoutController.observableList = FXCollections.<GitHubEvent>observableArrayList()
        centerLayoutController.listView = new ListView<>(centerLayoutController.observableList)

        when:
        centerLayoutController.onSuccess("[${GitHubJsonPayloadExamples.exampleSinglePushPayload}]")

        then:
        centerLayoutController.observableList.size() == 1
        centerLayoutController.observableList.each { GitHubEvent event ->
            event.id == "2671420212"
            event.type == "PushEvent"
            event.login == "SomeUser"
            event.created_at == "2015-02-01T01:02:03Z"
            !event.json.isEmpty()
        }
        1 * centerLayoutController.desktopNotifier.send({ GitHubEvent event ->
            event.id == "2671420212"
            event.type == "PushEvent"
            event.login == "SomeUser"
            event.created_at == "2015-02-01T01:02:03Z"
            !event.json.isEmpty()
        })
    }
}
