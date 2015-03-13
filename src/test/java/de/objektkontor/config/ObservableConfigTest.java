package de.objektkontor.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObservableConfigTest {

    @Mock
    private ConfigObserver<TestConfig> observer;

    private final List<ConfigUpdate> updates = new LinkedList<>();

    private TestConfig config;
    private TestConfig clone;

    @Before
    public void setUp() {
        config = new TestConfig().fill();
        clone = ConfigDuplicator.cloneConfig(config);
    }

    @Test
    public void testSetObserver() {
        assertFalse(config.hasObserver());
        config.setObserver(observer);
        assertTrue(config.hasObserver());
        try {
            config.setObserver(observer);
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void testRemoveObserver() {
        config.setObserver(observer);
        assertTrue(config.hasObserver());
        assertTrue(config.removeObserver() == observer);
        assertFalse(config.hasObserver());
    }

    @Test
    public void testObserverNotNotifiedForEqualConfig() throws Exception {
        config.setObserver(observer);
        config.notifyObserver(clone, updates);
        verify(observer, never()).reconfigure(any(TestConfig.class), anyListOf(ConfigUpdate.class));
    }

    @Test
    public void testObserverNotifiedForChangedConfig() throws Exception {
        config.setObserver(observer);
        config.setStringValue("different");
        config.notifyObserver(clone, updates);
        verify(observer).reconfigure(clone, updates);
    }

    @Test
    public void testObserverNotifiedForChangedConfigArray() throws Exception {
        config.setObserver(observer);
        config.getSubConfigs()[1].setStringValue("different");
        config.notifyObserver(clone, updates);
        verify(observer).reconfigure(clone, updates);
    }

    @Test
    public void testObserverNotNotifiedForChangedSubConfig() throws Exception {
        config.setObserver(observer);
        config.getSubConfig().setStringValue("different");
        config.notifyObserver(clone, updates);
        verify(observer, never()).reconfigure(any(TestConfig.class), anyListOf(ConfigUpdate.class));
    }
}
