package com.jhegg.github.notifier

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import javafx.concurrent.Service
import javafx.concurrent.Task

class GitHubService extends Service<String> {
    CenterLayoutController layoutController
    App app
    def gitHub = new HTTPBuilder()

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                gitHub.setUri(GitHubAddress.getResolvedUrl(app))
                gitHub.get(headers: getHeaders(), contentType: 'text/plain') { response, reader ->
                    assert response.status == 200
                    return reader.text
                }
            }
        }
    }

    @Override
    protected void succeeded() {
        super.succeeded()

        def result = new JsonSlurper().parseText(value)
        def events = result.collect {
            // todo determine which events we care about, and then build in support for parsing them
            new GithubEvent(id: it.id, type: it.type, login: it.actor.login, created_at: it.created_at, json: JsonOutput.toJson(it))
        }

        layoutController.updateEvents(events)
    }

    @Override
    protected void failed() {
        super.failed()
        layoutController.displayError("Failed retrieving results from ${GitHubAddress.getResolvedUrl(app)} due to:\n ${getException()}")
    }

    void setController(CenterLayoutController controller) {
        this.layoutController = controller
    }

    def getHeaders() {
        def headers = [
                'User-Agent': 'Apache HTTPClient',
                'Accept'    : 'application/vnd.github.v3+json',
        ]
        if (app.token) {
            headers << ['Authorization': "token $app.token"]
        }
        return headers
    }
}
