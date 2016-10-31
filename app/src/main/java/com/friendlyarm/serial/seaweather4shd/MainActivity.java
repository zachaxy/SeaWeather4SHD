package com.friendlyarm.serial.seaweather4shd;



/*import com.friendlyarm.Serial.dao.DBMsg;
import com.friendlyarm.Serial.dao.MyDatabaseHelper;
import com.friendlyarm.Serial.tools.Param;
import com.friendlyarm.Serial.tools.SysApplication;*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.friendlyarm.serial.seaweather4shd.dao.MyDatabaseHelper;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.SysApplication;

public class MainActivity extends Activity implements View.OnClickListener {

	/***
	 * 定义处理后台消息的两个handler h1用来处理天气信息 h2用来处理参数信息
	 */
	private Handler h1, h2,h3;

	public void setH1(Handler h) {
		h1 = h;
	}

	public void setH2(Handler h) {
		h2 = h;
	}
	
	public void setH3(Handler h) {
		h3 = h;
	}

	/*
	 * 用于展示海图的Fragment
	 */
	private MapFragment mapFragment;

	/*
	 * 用于展示设置的Fragment
	 */
	private SettingFragment settingFragment;

	/*
	 * 用于展示报警数据的Fragment
	 */
	private DBFragment dbFragment;

	/*
	 * 在侧边栏中地图选项所在的区域
	 */
	private View mapLayout;

	/*
	 * 在侧边栏中参数选项所在的区域
	 */
	private View settingLayout;

	/*
	 * 在侧边栏中历史记录选项所占的区域
	 */
	private View dbLayout;

	/*
	 * 在tab界面上显示海图图标的控件,主要用于选中后的颜色变换
	 */
	private ImageView mapImage;

	/*
	 * 在tab界面上显示设置图标的控件,主要用于选中后的颜色变换
	 */
	private ImageView settingImage;

	/*
	 * 在tab界面上显示数据查询图标的控件,主要用于选中后的颜色变换
	 */
	private ImageView dbImage;

	/*
	 * 用于对fragment的管理
	 */
	private FragmentManager fragmentManager;

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setTitle("测试软件V1.0");
		setContentView(R.layout.activity_main);
		SysApplication.getInstance().addActivity(this);
		initViews();
		//Param.MSGList
		
		/*openComs();
		initConf();*/
		initDB();
		 Cursor cursor = Param.db.query("tmpMSG",null,null,null,null,null,null);
         if(cursor.moveToLast()){
             do {
            	 if(Param.MSGList.size()==20){
            		 break;
            	 }
            	 int img = cursor.getInt(cursor.getColumnIndex("img"));
                 String time = cursor.getString(cursor.getColumnIndex("time"));
                 Log.d("setting收集数据库",time+"目前次数"+Param.MSGList.size());
                 String content = cursor.getString(cursor.getColumnIndex("msg"));
                 Param.MSGList.add(new Msg(img,content,time));
             }while (cursor.moveToPrevious());
         }
         cursor.close();
 
		 
		
		//获取fragment管理器
		fragmentManager = getFragmentManager();
		setTabSelection(0);
		
	}

	/**
	 * 初始化界面布局
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		mapLayout = findViewById(R.id.map_layout);
		settingLayout = findViewById(R.id.setting_layout);
		dbLayout = findViewById(R.id.db_layout);

		mapImage = (ImageView) findViewById(R.id.map_image);
		settingImage = (ImageView) findViewById(R.id.setting_image);
		dbImage = (ImageView) findViewById(R.id.db_image);

		mapLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		dbLayout.setOnClickListener(this);
	}

	/***
	 * 根据传入的index参数来设置选中的tab页
	 * @param index
	 *            每个tab页对应的下标。0表示海图浏览，1表示参数设置，2表示历史消息查看
	 */
	private void setTabSelection(int index) {
		
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			mapImage.setImageResource(R.drawable.map_select);
			if (mapFragment == null) {
				mapFragment = new MapFragment();
				transaction.add(R.id.content, mapFragment);
			} else {
				transaction.show(mapFragment);
			}
			break;
		case 1:
			settingImage.setImageResource(R.drawable.admin_select);
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
				transaction.add(R.id.content, settingFragment);
			} else {
				transaction.show(settingFragment);
			}
			break;
		case 2:
			dbImage.setImageResource(R.drawable.db_select);
			if (dbFragment == null) {
				dbFragment = new DBFragment();
				transaction.add(R.id.content, dbFragment);
			} else {
				transaction.show(dbFragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}

	/***
     * 清除tab状态码
     */
	private void clearSelection() {
		mapImage.setImageResource(R.drawable.map_unselect);
		settingImage.setImageResource(R.drawable.admin_unselect);
		dbImage.setImageResource(R.drawable.db_unselect);
	}

	/***
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (mapFragment != null) {
			transaction.hide(mapFragment);
		}
		if (settingFragment != null) {
			transaction.hide(settingFragment);
		}
		if (dbFragment != null) {
			transaction.hide(dbFragment);
		}
	}

	//点击侧边栏不同的tab选项,切换到不同的fragment.
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_layout:
			setTabSelection(0);
			break;
		case R.id.setting_layout:
			setTabSelection(1);
			break;
		case R.id.db_layout:
			setTabSelection(2);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle("退出程序")
		.setMessage("是否退出程序?\n退出程序后将无法获取服务!")
		.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Param.totalFlag = false;
				SysApplication.getInstance().exit();
				
			}
		})
		.setNegativeButton("取消", null);
		dialog.setCancelable(false);
		dialog.create();
		dialog.show();
	}
	
	 
	
	/***
	 * 打开所需的两个串口
	 */
//	private void openComs(){
//		Serial.openCom1();
//		Serial.openCom3();
//	}
	
	
	/***
	 * 初始化配置文件,以收集参数信息
	 */
//	private void initConf(){
//		Param.perf = new Perf(MainActivity.this);
//		Param.perf.readAll();
//	}
	
	/***
	 * 初始化数据库
	 */
	private void initDB(){
		Param.dbHelper = new MyDatabaseHelper(this,"SeaMSG.db",null,1);
		Param.db = Param.dbHelper.getWritableDatabase();
	}
	
//	private void recycleLink(){
//		
//	}
	
	
}
