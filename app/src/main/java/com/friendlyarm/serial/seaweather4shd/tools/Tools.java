package com.friendlyarm.serial.seaweather4shd.tools;


import java.text.ParseException;
import java.text.SimpleDateFormat;


import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.friendlyarm.serial.seaweather4shd.Locater;

/***
 * 辅助工具类
 * 
 * @author zhangxin
 * 
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
	 * 
	 * @param l
	 *            收到的像素位置
	 * @return 中心点相对位置
	 */
	public static  Locater transferLocate(Locater l) {
		 Locater l2 = new  Locater();
		l2.x = (int) (l.x / 2725.0 * 444.0 - 222.0);
		l2.y = (int) (l.y / 2725.0 * 444.0 - 222.0);
		return l2;
	}

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
            if(s.charAt(begin+2)=='c'&&s.charAt(begin+3)=='0'){
                begin = begin+2;
            }
        }
        return begin;
    }

    public static int findSilpBagTail(int begin, String s) {
        int tail = s.indexOf("c0", begin+2);
        while (tail > 0 && tail % 2 != 0) {
            tail = s.indexOf("c0", tail + 2);
        }
        return tail;
    }
    
    public static String str2time(String str){
    	
    	SimpleDateFormat formatter1=new SimpleDateFormat("yy-HH-dd HH:mm:ss");
        SimpleDateFormat formatter2=new SimpleDateFormat("yyHHddHHmmss");
        try {
			str =  formatter1.format(formatter2.parse(str));
		} catch (ParseException e) {
			Log.e("timeparse_error",str);
			e.printStackTrace();
		}
        return str;
    }

}
