package edu.neu.madproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    String uid;
    String username;
    Map<String, Long> history;

    public Users() {
    }

    public Users(String uid, String username, List<String> emojiList) {
        this.uid = uid;
        this.username = username;
        history = new HashMap<>();
        for (String key : emojiList) {
            history.put(key, 0L);
        }
    }

    public Users(String uid, String username, Map<String, Long> emojiList) {
        this.uid = uid;
        this.username = username;
        history = emojiList;
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


    public Map<String, Long> getHistory() {
        return this.history;
    }

    public void setHistory(Map<String, Long> history) {
        this.history = history;
    }
}
