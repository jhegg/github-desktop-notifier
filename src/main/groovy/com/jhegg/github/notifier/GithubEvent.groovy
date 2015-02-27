package com.jhegg.github.notifier

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
