package com.jhegg.github.notifier

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import javafx.concurrent.Service
import javafx.concurrent.Task

class GithubService extends Service<String> {
    CenterLayoutController layoutController
    App app

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                def github = new HTTPBuilder(resolvedUrl)
                github.get(headers: getHeaders(), contentType: 'text/plain') { response, reader ->
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
        layoutController.displayError("Failed retrieving results from ${getResolvedUrl()} due to:\n ${getException()}")
    }

    String getResolvedUrl() {
        if (app.githubEnterpriseHostname) {
            getResolvedGithubEnterprisePrefix() + getResolvedUrlSuffix()
        } else {
            app.githubUrlPrefix + getResolvedUrlSuffix()
        }
    }

    private String getResolvedGithubEnterprisePrefix() {
        String.format(app.githubEnterpriseUrlPrefixWithPlaceholder, app.githubEnterpriseHostname)
    }

    private String getResolvedUrlSuffix() {
        String.format(app.githubUrlSuffixWithPlaceholder, app.userName)
    }

    void setController(CenterLayoutController controller) {
        this.layoutController = controller
    }

    private def getHeaders() {
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
