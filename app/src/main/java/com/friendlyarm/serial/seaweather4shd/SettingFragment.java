package com.friendlyarm.serial.seaweather4shd;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*import com.friendlyarm.Serial.tools.Param;
import com.friendlyarm.Serial.tools.Perf;
import com.friendlyarm.Serial.tools.Protocol;*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Perf;
import com.friendlyarm.serial.seaweather4shd.tools.Protocol;

/**
 * Created by zhangxin on 2016/3/22.
 */
public class SettingFragment extends Fragment {

	

	// 管理员密钥
	static final String ADMIN_PASSWORD = "123456";

	// --------布局中的组件------------
	EditText userID;
	Button userIDSet;
	TextView date;

	// 频道的设置
	ListView channels;
	Button channels_w;
	List<String> channelsList;
	ChannelAdapter channelsAdapter;

	// 频偏的设置
	EditText offset;
	ImageButton offset_plus, offset_minus;
	Button offset_w;

	EditText autoUnlink;
	Button autoUnlinkSend;

	// 管理员锁
	ImageButton state_img;

	// -------------------------------------------------------------------------

	// 判断参数设置是否锁定,锁定则不可以修改参数
	boolean isLocked;

	// 用于加载自定义对话框
	LayoutInflater factory;

	// 所依附的Activity,这里只有一个!
	Activity mainActivity;

	 

	// 标记串口是否已经打开
	Boolean openFlag = false;

	// 当前view
	View SettingLayout;

	List<String> chanelIndexList;
	
	String tmpChannel = "";

	Timer t4;
	
	boolean leftCheck = true;
	boolean rightCheck = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);
		SettingLayout = settingLayout;
		init_widget(settingLayout);
		init_Adapter();
		unableEdit();
		bindListener();

		return settingLayout;
	}

	void init_widget(View view) {
		mainActivity = getActivity();
		isLocked = true;

		userID = (EditText) view.findViewById(R.id.usrID);
		userID.setText(String.valueOf(Param.mUsrID));
		userIDSet = (Button) view.findViewById(R.id.uidSet);

		date = (TextView) view.findViewById(R.id.date);

		channels = (ListView) view.findViewById(R.id.channels);
		//channels_w = (Button) view.findViewById(R.id.channels_w);

		offset = (EditText) view.findViewById(R.id.offset);
		offset.setText(Param.mOffSet);
		offset_plus = (ImageButton) view.findViewById(R.id.offset_plus);
		offset_minus = (ImageButton) view.findViewById(R.id.offset_minus);
		offset_w = (Button) view.findViewById(R.id.offset_w);

		autoUnlink = (EditText) view.findViewById(R.id.unlinkTime);
		autoUnlink.setText(String.valueOf(Param.unlinkTime));
		autoUnlinkSend = (Button)view.findViewById(R.id.unLinkSet);
		
		state_img = (ImageButton) view.findViewById(R.id.state_img);

		// LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
		factory = LayoutInflater.from(mainActivity);

		chanelIndexList = new ArrayList<String>();
		chanelIndexList.add("信道0: ");
		chanelIndexList.add("信道1: ");
		chanelIndexList.add("信道2: ");
		chanelIndexList.add("信道3: ");
		chanelIndexList.add("信道4: ");
		chanelIndexList.add("信道5: ");
		chanelIndexList.add("信道6: ");
		chanelIndexList.add("信道7: ");
		chanelIndexList.add("信道8: ");
		chanelIndexList.add("信道9: ");
	}

	void init_Adapter() {

		channelsList = Param.mChannels;
		channelsAdapter = new ChannelAdapter(mainActivity, chanelIndexList,
				channelsList);
		channels.setAdapter(channelsAdapter);

		// 屏蔽底层srollView的触摸事件,解决与listView的滑动冲突.
		channels.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});

		//每个信道的点击事件,用于配置信道频率
		channels.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// 如果显示是锁定状态,也可以滑动,但是点击无效.
				if (!isLocked) {
					
					final View channelInput = factory.inflate(
							R.layout.channel_config, null);
					
					final TextView warm = (TextView) channelInput
							.findViewById(R.id.channel_warning);

					final EditText channelLeft = (EditText) channelInput
							.findViewById(R.id.channel_left);
					channelLeft.setText(channelsList.get(arg2).substring(0, 2));
					
					final EditText channelRight = (EditText) channelInput
							.findViewById(R.id.channel_right);
					channelRight
							.setText(channelsList.get(arg2).substring(3, 7));
					//将左右两个check的初始值设置为true,以防止不修改直接提交不成功的bug
					leftCheck = true;
					rightCheck = true;
					
					channelLeft.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							if(s.toString().length()<1||s.toString().length()>2){
								warm.setText("* 信道的整数部分范围在2~29之间,请重新输入");
								warm.setTextColor(Color.RED);
								leftCheck = false;
							}
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void afterTextChanged(Editable s) {

							if(s.toString().length()<1||s.toString().length()>2){
								warm.setText("* 信道的整数部分范围在2~29之间,请重新输入");
								warm.setTextColor(Color.RED);
								leftCheck = false;
							}
							/*else{
								int left = Integer.valueOf(s.toString());
								if (left >= 30 || left < 2) {
									warm.setText("* 信道的整数部分范围在2.0000~29.9999之间,请重新输入");
									warm.setTextColor(Color.RED);
									leftCheck = false;
								} else {
									warm.setText("合法的输入");
									warm.setTextColor(Color.GREEN);
									leftCheck = true;
								}
							}*/
							String sValue = channelLeft.getText().toString()+"."+channelRight.getText().toString();
							double dValue = 0;
							try{
								dValue = Double.valueOf(sValue);
							}catch(NumberFormatException e){
								warm.setText("* 非法的字符输入,请重新输入");
								warm.setTextColor(Color.RED);
								leftCheck = false;
							}
							
							if(dValue<2.0||dValue>29.9999){
								warm.setText("* 信道的整数部分范围在2.0000~29.9999之间,请重新输入");
								warm.setTextColor(Color.RED);
								leftCheck = false;
							} else {
								warm.setText("合法的输入");
								warm.setTextColor(Color.GREEN);
								leftCheck = true;
							}
						}
					});

					channelRight.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							if (s.toString().length() < 1
									|| s.toString().length() > 4) {
								warm.setText("* 信道的小数部分范围在0~9999之间,请重新输入");
								warm.setTextColor(Color.RED);
								rightCheck = false;
							}
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {

						}

						@Override
						public void afterTextChanged(Editable s) {
							/*if(channelLeft.getText().equals("1")){
								warm.setText("* 信道的范围在2.0000~29.9999之间,请重新输入");
								warm.setTextColor(Color.RED);
								rightCheck = false;
							}*/
							if (s.toString().length() < 1
									|| s.toString().length() > 4) {
								warm.setText("* 信道的小数部分范围在0~9999之间,请重新输入");
								warm.setTextColor(Color.RED);
								rightCheck = false;
							}
							/* else {
								int i = Integer.valueOf(s.toString());
								if (i < 0 || i > 9999) {
									warm.setText("* 信道的小数部分范围在0~9999之间,请重新输入");
									warm.setTextColor(Color.RED);
									rightCheck = false;
								} else {
									warm.setText("合法的输入");
									warm.setTextColor(Color.GREEN);
									rightCheck = true;
								}
							}*/

							String sValue = channelLeft.getText().toString()+"."+channelRight.getText().toString();
							double dValue = 0;
							try{
								dValue = Double.valueOf(sValue);
							}catch(NumberFormatException e){
								warm.setText("* 非法的字符输入,请重新输入");
								warm.setTextColor(Color.RED);
								rightCheck = false;
							}
							
							if(dValue<2.0||dValue>29.9999){
								warm.setText("* 信道的整数部分范围在2.0000~29.9999之间,请重新输入");
								warm.setTextColor(Color.RED);
								rightCheck = false;
							} else {
								warm.setText("合法的输入");
								warm.setTextColor(Color.GREEN);
								rightCheck = true;
							}
							
						}
					});
					
					ViewGroup p = (ViewGroup) channelInput.getParent();
					if (p != null) {
						p.removeView(channelInput);
					}

					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getActivity());
					dialog.setTitle("频率设置");
					dialog.setView(channelInput);
					dialog.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 检测输入的频率值是否合法,不合法应给出提示
									// TODO: 先跳过检测,写死为12.0000;后期进行EditText动态检测
									/*tmpChannel = checkChannel(channelLeft
											.getText().toString(), channelRight
											.getText().toString());
									if (tmpChannel == null) {
										return;
									}*/
									//String s = "";
									if (leftCheck && rightCheck){
										tmpChannel = formatChannel(channelLeft.getText()
												.toString(), channelRight
												.getText().toString());
										
										Log.d("setting","发送的信道号是:"+tmpChannel);
										Param.param = "信道"+arg2;
										//Param.ack = -1;
										Param.ChannelAck = -2;
										//tmpChannel = "12.0000";
										t4 = new Timer();
										t4.schedule(new TimerTask() {
											int count = 0;
											@Override
											public void run() {
												if(count>0){
													if(Param.ChannelAck==1){
														Log.d("setting",
																"信道设置响应ack");
														channelsList.set(arg2, tmpChannel);
														Perf.editor.putString(Perf.P_CHANELS[arg2], tmpChannel);
														Perf.editor.commit();
														h2.sendEmptyMessage(1);
														t4.cancel();
														count = -1;
													}else if(Param.ChannelAck==0){
														Log.d("setting",
																"信道设置响应nack");
														h2.sendEmptyMessage(2);
														t4.cancel();
														count = -1;
													}
												}
												
												
												if(count==3){
													Log.d("setting",
															"信道设置无响应");
													h2.sendEmptyMessage(3);
													t4.cancel();
													Param.ChannelAck = -1;
													count = -1;
												}
												
												if(count != -1){
													Protocol.sendChannels(tmpChannel,arg2);
													count++;
												}
											}
										}, 0,800);
									}
								}

							});
					dialog.setNegativeButton("取消", null);
					dialog.setCancelable(false);
					dialog.create();
					dialog.show();
				}

			}
		});
		
		

	}
	
	private String formatChannel(String left, String right) {
		if (left.length() == 1) {
			left = "0" + left;
		}

		if (right.length() == 1) {
			right = right + "000";
		} else if (right.length() == 2) {
			right = right + "00";
		} else if (right.length() == 3) {
			right = right + "0";
		}

		return left + "." + right;

	}

	private String checkChannel(String left, String right) {
		int l = 0;
		int r = 0;

		try {
			l = Integer.parseInt(left);
		} catch (NumberFormatException e) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
			dialog.setTitle("格式错误");
			dialog.setMessage("非法的字符输入,请重新输入");
			dialog.setPositiveButton("确定", null);
			dialog.create();
			dialog.show();
			return null;
		}

		try {
			r = Integer.parseInt(right);
		} catch (NumberFormatException e) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
			dialog.setTitle("格式错误");
			dialog.setMessage("非法的字符输入,请重新输入");
			dialog.setPositiveButton("确定", null);
			dialog.create();
			dialog.show();
			return null;
		}

		if (l < 1 || l > 30) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
			dialog.setTitle("格式错误");
			dialog.setMessage("频率的整数部分最多为2位,范围是1~30,请重新输入");
			dialog.setPositiveButton("确定", null);
			dialog.create();
			dialog.show();
			return null;
		} else if (left.length() == 1) {
			left = "0" + left;
		}

		if (r < 0 || r > 9999) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
			dialog.setTitle("格式错误");
			dialog.setMessage("频率的小数部分最多为4位,请重新输入");
			dialog.setPositiveButton("确定", null);
			dialog.create();
			dialog.show();
			return null;
		} else if (right.length() == 1) {
			right = right + "000";
		} else if (right.length() == 2) {
			right = right + "00";
		} else if (right.length() == 3) {
			right = right + "0";
		}

		return left + "." + right;

	}

	void enableEdit() {
		userID.setEnabled(true);

		userIDSet.setEnabled(true);
		// channels.setEnabled(true);
		//channels_w.setEnabled(true);

		offset.setEnabled(true);
		offset_plus.setEnabled(true);
		offset_minus.setEnabled(true);
		offset_w.setEnabled(true);

		autoUnlinkSend.setEnabled(true);
		autoUnlink.setEnabled(true);

	}

	void unableEdit() {
		userID.setEnabled(false);
		userIDSet.setEnabled(false);
		// channels.setEnabled(false);
		//channels_w.setEnabled(false);

		offset.setEnabled(false);
		offset_plus.setEnabled(false);
		offset_minus.setEnabled(false);
		offset_w.setEnabled(false);
		
		autoUnlinkSend.setEnabled(false);
		autoUnlink.setEnabled(false);

	}

	/***
	 * 管理员登录窗口
	 */
	void showLoginDialog() {
		// 把activity_login中的控件定义在View中
		final View adminLoginView = factory
				.inflate(R.layout.admin_config, null);
		final EditText admin_pwd = (EditText) adminLoginView
				.findViewById(R.id.admin_pwd);
		final ImageButton showPWD = (ImageButton) adminLoginView
				.findViewById(R.id.show_password);
		showPWD.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					admin_pwd
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					break;
				case MotionEvent.ACTION_UP:
					admin_pwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					admin_pwd.setSelection(admin_pwd.getText().length());
					break;
				default:
					break;
				}
				return false;
			}
		});
		AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
		dialog.setTitle("管理员操作权限");
		dialog.setView(adminLoginView);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String pwd = admin_pwd.getText().toString();
				if (pwd.equals(ADMIN_PASSWORD)) {
					isLocked = false;
					enableEdit();
					state_img.setImageResource(R.drawable.unlock);
				} else {
					Toast.makeText(mainActivity, "密码错误", Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		dialog.setCancelable(false);
		dialog.create();
		dialog.show();
	}

	/***
	 * 管理员登出窗口
	 */
	void showLayoutDialog() {
		// TODO:需要判断内容是否已经保存,根据不同内容显示不同的Message
		AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
		dialog.setTitle("退出管理员操作");
		dialog.setMessage("所有的修改都将会被保存");
		dialog.setPositiveButton("确定", null);
		dialog.create();
		dialog.show();

		isLocked = true;
		state_img.setImageResource(R.drawable.lock);
		unableEdit();
	}

	/***
	 * 普通窗口,提高代码复用率
	 * @param title
	 * @param message
	 */
	void showDialog(String title, String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton("确定", null);
		dialog.create();
		dialog.show();
	}

	void bindListener() {
		//用户ID不需要向串口发送
		userIDSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = userID.getText().toString();
				int i = -1;
				try {
					i = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							mainActivity);
					dialog.setTitle("用户id格式错误");
					dialog.setMessage("非法的字符输入,用户id为1~65535之间的整数,请重新输入");
					dialog.setPositiveButton("确定", null);
					dialog.create();
					dialog.show();
					userID.setText(String.valueOf(Param.mUsrID));
				}

				if (i < 1 || i > 65535) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							mainActivity);
					dialog.setTitle("用户id范围错误");
					dialog.setMessage("用户id为1~65535之间的整数,请重新输入");
					dialog.setPositiveButton("确定", null);
					dialog.create();
					dialog.show();
					userID.setText(String.valueOf(Param.mUsrID));
				} else {
					Param.mUsrID = i;
					Param.perf.writePrefi(Perf.P_USERID, i);
				}

			}
		});

		/*channels_w.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Protocol.sendChannels(channelsList);
				//Protocol.sendChannels();
			}
		});*/

		offset_plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = offset.getText().toString();
				int i = -1;
				try {
					i = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					showDialog("频偏格式错误", "非法的字符输入,频偏为0~255之间的整数,请重新输入");
					userID.setText(String.valueOf(Param.mOffSet));
					return;
				}

				i = i + 1;
				if (i == 256) {
					i = 0;
				}

				s = String.valueOf(i);
				if (s.length() == 1) {
					s = "00" + s;
				} else if (s.length() == 2) {
					s = "0" + s;
				}
				offset.setText(s);
			}
		});

		offset_minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = offset.getText().toString();
				int i = -1;
				try {
					i = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					showDialog("频偏格式错误", "非法的字符输入,频偏为0~255之间的整数,请重新输入");
					userID.setText(String.valueOf(Param.mOffSet));
					return;
				}

				i = i - 1;
				if (i == -1) {
					i = 255;
				}
				s = String.valueOf(i);
				if (s.length() == 1) {
					s = "00" + s;
				} else if (s.length() == 2) {
					s = "0" + s;
				}
				offset.setText(s);
			}
		});

		offset_w.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String s = offset.getText().toString();
				int i = -1;
				try {
					i = Integer.parseInt(s);
				} catch (NumberFormatException e) {
					showDialog("频偏格式错误", "频偏不能为空,频偏为0~255之间的整数,请重新输入");
					offset.setText(String.valueOf(Param.mOffSet));
					return;
				}
				if (i < 0 || i > 255) {
					showDialog("频偏范围错误", "频偏为0~255之间的整数,请重新输入");
					offset.setText(Param.mOffSet);
				} else {
					if (s.length() == 1) {
						s = "00" + s;
					} else if (s.length() == 2) {
						s = "0" + s;
					}
					Param.mOffSet = s;
					Param.perf.writePrefs(Perf.P_OFFSET, s);
					Protocol.sendOffset(s);
				}

			}
		});

	 
		state_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isLocked) {
					// 弹出提示框,对isLoacked
					showLoginDialog();
				} else {
					// 弹出提示框,提示保存
					showLayoutDialog();
				}
			}
		});

		autoUnlinkSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String s = autoUnlink.getText().toString();
                Param.unlinkTime = Integer.valueOf(s);
                Param.unlinkCount = 0;
                Perf.editor.putInt(Perf.P_UNLINKTIME, Param.unlinkTime);
				Perf.editor.commit();
			}
		});
	}

	public final Handler h2 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//Toast.makeText(mainActivity, Param.param+"设置成功", Toast.LENGTH_SHORT).show();
			if(msg.what==1){
				channelsAdapter.notifyDataSetChanged();
				Log.d("setting","信道设置成功");
				Toast.makeText(mainActivity, "频率设置成功", Toast.LENGTH_SHORT).show();
			}else if(msg.what==2){
				Toast.makeText(mainActivity, "频率设置响应nack", Toast.LENGTH_SHORT).show();
			}else if(msg.what==3){
				Toast.makeText(mainActivity, "频率设置无响应", Toast.LENGTH_SHORT).show();
			}
			
		}
		
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		 ((MainActivity) activity).setH2(h2);
	}

}
