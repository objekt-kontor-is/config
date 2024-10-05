package de.objektkontor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class ConfigComparatorTest {

    @Test
    public void testEqualsWithNullValues() {
        assertTrue(ConfigComparator.equals(null, null));
        assertFalse(ConfigComparator.equals("value", null));
        assertFalse(ConfigComparator.equals(null, "value"));
        assertFalse(ConfigComparator.equals(new String[0], null));
        assertFalse(ConfigComparator.equals(null, new String[0]));
    }

    @Test
    public void testEqualsObjectValues() {
        assertConfigsEquals(true, "value", "value");
        assertConfigsEquals(false, "value", "different");
    }

    @Test
    public void testEqualsArrayOfValues() {
        String[] first = new String[] { "value1", "value2" };
        String[] second = new String[] { "value1", "value2" };
        assertConfigsEquals(true, first, second);
        second[1] = "different";
        assertConfigsEquals(false, first, second);
    }

    @Test
    public void testEqualsArrayOfArrayOfValues() {
        String[][] first = new String[][] {{ "value11", "value12" }, { "value21", "value22" }};
        String[][] second = new String[][] {{ "value11", "value12" }, { "value21", "value22" }};
        assertConfigsEquals(true, first, second);
        second[1][1] = "different";
        assertConfigsEquals(false, first, second);
    }

    @Test
    public void testEqualsWithEmptyConfigs() {
        TestConfigs configs = new TestConfigs();
        assertConfigsEquals(true, true, configs);
    }

    @Test
    public void testEqualsWithFullConfigs() {
        TestConfigs configs = new TestConfigs().fill();
        assertConfigsEquals(true, true, configs);
    }

    @Test
    public void testEqualsWithDifferentValues() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.setStringValue("different");
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithDifferentValuesAtDepth2() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setStringValue("different");
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentValuesAtDepth3() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setStringValue("different");
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentValueArrays() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.setStringValues(new String[] { "different" });
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithDifferentValueArraysAtDepth2() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setStringValues(new String[] { "different" });
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentValueArraysAtDepth3() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setStringValues(new String[] { "different" });
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentConfigIds() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfigs()[1].setId("different");
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValues() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.setIntegerPrimitiveValue(2);
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValuesAtDepth2() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setIntegerPrimitiveValue(2);
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValuesAtDepth3() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setIntegerPrimitiveValue(2);
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValueArrays() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.setIntegerPrimitiveValues(new int[] { 2, 3 });
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValueArraysAtDepth2() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setIntegerPrimitiveValues(new int[] { 2, 3 });
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsWithDifferentPrimitiveValueArraysAtDepth3() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setIntegerPrimitiveValues(new int[] { 2, 3 });
        assertConfigsEquals(true, false, configs);
    }

    @Test
    public void testEqualsConfigArraysAlwaysComparedToMaximalDepth() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfigs()[1].setStringValue("different");
        assertConfigsEquals(false, false, configs);

        configs = new TestConfigs().fill();
        configs.second.getSubConfigs()[1].getSubConfigs()[1].setStringValue("different");
        assertConfigsEquals(false, false, configs);
    }

    @Test
    public void testEqualsWithSpecificDepth() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setStringValue("different");
        assertTrue(ConfigComparator.equals(configs.first, configs.second, 1));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 2));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3));

        configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setStringValue("different");
        assertTrue(ConfigComparator.equals(configs.first, configs.second, 1));
        assertTrue(ConfigComparator.equals(configs.first, configs.second, 2));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3));
    }

    @Test
    public void testEqualsNeverIgnoresObserversAtRootDepth() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.setStringValue("different");
        assertFalse(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3, true));
        configs.first.setObserver(mock(ConfigObserver.class));
        assertFalse(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3, true));
    }

    @Test
    public void testEqualsIgnoresObserversAtDepth2() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().setStringValue("different");
        assertFalse(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3, true));
        configs.first.getSubConfig().setObserver(mock(ConfigObserver.class));
        assertTrue(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertTrue(ConfigComparator.equals(configs.first, configs.second, 3, true));
    }

    @Test
    public void testEqualsIgnoresObserversAtDepth3() {
        TestConfigs configs = new TestConfigs().fill();
        configs.second.getSubConfig().getSubConfig().setStringValue("different");
        assertFalse(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertFalse(ConfigComparator.equals(configs.first, configs.second, 3, true));
        configs.first.getSubConfig().getSubConfig().setObserver(mock(ConfigObserver.class));
        assertTrue(ConfigComparator.deepEquals(configs.first, configs.second, true));
        assertTrue(ConfigComparator.equals(configs.first, configs.second, 3, true));
    }

    @Test
    public void testEqualsArrayOfConfigs() {
        TestConfig[] first = new TestConfig[] { new TestConfig().fill(), new TestConfig().fill() };
        TestConfig[] second = new TestConfig[] { new TestConfig().fill(), new TestConfig().fill() };
        assertConfigsEquals(true, first, second);
        second[1].getSubConfigs()[1].getSubConfigs()[1].setStringValue("different");
        assertConfigsEquals(false, first, second);
    }

    @Test
    public void testDiffWorksWithoutExceptions() {
        TestConfigs configs = new TestConfigs();
        assertTrue(ConfigComparator.diff(configs.first, configs.second).length() == 0);

        configs = new TestConfigs();
        configs.first.fill();
        assertTrue(ConfigComparator.diff(configs.first, configs.second).length() > 0);

        configs = new TestConfigs();
        configs.second.fill();
        assertTrue(ConfigComparator.diff(configs.first, configs.second).length() > 0);

        configs = new TestConfigs().fill();
        configs.second.setStringValue("differs");
        configs.second.getStringValues()[1] = "differs";
        configs.second.getSubConfig().setDateValue(TestBaseConfig.createTestDate(03, 03, 2003));
        configs.second.getSubConfig().setDateValues(new Date[] { TestBaseConfig.createTestDate(03, 03, 2003) });
        configs.second.getSubConfig().getSubConfig().getStringValues()[0] = null;
        assertTrue(ConfigComparator.diff(configs.first, configs.second).length() > 0);

        //System.out.println(ConfigComparator.diff(configs.first, configs.second));
    }

    public void assertConfigsEquals(boolean expectedEqualsResult, Object first, Object second) {
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, ConfigComparator.SHALLOW));
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, ConfigComparator.SHALLOW, false));
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, ConfigComparator.DEEP));
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, ConfigComparator.DEEP, false));
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, 3));
        assertEquals(expectedEqualsResult, ConfigComparator.equals(first, second, 3, false));
    }

    public void assertConfigsEquals(boolean expectedShallowEqualsResult, boolean expectedDeepEqualsResult, TestConfigs configsToTest) {
        assertEquals(expectedShallowEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, ConfigComparator.SHALLOW));
        assertEquals(expectedShallowEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, ConfigComparator.SHALLOW, false));
        assertEquals(expectedDeepEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, ConfigComparator.DEEP));
        assertEquals(expectedDeepEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, ConfigComparator.DEEP, false));
        assertEquals(expectedDeepEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, 3));
        assertEquals(expectedDeepEqualsResult, ConfigComparator.equals(configsToTest.first, configsToTest.second, 3, false));
    }

    private static class TestConfigs {

        final TestConfig first = new TestConfig();
        final TestConfig second = new TestConfig();

        public TestConfigs fill() {
            first.fill();
            second.fill();
            return this;
        }
    }
}
