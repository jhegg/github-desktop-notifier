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

    def setup() {
        listView.selectionModel = selectionModel
        centerLayoutController.observableList = observableList
        centerLayoutController.listView = listView
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
}
