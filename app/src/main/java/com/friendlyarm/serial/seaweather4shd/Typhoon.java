package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.List;

public class Typhoon {
	public int typhoonNo;
	public Locater locater;
	public List<Locater> list = new ArrayList<Locater>();
	public Typhoon(int typhoonNo, List<Locater> list) {
		this.typhoonNo = typhoonNo;
		this.list = list;
	}
	
	public int windPower1;
	public int windPower2;
	public int windDire1;
	public int windDire2;
	public String content;
	
	/*public Typhoon(int typhoonNo, Locater locater,String content) {
		this.typhoonNo = typhoonNo;
		this.locater = locater;
		this.content = content;
	}*/

	public Typhoon(int typhoonNo, Locater locater, int windPower1,
			int windPower2, int windDire1, int windDire2, String content) {
		this.typhoonNo = typhoonNo;
		this.locater = locater;
		this.windPower1 = windPower1;
		this.windPower2 = windPower2;
		this.windDire1 = windDire1;
		this.windDire2 = windDire2;
		this.content = content;
	}
	
	
	
	
	public String buildText(){
		return "(风力"+windPower1+"到"+windPower2+"级,"+getWindDir(windDire1)+"风转"+getWindDir(windDire2)+"风)"+content;
	}
	
	private String getWindDir(int i){
		String s = "";
		switch(i){
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
