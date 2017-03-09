package com.friendlyarm.serial.seaweather4shd;

/**
 * Created by zhangxin on 2017/3/9.
 * <p>
 * Description :
 * 针对Locater的改造,这里讲坐标改为double类型,用于GPS定位时
 */

public class Locator2 {
    public double x;
    public double y;




    public Locator2(double x, double y) {
        this.x = x;
        this.y = y;
    }


}
