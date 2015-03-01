package com.jhegg.github.notifier

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage

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
        SwingUtilities.invokeLater { addAppToTray() }
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

        trayIcon = new TrayIcon(createImage())
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

    private Image createImage() {
        String text = "GN"

        /*
           Because font metrics is based on a graphics context, we need to create
           a small, temporary image so we can ascertain the width and height
           of the final image
         */
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = img.createGraphics()
        Font font = new Font("Arial", Font.PLAIN, 10)
        g2d.setFont(font)
        g2d.dispose()

        img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
        g2d = img.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g2d.setFont(font)
        g2d.setColor(Color.GREEN)
        g2d.drawString(text, 0, 11)
        g2d.dispose()

        return img
    }

    private void showStage() {
        if (primaryStage) {
            primaryStage.show()
            primaryStage.toFront()
        }
    }
}