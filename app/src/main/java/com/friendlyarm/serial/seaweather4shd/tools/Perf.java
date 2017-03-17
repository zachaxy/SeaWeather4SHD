package com.friendlyarm.serial.seaweather4shd.tools;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Perf {
	// 配置文件设置
	static final String PERF = "PARAMSETTING";
	public static SharedPreferences sp;
	public static SharedPreferences sp_area;
	public static SharedPreferences.Editor editor;

	// 配置文件中的key

	// ------------配置文件的KEY----------------------------------
	public static final String P_USERID = "P_USERID";
	public static final String P_DATE = "P_DATE";
	public static final String P_UNLINKTIME = "P_UNLINKTIME";

	// 10个信道的频率
	static final String P_CHANNEL0 = "P_CHANNEL0";
	static final String P_CHANNEL1 = "P_CHANNEL1";
	static final String P_CHANNEL2 = "P_CHANNEL2";
	static final String P_CHANNEL3 = "P_CHANNEL3";
	static final String P_CHANNEL4 = "P_CHANNEL4";
	static final String P_CHANNEL5 = "P_CHANNEL5";
	static final String P_CHANNEL6 = "P_CHANNEL6";
	static final String P_CHANNEL7 = "P_CHANNEL7";
	static final String P_CHANNEL8 = "P_CHANNEL8";
	static final String P_CHANNEL9 = "P_CHANNEL9";

	public static final String[] P_CHANELS = { "P_CHANNEL0", "P_CHANNEL1",
			"P_CHANNEL2", "P_CHANNEL3", "P_CHANNEL4", "P_CHANNEL5",
			"P_CHANNEL6", "P_CHANNEL7", "P_CHANNEL8", "P_CHANNEL9" };

	// 频偏
	public static final String P_OFFSET = "P_OFFSET";

	// 音量大小
	public static final String P_SOUNDS = "P_SOUNDS";

	public static final String P_SOUND = "P_SOUND";
	// 音频带宽
	public static final String P_EXTENT = "P_EXTENT";

	// 信噪比
	static final String P_SNR = "P_SNR";

	// 数传速率
	static final String P_DTR = "P_DTR";
	
	//右边栏列表数据库中的消息的数量
	static final String P_TMPDBCOUNT = "P_TMPDBCOUNT";

	public Perf(Activity mainActivity) {
		sp = mainActivity.getSharedPreferences(PERF, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public Perf(Context context) {
		sp = context.getSharedPreferences(PERF, Context.MODE_PRIVATE);
		editor = sp.edit();
//        sp_area = context.getSharedPreferences("SP_AREA", Context.MODE_PRIVATE);
	}

	public List<String> readChannels() {
		ArrayList<String> channlesList = new ArrayList<String>();
		channlesList.add(sp.getString(P_CHANNEL0, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL1, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL2, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL3, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL4, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL5, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL6, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL7, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL8, "10.0000"));
		channlesList.add(sp.getString(P_CHANNEL9, "10.0000"));

		return channlesList;
	}

	public void writeChannles(List<String> channelsList) {
		editor.putString(P_CHANNEL0, channelsList.get(0));
		editor.putString(P_CHANNEL1, channelsList.get(1));
		editor.putString(P_CHANNEL2, channelsList.get(2));
		editor.putString(P_CHANNEL3, channelsList.get(3));
		editor.putString(P_CHANNEL4, channelsList.get(4));
		editor.putString(P_CHANNEL5, channelsList.get(5));
		editor.putString(P_CHANNEL6, channelsList.get(6));
		editor.putString(P_CHANNEL7, channelsList.get(7));
		editor.putString(P_CHANNEL8, channelsList.get(8));
		editor.putString(P_CHANNEL9, channelsList.get(9));
		editor.commit();
	}

	public void writePrefs(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void writePrefi(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public String readPref(String key, String defaultValue) {
		return sp.getString(key, defaultValue);
	}

	/***
	 * 初始化所有的需要上报的配置变量,这里读取的都是默认值,只在初始化的时候用一次,然后将变量进行上报.
	 * 默认音量:15,默认频偏:180,默认声音:开,默认音频带宽:0,默认10个信道:06.6666,默认用户ID:100;
	 */
	public void readAll() {
		editor.putInt(P_SOUNDS, 18);
		editor.commit();
		editor.putBoolean(P_SOUND, true);
		editor.commit();
		
		Param.mSounds = sp.getInt(P_SOUNDS, 18);
		Param.mOffSet = readPref(P_OFFSET, "180");
		Param.mSound = sp.getBoolean(P_SOUND, true);
		Param.mSoundExtent = sp.getInt(P_EXTENT, 0);
		Param.mChannels = readChannels();
		Param.mUsrID = sp.getInt(P_USERID, 100);
		Param.unlinkTime = sp.getInt(P_UNLINKTIME, 60);

		String tmpData = sp.getString(P_DATE, "000000000000");
		if (!tmpData.equals("000000000000")) {
			tmpData =  SymEncrypt.decrypt(tmpData, Param.PERF_PASSWORD);
		}

		Param.mDate = tmpData;

		Param.tmpDBCount = sp.getInt(P_TMPDBCOUNT, 0);
	}
}
