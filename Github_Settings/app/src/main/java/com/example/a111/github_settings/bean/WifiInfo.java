package com.example.a111.github_settings.bean;

import java.io.Serializable;

/**
 * Created by 111 on 2016/5/16.
 */
public class WifiInfo implements Serializable {

    private String name;
    private String pass = "";
    private String type = "";

    public WifiInfo() {
    }


    public WifiInfo(String name, String pass, String type) {
        this.name = name;
        this.pass = pass;
        this.type = type;
    }

    public WifiInfo(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WifiInfo{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public boolean equals(WifiInfo wifiInfo) {

        if (wifiInfo.getName().equals(name) && wifiInfo.getPass().equals(pass) && wifiInfo.getType().equals(type)) {

            return true;
        }
        return false;
    }
}
