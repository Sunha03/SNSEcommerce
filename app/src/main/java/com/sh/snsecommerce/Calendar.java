package com.sh.snsecommerce;

public class Calendar {
    private String id;
    private String content;
    private String date;

    public Calendar(String id, String content, String date) {
        this.id = id;
        this.content = content;
        this.date = date;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}
