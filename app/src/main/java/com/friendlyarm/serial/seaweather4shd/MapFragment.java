package com.friendlyarm.serial.seaweather4shd;


import android.os.Process;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/*import com.friendlyarm.AndroidSDK.HardwareControler;
import com.friendlyarm.Serial.dao.DBMsg;
import com.friendlyarm.Serial.tools.BytesUtil;
import com.friendlyarm.Serial.tools.Param;
import com.friendlyarm.Serial.tools.Perf;
import com.friendlyarm.Serial.tools.Protocol;
import com.friendlyarm.Serial.tools.Serial;
import com.friendlyarm.Serial.tools.SymEncrypt;
import com.friendlyarm.Serial.tools.Tools;
import com.friendlyarm.Serial.Information;
import com.friendlyarm.Serial.view.MarqueenTextView;
import com.friendlyarm.Serial.view.ZoomImageView;*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyarm.serial.seaweather4shd.tools.BytesUtil;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Perf;
import com.friendlyarm.serial.seaweather4shd.tools.Protocol;
import com.friendlyarm.serial.seaweather4shd.tools.Serial;
import com.friendlyarm.serial.seaweather4shd.tools.SymEncrypt;
import com.friendlyarm.serial.seaweather4shd.tools.Tools;
import com.friendlyarm.serial.seaweather4shd.view.MarqueenTextView;
import com.friendlyarm.serial.seaweather4shd.view.ZoomImageView;

/**
 * Created by zhangxin on 2016/3/22.
 */
public class MapFragment extends Fragment {

	private ZoomImageView zoomImageView;

	private MarqueenTextView mNewMsg;
	private TextToSpeech tts;
	private ListView mMsgList;
	private  MsgAdapter adapter;

	private ImageButton sound;
	private ImageButton sounds;
	// private boolean soundSwitch = true;
	private TextView cRate;
	private TextView cBi;
	private TextView cNo;
	private TextView date;

	// 监听参数设置的线程!!!
	private Thread listen1;
	private Thread listen2;
	private Thread listen3;
	private Thread listen4;

	private SharedPreferences mDBPref;
	private int dbCout1;
	private int dbCout2;
	private int dbCout3;

	private ConcurrentHashMap<String, Information> infoMap; // 模拟多线程的穿插
	// private ConcurrentSkipListSet<String> infoList;
	private ArrayList<String> hasReceivedBefore = new ArrayList<String>();
	// 用来存储从 天气/短信串口接受来的数据
	byte[] buf = new byte[100];
	byte[] tmpBuf = new byte[20 * 1024];
	// 用来存储 参数设置串口接受到的消息
	byte[] buf1 = new byte[2048];
	// 用来拼接从串口接收来的数据
	// StringBuilder sb = new StringBuilder();
	StringBuffer sb = new StringBuffer(1024 * 20);
	// 用来拼接从参数设置串口接收来的数据
	StringBuilder sb1 = new StringBuilder();

	// 用来拼接真正的数据
	String text = "";

	// 用来拼接真正的数据(从参数设置串口)
	String text2 = "";

	// 等待生成台风消息后动态生成对象,这两个对象都用于组织在消息显示时的逻辑处理.
	 Typhoon typhoon;
	// 等待生成天气后在动态生成对象.
	 Weather weather;

	// t1:开机主动查询,t2:静噪开关,t3:音量调节
	Timer t1;
	Timer t2;
	Timer t3;

	// 用在音量调节时候的临时值,取消的是后关闭.
	boolean tmpState;
	int tmpSounds = 18;

	// ack线程开关
	boolean ackFlag = false;

	Thread ackThread;
	DislinkThread dislinkThread1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/***
	 * 该方法主要是用于绑定Fragment的界面（view）
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mapLayout = inflater
				.inflate(R.layout.map_layout, container, false);

		//dislinkThread = new DislinkThread();
		dbCout1 = Param.perf.sp.getInt("dbCout1", 0);
		dbCout2 = Param.perf.sp.getInt("dbCout2", 0);
		dbCout3 = Param.perf.sp.getInt("dbCout3", 0);
		infoMap = new ConcurrentHashMap<String, Information>();
		zoomImageView = ( ZoomImageView) mapLayout.findViewById(R.id.zoomImg);
		tts = new TextToSpeech(getActivity(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
							int result = tts.setLanguage(Locale.CHINESE);
							if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
									&& result != TextToSpeech.LANG_AVAILABLE)
								Toast.makeText(getActivity(), "不支持的语音格式",
										Toast.LENGTH_LONG).show();
						}
					}
				});
		mNewMsg = ( MarqueenTextView) mapLayout.findViewById(R.id.new_msg);

		mMsgList = (ListView) mapLayout.findViewById(R.id.lv_msg);

		adapter = new  MsgAdapter(getActivity(), Param.MSGList);

		mMsgList.setAdapter(adapter);

		mMsgList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle("详细信息")
						.setMessage(Param.MSGList.get(arg2).showMsg())
						.setPositiveButton("确认", null).show();

				if (!Param.MSGList.get(arg2).isRead) {
					adapter.updataView(arg2, mMsgList);
				}
			}

		});

		cRate = (TextView) mapLayout.findViewById(R.id.xindaosulv);
		cBi = (TextView) mapLayout.findViewById(R.id.xinzaobi);
		cNo = (TextView) mapLayout.findViewById(R.id.xindaohao);
		date = (TextView) mapLayout.findViewById(R.id.date);
		date.setText(BytesUtil.formatTime(Param.mDate.toCharArray()));
		sound = (ImageButton) mapLayout.findViewById(R.id.sound_switch);
		if (Param.mSound) {
			sound.setImageResource(R.drawable.sound_on);
		} else {
			sound.setImageResource(R.drawable.sound_off);
		}
		sound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String text = "";

				// 用来暂存音量的开关状态
				tmpState = !Param.mSound;

				if (Param.mSound) {
					text = "确认关闭声音吗?";
				} else {
					text = "确认打开声音吗?";
				}

				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity())
						.setTitle("音量调节")
						.setMessage(text)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Param.SoundAck = -2;
										Param.param = "静噪";

										t2 = new Timer();
										t2.schedule(new TimerTask() {
											int count = 0;

											@Override
											public void run() {
												if (count > 0) {
													if (Param.SoundAck == 1) {
														Log.d("setting",
																"静噪设置响应ack,t2将被取消");
														h1.sendEmptyMessage(20);
														t2.cancel();
														count = -1;
													} else if (Param.SoundAck == 0) {
														Log.d("setting",
																"静噪设置响应nack,t2将被取消");
														h1.sendEmptyMessage(21);
														t2.cancel();
														count = -1;
													}
												}

												if (count == 3) {
													Log.d("setting",
															"静噪设置无响应,t2将被取消");
													h1.sendEmptyMessage(22);
													t2.cancel();
												}

												if (count != -1) {
													Protocol.sendSound(tmpState);
													count++;
												}

											}
										}, 0, 800);
									}
								}).setNegativeButton("取消", null);
				dialog.setCancelable(false);
				dialog.create();
				dialog.show();

			}
		});

		sounds = (ImageButton) mapLayout.findViewById(R.id.sounds);
		sounds.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = LayoutInflater
						.from(getActivity());
				View soundView = layoutInflater.inflate(R.layout.linebar, null);

				final TextView tv = (TextView) soundView.findViewById(R.id.tv);

				final SeekBar sb = (SeekBar) soundView
						.findViewById(R.id.linebar1);
				sb.setProgress(Param.mSounds);
				sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						tv.setText("当前的音量为:" + progress);
						tmpSounds = progress;
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
				});

				if (Param.mSound) {
					sb.setEnabled(true);
					tv.setText("当前音量:" + Param.mSounds);
				} else {
					sb.setEnabled(false);
					tv.setText("当前为静音状态,若想调节音量,请打开声音");
				}

				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity())
						.setTitle("音量调节")
						.setView(soundView)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										if (Param.mSound) {
											Param.SoundsAck = -2;
											Param.param = "音量";

											t3 = new Timer();
											t3.schedule(new TimerTask() {
												int count = 0;

												@Override
												public void run() {
													if (count > 0) {
														if (Param.SoundsAck == 1) {
															Log.d("setting",
																	"音量设置响应ack");
															h1.sendEmptyMessage(20);
															t3.cancel();
														} else if (Param.SoundsAck == 0) {
															Log.d("setting",
																	"音量设置响应nack");
															h1.sendEmptyMessage(21);
															t3.cancel();
														}
													}

													if (count == 3) {
														Log.d("setting",
																"音量设置无响应");
														h1.sendEmptyMessage(22);
														t3.cancel();
													}

													if (count != -1) {
														Protocol.sendSounds(tmpSounds);
														count++;
													}

												}
											}, 0, 800);
										}
									}
								}).setNegativeButton("取消", null);
				dialog.setCancelable(false);
				dialog.create();
				dialog.show();

			}
		});
		// 用来接受天气消息
		listen1 = new Thread(new Runnable() {
			@Override
			public void run() {
				// Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);//-8
				// Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
				while (Param.totalFlag) {
					int n = 0; // 从串口读取到的字节数
					int beginIndex = 0; // 8303包对应的起始位置
					int endIndex = 0; // 8304包对应的起始位置
					int m = HardwareControler.select(Serial.fd1, 0, 10);
					if (m == 1) {
						//dislinkThread.onPause();
						while ((n = HardwareControler.read(Serial.fd1, buf,
								buf.length)) > 0) {
							String logText = BytesUtil.bytesToHexString(buf, n);
							// Log.d("###", "接收到的数据长度:" + n);
							// Log.d("###", "接收到的数据内容:" + logText);
							sb.append(logText);
							Log.e("get", logText);
						}
					}

					// Log.d("$$$", "select==0时:" + sb.toString());
					// System.out.println("sb中的数据->"+sb.toString());
					// 在没有数据读的时候开始解析

					while ((endIndex = sb.indexOf("c0a008828183040004c0")) > 0) {
						Log.e("###", "------------------------");
						//dislinkThread.onResume();
						Log.e("###", "已检测到8304包,其长度是:" + sb.length());
						beginIndex = sb.indexOf("c0a008828183030003c0");
						if (beginIndex < 0) {
							Log.e("###",
									"已检测到8304包,但是没有检测到8303包,严重错误,那么丢弃当前8304包以及之前的所有数据!!!");
							sb = sb.delete(0, endIndex + 20);
							break;
						}
						Log.e("###", "起始结束索引是" + beginIndex + "---" + endIndex);

						text = sb.substring(beginIndex, endIndex + 20);
						Log.e("###", "截取到的内容,text长度是:" + text.length());
						// Log.e("###cha看内容", text);
						sb = sb.delete(0, endIndex + 20);

						/*
						 * Message message = Message.obtain(); message.obj =
						 * text; h1.sendMessage(message);
						 */
						if (text.startsWith("c0a008828183030003c0")) {
							// if ((beginIndex =
							// text.indexOf("c0a008828183030003c0"))>0) {
							Log.e("###", "已检测到8303包");
							StringBuilder data = new StringBuilder();
							text = text.substring(20, text.length() - 20);
							// Log.d("###", "在8303和8304之间的包是-->" + text);
							if (text.startsWith("c0") && text.endsWith("c0")) {
								Log.d("###", "进入8303与8304之间的数据是以c0--c0");

								// 目前这个转换顺序是正确的
								// TODO:这样替换可能导致奇数位被替换
								/*
								 * text = text.replace("dbdc", "c0").replace(
								 * "dbdd", "db");
								 */
								text = Tools
										.transferReplace(text, "dbdc", "c0");
								text = Tools
										.transferReplace(text, "dbdd", "db");
								// 执行了第一次替换,接下来从text中取出数据
								while (text.length() > 0) {
									// Log.d("###", "将text进行截断,可能会陷入死循环!!!");
									if (text.startsWith("c0a0")) {
										if (text.indexOf("828184") == 6) {
											// 判断是不是是真正的数据包!!!
											int len = Integer.valueOf(
													text.substring(4, 6), 16);
											if (text.charAt(len * 2 + 4 - 2) != 'c'
													&& text.charAt(len * 2 + 4 - 1) != '0') {
												Log.e("###",
														"错误的包格式,可能会导致死循环,丢弃该包");
												text = "";
												break;
											}
											data = data.append(text.substring(
													12, 12 + (len - 7) * 2));
											text = text
													.substring((len + 2) * 2);
										} else {
											int len = Integer.valueOf(
													text.substring(4, 6), 16);
											text = text
													.substring((len + 2) * 2);
										}
									} else {
										Log.e("###", "居然有数据不是以c0a0开头,严重错误!!!!");
										Log.e("###",
												"data的已有长度" + data.length());
										Log.e("###",
												"text的已有内容长度" + data.toString());
										Log.e("###",
												"text的剩余长度" + text.length());
										Log.e("###", "text的剩余内容" + text);
										text = "";
										break;
									}
								}// while (text.length() > 0)
									// ----------------------------------------------------
									// 这一步已经将碎数据取出来了,接下来要截取数据帧,以为c0~c0
								String time = "";
								String frameString = data.toString();
								// boolean firstBadBag = true;
								// count代表总包数,index代表帧序号
								int count = -1;
								int index = -1;
								while (frameString.length() > 0) {
									int i, k;
									i = Tools.findSilpBagHead(frameString);
									k = Tools.findSilpBagTail(i, frameString);
									Log.d("###", i + "<-->" + k);
									if (i >= k) {
										Log.d("###", "i>=k");
										break;
									}
									if (i % 2 != 0) {
										Log.d("###", "i%2!=0");
										break;
									}
									if (k % 2 != 0) {
										Log.d("###", "k%2!=0");
										break;
									}

									// ss是一个数据帧.但是还未进行替换.
									String ss = frameString.substring(i, k + 2);
									ss = Tools
											.transferReplace(ss, "dbdc", "c0");
									ss = Tools
											.transferReplace(ss, "dbdd", "db");
									// ss这时是一个真正的数据帧.接下里处理这一帧.

									byte[] infoi = BytesUtil
											.hexStringToBytes(ss);
									byte[] tmp1 = new byte[13];
									try {
										System.arraycopy(infoi, 1, tmp1, 0, 13);
									} catch (Exception e) {
										Log.e("###", "3丢包了...");
										break;
									}

									// -17,把tmp2的crc也删去了,但是把校验和放在了ocrc中
									byte[] tmp2 = new byte[infoi.length - 17];
									try {
										System.arraycopy(infoi, 14, tmp2, 0,
												infoi.length - 17);
									} catch (Exception e) {
										Log.e("###", "4丢包了...");
										break;
									}

									byte[] oCrc = null;
									if (infoi.length >= 3) {
										oCrc = new byte[2];
										oCrc[0] = infoi[infoi.length - 3];
										oCrc[1] = infoi[infoi.length - 2];
									} else {
										Log.e("###", "4丢包了...");
										break;
									}

									if (BytesUtil.bytesToHexString(tmp2) == null
											|| BytesUtil.bytesToHexString(tmp1) == null) {
										Log.d("###", "tmp2或者tmp1是null,直接丢弃");
										break;
									}

									// Log.d("w23",BytesUtil.bytesToHexString(tmp2));
									// 只有crc1和crc2的校验均正确,那么这一帧可用
									if (checkCRC(tmp1) && checkCRC(tmp2, oCrc)) {
										// if (true) {
										byte[] bTime = new byte[6];
										try {
											System.arraycopy(infoi, 1, bTime,
													0, 6);
										} catch (Exception e) {
											Log.e("###", "5丢包了");
											break;
										}

										time = parseTimeInMSG(bTime);
										count = Integer.valueOf(BytesUtil
												.bytesToHexString(new byte[] {
														tmp1[6], tmp1[7] }), 16);
										index = Integer.valueOf(BytesUtil
												.bytesToHexString(new byte[] {
														tmp1[8], tmp1[9] }), 16) - 1;
										if (hasReceivedBefore.contains(time)) {
											Log.e("tmp", "之前收到过相同的时间戳,直接舍弃->"
													+ time);
											System.out
													.println("之前收到过相同的时间戳,直接舍弃->"
															+ time);
											break;
										}
										Log.d("tmp", "第" + index
												+ "帧中解析到的time是:" + time);
										if (infoMap.containsKey(time)) {
											if (infoMap.get(time).bflag[index] == '0') {
												infoMap.get(time).list.set(
														index,
														BytesUtil
																.bytesToHexString(tmp2));
												infoMap.get(time).start = System
														.currentTimeMillis();
												infoMap.get(time)
														.setFlag(index);
												Log.e("tmp", "未收集的包:" + index);
											}
										} else {
											Log.d("tmp",
													"新的消息,需要加入到map中,新创建一个info对象,time和count是"
															+ time + "--"
															+ count);
											Information info = new Information(
													time, count);
											info.start = System
													.currentTimeMillis();
											info.list.set(index, BytesUtil
													.bytesToHexString(tmp2));
											if (count <= 2) {
												info.waitSeconds = 3 * count * 8 + 20;
											} else {
												info.waitSeconds = 3 * count * 2 + 20;
											}
											Log.e("tmp", "未收集的包:" + index
													+ "  需要等待的时间是:"
													+ info.waitSeconds);
											info.setFlag(index);
											infoMap.put(time, info);
										}
									}/*
									 * else if (firstBadBag && index > -1) { if
									 * ((!time.equals("")) &&
									 * (infoMap.containsKey(time))) { //
									 * TODO:等待时间的规则...需要更正.
									 * infoMap.get(time).waitSeconds = (2 *
									 * count - index) * 2 + 30; Log.e("tmp",
									 * "启动更新时间:" +
									 * index+"    需要等待的时间是:"+infoMap.
									 * get(time).waitSeconds); firstBadBag =
									 * false; }
									 * 
									 * }
									 */
									frameString = frameString.substring(k + 2);
								}// while(frameString.length()>0)

								if ((!time.equals(""))
										&& (infoMap.containsKey(time))) {
									Information info = infoMap.get(time);
									info.n++;
									info.start = System.currentTimeMillis();
									if (info.n == 1) {
										if (count > 0 && count <= 2) {
											info.waitSeconds = (3 * count - index) * 8 + 20;
										} else {
											info.waitSeconds = (3 * count - index) * 2 + 20;
										}
									} else if (info.n == 2) {
										if (count > 0 && count <= 2) {
											info.waitSeconds = (2 * count - index) * 8 + 10;
										} else {
											info.waitSeconds = (2 * count - index) * 2 + 10;
										}
									}
									Log.e("tmp", "第" + info.n + "次,将等待时间改为:"
											+ info.waitSeconds);
								}
							}
						}// if (text.startsWith("c0a008828183030003c0"))
					}// while((endIndex=sb.indexOf("c0a008828183040004c0"))>0)

				}
			}
		});
		listen1.start();

		// -----------接听参数设置接口发来的数据.-------------------
		listen2 = new Thread(new Runnable() {

			@Override
			public void run() {
				// 接收参数串口发来的消息
				while (Param.totalFlag) {
					int m = HardwareControler.select(Serial.fd3, 0, 0);
					int n;
					if (m == 1) {
						while ((n = HardwareControler.read(Serial.fd3, buf1,
								buf1.length)) > 0) {
							String logText = BytesUtil
									.bytesToHexString(buf1, n);
							// Log.d("###串口2", "接收到的数据长度:" + n);
							// Log.d("###串口2", "接收到的数据内容:" + logText);
							sb1.append(logText);
						}
					}

					String s = sb1.toString();
					sb1 = sb1.delete(0, sb1.length());

					// parseParam(s);
					while (s.length() > 0) {

						// Log.e("err",s);
						if (s.startsWith("00")) {
							s = s.substring(2);
						} else if (s.startsWith("0273313103")) {
							// SDR正常回复
							Param.SDRAck = 1;
							// Log.e("setting", "SRD正常回复");
							/*
							 * if (s.length() >= 10) { s = s.substring(10); }
							 * else { break; }
							 */
							s = s.substring(10);

						} else if (s.startsWith("0273313003")) {
							// SDR错误回复
							Param.SDRAck = 0;
							/*
							 * if (s.length() >= 10) { s = s.substring(10); }
							 * else { break; }
							 */
							s.substring(10);
						} else if (s.startsWith("02733331")) {
							// 信道号
							if (s.length() < 14) {
								break;
							}
							//收到信道号,表示建链,那么就开启拆链计时
							//需要在l1中,如果接收到数据,就停止计时
							//dislinkThread.onStart();
							Param.mSNN = new String(BytesUtil
									.hexStringToBytes(s.substring(8, 12)));
							Param.unlinkCount = 0;
							h1.sendEmptyMessage(16);
							/*
							 * if (s.length() >= 14) { s = s.substring(14); }
							 * else { break; }
							 */
							s = s.substring(14);
						} else if (s.startsWith("02733330")) {
							if (s.length() < 14) {
								break;
							}
							//显示扫描中,停止线程.
							//不是阻塞线程,而是将flag设为false,只计时,不发数据
							//dislinkThread.onPause();
							s = s.substring(14);
							// 显示扫描中...
							h1.sendEmptyMessage(36);
						} else if (s.startsWith("027334")) {
							// 信道速率
							if (s.length() < 18) {
								break;
							}
							Param.mDTR = new String(BytesUtil
									.hexStringToBytes(s.substring(6, 16)));
							// Log.d("setting信道速率", Param.mDTR);
							h1.sendEmptyMessage(12);
							if (s.length() >= 18) {
								s = s.substring(18);
							} else {
								break;
							}

						} else if (s.startsWith("027336")) {
							// 信噪比,没有吐

							if (s.length() < 18) {
								break;
							}
							Param.mSNR = new String(BytesUtil
									.hexStringToBytes(s.substring(6, 16)));
							h1.sendEmptyMessage(17);
							s = s.substring(18);

						} else if (s.startsWith("027337")) {
							// 频偏
							if (s.length() >= 20) {
								s = s.substring(20);
							} else {
								break;
							}
						} else if (s.startsWith("020603")) {
							// Param.ack = 1;
							if (Param.SoundAck == -2) {
								Log.e("setting", "已经将静噪ack置为1了");
								Param.SoundAck = 1;
							} else if (Param.SoundsAck == -2) {
								Log.e("setting", "已经将声音ack置为1了");
								Param.SoundsAck = 1;
							} else if (Param.SDRAck == -2) {
								Log.e("setting", "SDR ack置为1了");
								Param.SDRAck = 1;
							} else if (Param.ChannelAck == -2) {
								Log.e("setting", "信道 ack置为1了");
								Param.ChannelAck = 1;
							}
							if (s.length() >= 6) {
								s = s.substring(6);
							} else {
								break;
							}
						} else if (s.startsWith("021503")) {
							Param.ack = 0;
							if (s.length() >= 6) {
								s = s.substring(6);
							} else {
								break;
							}
						} else {
							// 未知的消息类型
							// Log.e("setting", "未知的消息.严重的错误!!!!!!" + s);
							try {
								int i = s.indexOf("0302");
								if (i > 0 && i % 2 == 0) {
									s = s.substring(i + 2);
								}
							} catch (Exception e) {

							}
							break;
						}
					}// while(s.lengh>0)
					sb1.append(s);
				}
			}
		});
		listen2.start();

		// listen3用来扫描是否超时，并解析数据
		listen3 = new Thread(new Runnable() {

			@Override
			public void run() {
				String key = "";
				Information out;
				while (Param.totalFlag) {
					// 用infoMap是否为空来作为该线程的一个开关
					if (!infoMap.isEmpty()) {
						// Log.d("###","进入线程同步的map检测");
						for (Entry<String, Information> entry : infoMap
								.entrySet()) {
							String k = entry.getKey();
							// Log.d("get", "获取到当前的key是:  " + k);
							Information info = entry.getValue();
							if (info.flag) {
								Log.d("get", "flag里不包含0,表明已经信息已收集完整");
								key = k;
							} else if (info.n == 3) {
								if (info.bflag[0] == '1') {
									Log.d("get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
									key = k;
								} else {
									Log.d("get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
									infoMap.remove(k);
								}
							} else if ((System.currentTimeMillis() - info.start) / 1000 >= info.waitSeconds) {
								Log.d("get", "第三遍还未收到,但是已经超时"
										+ info.waitSeconds + "s");
								Log.d("get", "此时info的消息是: count->" + info.count
										+ "  发送次数->" + info.n + "  消息列表中的数量->"
										+ info.list.size() + "  发送的标志->"
										+ new String(info.bflag));
								if (info.bflag[0] == '1') {
									Log.d("get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
									key = k;
								} else {
									Log.d("get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
									infoMap.remove(k);
								}
							}
							// TODO:每次解决完一个消息就处理一下.
							if (key != "") {
								out = infoMap.remove(key);
								Log.d("get获取到的key是", "run: " + key);
								StringBuilder sb = new StringBuilder();
								/*
								 * for (String s : out.list) { sb.append(s); }
								 */
								for (int i = 0; i < out.bflag.length; i++) {
									if (out.bflag[i] == '1') {
										sb.append(out.list.get(i));
									} else {
										sb.append("28cffbcfa2d2d1c6c6cbf029");// (信息已破损)
										break;
									}
								}
								Log.d("get获取到的内容是", sb.toString());
								byte[] realData = BytesUtil.hexStringToBytes(sb
										.toString());
								Log.e("###", "添加进已接收列表的时间戳是:" + key);
								hasReceivedBefore.add(key);
								if (hasReceivedBefore.size() >= 10) {
									hasReceivedBefore.remove(0);
								}
								parseMSG(key, realData);
								key = "";
							}// if(key!="")
						}// for(infoMap)
					}// if(infoMap!=null)
				}// while(true)
			}
		});
		listen3.start();

		//dislinkThread.start();
		/*
		 * SearchThread searchThread = new SearchThread(); searchThread.start();
		 */

		/*listen4 = new Thread(new Runnable() {

			@Override
			public void run() {

				while (Param.totalFlag) {
					Param.unlinkCount = 0;
					while (Param.unlinkCount < Param.unlinkTime) {
						try {
							Thread.sleep(1000 * 60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Param.unlinkCount++;
					}
					Protocol.autoUnlink();
					Log.d("###自动拆链", "已发送自动拆链");
				}
			}
		});

		listen4.start();*/
		
		return mapLayout;
	}

	public final Handler h1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// super.handleMessage(msg);
			// 将进行转换msg.obj
			// zoomImageView.invalidate();
			// 11:处理MSGList刷新+跑马灯文字刷新+TTS语音播报
			// 12:数据传输收率显示
			// 13:数据传输收率隐藏
			// 14:有效期刷新
			// 15:天气的刷新
			// 16:台风的刷新
			// 17:信道号的显示
			// 18:sdr正确的回复
			// 19:sdr错误的回复
			// 20:ack
			// 21:nack
			// 22:无响应
			String text = "hehe";
			if (Param.MSGList.size() > 0) {
				text = Param.MSGList.get(0).mMsgContent;
			}
			switch (msg.what) {
			case 11:
				Log.d("@@@", "检测到处理信息");
				adapter.notifyDataSetChanged();
				mMsgList.setAdapter(adapter);
				String show;
				if (text.length() >= 200) {
					show = text.substring(0, 200);
				} else {
					show = text;
				}
				mNewMsg.setText(show);
				tts.speak(show, TextToSpeech.QUEUE_FLUSH, null);
				break;
			case 12:
				cRate.setText(Param.mDTR);
				break;
			case 13:
				// cRate.setText(" ");
				break;
			case 14:
				// 14好像不用了吧
				char[] c = Param.mDate.toCharArray();
				String date_text = BytesUtil.formatTime(c);
				/*
				 * for (int i = 0; i < c.length - 2; i++) { date_text =
				 * date_text + c[i]; if (i % 2 == 1 && i < 5) { date_text =
				 * date_text + "-"; }
				 * 
				 * if (i == 5) { date_text = date_text + " "; }
				 * 
				 * if (i == 7) { date_text = date_text + ":"; } }
				 */

				Log.e("setting", "收到的注册时间是:" + date_text);
				date.setText(date_text);
				// 已经在handler发送msg之前就已经修改过pref里的数据
				break;
			case 15:
				adapter.notifyDataSetChanged();
				mMsgList.setAdapter(adapter);
				/*
				 * if (text.length() >= 200) { mNewMsg.setText(text.substring(0,
				 * 200)); } else { mNewMsg.setText(text); } tts.speak(text,
				 * TextToSpeech.QUEUE_FLUSH, null);
				 */
				String show1;
				if (text.length() >= 200) {
					show1 = text.substring(0, 200);
				} else {
					show1 = text;
				}
				mNewMsg.setText(show1);
				tts.speak(show1, TextToSpeech.QUEUE_FLUSH, null);
				zoomImageView.weather = weather;
				zoomImageView.invalidate();
				break;
			case 16:
				cNo.setText(Param.mSNN);
				break;
			case 17:
				cBi.setText(Param.mSNR);
				break;
			case 18:
				// sdr回复正确
				// Toast.makeText(getActivity(), "SDR回复正常", Toast.LENGTH_LONG)
				// .show();
				break;
			case 19:
				// SDR回复失败
				// Toast.makeText(getActivity(), "SDR回复错误",
				// Toast.LENGTH_LONG).show();
				// showDialog("SDR回复错误");
				break;
			case 20:
				// ack
				if (Param.param.equals("静噪")) {
					Param.mSound = tmpState;
					if (tmpState) {
						Log.d("###善后", "设置成on");
						sound.setImageResource(R.drawable.sound_on);
					} else {
						Log.d("###善后", "设置成off");
						sound.setImageResource(R.drawable.sound_off);
					}
					Perf.editor.putBoolean(Perf.P_SOUND, tmpState);
					Perf.editor.commit();
				} else if (Param.param.equals("音量")) {
					Param.mSounds = tmpSounds;
					Perf.editor.putInt(Perf.P_SOUNDS, tmpSounds);
					Perf.editor.commit();
				}
				Toast.makeText(getActivity(), Param.param + "设置成功",
						Toast.LENGTH_LONG).show();
				break;
			case 21:
				// nack
				showDialog(Param.param + "设置失败,请重新设置");
				break;
			case 22:
				// 无参数响应
				showDialog(Param.param + "设置无响应");
				break;
			case 36:
				cRate.setText("");
				cBi.setText("");
				cNo.setText("扫描中");
				break;
			default:
				break;
			}

			Param.ack = -1;
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).setH1(h1);
		// ((MainActivity) activity).setH1(h2);
	}

	private void parseSDRstate(byte b) {
		if (b == 0x31) {
			Toast.makeText(getActivity(), "SDR正常回复", Toast.LENGTH_SHORT).show();
		} else if (b == 0x30) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("错误信息");
			dialog.setMessage("SDR状态错误!!!");
			dialog.setPositiveButton("确定", null);
			dialog.create();
			dialog.show();
		}
	}

	private boolean checkCRC(byte[] bs) {
		int crc = 0;
		char c;
		int len = bs.length - 2;
		int j = 0;
		while (len-- != 0) {
			for (c = 0x80; c != 0; c /= 2) {
				if ((crc & 0x8000) != 0) {
					crc *= 2;
					crc ^= 0x1021;
				} else {
					crc *= 2;
				}

				if ((bs[j] & c) != 0) {
					crc ^= 0x1021;
				}
			}
			j++;
		}

		byte[] bb = new byte[2];// 用来存放传入的字节数组的原始校验和
		bb[0] = bs[bs.length - 2];
		bb[1] = bs[bs.length - 1];
		// int oCrc = bb[0]*256+bb[1];
		int oCrc = ((bb[0] + 256) % 256) * 256 + (bb[1] + 256) % 256;
		if (crc < 0) {
			crc = (crc + Integer.MAX_VALUE + 1) % (Integer.MAX_VALUE + 1);
		}
		// Log.e("###校验和1", "收到的校验和是" + oCrc);
		// Log.e("###校验和1", "算出的校验和是" + crc % 65536);
		return (crc % 65536) == (oCrc);
	}

	/***
	 * 校验crc
	 * 
	 * @param bs
	 *            需要验证的源数据(不包含crc字段)
	 * @param oCrc
	 *            源数据中附带的crc
	 * @return
	 */
	private boolean checkCRC(byte[] bs, byte[] oCrc) {
		int crc = 0;
		char c;
		int len = bs.length;
		int j = 0;
		while (len-- != 0) {
			for (c = 0x80; c != 0; c /= 2) {
				if ((crc & 0x8000) != 0) {
					crc *= 2;
					crc ^= 0x1021;
				} else {
					crc *= 2;
				}

				if ((bs[j] & c) != 0) {
					crc ^= 0x1021;
				}
			}
			j++;
		}

		// String oCrc1 = BytesUtil.bytesToHexString(oCrc);
		// int oCrc1 = oCrc[0]*256+oCrc[1];
		int oCrc1 = ((oCrc[0] + 256) % 256) * 256 + (oCrc[1] + 256) % 256;
		if (crc < 0) {
			crc = (crc + Integer.MAX_VALUE + 1) % (Integer.MAX_VALUE + 1);
		}
		// Log.e("###校验和2", "收到的校验和是" + oCrc1);
		// Log.e("###校验和2", "算出的校验和是" + crc % 65536);
		return (crc % 65536) == (oCrc1);
	}

	/***
	 * 
	 * @param time
	 *            时间戳
	 * @param b
	 *            不包含校验码的纯数据
	 */
	private void parseMSG(String time, byte[] b) {
		int infoType = b[0];
		Log.d("###", "消息类型是:" + infoType);
		byte[] bID = { b[1], b[2] };
		int userID = Integer.valueOf(BytesUtil.bytesToHexString(bID), 16);
		Log.d("###", "用户ID是:" + userID);
		int isClearTyphoon = b[3];
		if (isClearTyphoon == 1) {
			Param.IsTyphonClear = true;
			// 将台风map中在内存中的数据全部清除
			Param.typhoonMap.clear();
		}

		switch (infoType) {
		case 1:
			// TODO:短消息,发送方的手机号码如何处理?
			if ((userID != 0) && (Param.mUsrID != userID)) {
				Log.d("###", "不是群发,不是本机ID,舍弃");
				break;
			}

			if ((Param.mUsrID == userID)
					&& (Long.valueOf(time) > Long.valueOf(Param.mDate))) {
				Log.d("###", "是本机ID,但是已过期,舍弃");
				break;
			}
			String content1 = "";
			byte[] bpNo = { b[4], b[5], b[6], b[7], b[8], b[9], b[10] };
			String phoneNo = BytesUtil.bytesToHexString(bpNo).substring(0, 13);
			try {
				content1 = new String(b, 4 + 7, b.length - 4 - 7, "gbk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				// System.out.println("有乱码");
				Log.e("###", "短消息中的类型不能解析为中文");
			}
			addToMSGList(new Msg(R.drawable.msg_unread, content1, time));
			ContentValues value1 = new ContentValues();
			value1.put("time", time);
			value1.put("phoneNo", phoneNo);
			value1.put("content", content1);
			Param.db.insert("msg", null, value1);
			Param.db1 = true;
			h1.sendEmptyMessage(11);
			dbCout1++;
			if (dbCout1 > 1000) {
				Cursor cursor = Param.db.query("msg", null, null, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					Param.db.delete("msg", "id = ?",
							new String[] { String.valueOf(id) });
				}
				dbCout1--;
			}
			Perf.editor.putInt("dbCout1", dbCout1).commit();

			break;
		case 2:
			// 一般气象消息
			if ((userID != 0)
					&& (Long.valueOf(time) > Long.valueOf(Param.mDate))) {
				Log.d("###", "超时舍弃");
				break;
			}

			/*
			 * if ((Param.mUsrID == userID) && (Long.valueOf(time) >
			 * Long.valueOf(Param.mDate))) { Log.d("###", "是本机ID,但是已过期,舍弃");
			 * break; }
			 */
			parseWeather(b);
			String oWeatherContent = weather.buildText();
			addToMSGList(new Msg(Param.weatherTypeMap[weather.weatherType],
					oWeatherContent, time));
			ContentValues value2 = new ContentValues();
			value2.put("time", time);
			value2.put("content", oWeatherContent);
			Param.db.insert("weather", null, value2);
			Param.db3 = true;
			h1.sendEmptyMessage(15);
			dbCout3++;
			if (dbCout3 > 1000) {
				Cursor cursor = Param.db.query("weather", null, null, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					Param.db.delete("msg", "id = ?",
							new String[] { String.valueOf(id) });
				}
				dbCout3--;
			}
			Perf.editor.putInt("dbCout3", dbCout3).commit();
			// value2.clear();
			break;
		case 3:
			// 紧急气象消息,除了用户名验证不同外,其他流程一样

			parseWeather(b);
			String sWeatherContent = weather.buildText();
			addToMSGList(new Msg(Param.weatherTypeMap[weather.weatherType],
					sWeatherContent, time));
			ContentValues value3 = new ContentValues();
			value3.put("time", time);
			value3.put("content", sWeatherContent);
			Param.db.insert("weather", null, value3);
			Param.db3 = true;
			h1.sendEmptyMessage(15);
			dbCout3++;
			if (dbCout3 > 1000) {
				Cursor cursor = Param.db.query("weather", null, null, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					Param.db.delete("msg", "id = ?",
							new String[] { String.valueOf(id) });
				}
				dbCout3--;
			}
			Perf.editor.putInt("dbCout3", dbCout3).commit();
			// value3.clear();
			break;
		case 4:
			if ((userID != 0)
					&& (Long.valueOf(time) > Long.valueOf(Param.mDate))) {
				Log.d("###", "超时舍弃");
				break;
			}

			/*
			 * if ((userID != 0) && (Param.mUsrID != userID)) { Log.d("###",
			 * "不是群发,不是本机ID,舍弃"); break; }
			 * 
			 * if ((Param.mUsrID == userID) && (Long.valueOf(time) >
			 * Long.valueOf(Param.mDate))) { Log.d("###", "是本机ID,但是已过期,舍弃");
			 * break; }
			 */
			// 商务信息
			String content2 = "";
			try {
				content2 = new String(b, 4, b.length - 4, "gbk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				// System.out.println("有乱码");
				Log.e("###", "商务消息中的类型不能解析为中文");
			}

			addToMSGList(new Msg(R.drawable.business_msg_unread, content2, time));
			ContentValues value4 = new ContentValues();
			value4.put("time", time);
			value4.put("content", content2);
			Param.db.insert("bmsg", null, value4);
			Param.db2 = true;
			h1.sendEmptyMessage(11);
			dbCout2++;
			if (dbCout2 > 1000) {
				Cursor cursor = Param.db.query("bmsg", null, null, null, null,
						null, null);
				if (cursor.moveToFirst()) {
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					Param.db.delete("msg", "id = ?",
							new String[] { String.valueOf(id) });
				}
				dbCout2--;
			}
			Perf.editor.putInt("dbCout2", dbCout2).commit();
			// value4.clear();
			break;
		case 5:
		case 9:
			// 用户缴费或注册用户.
			if ((userID != 0) && (Param.mUsrID != userID)) {
				break;
			}
			byte[] d = { b[4], b[5], b[6], b[7], b[8], b[9] };
			Param.mDate = parseTimeInMSG(d);
			String privateData = SymEncrypt.encrypt(Param.mDate,
					Param.PERF_PASSWORD);
			Param.perf.writePrefs(Perf.P_DATE, privateData);
			h1.sendEmptyMessage(14);
			break;
		case 8:
			// 用户注销
			if ((userID != 0) && (Param.mUsrID != userID)) {
				break;
			}
			Param.mDate = "000000000000";
			Param.perf.writePrefs(Perf.P_DATE, "000000000000");
			h1.sendEmptyMessage(14);
			break;
		case 10:
			// 轨迹信息:台风.
			parseTyphoon(b);
			String phoonContent = typhoon.buildText();
			addToMSGList(new Msg(Param.weatherTypeMap[38], phoonContent, time));
			ContentValues value5 = new ContentValues();
			value5.put("typhoonNo", typhoon.typhoonNo);
			value5.put("locateX", typhoon.locater.x);
			value5.put("locateY", typhoon.locater.y);
			Param.db.insert("typhoon", null, value5);

			// TODO:台风信息如何往天气消息里面添加
			/*
			 * ContentValues value6 = new ContentValues(); value6.put("time",
			 * time); value6.put("content", weather.text);
			 * Param.db.insert("weather", null, value6);
			 */
			h1.sendEmptyMessage(15);
			break;
		default:
			break;
		}
	}

	private String parseTimeInMSG(byte[] b) {
		int year = b[0];
		int month = b[1];
		int day = b[2];
		int hour = b[3];
		int minus = b[4];
		int second = b[5];
		/*
		 * String s = year + "-" + month + "-" + day + " " + hour + ":" + minus
		 * + ":" + second;
		 */
		// String s = ""+year + month + day + hour + minus + second;
		String s = String.format("%02d%02d%02d%02d%02d%02d", year, month, day,
				hour, minus, second);
		return s;
	}

	private void addToMSGList(Msg msg) {
		if (Param.MSGList.size() == 20) {
			Param.MSGList.remove(19);
			Param.MSGList.add(0, msg);
		} else {
			Param.MSGList.add(0, msg);
		}

		ContentValues value0 = new ContentValues();
		value0.put("img", msg.mMsgImg);
		value0.put("time", msg.mMsgTime);
		value0.put("msg", msg.mMsgContent);

		Param.db.insert("tmpMSG", null, value0);
		value0.clear();

	}

	// 传入的是实际的data域
	private void parseWeather(byte[] data) {
		int weatherType = data[4]; // 天气的标识码;
		byte[] bArea = { data[5], data[6], data[7], data[8] };
		String area = Integer.toBinaryString(Integer.valueOf(
				BytesUtil.bytesToHexString(bArea), 16));

		Log.e("###", "天气类型和区域 " + weatherType + "  " + area);
		int windPower1 = (data[9] >> 4) & 0x0f;
		int windPower2 = data[9] & 0x0f;
		int windDire1 = (data[10] >> 4) & 0x0f;
		int windDire2 = data[10] & 0x0f;
		String text = "";
		try {
			text = new String(data, 11, data.length - 11, "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("###", "天气类型中的内容不能转化为中文!!!");
		}
		Log.d("###", "天气中的内容是:" + text);
		weather = new com.friendlyarm.Serial.Weather(weatherType, area, windPower1, windPower2,
				windDire1, windDire2, text);
	}

	private void parseTyphoon(byte[] data) {
		Param.IsTyphonClear = false;
		int typhoonNo = data[4]; // 台风代号
		byte[] bx = new byte[] { data[5], data[6] };
		byte[] by = new byte[] { data[7], data[8] };

		Log.e("###", "!!!收到的坐标:" + BytesUtil.bytesToHexString(bx));
		Log.e("###", "!!!收到的坐标:" + BytesUtil.bytesToHexString(by));
		int x = Integer.valueOf(BytesUtil.bytesToHexString(bx), 16);
		int y = Integer.valueOf(BytesUtil.bytesToHexString(by), 16);
		Log.d("###", "台风坐标(" + x + "," + y + ")" + ", 台风代号:" + typhoonNo);
		Locater locater = Tools.transferLocate(new Locater(x, y));

		int tWindPower1 = (data[9] >> 4) & 0x0f;
		int tWindPower2 = data[9] & 0x0f;
		int tWindDire1 = (data[10] >> 4) & 0x0f;
		int tWindDire2 = data[10] & 0x0f;
		String text = "";
		try {
			text = new String(data, 11, data.length - 11, "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Log.e("###", "天气类型中的内容不能转化为中文!!!");
		}

		// ArrayList<Locater> list = null;
		if (Param.typhoonMap.containsKey(typhoonNo)) {
			Log.d("###", "当前台风以存在");
			Param.typhoonMap.get(typhoonNo).add(locater);
		} else {
			Log.d("###", "新台风");
			ArrayList<Locater> list = new ArrayList<Locater>();
			Cursor cursor = Param.db.rawQuery(
					"select * from typhoon where typhoonNo=?",
					new String[] { String.valueOf(typhoonNo) });
			if (cursor.moveToFirst()) {
				do {
					int xi = cursor.getInt(cursor.getColumnIndex("locateX"));
					int yi = cursor.getInt(cursor.getColumnIndex("locateY"));
					list.add(new Locater(xi, yi));
				} while (cursor.moveToNext());
			}
			cursor.close();
			Log.d("###", "从数据库中读取到的数据个数:" + list.size());
			list.add(locater);
			Param.typhoonMap.put(typhoonNo, list);
		}
		typhoon = new com.friendlyarm.Serial.Typhoon(typhoonNo, locater, tWindPower1, tWindPower2,
				tWindDire1, tWindDire2, text);
	}

	// 收到后直接解析了,并不在函数中处理了
	private void parseParam(String s) {
		if (s.equals("0273313103")) {
			// SDR正确回复!!!
			Log.d("###setting", "解析到正确的SDR回复.");
			Param.ack = 1;
			// h1.sendEmptyMessage(18);
		} else if (s.equals("0273313003")) {
			// SDR回复错误!!!
			Param.ack = 0;
			// h1.sendEmptyMessage(19);
		} else if (s.equals("020603")) {
			// ack
			Log.d("###setting", "解析到正确的ack回复.");
			Param.ack = 1;
			// h1.sendEmptyMessage(20);
		} else if (s.equals("021503")) {
			// nack
			Param.ack = 0;
			// h1.sendEmptyMessage(21);
		} else if (s.startsWith("027333")) {
			// 解析信道号
			s = s.substring(6, 12);
			Param.mSNN = new String(BytesUtil.hexStringToBytes(s));
			h1.sendEmptyMessage(16);
		} else if (s.startsWith("027336")) {
			// 解析信噪比
			s = s.substring(6, 16);
			Param.mSNR = new String(BytesUtil.hexStringToBytes(s));
			h1.sendEmptyMessage(17);
		} else {
			// showDialog("未知的消息\n"+s);
			// h1.sendEmptyMessage(22);
		}
	}

	private void showDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				(MainActivity) getActivity());
		dialog.setTitle("nack提示");
		dialog.setMessage(message);
		dialog.setPositiveButton("确定", null);
		dialog.create();
		dialog.show();
	}

	// 保留函数,已不在MainActivity中使用.
	private void querySDR() {
		Param.param = "SDR查询";
		Param.SDRAck = -2;

		t1 = new Timer();
		t1.schedule(new TimerTask() {
			int count = 0;

			@Override
			public void run() {
				// 开机主动查询
				Log.e("setting", " " + Param.SDRAck);
				if (count > 0) {
					if (Param.SDRAck == 1) {
						Log.d("setting", "SDR响应ack,t1将被取消");
						h1.sendEmptyMessage(18);
						t1.cancel();
						count = -1;
					} else if (Param.SDRAck == 0) {
						Log.d("setting", "SDR响应nack,t1将被取消");
						h1.sendEmptyMessage(19);
						t1.cancel();
						count = -1;
					}
				}

				if (count == 3) {
					Log.d("setting", "SDR无响应,t1将被取消");
					h1.sendEmptyMessage(22);
					t1.cancel();
					count = -1;
				}

				if (count != -1) {
					Protocol.querySDRState();
					count++;
				}

			}
		}, 0, 1000);
	}

	class SearchThread extends Thread {
		private Object mPauseLock;
		private boolean mPauseFlag;

		public SearchThread() {

			mPauseLock = new Object();
			mPauseFlag = true;
		}

		public void onPause() {
			synchronized (mPauseLock) {
				mPauseFlag = true;
			}
		}

		public void onResume() {
			synchronized (mPauseLock) {
				mPauseFlag = false;
				mPauseLock.notifyAll();
			}
		}

		private void pauseThread() {
			synchronized (mPauseLock) {
				if (mPauseFlag) {
					try {
						mPauseLock.wait();
					} catch (Exception e) {
						Log.v("thread", "fails");
					}
				}
			}
		}

		@Override
		public void run() {
			// Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST); //19
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND); // 10
			String key = "";
			Information out;
			while (true) {
				pauseThread();
				// 用infoMap是否为空来作为该线程的一个开关
				if (!infoMap.isEmpty()) {
					// Log.d("###","进入线程同步的map检测");
					for (Entry<String, Information> entry : infoMap.entrySet()) {
						String k = entry.getKey();
						// Log.d("get", "获取到当前的key是:  " + k);
						Information info = entry.getValue();
						// Log.d("l3","获取到当前的info是:  "+);
						if (info.flag) {
							Log.d("get", "flag里不包含0,表明已经信息已收集完整");
							key = k;
						} else if (info.n == 3) {
							if (info.bflag[0] == '1') {
								Log.d("get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
								key = k;
							} else {
								Log.d("get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
								infoMap.remove(k);
							}
						} else if ((System.currentTimeMillis() - info.start) / 1000 >= 30) {
							Log.d("get", "第三遍还未收到,但是已经超时30s");
							Log.d("get", "此时info的消息是: count->" + info.count
									+ "  发送次数->" + info.n + "  消息列表中的数量->"
									+ info.list.size() + "  发送的标志->"
									+ new String(info.bflag));
							if (info.bflag[0] == '1') {
								Log.d("get", "已经明确的发了三遍了,可是信息不完整,还好头消息还在");
								key = k;
							} else {
								Log.d("get", "已经明确发了三遍了,但是头消息任然不完整,直接舍弃");
								infoMap.remove(k);
							}
						}
					}
					if (key != "") {
						out = infoMap.remove(key);
						Log.d("get获取到的key是", "run: " + key);
						StringBuilder sb = new StringBuilder();
						for (String s : out.list) {
							sb.append(s);
						}
						byte[] realData = BytesUtil.hexStringToBytes(sb
								.toString());
						Log.e("###", "添加进已接收列表的时间戳是:" + key);
						hasReceivedBefore.add(key);
						if (hasReceivedBefore.size() >= 10) {
							hasReceivedBefore.remove(0);
						}
						parseMSG(key, realData);
						key = "";
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}// while(true)
		}

	}

	class DislinkThread extends Thread {
		private Object mPauseLock;
		private boolean mPauseFlag;
		private long startTime = System.currentTimeMillis();
		private boolean isContinue = false;

		public DislinkThread() {

			mPauseLock = new Object();
			mPauseFlag = false;
		}

		public void onReSart(){
			synchronized (mPauseLock) {
				startTime = System.currentTimeMillis();
				isContinue = true;
				mPauseFlag = false;
				mPauseLock.notifyAll();
			}
		}
		public void onPause() {
			synchronized (mPauseLock) {
				mPauseFlag = true;
			}
		}
		
		public void onStart() {
			synchronized (mPauseLock) {
				startTime = System.currentTimeMillis();
				isContinue = true;
				mPauseFlag = false;
				mPauseLock.notifyAll();
			}
		}

		public void onResume() {
			synchronized (mPauseLock) {
				mPauseFlag = false;
				mPauseLock.notifyAll();
			}
		}

		private void pauseThread() {
			synchronized (mPauseLock) {
				if (mPauseFlag) {
					try {
						mPauseLock.wait();
					} catch (Exception e) {
						Log.v("thread", "fails");
					}
				}
			}
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST); // 19
			while (Param.totalFlag) {
				pauseThread();
				while((System.currentTimeMillis() - startTime)/60000 < Param.unlinkTime){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pauseThread();
					if(!isContinue){
						break;
					}
				}
				if(isContinue){
					Protocol.autoUnlink();
					Log.d("###自动拆链", "已发送自动拆链");
				}
			}
		}

	}
}
