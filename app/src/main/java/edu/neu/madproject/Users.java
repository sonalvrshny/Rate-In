package edu.neu.madproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    String uid;
    String username;
    Map<String, Long> history;
    Map<String, Long> writeHistory;

    public Users() {
    }

    public Users(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
