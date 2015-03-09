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
            case "CreateEvent":
                return [title: parseCreateTitle(parsedJson), message: parseCreateMessage(parsedJson)]
            case "ForkEvent":
                return [title: "GitHub Repo Forked", message: parseForkMessage(parsedJson)]
            case "GollumEvent":
                return [title: "GitHub Wiki", message: parseGollumMessage(parsedJson)]
            case "IssuesEvent":
                return [title: parseIssuesTitle(parsedJson), message: parseIssuesMessage(parsedJson)]
            case "IssueCommentEvent":
                return [title: "GitHub Issue Comment", message: parseIssueCommentMessage(parsedJson)]
            case "PushEvent":
                return [title: "GitHub Push Event", message: parsePushMessage(parsedJson)]
            case "WatchEvent":
                return [title: "GitHub Repo Starred", message: parseWatchMessage(parsedJson)]
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

    private def parseForkMessage(def parsedJson) {
        return "$parsedJson.actor.login forked $parsedJson.repo.name into ${parsedJson.payload.forkee.full_name}"
    }

    private def parseIssuesTitle(def parsedJson) {
        return "GitHub Issue ${parsedJson.payload.action.capitalize()}"
    }

    private def parseIssuesMessage(def parsedJson) {
        String action = parsedJson.payload.action
        return "$parsedJson.actor.login $action issue #$parsedJson.payload.issue.number on $parsedJson.repo.name"
    }

    private def parseIssueCommentMessage(def parsedJson) {
        return "$parsedJson.actor.login commented on issue #$parsedJson.payload.issue.number on $parsedJson.repo.name"
    }

    private def parseWatchMessage(def parsedJson) {
        return "$parsedJson.actor.login starred $parsedJson.repo.name"
    }

    private def parseGollumMessage(def parsedJson) {
        return "$parsedJson.actor.login updated the wiki for repo $parsedJson.repo.name"
    }
}
