package edu.neu.madproject;

import java.util.Map;

public class UsersStats {
    Map<String, Long> reads;
    Map<String, Long> writes;
    Map<String, Long> tagReads;
    Map<String, Long> tagWrites;

    public UsersStats() {
    }

    public UsersStats(Map<String, Long> interactCatList,
                      Map<String, Long> writeCatList,
                      Map<String, Long> tagReads,
                      Map<String, Long> tagWrites) {
        this.reads = interactCatList;
        this.writes = writeCatList;
        this.tagReads = tagReads;
        this.tagWrites = tagWrites;
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

    public Map<String, Long> getTagWrites() {
        return this.tagWrites;
    }

    public void setTagWrites(Map<String, Long> tagWrites) {
        this.tagWrites = tagWrites;
    }

    public Map<String, Long> getTagReads() {
        return this.tagReads;
    }

    public void setTagReads(Map<String, Long> tagReads) {
        this.tagReads = tagReads;
    }
}
