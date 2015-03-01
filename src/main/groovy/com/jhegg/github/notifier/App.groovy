package com.jhegg.github.notifier

import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import org.apache.commons.lang.SystemUtils

import javax.imageio.ImageIO
import javax.swing.*
import java.awt.*
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import java.util.List

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
    private TrayIcon trayIcon

    static void main(String[] args) {
        launch(App.class, args)
    }

    void exitApp() {
        SystemTray tray = SystemTray.getSystemTray();
        Platform.exit()
        tray.remove(trayIcon)
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
        Platform.setImplicitExit(false)
        Platform.runLater { addAppToTray() }
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
        primaryStage.title = "GitHub Desktop Notifier"
        buildScene()
        configureControllers()
        enforceMinimumWindowSize()
        primaryStage.show()
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

    private void addAppToTray()  {
        Toolkit.getDefaultToolkit()
        if (!SystemTray.isSupported()) {
            System.out.println("No system tray support, application exiting.")
            Platform.exit()
        }

        SystemTray tray = SystemTray.getSystemTray()
        trayIcon = new TrayIcon(ImageIO.read(this.getClass().getResource(getIconResourcePath())))
        trayIcon.addActionListener({ Platform.runLater {this.showStage()}} as ActionListener)

        MenuItem exitItem = new MenuItem("Exit")
        exitItem.addActionListener({exitApp()} as ActionListener)

        PopupMenu popup = new PopupMenu()
        popup.add(exitItem)
        trayIcon.setPopupMenu(popup)

        try {
            tray.add(trayIcon)
        } catch (AWTException e) {
            e.printStackTrace()
        }
    }

    private String getIconResourcePath() {
        if (SystemUtils.IS_OS_LINUX) {
            // At least under XFCE, the icon that Java creates is NOT transparent, and looks horrible.
            return "/github-notifier-black-background.png"
        }
        return "/github-notifier.png"
    }

    private void showStage() {
        if (primaryStage) {
            primaryStage.show()
            primaryStage.toFront()
        }
    }
}