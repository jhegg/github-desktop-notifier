package com.jhegg.github.notifier

import de.saxsys.javafx.test.JfxRunner
import de.saxsys.javafx.test.TestInJfxThread
import javafx.scene.control.Label
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

import static junit.framework.Assert.assertNotNull

@RunWith(JfxRunner.class)
public class RootLayoutControllerJunitTest {

    @Test
    @TestInJfxThread
    public void testInitializeSetsTitleOnAboutBox() {
        RootLayoutController rootLayoutController = new RootLayoutController()
        rootLayoutController.initialize()
        Assert.assertEquals("About", rootLayoutController.aboutBox.getTitle())
    }

    @Test
    @TestInJfxThread
    public void testAddressIsConfiguredWhenControllerIsConfigured() {
        RootLayoutController rootLayoutController = new RootLayoutController()
        rootLayoutController.address = new Label()
        App app = new App()
        app.userName = "josh"
        rootLayoutController.configure(app)
        assertNotNull(rootLayoutController.editPreferencesController)
        Assert.assertEquals("https://api.github.com/users/josh/received_events", rootLayoutController.address.getText())
    }

    @Test
    @TestInJfxThread
    public void testAddressIsConfiguredWithEnterpriseApiWhenControllerIsConfigured() {
        RootLayoutController rootLayoutController = new RootLayoutController()
        rootLayoutController.address = new Label()
        App app = new App()
        app.userName = "josh"
        app.gitHubEnterpriseHostname = "github.example.com"
        rootLayoutController.configure(app)
        assertNotNull(rootLayoutController.editPreferencesController)
        Assert.assertEquals("https://github.example.com/api/v3/users/josh/received_events", rootLayoutController.address.getText())
    }

    @Test
    public void testUpdatingLastFetchTimeSetsValue() {
        RootLayoutController rootLayoutController = new RootLayoutController()
        rootLayoutController.lastFetchTime = Mockito.mock(Label.class)
        rootLayoutController.updateLastFetchTime()
        Mockito.verify(rootLayoutController.lastFetchTime).setText(Mockito.anyString())
    }
}
