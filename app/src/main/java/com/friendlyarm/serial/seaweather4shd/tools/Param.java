package com.friendlyarm.serial.seaweather4shd.tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;

import com.felhr.usbserial.UsbSerialDevice;
import com.friendlyarm.serial.seaweather4shd.Locater;
import com.friendlyarm.serial.seaweather4shd.Msg;
import com.friendlyarm.serial.seaweather4shd.R;
import com.friendlyarm.serial.seaweather4shd.dao.MyDatabaseHelper;

/*import com.friendlyarm.Serial.Locater;
import com.friendlyarm.Serial.Msg;
import com.friendlyarm.Serial.R;
import com.friendlyarm.Serial.dao.MyDatabaseHelper;*/

public class Param {
    public static UsbSerialDevice serialPort;
    public static int BAUD_RATE = 9600;

    public final static String PERF_PASSWORD = "fenghuoa";

    public static boolean T_SWiTCH;

    // 用来在MapFragment的右侧存气象局发来的消息.
    public static List<Msg> MSGList = new ArrayList<Msg>();

    // ----------------参数设置文件中的VALUE-----------------------

    // 用户ID
    public static int mUsrID;

    // 有效期
    public static String mDate;

    public static String mChanel1;
    public static String mChanel2;
    public static String mChanel3;
    public static String mChanel4;
    public static String mChanel5;
    public static String mChanel6;
    public static String mChanel7;
    public static String mChanel8;
    public static String mChanel9;
    public static String mChanel10;

    // 10个信道
    public static List<String> mChannels;

    // 频偏
    public static String mOffSet;

    // 音量大小
    public static int mSounds;

    // 静噪
    public static boolean mSound;
    // 音频带宽
    public static int mSoundExtent;

    // 信噪比
    public static String mSNR;

    // 信道号
    public static String mSNN;

    // 数据传输速率
    public static String mDTR;

    // 配置文件类
    public static Perf perf;

    // 不用map存储天气类型了
    /*
	 * public static final Map<Integer, Integer> weatherTypeMap = new
	 * HashMap<Integer, Integer>(){ { put(1, R.drawable.d00); put(2,
	 * R.drawable.d01); } };
	 */

    public static boolean IsTyphonClear = false;

    // 用数组来存储天气类型,第一个索引手动赋值为0,类型从1开始的,我也是醉了...
    public static final int[] weatherTypeMap = {R.drawable.w0, R.drawable.w1,
            R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5,
            R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9,
            R.drawable.w10, R.drawable.w11, R.drawable.w12, R.drawable.w13,
            R.drawable.w14, R.drawable.w15, R.drawable.w16, R.drawable.w17,
            R.drawable.w18, R.drawable.w19, R.drawable.w20, R.drawable.w21,
            R.drawable.w22, R.drawable.w23, R.drawable.w24, R.drawable.w25,
            R.drawable.w26, R.drawable.w27, R.drawable.w28, R.drawable.w29,
            R.drawable.w30, R.drawable.w31, R.drawable.w32, R.drawable.w33,
            R.drawable.w34, R.drawable.w35, R.drawable.w36, R.drawable.w37,
            R.drawable.w38};

    public static final String[] weatherName = {"空",
            "晴", "多云", "阴天", "小雨", "中雨", "大雨", "暴雨", "大暴雨",
            "特大暴雨", "阵雨", "雷阵雨", "雷阵雨伴有冰雹", "雷电", "冰雹", "冻雨", "霜冻",
            "雨夹雪", "小雪", "中雪", "大雪", "暴雪", "轻雾", "雾", "浓雾",
            "霾", "小雨-中雨", "中雨-大雨", "大雨-暴雨", "暴雨-大暴雨", "大暴雨-特大暴雨", "小雪-中雪", "中雪-大雪",
            "大雪-暴雪", "浮尘", "扬沙", "沙尘暴", "强沙尘暴", "台风"
    };
    // 用数组存储19个海区相对于海图中心点位置的坐标,照例,数组的第一个索引设为0,以便跟对方从1开始的行为对应
	/*
	 * public static final Locater[] seaAreas = { new Locater(0, 0), new
	 * Locater(-166, -179), new Locater(-37, -187), new Locater(102, -188), new
	 * Locater(172, -51), new Locater(15, -128), new Locater(-134, -89), new
	 * Locater(-201, -85), new Locater(-194, 46), new Locater(-38, -46), new
	 * Locater(86, -14), new Locater(136, 53), new Locater(16, 40), new
	 * Locater(-76, 66), new Locater(-135, 111), new Locater(-161, 192), new
	 * Locater(9, 178), new Locater(58, 102), new Locater(132, 187), new
	 * Locater(184, 140) };
	 */
    /*public static final Locater[] seaAreas = {new Locater(0, 0),
            new Locater(-169, -182), new Locater(-40, -190),
            new Locater(99, -191), new Locater(169, -54),
            new Locater(12, -131), new Locater(-137, -92),
            new Locater(-204, -88), new Locater(-197, 43),
            new Locater(-41, -49), new Locater(83, -17), new Locater(133, 50),
            new Locater(13, 37), new Locater(-79, 63), new Locater(-138, 108),
            new Locater(-164, 189), new Locater(6, 175), new Locater(55, 99),
            new Locater(129, 184), new Locater(181, 137)};*/
    public static final Locater[] seaAreas = {
            new Locater(0, 0),
            new Locater(-275,-296),
            new Locater(-64,-309),
            new Locater(164,-310),
            new Locater(279,-86),
            new Locater(22,-212),
            new Locater(-223,-148),
            new Locater(-333,-143),
            new Locater(-321,74),
            new Locater(-65,-78),
            new Locater(139,-26),
            new Locater(221,84),
            new Locater(24,63),
            new Locater(-127,106),
            new Locater(-224,179),
            new Locater(-267,312),
            new Locater(12,289),
            new Locater(93,164),
            new Locater(214,304),
            new Locater(299,227)
    };

    public static Bitmap[] seaAreasWeatherType = new Bitmap[20];

    public static Bitmap[] bitmaps;
    public static LinkedHashMap<Integer, ArrayList<Locater>> typhoonMap = new LinkedHashMap<Integer, ArrayList<Locater>>();
    public static MyDatabaseHelper dbHelper;
    public static SQLiteDatabase db;

    public static final int[] colors = {Color.RED, Color.YELLOW, Color.GREEN,
            Color.CYAN, Color.MAGENTA};

    public static final Path[] typhoonPaths = {new Path(), new Path(),
            new Path(), new Path(), new Path()};

    public static String param = "SDR状态回复错误";

    // 定义ack的初始状态为-2,若为-1:无响应; 若为0:nack, 若为1:ack,每次用完后都应该置为-2值.
    public static int ack = -1;
    public static volatile int SDRAck = -1;
    public static volatile int SoundAck = -1;
    public static volatile int SoundsAck = -1;
    public static volatile int ChannelAck = -1;
    public static volatile int unLinkAck = -1;

    public static volatile int unlinkCount = 0;

    //默认自动拆链时间是60分钟
    public static volatile int unlinkTime = 60;

    public static int tmpDBCount;
    /*public static int tmpDBCount = 0;
    public static int tmpDBCount = 0;
    public static int tmpDBCount = 0;*/
    public static boolean db1 = true;
    public static boolean db2 = true;
    public static boolean db3 = true;

    public static boolean totalFlag = true;

}
