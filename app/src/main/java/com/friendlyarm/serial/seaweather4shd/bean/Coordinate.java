package com.friendlyarm.serial.seaweather4shd.bean;

/**
 * Created by zhangxin on 2017/2/20.
 * <p>
 * Description : 用来盛放gps位置坐标;
 */

public class Coordinate {
    double x;
    double y;

    public Coordinate() {
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
