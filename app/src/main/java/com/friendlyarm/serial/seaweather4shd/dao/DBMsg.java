package com.friendlyarm.serial.seaweather4shd.dao;




/**
 * Created by zhangxin on 2016/4/26.
 */
public class DBMsg extends  DBTable {
    public String phoneNo;
   /* public String time;
    public String content;*/

    public DBMsg(String time, String phoneNo, String content) {
        /*this.time = time;
        this.phoneNo = phoneNo;
        this.content = content;*/
    	super(time,content);
    	this.phoneNo = phoneNo;
    }
}
