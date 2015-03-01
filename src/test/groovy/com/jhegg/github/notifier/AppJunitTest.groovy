package com.jhegg.github.notifier

import de.saxsys.javafx.test.JfxRunner
import de.saxsys.javafx.test.TestInJfxThread
import javafx.stage.Stage
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull

@RunWith(JfxRunner.class)
public class AppJunitTest {
    @Test
    @TestInJfxThread
    public void testConfigurePrimaryStage() throws Exception {
        App app = new App()
        app.primaryStage = new Stage()
        app.configurePrimaryStage()
        assertEquals("GitHub Desktop Notifier", app.primaryStage.getTitle())
        assertEquals(700d, app.primaryStage.getMinWidth())
        assertEquals(520d, app.primaryStage.getMinHeight())
        assertNotNull(app.rootLayoutController)
        assertNotNull(app.centerLayoutController)
        assertEquals(app.centerLayoutController.rootLayoutController, app.rootLayoutController)
    }

    @Test
    @TestInJfxThread
    public void testShowStage() throws Exception {
        App app = new App()
        def primaryStage = Mockito.mock(Stage)
        app.primaryStage = primaryStage
        primaryStage.metaClass.show = {} // #show is final and can't be mocked, so I have to override it
        app.showStage()
        Mockito.verify(primaryStage).toFront()
    }
}