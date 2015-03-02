package com.jhegg.github.notifier

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
}
