package com.friendlyarm.serial.seaweather4shd;

public class Locater {
	public int x;
	public int y;
	public String text = "";
	
	
	public Locater(){}
	
	public Locater(int x, int y, String text) {
		this.x = x;
		this.y = y;
		this.text = text;
	}
	
	public Locater(int x, int y) {
		this.x = x;
		this.y = y;

	}
	
	
}
