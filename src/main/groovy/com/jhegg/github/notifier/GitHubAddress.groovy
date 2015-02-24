package com.jhegg.github.notifier

class GitHubAddress {
    static String getResolvedUrl(App app) {
        if (app.gitHubEnterpriseHostname) {
            getResolvedGitHubEnterprisePrefix(app) + getResolvedUrlSuffix(app)
        } else {
            app.gitHubUrlPrefix + getResolvedUrlSuffix(app)
        }
    }

    private static String getResolvedGitHubEnterprisePrefix(App app) {
        String.format(app.gitHubEnterpriseUrlPrefixWithPlaceholder, app.gitHubEnterpriseHostname)
    }

    private static String getResolvedUrlSuffix(App app) {
        String.format(app.gitHubUrlSuffixWithPlaceholder, app.userName)
    }
}
