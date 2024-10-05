package de.objektkontor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.objektkontor.config.ConfigTracker.Modifier;

@ExtendWith(MockitoExtension.class)
public class ConfigTrackerTest {

	@Mock private ConfigObserver<TestBaseConfig> observer;
	@Mock private ConfigObserver<TestSubConfig> subObserver;

	private ConfigTracker<Object> configTracker;
	private TestConfig sourceConfig;
	private TestConfig workingCopy;
	private final Object trackingData = new Object();

	@BeforeEach
	public void setUp() {
		configTracker = new ConfigTracker<>();
		sourceConfig = new TestConfig().fill();
		workingCopy = configTracker.register(sourceConfig, trackingData);
		workingCopy.setObserver(observer);
		workingCopy.getSubConfig().setObserver(subObserver);
	}

	@Test
	public void testSyncConfigsNotifiesAllObservers() throws Exception {
		sourceConfig.setStringValue("differs");
		sourceConfig.getSubConfig().setStringValue("differs");
		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));

		configTracker.syncConfigs();

		InOrder inOrder = inOrder(observer, subObserver);
		inOrder.verify(subObserver).reconfigure(eq(sourceConfig.getSubConfig()), anyList());
		inOrder.verify(observer).reconfigure(eq(sourceConfig), anyList());

		assertTrue(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));
	}

	@Test
	public void testSyncConfigsNotifiesOnlySubObserver() throws Exception {
		sourceConfig.getSubConfig().setStringValue("differs");
		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));

		configTracker.syncConfigs();

		verify(subObserver).reconfigure(eq(sourceConfig.getSubConfig()), anyList());
		verify(observer, never()).reconfigure(any(TestConfig.class), anyList());

		assertTrue(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));
	}

	@Test
	public void testSyncConfigsExecutesConfigUpdateOperations() throws Exception {
		sourceConfig.setStringValue("differs");
		sourceConfig.getSubConfig().setStringValue("differs");
		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));

		final ConfigUpdate configUpdate1 = mock(ConfigUpdate.class);
		final ConfigUpdate configUpdate2 = mock(ConfigUpdate.class);
		final ConfigUpdate subConfigUpdate1 = mock(ConfigUpdate.class);
		final ConfigUpdate subConfigUpdate2 = mock(ConfigUpdate.class);

		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			List<ConfigUpdate> updates = invocation.getArgument(1, List.class);
			updates.add(configUpdate1);
			updates.add(configUpdate2);
			return null;
		}).when(observer).reconfigure(eq(sourceConfig), anyList());

		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			List<ConfigUpdate> updates = invocation.getArgument(1, List.class);
			updates.add(subConfigUpdate1);
			updates.add(subConfigUpdate2);
			return null;
		}).when(subObserver).reconfigure(eq(sourceConfig.getSubConfig()), anyList());

		doThrow(Exception.class).when(subConfigUpdate1).apply();

		configTracker.syncConfigs();

		InOrder inOrder = inOrder(configUpdate1, configUpdate2, subConfigUpdate1, subConfigUpdate2);

		inOrder.verify(subConfigUpdate1).prepare();
		inOrder.verify(subConfigUpdate2).prepare();
		inOrder.verify(configUpdate1).prepare();
		inOrder.verify(configUpdate2).prepare();

		assertTrue(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));
		assertEquals("differs", workingCopy.getStringValue());
		assertEquals("differs", workingCopy.getSubConfig().getStringValue());

		inOrder.verify(subConfigUpdate1).apply();
		inOrder.verify(subConfigUpdate2).apply();
		inOrder.verify(configUpdate1).apply();
		inOrder.verify(configUpdate2).apply();

		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testSyncConfigsDiscardsConfigUpdateOperationsOnError() throws Exception {
		sourceConfig.setStringValue("differs");
		sourceConfig.getSubConfig().setStringValue("differs");
		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));

		final ConfigUpdate configUpdate1 = mock(ConfigUpdate.class);
		final ConfigUpdate configUpdate2 = mock(ConfigUpdate.class);
		final ConfigUpdate subConfigUpdate1 = mock(ConfigUpdate.class);
		final ConfigUpdate subConfigUpdate2 = mock(ConfigUpdate.class);

		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			List<ConfigUpdate> updates = invocation.getArgument(1, List.class);
			updates.add(configUpdate1);
			updates.add(configUpdate2);
			return null;
		}).when(observer).reconfigure(eq(sourceConfig), anyList());

		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			List<ConfigUpdate> updates = invocation.getArgument(1, List.class);
			updates.add(subConfigUpdate1);
			updates.add(subConfigUpdate2);
			return null;
		}).when(subObserver).reconfigure(eq(sourceConfig.getSubConfig()), anyList());

		doThrow(Exception.class).when(configUpdate1).prepare();
		doThrow(Exception.class).when(subConfigUpdate1).discard();

		try {
			configTracker.syncConfigs();
			fail("Exception expected");
		} catch (Exception e) {
		}

		InOrder inOrder = inOrder(configUpdate1, configUpdate2, subConfigUpdate1, subConfigUpdate2);

		inOrder.verify(subConfigUpdate1).prepare();
		inOrder.verify(subConfigUpdate2).prepare();
		inOrder.verify(configUpdate1).prepare();

		inOrder.verify(configUpdate1).discard();
		inOrder.verify(subConfigUpdate2).discard();
		inOrder.verify(subConfigUpdate1).discard();

		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));
		assertEquals("value", workingCopy.getStringValue());
		assertEquals("subconfig.value", workingCopy.getSubConfig().getStringValue());

		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void testUpdateConfigsRequestsNewConfigsByCallerAndSyncThem() throws Exception {
		@SuppressWarnings("unchecked")
		Modifier<Object> modifier = mock(Modifier.class);

		TestConfig newConfig = new TestConfig().fill();
		newConfig.setStringValue("differs");
		newConfig.getSubConfig().setStringValue("differs");
		when(modifier.modify(workingCopy, sourceConfig, trackingData)).thenReturn(newConfig);

		configTracker.updateConfigs(modifier);

		InOrder inOrder = inOrder(observer, subObserver);
		inOrder.verify(subObserver).reconfigure(eq(newConfig.getSubConfig()), anyList());
		inOrder.verify(observer).reconfigure(eq(newConfig), anyList());

		assertFalse(ConfigComparator.deepEquals(workingCopy, sourceConfig, false));
		assertTrue(ConfigComparator.deepEquals(workingCopy, newConfig, false));
	}

	@Test
	public void testUpdateConfigsChecksThatNewConfigIsNotNull() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			@SuppressWarnings("unchecked")
			Modifier<Object> modifier = mock(Modifier.class);
			when(modifier.modify(any(ObservableConfig.class), any(ObservableConfig.class), any())).thenReturn(null);
			try {
				configTracker.updateConfigs(modifier);
			} finally {
				verify(modifier).modify(workingCopy, sourceConfig, trackingData);
			}
		});
	}

	@Test
	public void testUpdateConfigsChecksThatNewConfigIsNotAWorkingCopyItself() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			@SuppressWarnings("unchecked")
			Modifier<Object> modifier = mock(Modifier.class);
			when(modifier.modify(any(ObservableConfig.class), any(ObservableConfig.class), any())).thenReturn(workingCopy);
			try {
				configTracker.updateConfigs(modifier);
			} finally {
				verify(modifier).modify(workingCopy, sourceConfig, trackingData);
			}
		});
	}

	@Test
	public void testUpdateConfigsChecksThatNewConfigIsAnInstanceOfCorrectType() throws Exception {
		assertThrows(IllegalStateException.class, () -> {
			@SuppressWarnings("unchecked")
			Modifier<Object> modifier = mock(Modifier.class);
			when(modifier.modify(any(ObservableConfig.class), any(ObservableConfig.class), any())).thenReturn(new ObservableConfig() {
			});
			try {
				configTracker.updateConfigs(modifier);
			} finally {
				verify(modifier).modify(workingCopy, sourceConfig, trackingData);
			}
		});
	}
}
