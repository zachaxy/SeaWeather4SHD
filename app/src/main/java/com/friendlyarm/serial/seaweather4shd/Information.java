package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Information {

	public String timeStamp;// 时间戳
	public List<String> list = new ArrayList<String>();
	public boolean flag = false; //表示位，00000000；末尾表示第一帧，如果crc正确，置位1；
	public int n = 0; // 表示接受到的次数，如果达到三次，停止接受，并将HashMap中的相应key删掉
	public int count; // 消息的帧数。
	public volatile long start;
	public byte[] bflag;
	public volatile int waitSeconds = 30;

	public Information(String timeStamp, int count) {
		this.timeStamp = timeStamp;
		this.count = count;
		initFlag();
		initList();
	}

	public void initFlag() {
		bflag = new byte[count];
		Arrays.fill(bflag, (byte) 48); // 二进制0
	}
	
	public void setFlag(int i){
		bflag[i] = '1';
		if(!new String(bflag).contains("0")){
			flag = true;
		}
	}

	public void initList() {
		for (int i = 0; i < count; i++) {
			// 用6个*来初始化未接到的消息。
			list.add("******");
		}
	}
}