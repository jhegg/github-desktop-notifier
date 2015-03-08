package com.jhegg.github.notifier

import groovy.json.JsonSlurper
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GitHubEvent implements Comparable {
    String id
    String type
    String login
    String created_at
    String json

    @Override
    public String toString() {
        "${login} - ${created_at}"
    }

    @Override
    int compareTo(Object o) {
        GitHubEvent otherEvent = (GitHubEvent)o
        if (this.id.equals(otherEvent.id)) { return 0 }
        if (this.id < otherEvent.id ) { return -1 }
        return 1
    }

    def parse() {
        def parsedJson = new JsonSlurper().parseText(json)
        switch (parsedJson.type) {
            case "PushEvent":
                return [title: "GitHub Push Event", message: parsePushMessage(parsedJson)]
            case "CreateEvent":
                return [title: parseCreateTitle(parsedJson), message: parseCreateMessage(parsedJson)]
            default:
                def loginName = parsedJson.actor.login
                def repoName = parsedJson.repo.name
                return [title: parsedJson.type, message: "$loginName acted on repo $repoName"]
        }
    }

    private def parsePushMessage(def parsedJson) {
        def numCommits = parsedJson.payload.size
        def pluralCommits = numCommits == 1 ? '' : 's'
        def ref = parsedJson.payload.ref
        return "$parsedJson.actor.login pushed $numCommits commit$pluralCommits to $ref in $parsedJson.repo.name"
    }

    private def parseCreateTitle(def parsedJson) {
        String createdObject = parsedJson.payload.ref_type
        return "GitHub ${createdObject.capitalize()} Created"
    }

    private def parseCreateMessage(def parsedJson) {
        String createdObject = parsedJson.payload.ref_type
        def ref = parsedJson.payload.ref
        switch (createdObject) {
            case "repository":
                return "$parsedJson.actor.login created $createdObject $parsedJson.repo.name"
            default:
                return "$parsedJson.actor.login created $createdObject $ref in $parsedJson.repo.name"
        }
    }
}
