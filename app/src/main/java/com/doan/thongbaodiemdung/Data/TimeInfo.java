package com.doan.thongbaodiemdung.Data;

import java.io.Serializable;

/**
 * Created by Asus on 01-01-18.
 */

public class TimeInfo implements Serializable {
    private int id;
    private int hour;
    private int minute;
    private String note;

    public int getId() {
        return id;
    }

    public TimeInfo setId(int id) {
        this.id = id;
        return this;
    }

    public int getHour() {
        return hour;
    }

    public TimeInfo setHour(int h) {
        this.hour = h;
        return this;
    }

    public int getMinute() {
        return minute;
    }

    public TimeInfo setMinute(int m) {
        this.minute = m;
        return this;
    }

    public String getNote() {
        return note;
    }

    public TimeInfo setNote(String n) {
        this.note = n;
        return this;
    }
}
