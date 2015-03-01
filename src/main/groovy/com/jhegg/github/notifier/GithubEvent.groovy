package com.jhegg.github.notifier

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GitHubEvent {
    String id
    String type
    String login
    String created_at
    String json

    @Override
    public String toString() {
        "${login} - ${created_at}"
    }
}
