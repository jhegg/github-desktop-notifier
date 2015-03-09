package com.jhegg.github.notifier

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.jhegg.github.notifier.GitHubJsonPayloadExamples.*

class GitHubEventTest extends Specification {
    @Shared def event = new GitHubEvent(id: 1)
    @Shared def eventTwo = new GitHubEvent(id: 2)

    def "CompareTo greater than"() {
        expect:
        eventTwo > event
    }

    def "CompareTo less than"() {
        expect:
        event < eventTwo
    }

    def "CompareTo equals"() {
        expect:
        event == event
        eventTwo == eventTwo
    }

    def "CompareTo not equals"() {
        expect:
        event != eventTwo
        eventTwo != event
    }

    @Unroll
    def "title and message for type: #type"() {
        given:
        GitHubEvent theEvent = new GitHubEvent(type: type, login: "login", created_at: "now", json: json)

        expect:
        theEvent.parse() == [title: exampleEvent.title, message: exampleEvent.message]

        where:
        type | json || exampleEvent
        "CreateEvent" | exampleCreateRepoEventJson || createRepoEventExample
        "CreateEvent" | exampleCreateBranchEventJson || createBranchEventExample
        "DeleteEvent" | exampleDeleteEventJson || deleteEventExample
        "ForkEvent" | exampleForkEventJson || forkEventExample
        "GollumEvent" | exampleGollumEventJson || gollumEventExample
        "IssuesEvent" | exampleIssuesEventJson || issuesEventExample
        "IssueCommentEvent" | exampleIssueCommentEventJson || issueCommentEventExample
        "PushEvent" | exampleSinglePushPayload || pushEventExample
        "PullRequestEvent" | examplePullRequestEventJson || pullRequestEventExample
        "WatchEvent" | exampleWatchEventJson || watchEventExample
        "UnknownEvent" | exampleUnknownEventJson || unknownEventExample
    }

    static def pushEventExample = [
            title: "GitHub Push Event",
            message: "SomeUser pushed 1 commit to refs/heads/master in SomeOrg/i-made-this",
    ]

    static def createRepoEventExample = [
            title: "GitHub Repository Created",
            message: "CreatorUser created repository SomeOrg/some-new-repo",
    ]

    static def createBranchEventExample = [
            title: "GitHub Branch Created",
            message: "CreatorUser created branch toggle-system-tray-icon in SomeOrg/some-existing-repo",
    ]

    static def forkEventExample = [
            title: "GitHub Repo Forked",
            message: "ForkUser forked OriginalOrg/repo-name into NewOrg/repo-name",
    ]

    static def issuesEventExample = [
            title: "GitHub Issue Opened",
            message: "SomeUser opened issue #1 on SomeOrg/repo-name",
    ]

    static def issueCommentEventExample = [
            title: "GitHub Issue Comment",
            message: "SomeUser commented on issue #1 on SomeOrg/repo-name",
    ]

    static def watchEventExample = [
            title: "GitHub Repo Starred",
            message: "SomeUser starred SomeOrg/repo-name",
    ]

    static def gollumEventExample = [
            title: "GitHub Wiki",
            message: "SomeUser updated the wiki for repo SomeOrg/repo-name",
    ]

    static def pullRequestEventExample = [
            title: "GitHub Pull Request",
            message: "SomeUser opened pull request #1 on repo SomeOrg/repo-name",
    ]

    static def deleteEventExample = [
            title: "GitHub Delete Event",
            message: "SomeUser deleted branch my-branch on repo SomeOrg/repo-name",
    ]

    static def unknownEventExample = [
            title: "UnknownEvent",
            message: "username acted on repo username/reponame",
    ]
}
