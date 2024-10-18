package com.example.calllogdemo;

public class MyCallLog {
    private String type;
    private String nameOrNumber;
    private String date;

    public MyCallLog(String type, String nameOrNumber, String date) {
        this.type = type;
        this.nameOrNumber = nameOrNumber;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameOrNumber() {
        return nameOrNumber;
    }

    public void setNameOrNumber(String nameOrNumber) {
        this.nameOrNumber = nameOrNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
