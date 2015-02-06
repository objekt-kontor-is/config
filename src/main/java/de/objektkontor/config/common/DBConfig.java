package de.objektkontor.config.common;

import de.objektkontor.config.ConfigParameter;

public class DBConfig {

    @ConfigParameter
    private String driver;

    @ConfigParameter
    private String url;

    @ConfigParameter
    private String user;

    @ConfigParameter
    private String password;

    public String getDriver() {
        return driver;
    }

    public DBConfig setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DBConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public DBConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public DBConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
