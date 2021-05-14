package com.druid.interview.takehometest;

public class Log {
    public String timeStamp;
    public String userID;
    public String path;

    private String getField(String[] raw, int index) {
      return  raw.length > 1 &&
              raw[index] != null ? raw[index] : null;
    }

    public Log() {

    }

    public Log(String line) {
        String[] raw = line.split(",");
        timeStamp = getField(raw, 0);
        userID = getField(raw, 1);
        path = getField(raw, 2);
    }

    public int compare(Log l1) {
        return l1.path.compareTo(this.path);
    }
}
