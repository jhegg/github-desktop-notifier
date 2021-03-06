package com.jhegg.github.notifier

class GitHubJsonPayloadExamples {

    static String exampleSinglePushPayload = """{
    "actor": {
        "avatar_url": "https://avatars.githubusercontent.com/u/12345?",
        "gravatar_id": "",
        "id": 12345,
        "login": "SomeUser",
        "url": "https://api.github.com/users/SomeUser"
    },
    "created_at": "2015-02-01T01:02:03Z",
    "id": "2671420212",
    "org": {
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
        "gravatar_id": "",
        "id": 123456,
        "login": "SomeOrg",
        "url": "https://api.github.com/orgs/SomeOrg"
    },
    "payload": {
        "before": "8aeb1085cf37920495bac0f0c0ea00d7cd6d2105",
        "commits": [
            {
                "author": {
                    "email": "someuser@example.com",
                    "name": "Some User"
                },
                "distinct": true,
                "message": "I made this thing",
                "sha": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
                "url": "https://api.github.com/repos/SomeOrg/i-made-this/commits/05351301f9400ddaf5d7aaec4f55ab13a06986c3"
            }
        ],
        "distinct_size": 1,
        "head": "05351301f9400ddaf5d7aaec4f55ab13a06986c3",
        "push_id": 551032016,
        "ref": "refs/heads/master",
        "size": 1
    },
    "public": true,
    "repo": {
        "id": 1234567,
        "name": "SomeOrg/i-made-this",
        "url": "https://api.github.com/repos/SomeOrg/i-made-this"
    },
    "type": "PushEvent"
}"""

    static String exampleCreateRepoEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "CreatorUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "description": "",
        "master_branch": "master",
        "pusher_type": "user",
        "ref": null,
        "ref_type": "repository"
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/some-new-repo",
    },
    "type": "CreateEvent"
}"""

    static String exampleCreateBranchEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "CreatorUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "description": "",
        "master_branch": "master",
        "pusher_type": "user",
        "ref": "toggle-system-tray-icon",
        "ref_type": "branch"
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/some-existing-repo",
    },
    "type": "CreateEvent"
}"""

    static String exampleForkEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "ForkUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "forkee": {
            "full_name": "NewOrg/repo-name",
        },
    },
    "created_at": "2015-02-02T01:15:07Z",
    "public": true,
    "repo": {
        "name": "OriginalOrg/repo-name",
    },
    "type": "ForkEvent"
}"""

    static String exampleIssuesEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "action": "opened",
        "issue": {
            "assignee": null,
            "body": "The reason this is an issue is because x, y, and z.",
            "number": 1,
            "title": "This is an issue",
        }
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "IssuesEvent"
}"""

    static String exampleIssueCommentEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "action": "created",
        "issue": {
            "assignee": null,
            "body": "The reason this is an issue is because x, y, and z.",
            "number": 1,
            "title": "This is an issue",
        },
        "comment": {
            "body": "This is my comment",
        },
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "IssueCommentEvent"
}"""

    static String exampleWatchEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "action": "started",
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "WatchEvent"
}"""

    static String exampleGollumEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "pages": [
            {
                "page_name": "Home",
                "title": "Home",
                "action": "edited",
            }
        ],
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "GollumEvent"
}"""

    static String examplePullRequestEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "action": "opened",
        "number": 1,
        "pull_request": {
        },
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "PullRequestEvent"
}"""

    static String exampleDeleteEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "SomeUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
        "ref_type": "branch",
        "ref": "my-branch",
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/repo-name",
    },
    "type": "DeleteEvent"
}"""

    static String exampleUnknownEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "username",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "payload": {
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "username/reponame",
    },
    "type": "UnknownEvent"
}"""
}
