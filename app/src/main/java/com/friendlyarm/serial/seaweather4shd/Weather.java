package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.List;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.view.ZoomImageView;

public class Weather {

	public int weatherType;
	public List<Character> area = new ArrayList<Character>();
	public int windPower1;
	public int windPower2;
	public int windDire1;
	public int windDire2;
	public String text;
	public Bitmap bitmap;

	public Weather(){};
	
	public Weather(int weatherType, String area, int windPower1,
			int windPower2, int windDire1, int windDire2, String text) {
		super();
		this.weatherType = weatherType;
		this.windPower1 = windPower1;
		this.windPower2 = windPower2;
		this.windDire1 = windDire1;
		this.windDire2 = windDire2;
		// this.text =
		// text+"-"+weatherType+"-"+windPower1+"-"+windPower2+"-"+windDire1+"-"+windDire2;
		this.text = text;
		// area域的处理
		//this.area.add('0');
		if(Param.bitmaps==null){
			initParamBitmaps();
		}
		for (int i = 0; i < area.length(); i++) {
			// this.area.add(area.charAt(area.length()-1-i));
			if (area.charAt(area.length() - 1 - i) == '1') {
				Param.seaAreasWeatherType[i+1] = Param.bitmaps[weatherType];
			}
		}
		this.bitmap = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[this.weatherType]);
	}

	public String buildText() {
		String wp = "";
		String wd = "";
		
		if((windPower1==0)&&(windPower2==0)){
			wp = "";
		}else if(windPower2==windPower1){
			wp = ", 风力" + windPower1+"级";
		}else if(windPower2!=windPower1){
			wp = ", 风力" + windPower1+"到"+windPower2+"级";
		}
		
		
		
		if((windDire1==0)&&(windDire2==0)){
			wd = "";
		}else if(windDire1 == windDire2){
			wd = ", "+getWindDir(windDire1) + "风";
		}else if(windDire1 != windDire2){
			wd = ", "+getWindDir(windDire1) + "风转"+ getWindDir(windDire2) + "风";
		}
		
		
		return "(天气:"+Param.weatherName[weatherType]+wp+wd+")"+text;
	}

	private String getWindDir(int i) {
		String s = "";
		switch (i) {
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

     private void initParamBitmaps(){
    	 
	 Param.bitmaps  = new Bitmap[39];
		Param.bitmaps[0] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[0]);
		Param.bitmaps[1] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[1]);
		Param.bitmaps[2] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[2]);
		Param.bitmaps[3] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[3]);
		Param.bitmaps[4] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[4]);
		Param.bitmaps[5] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[5]);
		Param.bitmaps[6] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[6]);
		Param.bitmaps[7] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[7]);
		Param.bitmaps[8] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[8]);
		Param.bitmaps[9] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[9]);
		Param.bitmaps[10] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[10]);
		Param.bitmaps[11] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[11]);
		Param.bitmaps[12] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[12]);
		Param.bitmaps[13] = BitmapFactory.decodeResource(
				ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[13]);
		Param.bitmaps[14] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[14]);
		Param.bitmaps[15] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[15]);
		Param.bitmaps[16] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[16]);
		Param.bitmaps[17] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[17]);
		Param.bitmaps[18] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[18]);
		Param.bitmaps[19] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[19]);
		Param.bitmaps[20] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[20]);
		Param.bitmaps[21] = BitmapFactory.decodeResource(
			 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[21]);
		Param.bitmaps[22] = BitmapFactory.decodeResource(
		 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[22]);
		Param.bitmaps[23] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[23]);
		Param.bitmaps[24] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[24]);
		Param.bitmaps[25] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[25]);
		Param.bitmaps[26] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[26]);
		Param.bitmaps[27] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[27]);
		Param.bitmaps[28] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[28]);
		Param.bitmaps[29] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[29]);
		Param.bitmaps[30] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[30]);
		Param.bitmaps[31] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[31]);
		Param.bitmaps[32] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[32]);
		Param.bitmaps[33] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[33]);
		Param.bitmaps[34] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[34]);
		Param.bitmaps[35] = BitmapFactory.decodeResource(
				  ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[35]);
		Param.bitmaps[36] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[36]);
		Param.bitmaps[37] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[37]);
		Param.bitmaps[38] = BitmapFactory.decodeResource(
				 ZoomImageView.zoomContext.getResources(),
				Param.weatherTypeMap[38]);
		
		
		for (int i = 0; i < Param.seaAreasWeatherType.length; i++) {
   		 Param.seaAreasWeatherType[i] = Param.bitmaps[0];
		}
     }
	 
}
