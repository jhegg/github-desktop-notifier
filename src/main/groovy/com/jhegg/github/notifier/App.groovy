package com.jhegg.github.notifier

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.apache.commons.lang.SystemUtils

import javax.imageio.ImageIO
import java.awt.*
import java.awt.event.ActionListener
import java.util.List

class App extends Application {
    static final String gitHubUrlPrefix = "https://api.github.com/"
    static final String gitHubEnterpriseUrlPrefixWithPlaceholder = "https://%s/api/v3/"
    String gitHubEnterpriseHostname = GString.EMPTY
    static final String gitHubUrlSuffixWithPlaceholder = "users/%s/received_events"
    String userName = GString.EMPTY
    String token = GString.EMPTY
    boolean useTrayIcon = true

    protected Stage primaryStage
    protected CenterLayoutController centerLayoutController
    protected RootLayoutController rootLayoutController
    private TrayIcon trayIcon

    static void main(String[] args) {
        launch(App.class, args)
    }

    void exitApp() {
        Platform.exit()
        /**
         * Force an exit to ignore benign error messages on Mac. Normally we would just unload the tray icon to get
         * AWT to exit, but on Mac that deadlocks the JavaFX and AWT threads. Using EventQueue#invokeLater to prevent
         * the deadlock causes some harmless error messages to show up during the exit, but they're annoying. So, we
         * just shut it all down, instead.
         */
        System.exit(0)
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
        primaryStage.show()
        if (useTrayIcon) {
            Platform.runLater { addAppToTray() }
        }
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

    void configurePrimaryStage() {
        primaryStage.title = "GitHub Desktop Notifier"
        buildScene()
        configureControllers()
        enforceMinimumWindowSize()
    }

    private void buildScene() {
        def rootLayout = getRootLayout()
        rootLayout.setCenter(getCenterLayout())
        primaryStage.scene = new Scene(rootLayout)
    }

    private BorderPane getRootLayout() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('RootLayout.fxml') as URL)
        BorderPane pane = loader.load()
        rootLayoutController = loader.getController()
        return pane
    }

    private Pane getCenterLayout() {
        def loader = new FXMLLoader(getClass().getClassLoader().getResource('CenterLayout.fxml') as URL)
        Pane pane = loader.load()
        centerLayoutController = loader.getController()
        return pane
    }

    private void configureControllers() {
        rootLayoutController.configure(this)

        centerLayoutController.setApp(this)
        centerLayoutController.rootLayoutController = rootLayoutController
        centerLayoutController.initializeGithubService()
    }

    private void enforceMinimumWindowSize() {
        // prevent users from resizing the window so small that the status bar disappears
        primaryStage.setMinWidth(700)
        primaryStage.setMinHeight(520)
    }

    void addAppToTray()  {
        SystemTray tray = getSystemTray()
        trayIcon = buildTrayIcon()
        trayIcon.addActionListener({ Platform.runLater { this.showStage() } } as ActionListener)
        addStageListeners()

        MenuItem exitItem = new MenuItem("Exit")
        exitItem.addActionListener({exitApp()} as ActionListener)

        PopupMenu popup = new PopupMenu()
        popup.add(exitItem)
        trayIcon.setPopupMenu(popup)
        trayIcon.setImageAutoSize(true)

        try {
            tray.add(trayIcon)
        } catch (AWTException e) {
            e.printStackTrace()
        }
    }

    private void addStageListeners() {
        def minimizeWindowChangeListener = [
                changed: { ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ->
                    if (trayIcon && newValue) {
                        primaryStage.hide()
                    }
                }] as ChangeListener<Boolean>
        primaryStage.iconifiedProperty().addListener(minimizeWindowChangeListener)

        primaryStage.setOnCloseRequest([handle: { WindowEvent event -> stop() }] as EventHandler<WindowEvent>)
    }

    private TrayIcon buildTrayIcon() {
        new TrayIcon(ImageIO.read(this.getClass().getResource(getIconResourcePath())))
    }

    private SystemTray getSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("No system tray support, application exiting.")
            Platform.exit()
        }
        return SystemTray.getSystemTray()
    }

    String getIconResourcePath() {
        if (isOsLinux()) {
            // At least under XFCE, the icon that Java creates is NOT transparent, and looks horrible.
            return "/github-notifier-black-background-256.png"
        }
        return "/github-notifier.png"
    }

    boolean isOsLinux() {
        SystemUtils.IS_OS_LINUX
    }

    void showStage() {
        if (primaryStage) {
            primaryStage.setIconified(false)
            primaryStage.show()
            primaryStage.toFront()
        }
    }

    void removeAppFromTray() {
        if (trayIcon) {
            Platform.runLater {
                SystemTray tray = getSystemTray()
                tray.remove(trayIcon)
                trayIcon = null
            }
        }
    }
}