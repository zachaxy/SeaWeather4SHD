package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.List;



import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.friendlyarm.serial.seaweather4shd.dao.DBBMsg;
import com.friendlyarm.serial.seaweather4shd.dao.DBBMsgAdapter;
import com.friendlyarm.serial.seaweather4shd.dao.DBMsg;
import com.friendlyarm.serial.seaweather4shd.dao.DBMsgAdapter;
import com.friendlyarm.serial.seaweather4shd.dao.DBWeather;
import com.friendlyarm.serial.seaweather4shd.dao.DBWeatherAdapter;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.view.RefreshableView;

/**
 * Created by zhangxin on 2016/3/22.
 */
public class DBFragment extends Fragment {

	ListView l1;
	ListView l2;
	ListView l3;

	List<DBMsg> list1;
	List<DBBMsg> list2;
	List<DBWeather> list3;

	Button b1;
	Button b2;
	Button b3;

	RelativeLayout rl1, rl2, rl3;

	 RefreshableView rf1, rf2, rf3;

	boolean mOnce1;
	boolean mOnce2;
	boolean mOnce3;

	DBMsgAdapter adapter1;
	DBBMsgAdapter adapter2;
	DBWeatherAdapter adapter3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View dbLayout = inflater.inflate(R.layout.db_layout, container, false);

		l1 = (ListView) dbLayout.findViewById(R.id.db_lv1);
		l2 = (ListView) dbLayout.findViewById(R.id.db_lv2);
		l3 = (ListView) dbLayout.findViewById(R.id.db_lv3);

		list1 = new ArrayList<DBMsg>();
		list2 = new ArrayList<DBBMsg>();
		list3 = new ArrayList<DBWeather>();

		b1 = (Button) dbLayout.findViewById(R.id.db_msg);
		b2 = (Button) dbLayout.findViewById(R.id.db_business_msg);
		b3 = (Button) dbLayout.findViewById(R.id.db_weather);

		rf1 = ( RefreshableView) dbLayout.findViewById(R.id.db_rf1);
		rf2 = ( RefreshableView) dbLayout.findViewById(R.id.db_rf2);
		rf3 = ( RefreshableView) dbLayout.findViewById(R.id.db_rf3);

		rl1 = (RelativeLayout) dbLayout.findViewById(R.id.db_rl1);
		rl2 = (RelativeLayout) dbLayout.findViewById(R.id.db_rl2);
		rl3 = (RelativeLayout) dbLayout.findViewById(R.id.db_rl3);

		adapter1 = new DBMsgAdapter(list1, getActivity());
		l1.setAdapter(adapter1);

		adapter2 = new DBBMsgAdapter(list2, getActivity());
		l2.setAdapter(adapter2);

		adapter3 = new DBWeatherAdapter(list3, getActivity());
		l3.setAdapter(adapter3);
		
		readMsg();

		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearSel();
				b1.setBackgroundColor(Color.parseColor("#00ffe1"));
				rl1.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						readMsg();
					}
				}).start();
			}
		});

		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearSel();
				b2.setBackgroundColor(Color.parseColor("#00ffe1"));
				rl2.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {

					@Override
					public void run() {
						readBMSG();
					}
				}).start();
			}
		});

		b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearSel();
				b3.setBackgroundColor(Color.parseColor("#00ffe1"));
				rl3.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						readWeather();
					}
				}).start();
			}
		});
		
		rf1.setOnRefreshListener(
				new  RefreshableView.PullToRefreshListener() {
					@Override
					public void onRefresh() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						readMsg();
						rf1.finishRefreshing();
					}
				}, 0);
		
		rf2.setOnRefreshListener(
				new  RefreshableView.PullToRefreshListener() {
					@Override
					public void onRefresh() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						readBMSG();
						rf2.finishRefreshing();
					}
				}, 0);
		
		rf3.setOnRefreshListener(
				new  RefreshableView.PullToRefreshListener() {
					@Override
					public void onRefresh() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						readWeather();
						rf3.finishRefreshing();
					}
				}, 0);
		
		return dbLayout;
	}

	private void clearSel() {
		b1.setBackgroundColor(Color.parseColor("#ffffff"));
		b2.setBackgroundColor(Color.parseColor("#ffffff"));
		b3.setBackgroundColor(Color.parseColor("#ffffff"));
		rl1.setVisibility(View.INVISIBLE);
		rl2.setVisibility(View.INVISIBLE);
		rl3.setVisibility(View.INVISIBLE);
	}

	private void selectDB(int index) {
		switch (index) {
		case 1:
			b1.setBackgroundColor(Color.parseColor("#00ffe1"));
			l1.setVisibility(View.VISIBLE);
			break;
		case 2:
			b2.setBackgroundColor(Color.parseColor("#00ffe1"));
			l2.setVisibility(View.VISIBLE);
			break;
		case 3:
			b3.setBackgroundColor(Color.parseColor("#00ffe1"));
			l3.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void readMsg() {
		Cursor cursor = Param.db.query("msg", null, null, null, null, null,
				null);
		if (Param.db1 && cursor.moveToLast()) {
			list1.clear();
			for (int i = 0; i < 1000; i++) {
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String phoneNo = cursor.getString(cursor
						.getColumnIndex("phoneNo"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				list1.add(new DBMsg(time, phoneNo, content));
				if (!cursor.moveToPrevious()) {
					break;
				}
			}
			Param.db1 = false;
			h3.sendEmptyMessage(1);
		}
		cursor.close();
	}

	private void readBMSG() {
		Cursor cursor = Param.db.query("bmsg", null, null, null, null, null,
				null);
		if (Param.db2 && cursor.moveToLast()) {
			list2.clear();
			for (int i = 0; i < 1000; i++) {
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				list2.add(new DBBMsg(time, content));
				if (!cursor.moveToPrevious()) {
					break;
				}
			}
			Param.db2 = false;
			h3.sendEmptyMessage(2);
		}
		cursor.close();

	}

	private void readWeather() {

		Cursor cursor = Param.db.query("weather", null, null, null, null, null,
				null);
		if (Param.db3 && cursor.moveToLast()) {
			/*
			 * do { String time =
			 * cursor.getString(cursor.getColumnIndex("time")); String content =
			 * cursor.getString(cursor .getColumnIndex("content"));
			 * list3.add(new DBWeather(time, content)); } while
			 * (cursor.moveToPrevious());
			 */
			for (int i = 0; i < 1000; i++) {
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String content = cursor.getString(cursor
						.getColumnIndex("content"));
				list3.add(new DBWeather(time, content));
				if (!cursor.moveToPrevious()) {
					break;
				}
			}
			Param.db3 = false;
			h3.sendEmptyMessage(3);
		}
		cursor.close();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		(( MainActivity) activity).setH1(h3);
	}

	public final Handler h3 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter1.notifyDataSetChanged();
				l1.setAdapter(adapter1);
				break;
			case 2:
				adapter2.notifyDataSetChanged();
				l2.setAdapter(adapter2);
				break;
			case 3:
				adapter3.notifyDataSetChanged();
				l3.setAdapter(adapter3);
				break;
			default:
				break;

			}
		}

	};
}
