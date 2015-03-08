package com.jhegg.github.notifier

import de.saxsys.javafx.test.JfxRunner
import de.saxsys.javafx.test.TestInJfxThread
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.junit.Test
import org.junit.runner.RunWith

import static junit.framework.Assert.assertEquals
import static junit.framework.TestCase.assertFalse

@RunWith(JfxRunner.class)
public class EditPreferencesControllerJunitTest {

    @Test
    @TestInJfxThread
    public void testConfigure() throws Exception {
        App app = new App()
        app.token = "12345"
        app.userName = "josh"
        app.gitHubEnterpriseHostname = "localhost"
        app.useTrayIcon = false

        Pane pane = new Pane()

        EditPreferencesController editPreferencesController = new EditPreferencesController()
        editPreferencesController.token = new TextField()
        editPreferencesController.userName = new TextField()
        editPreferencesController.gitHubEnterpriseHostname = new TextField()
        editPreferencesController.systemTrayIcon = new CheckBox()

        app.primaryStage = new Stage()

        editPreferencesController.configure(app, pane)

        assertEquals("12345", editPreferencesController.token.getText())
        assertEquals("josh", editPreferencesController.userName.getText())
        assertEquals("localhost", editPreferencesController.gitHubEnterpriseHostname.getText())
        assertFalse(editPreferencesController.systemTrayIcon.selected)
    }

    @Test
    @TestInJfxThread
    public void testCloseDialog() throws Exception {
        EditPreferencesController editPreferencesController = new EditPreferencesController()
        editPreferencesController.dialogStage = new Stage()
        def stage = editPreferencesController.dialogStage
        editPreferencesController.closeDialog()
        assertFalse(stage.isShowing())
    }
}