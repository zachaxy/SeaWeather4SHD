package com.friendlyarm.serial.seaweather4shd.dao;


import com.friendlyarm.serial.seaweather4shd.tools.Tools;

//所有数据库展示父类,包含时间和内容两项.
public class DBTable {
	public String time;
    public String content;
    
	public DBTable(String time, String content) {
		this.time = Tools.str2time(time);
		this.content = content;
	}
    
    
}
