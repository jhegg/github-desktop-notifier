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

    static String exampleCreateEventJson = """{
    "id": "2671420223",
    "actor": {
        "login": "CreatorUser",
        "avatar_url": "https://avatars.githubusercontent.com/u/123456?",
    },
    "created_at": "2015-02-02T01:15:07Z",
    "repo": {
        "name": "SomeOrg/some-new-repo",
    },
    "type": "CreateEvent"
}"""
}
