package edu.neu.madproject;

import java.util.Map;

public class UsersStats {
    Map<String, Long> reads;
    Map<String, Long> writes;

    public UsersStats() {
    }

    public UsersStats(Map<String, Long> interactCatList,
                      Map<String, Long> writeCatList) {
        this.reads = interactCatList;
        this.writes = writeCatList;
    }

    public Map<String, Long> getReads() {
        return this.reads;
    }

    public void setReads(Map<String, Long> reads) {
        this.reads = reads;
    }

    public Map<String, Long> getWrites() {
        return this.writes;
    }

    public void setWrites(Map<String, Long> writes) {
        this.writes = writes;
    }
}
