package com.naukma.cauliflower.mail;

/**
 * Created by ihor on 05.12.2014.
 */
public enum EmailProperties {

    SERVER("smtp.gmail.com", 587);


    private String host;
    private Integer port;

    EmailProperties(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}