package edu.neu.madcourse.assignment7;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    String uid;
    String username;
    String status;
    String notificationToken;
    Map<String, Long> history;

    public Users() {

    }


    public Users(String uid, String username, String status, String notificationToken, List<String> emojiList) {
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.notificationToken = notificationToken;
        history = new HashMap<>();
        for (String key : emojiList) {
            history.put(key, 0L);
        }
    }


    public Users(String uid, String username, String status, String notificationToken, Map<String, Long> emojiList) {
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.notificationToken = notificationToken;
        history = emojiList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Long> getHistory() {
        return this.history;
    }

    public void setHistory(Map<String, Long> history) {
        this.history = history;
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

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }
}
