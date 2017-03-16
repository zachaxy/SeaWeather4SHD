package com.friendlyarm.serial.seaweather4shd.bean;

import com.friendlyarm.serial.seaweather4shd.Locater;

/**
 * Created by zhangxin on 2017/2/20.
 * <p>
 * Description :
 */

public class SeaArea {
    public int size;
    public Locater a;
    public Locater b;
    public Locater c;
    public Locater d;
    public Locater e;


    //别提供get方法了,直接计算好算了;
    public double area = 0;

    public SeaArea(Locater a, Locater b, Locater c, Locater d, double area) {
        size = 4;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.area = area;
    }

    public SeaArea(Locater a, Locater b, Locater c, Locater d,Locater e, double area) {
        size = 5;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.area = area;
    }

    public SeaArea(){}
}
