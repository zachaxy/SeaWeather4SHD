package com.friendlyarm.serial.seaweather4shd.tools;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.friendlyarm.serial.seaweather4shd.Locater;
import com.friendlyarm.serial.seaweather4shd.bean.SeaArea;

/***
 * 辅助工具类
 *
 * @author zhangxin
 */

public class Tools {
    public static void prompt(Activity a, String s) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(a);
        dialog.setTitle("错误的参数设置");
        dialog.setMessage(s);
        dialog.setPositiveButton("确定", null);
        dialog.create();
        dialog.show();
    }

    /***
     * 坐标点转换函数,将像素位置转为中心点相对位置
     * 这个4677.0是zhangxiaoxi电脑上图片的坐标,发来的坐标也是相对这个的大小,所以将这个弄到Params中
     *
     * @param l 收到的像素位置
     * @return 中心点相对位置  y/2725.0*444.0-222.0-->x*727/4677-363
     */
    public static Locater transferLocate(Locater l) {
        Locater l2 = new Locater();
        /*l2.x = (int) (l.x * 727 / 4677.0 - 363.0);
        l2.y = (int) (l.y * 727 / 4677.0 - 363.0);*/
        l2.x = (int) (l.x * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
        l2.y = (int) (l.y * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
        return l2;
    }

    /***
     * 针对单个点的坐标
     *
     * @param l
     * @return
     */
    public static int transferLocate(int l) {
        return (int) (l * Param.ACTUAL_IMAGE_SIZE / Param.ORIGINAL_IMAGE_SIZE - Param.ACTUAL_IMAGE_SIZE / 2);
    }

    /*//是上一个方法的逆运算;相对中心点坐标转未缩放view坐标
    public static int transferLocate2Origin(float x) {
        return (int) ((x + Param.ACTUAL_IMAGE_SIZE / 2) * Param.ORIGINAL_IMAGE_SIZE / Param.ACTUAL_IMAGE_SIZE);
    }*/

    public static String transferReplace(String src, String oldChar,
                                         String newChar) {
        StringBuilder src0 = new StringBuilder();
        src0.append(src);
        return transferReplace(src0, oldChar, newChar);
    }

    public static String transferReplace(StringBuilder src, String oldChar,
                                         String newChar) {
        int index = src.indexOf(oldChar);
        while (index >= 0) {
            if (index % 2 == 0) {
                src = src.replace(index, index + 4, newChar);
            }
            index = src.indexOf(oldChar, index + 2);
        }
        return src.toString();
    }

    public static int findSilpBagHead(String s) {
        int begin = s.indexOf("c0");
        while (begin > 0 && begin % 2 != 0) {
            begin = s.indexOf("c0", begin + 2);
        }
        if (begin % 2 == 0 && s.length() - begin >= 2) {
            if (s.charAt(begin + 2) == 'c' && s.charAt(begin + 3) == '0') {
                begin = begin + 2;
            }
        }
        return begin;
    }

    public static int findSilpBagTail(int begin, String s) {
        int tail = s.indexOf("c0", begin + 2);
        while (tail > 0 && tail % 2 != 0) {
            tail = s.indexOf("c0", tail + 2);
        }
        return tail;
    }

    public static String str2time(String str) {

        SimpleDateFormat formatter1 = new SimpleDateFormat("yy-HH-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyHHddHHmmss");
        try {
            str = formatter1.format(formatter2.parse(str));
        } catch (ParseException e) {
            Log.e("timeparse_error", str);
            e.printStackTrace();
        }
        return str;
    }

    //针对gps和点击选区域;
    public static final double ORIGINAL_X = 98.7379;
    public static final double ORIGINAL_Y = 41.23107;
    public static final double ORIGINAL_INTERVAL = 38.9768;

    public static final List<SeaArea> list = new ArrayList<>();

    static {
//        list.add();
    }

    //这样gps位置也可以调用这个方法,实现在图片中的
    public static Locater getLoationInView(double x, double y) {
        int xi = (int) Math.round((x - ORIGINAL_X) / ORIGINAL_INTERVAL);
        int yi = (int) Math.round((ORIGINAL_Y - y) / ORIGINAL_INTERVAL);

        return new Locater(xi, yi);
    }

    public static boolean pInQuadrangle(Locater a, Locater b, Locater c, Locater d, Locater p) {
        double dTriangle = triangleArea(a, b, p) + triangleArea(b, c, p)
                + triangleArea(c, d, p) + triangleArea(d, a, p);
        double dQuadrangle = triangleArea(a, b, c) + triangleArea(c, d, a);
        return dTriangle == dQuadrangle;
    }


    /***
     * 查看p是否在区域中;
     *
     * @param seaArea 遍历18个海区;
     * @param p       当前坐标p,并不是相对中心点的,而是相对于移动端原始view的大小;
     * @return
     */
    public static boolean pInQuadrangle(SeaArea seaArea, Locater p) {
        double dTriangle = -1;
        if (seaArea.size == 4) {
            dTriangle = triangleArea(seaArea.a, seaArea.b, p)
                    + triangleArea(seaArea.b, seaArea.c, p)
                    + triangleArea(seaArea.c, seaArea.d, p)
                    + triangleArea(seaArea.d, seaArea.a, p);
        } else if (seaArea.size == 5) {
            dTriangle = triangleArea(seaArea.a, seaArea.b, p)
                    + triangleArea(seaArea.b, seaArea.c, p)
                    + triangleArea(seaArea.c, seaArea.d, p)
                    + triangleArea(seaArea.d, seaArea.e, p)
                    + triangleArea(seaArea.e, seaArea.a, p);
        }
        return dTriangle == seaArea.area;
    }

    // 返回三个点组成三角形的面积,既然面积这么算最多也就是个double,那么直接传入整数吧;
    //全部转为整数,所得的面积最多是xxx.5,可以用==精确比对,缺点是:存在各个海区的边界值误差
    private static double triangleArea(Locater a, Locater b, Locater c) {
        double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
                - c.x * b.y - a.x * c.y) / 2.0);
        return result;
    }

    //计算四边形面积
    private static double triangleArea(Locater a, Locater b, Locater c, Locater d) {
        return triangleArea(a, b, c) + triangleArea(c, d, a);
    }

    //计算五边形面积
    private static double triangleArea(Locater a, Locater b, Locater c, Locater d, Locater e) {
        return triangleArea(a, b, c) + triangleArea(a, c, d) + triangleArea(a, d, e);
    }


}
