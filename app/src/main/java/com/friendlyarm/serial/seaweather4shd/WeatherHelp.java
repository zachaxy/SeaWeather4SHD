package com.friendlyarm.serial.seaweather4shd;

/**
 * Created by zhangxin on 2017/3/17.
 * <p>
 * Description :
 */

public class WeatherHelp {
    public int weatherType = 0;  //天气类型;
    public String wind_power = "";
    public String text = "";  //文字消息
    public String time = "1234567890";     //发送时间

    public WeatherHelp(){}

    public WeatherHelp(int weatherType, String wind_power, String text, String time) {
        this.weatherType = weatherType;
        this.wind_power = wind_power;
        this.text = text;
        this.time = time;
    }
}
