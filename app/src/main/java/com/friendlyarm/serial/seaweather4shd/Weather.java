package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.view.ZoomImageView;

public class Weather {

    public int weatherType;
    public List<Character> area = new ArrayList<Character>();
    public int windPower1;
    public int windPower2;
    public int windDire1;
    public int windDire2;
    public String text;
    public Bitmap bitmap;
    public String time;

    public Weather() {
    }

    public Weather(int weatherType, String area,
                   int windPower1, int windPower2,
                   int windDire1, int windDire2,
                   String text, String time) {
        this.weatherType = weatherType;
        this.windPower1 = windPower1;
        this.windPower2 = windPower2;
        this.windDire1 = windDire1;
        this.windDire2 = windDire2;
        // this.text =
        // text+"-"+weatherType+"-"+windPower1+"-"+windPower2+"-"+windDire1+"-"+windDire2;
        this.text = text;
        this.time = time;
        // area域的处理
        //this.area.add('0');

        for (int i = 0; i < area.length(); i++) {
            // this.area.add(area.charAt(area.length()-1-i));
            if (area.charAt(area.length() - 1 - i) == '1') {
                Param.seaAreasWeatherType[i + 1] = Param.bitmaps[weatherType];
                Param.weaherDetail[i + 1].weatherType = weatherType;
                Param.weaherDetail[i + 1].wind_power = buildTextForDetailWithWrap();
                Param.weaherDetail[i + 1].text = text;
                Param.weaherDetail[i + 1].time = time;
            }
        }
        this.bitmap = BitmapFactory.decodeResource(
                ZoomImageView.zoomContext.getResources(),
                Param.weatherTypeMap[this.weatherType]);

    }

    public Weather(int weatherType, String area,
                   int windPower1, int windPower2,
                   int windDire1, int windDire2,
                   String text) {
       /* this.weatherType = weatherType;
        this.windPower1 = windPower1;
        this.windPower2 = windPower2;
        this.windDire1 = windDire1;
        this.windDire2 = windDire2;
        // this.text =
        // text+"-"+weatherType+"-"+windPower1+"-"+windPower2+"-"+windDire1+"-"+windDire2;
        this.text = text;
        // area域的处理
        //this.area.add('0');
        if (Param.bitmaps == null) {
            initParamBitmaps();
        }
        for (int i = 0; i < area.length(); i++) {
            // this.area.add(area.charAt(area.length()-1-i));
            if (area.charAt(area.length() - 1 - i) == '1') {
                Param.seaAreasWeatherType[i + 1] = Param.bitmaps[weatherType];
                Param.weaherDetail[i + 1].weatherType = weatherType;
                Param.weaherDetail[i + 1].wind_power =  ;
                Param.weaherDetail[i + 1].text = weatherType;
                Param.weaherDetail[i + 1].time = weatherType;
            }
        }
        this.bitmap = BitmapFactory.decodeResource(
                ZoomImageView.zoomContext.getResources(),
                Param.weatherTypeMap[this.weatherType]);*/
    }

    public String buildText() {
        String wd = "";
        String wp = "";



        if ((windDire1 == 0) && (windDire2 == 0)) {
            wd = "";
        } else if (windDire1 == windDire2) {
            wd = ", " + getWindDir(windDire1) + "风";
        } else if (windDire1 != windDire2) {
            wd = ", " + getWindDir(windDire1) + "风转" + getWindDir(windDire2) + "风";
        }



        if ((windPower1 == 0) && (windPower2 == 0)) {
            wp = "";
        } else if (windPower2 == windPower1) {
            wp = ", 风力" + windPower1 + "级";
        } else if (windPower2 != windPower1) {
            wp = ", 风力" + windPower1 + "到" + windPower2 + "级";
        }


        return "(天气:" + Param.weatherName[weatherType] + wd + wp + ")" + text;
    }


    public String buildTextForDetailWithWrap() {
        String wd = "";
        String wp = "";


        if ((windDire1 == 0) && (windDire2 == 0)) {
            wd = "";
        } else if (windDire1 == windDire2) {
            wd = " " + getWindDir(windDire1) + "风";
        } else if (windDire1 != windDire2) {
            wd = " " + getWindDir(windDire1) + "风转" + getWindDir(windDire2) + "风";
        }


        if ((windPower1 == 0) && (windPower2 == 0)) {
            wp = "";
        } else if (windPower2 == windPower1) {
            wp = ", 风力" + windPower1 + "级";
        } else if (windPower2 != windPower1) {
            wp = ", 风力" + windPower1 + "到" + windPower2 + "级";
        }




        return wd + wp;
    }


    private String getWindDir(int i) {
        String s = "";
        switch (i) {
            case 1:
                s = "东";
                break;
            case 2:
                s = "南";
                break;
            case 3:
                s = "西";
                break;
            case 4:
                s = "北";
                break;
            case 5:
                s = "东南";
                break;
            case 6:
                s = "东北";
                break;
            case 7:
                s = "西南";
                break;
            case 8:
                s = "西北";
                break;
            default:
                break;
        }

        return s;
    }
}
