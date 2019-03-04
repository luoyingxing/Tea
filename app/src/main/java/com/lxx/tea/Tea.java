package com.lxx.tea;

import java.io.Serializable;

/**
 * author:  luoyingxing
 * date: 2019/3/4.
 */
public class Tea implements Serializable {
    private int id;
    private String name;
    private String times ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }
}