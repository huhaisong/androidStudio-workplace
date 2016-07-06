package com.example.a111.myapplication1;

import java.io.Serializable;

/**
 * wifi的信息
 */
public class WifiInfo implements Serializable {
    public String Ssid = "";
    public String Password = "";


    public WifiInfo(String ssid, String password) {
        Ssid = ssid;
        Password = password;
    }

    public WifiInfo() {
    }


    public String getSsid() {
        return Ssid;
    }

    public void setSsid(String ssid) {
        Ssid = ssid;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "WifiInfo{" + "Ssid='" + Ssid + '\'' + ", Password='" + Password + '\'' + '}';
    }
}

