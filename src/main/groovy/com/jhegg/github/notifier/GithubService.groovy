package com.jhegg.github.notifier

import groovyx.net.http.HTTPBuilder
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.util.Duration

class GitHubService extends ScheduledService<String> {
    App app
    def gitHub = new HTTPBuilder()

    GitHubService() {
    }

    GitHubService(Duration duration) {
        this.setPeriod(duration)
    }

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
