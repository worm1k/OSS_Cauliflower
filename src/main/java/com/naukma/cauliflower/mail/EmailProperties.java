package com.naukma.cauliflower.mail;

/**
 * Created by ihor on 05.12.2014.
 */
public enum EmailProperties {

    SERVER("smtp.gmail.com",587);


    private String HOST;
    private Integer PORT;

    EmailProperties(String HOST, Integer PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }


    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public Integer getPORT() {
        return PORT;
    }

    public void setPORT(Integer PORT) {
        this.PORT = PORT;
    }

}
