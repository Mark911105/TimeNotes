package com.marco.timernotes.entity;

/**
 * User: Administrator
 * Date: 2016/5/24
 * Time: 23:52
 * 数据保存实例
 */
public class ItemNotes {
    private int    noteid       = 0;
    private String title        = "";
    private String content      = "";
    private Long   time         = 1l;
    private Long   hintTime     = 1l;

    public ItemNotes() {
    }

    public ItemNotes(String title, String content, Long time, Long hintTime) {
            this.title = title;
            this.content = content;
            this.time = time;
            this.hintTime = hintTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getNoteid() {
        return noteid;
    }

    public Long getHintTime() {
        return hintTime;
    }

    public void setHintTime(Long hintTime) {
        this.hintTime = hintTime;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

}
