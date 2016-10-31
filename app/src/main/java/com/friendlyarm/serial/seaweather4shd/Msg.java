package com.friendlyarm.serial.seaweather4shd;


import com.friendlyarm.serial.seaweather4shd.tools.BytesUtil;

//定义信息的属性:图像,内容,时间
public class Msg {
	public boolean isRead = false;
	public int mMsgImg;
	public String mMsgContent;
	public String mMsgTime;

	public Msg(int mMsgImg, String mMsgContent, String mMsgTime) {
		super();
		this.mMsgImg = mMsgImg;
		this.mMsgContent = mMsgContent;
		this.mMsgTime = mMsgTime;
	}
	
	public String showMsg(){
		return mMsgContent+"\n"+"接收时间是: "+ BytesUtil.formatTime(mMsgTime.toCharArray());
	}
}
