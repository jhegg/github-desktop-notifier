package com.jhegg.github.notifier

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage

class App extends Application {
    static final String gitHubUrlPrefix = "https://api.github.com/"
    static final String gitHubEnterpriseUrlPrefixWithPlaceholder = "https://%s/api/v3/"
    String gitHubEnterpriseHostname = GString.EMPTY
    static final String gitHubUrlSuffixWithPlaceholder = "users/%s/received_events"
    String userName = GString.EMPTY
    String token = GString.EMPTY

    protected Stage primaryStage
    protected CenterLayoutController centerLayoutController
    protected RootLayoutController rootLayoutController

    static void main(String[] args) {
        launch(App.class, args)
    }

    void exitApp() {
        Platform.exit()
    }

    @Override
    void start(Stage primaryStage) throws Exception {
        def options = parseArguments(getParameters().raw)
        if (options.h) {
            stop()
            return
        }

        this.primaryStage = primaryStage
        configurePrimaryStage()
    }

    def parseArguments(List<String> arguments) {
        def cli = new CliBuilder()
        cli.with {
            h longOpt: 'help', 'Show usage information'
            t longOpt: 'token', args: 1, 'GitHub OAuth token (Optional)'
            u longOpt: 'user', args: 1, 'GitHub username to be queried (Required)'
            n longOpt: 'hostname', args: 1, 'GitHub Enterprise hostname (Optional)'
        }
        def options = cli.parse(arguments)
        if (options.h) {
            cli.usage()
        }
        if (options.t) {
            token = options.t
        }
        if (options.u) {
            userName = options.u
        }
        if (options.n) {
            gitHubEnterpriseHostname = options.n
        }
        return options
    }

    @Override
    void stop() throws Exception {
        super.stop()
        exitApp()
    }

    private void configurePrimaryStage() {
        primaryStage.title = "GitHub Events using Groovy"
        primaryStage.scene = getScene()
        primaryStage.show()

        // prevent users from resizing the window so small that the status bar disappears
        primaryStage.setMinWidth(700)
        primaryStage.setMinHeight(520)
    }

    private def getScene() {
        def rootLayout = getRootLayout()
        rootLayout.setCenter(getCenterLayout())
        new Scene(rootLayout)
    }

    private BorderPane getRootLayout() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('RootLayout.fxml') as URL)
        BorderPane pane = loader.load()
        rootLayoutController = loader.getController()
        rootLayoutController.configure(this)
        return pane
    }

    private Pane getCenterLayout() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('CenterLayout.fxml') as URL)
        Pane pane = loader.load()
        centerLayoutController = loader.getController()
        centerLayoutController.setApp(this)
        centerLayoutController.initializeGithubService()
        return pane
    }
}