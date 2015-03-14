package de.objektkontor.config;

import java.util.Calendar;
import java.util.Date;

import de.objektkontor.config.annotation.ConfigParameter;

public class TestBaseConfig extends ObservableConfig {

    @ConfigParameter(description = "Test string value")
    private String stringValue;

    @ConfigParameter(description = "Test array of string values")
    private String[] stringValues;

    @ConfigParameter
    private Boolean booleanValue;

    @ConfigParameter
    private Boolean[] booleanValues;

    @ConfigParameter
    private boolean booleanPrimitiveValue;

    @ConfigParameter
    private boolean[] booleanPrimitiveValues;

    @ConfigParameter
    private Integer integerValue;

    @ConfigParameter
    private Integer[] integerValues;

    @ConfigParameter
    private int integerPrimitiveValue;

    @ConfigParameter
    private int[] integerPrimitiveValues;

    @ConfigParameter
    private Long longValue;

    @ConfigParameter
    private Long[] longValues;

    @ConfigParameter
    private long longPrimitiveValue;

    @ConfigParameter
    private long[] longPrimitiveValues;

    @ConfigParameter
    private Date dateValue;

    @ConfigParameter
    private Date[] dateValues;

    @ConfigParameter
    private TestEnum enumValue;

    @ConfigParameter
    private TestEnum[] enumValues;

    public TestBaseConfig fill() {
        return fill(null);
    }

    public TestBaseConfig fill(String prefix) {
        setStringValue(stringValue(prefix, "value"));
        setStringValues(new String[] { stringValue(prefix, "value1"), stringValue(prefix, "value2") });
        setBooleanValue(Boolean.TRUE);
        setBooleanValues(new Boolean[] { Boolean.TRUE, Boolean.FALSE });
        setBooleanPrimitiveValue(true);
        setBooleanPrimitiveValues(new boolean[] { true, false });
        setIntegerValue(new Integer(1));
        setIntegerValues(new Integer[] { new Integer(1), new Integer(2) });
        setIntegerPrimitiveValue(1);
        setIntegerPrimitiveValues(new int[] { 1, 2 });
        setLongValue(new Long(1));
        setLongValues(new Long[] { new Long(1), new Long(2) });
        setLongPrimitiveValue(1);
        setLongPrimitiveValues(new long[] { 1, 2 });
        setDateValue(createTestDate(11, 1, 2011));
        setDateValues(new Date[] { createTestDate(1, 11, 2011), createTestDate(2, 12, 2012) });
        setEnumValue(TestEnum.A);
        setEnumValues(new TestEnum[] { TestEnum.B, TestEnum.C });
        return this;
    }

    private String stringValue(String prefix, String value) {
        return prefix == null ? value : prefix + "." + value;
    }

    public static Date createTestDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String[] getStringValues() {
        return stringValues;
    }

    public void setStringValues(String[] stringValues) {
        this.stringValues = stringValues;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Boolean[] getBooleanValues() {
        return booleanValues;
    }

    public void setBooleanValues(Boolean[] booleanValues) {
        this.booleanValues = booleanValues;
    }

    public boolean isBooleanPrimitiveValue() {
        return booleanPrimitiveValue;
    }

    public void setBooleanPrimitiveValue(boolean booleanPrimitiveValue) {
        this.booleanPrimitiveValue = booleanPrimitiveValue;
    }

    public boolean[] getBooleanPrimitiveValues() {
        return booleanPrimitiveValues;
    }

    public void setBooleanPrimitiveValues(boolean[] booleanPrimitiveValues) {
        this.booleanPrimitiveValues = booleanPrimitiveValues;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Integer[] getIntegerValues() {
        return integerValues;
    }

    public void setIntegerValues(Integer[] integerValues) {
        this.integerValues = integerValues;
    }

    public int getIntegerPrimitiveValue() {
        return integerPrimitiveValue;
    }

    public void setIntegerPrimitiveValue(int integerPrimitiveValue) {
        this.integerPrimitiveValue = integerPrimitiveValue;
    }

    public int[] getIntegerPrimitiveValues() {
        return integerPrimitiveValues;
    }

    public void setIntegerPrimitiveValues(int[] integerPrimitiveValues) {
        this.integerPrimitiveValues = integerPrimitiveValues;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Long[] getLongValues() {
        return longValues;
    }

    public void setLongValues(Long[] longValues) {
        this.longValues = longValues;
    }

    public long getLongPrimitiveValue() {
        return longPrimitiveValue;
    }

    public void setLongPrimitiveValue(long longPrimitiveValue) {
        this.longPrimitiveValue = longPrimitiveValue;
    }

    public long[] getLongPrimitiveValues() {
        return longPrimitiveValues;
    }

    public void setLongPrimitiveValues(long[] longPrimitiveValues) {
        this.longPrimitiveValues = longPrimitiveValues;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Date[] getDateValues() {
        return dateValues;
    }

    public void setDateValues(Date[] dateValues) {
        this.dateValues = dateValues;
    }

    public TestEnum getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(TestEnum enumValue) {
        this.enumValue = enumValue;
    }

    public TestEnum[] getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(TestEnum[] enumValues) {
        this.enumValues = enumValues;
    }

    @Override
    public boolean equals(Object obj) {
        return ConfigComparator.deepEquals(this, obj, false);
    }
}