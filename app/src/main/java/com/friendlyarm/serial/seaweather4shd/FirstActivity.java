package com.friendlyarm.serial.seaweather4shd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.friendlyarm.serial.seaweather4shd.R;
import com.friendlyarm.serial.seaweather4shd.dao.MyDatabaseHelper;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Perf;
import com.friendlyarm.serial.seaweather4shd.tools.Protocol;
import com.friendlyarm.serial.seaweather4shd.tools.Serial;
import com.friendlyarm.serial.seaweather4shd.tools.SysApplication;

import java.util.Timer;
import java.util.TimerTask;


public class FirstActivity extends Activity {

    String TAG = "###";
    TextView tv1, tv2, tv3, tv4, tv5;
    ImageView img1, img2, img3, img4;
    Button btn1, btn2;
    Handler h1;

    byte[] buf0 = new byte[2048];
    // 用来拼接从串口接收来的数据
    StringBuilder sb0 = new StringBuilder();

    FirstInitThread mFirstInitThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        SysApplication.getInstance().addActivity(this);
        initViews();
        openComs();
        initConf();
        //initDB();
        mFirstInitThread = new FirstInitThread();
        mFirstInitThread.start();
        querySRD();


    }

    @SuppressLint("HandlerLeak")
    void initViews() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);

        h1 = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                // super.handleMessage(msg);
                switch (msg.what) {
                    case 0x01:
                        tv3.setText("SDR回复正常");
                        img3.setImageResource(R.drawable.ok);
                        tv4.setText("设置接收机频率:");
                        sendFreqs(0);
                        break;
                    case 0x02:
                        tv3.setText("SDR回复异常");
                        img3.setImageResource(R.drawable.nok);
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(
                                FirstActivity.this);
                        dialog1.setTitle("SDR回复异常")
                                .setMessage("SDR回复异常,xxxxxx")
                                .setPositiveButton("再次查询",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                querySRD();
                                            }
                                        })
                                .setNegativeButton("退出程序",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                mFirstInitThread.interrupt();
                                                finish();
                                                System.exit(0);
                                            }
                                        });
                        dialog1.setCancelable(false);
                        dialog1.create();
                        dialog1.show();
                        break;
                    case 0x03:
                        tv3.setText("SDR无响应");
                        img3.setImageResource(R.drawable.nok);
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(
                                FirstActivity.this);
                        dialog2.setTitle("SDR无响应")
                                .setMessage("SDR无响应,xxxxxx")
                                .setPositiveButton("再次查询",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                //TODO:!!!!!!!!!!!!!!!!!!!
                                                querySRD();
                                            /*Intent intent = new Intent(FirstActivity.this,MainActivity.class);
											startActivity(intent);
											finish();*/
                                            }
                                        })
                                .setNegativeButton("退出程序",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                mFirstInitThread.interrupt();
                                                finish();
                                                System.exit(0);
                                            }
                                        });
                        dialog2.setCancelable(false);
                        dialog2.create();
                        dialog2.show();
                        break;

                    case 0x04:
                        int index = msg.arg1 + 1;
                        tv5.setText(String.valueOf(index) + "/10");
                        img4.setImageResource(R.drawable.ok);
                        if (index == 10) {
                            mFirstInitThread.interrupt();
                            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 0x05:
                        AlertDialog.Builder dialog3 = new AlertDialog.Builder(
                                FirstActivity.this);
                        dialog3.setTitle("信道" + msg.arg1 + "设置异常")
                                .setMessage(
                                        "信道设置异常,您可以选择继续设置接收机频率,或者取消设置,直接进入程序,在程序的参数设置界面仍然可以设置频率!")
                                .setPositiveButton("再次设置",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                sendFreqs(msg.what);
                                            }
                                        })
                                .setNegativeButton("取消设置",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
											/*
											 * finish(); System.exit(0);
											 */
                                                mFirstInitThread.interrupt();
                                                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                        dialog3.setCancelable(false);
                        dialog3.create();
                        dialog3.show();
                        break;
                    case 0x06:
                        AlertDialog.Builder dialog4 = new AlertDialog.Builder(
                                FirstActivity.this);
                        dialog4.setTitle("信道" + msg.arg1 + "设置无法响应")
                                .setMessage("信道设置无法响应,您可以选择继续设置接收机频率,或者退出程序,联系管理员!")
                                .setPositiveButton("再次设置",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                Log.d("settingFreqtwice", "再次设置频率");
                                                sendFreqs(0);
                                            }
                                        })
                                .setNegativeButton("退出程序",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                mFirstInitThread.interrupt();
                                                finish();
                                                System.exit(0);
                                            }
                                        });
                        dialog4.setCancelable(false);
                        dialog4.create();
                        dialog4.show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    void querySRD() {

        // Param.param = "SDR查询";
        Param.SDRAck = -2;

        final Timer t1;

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
                        h1.sendEmptyMessage(0x01);
                        t1.cancel();
                        count = -1;
                    } else if (Param.SDRAck == 0) {
                        Log.d("setting", "SDR响应nack,t1将被取消");
                        h1.sendEmptyMessage(0x02);
                        t1.cancel();
                        count = -1;
                    }
                }

                if (count == 3) {
                    Log.d("setting", "SDR无响应,t1将被取消");
                    h1.sendEmptyMessage(0x03);
                    t1.cancel();
                    count = -1;
                }

                if (count != -1) {
                    Log.d("setting", "run: sendSDRquery");
                    Protocol.querySDRState();
                    count++;
                }

            }
        }, 0, 800);
    }

    void sendFreqs(final int index0) {
        // Log.d(TAG, "sendFreqs: 信道频率设置已完成");
        Param.ChannelAck = -2;
        final Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            int index = index0;
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                if (count > 0) {
                    if (Param.ChannelAck == 1) {
                        Log.d("setting", "freq:->" + index + "has been set!");
                        Message msgOk = new Message();
                        msgOk.what = 0x04;
                        msgOk.arg1 = index;
                        h1.sendMessage(msgOk);
                        index++;
                        count = 0;
                        Param.ChannelAck = -2;
                        if (index == 10) {
                            t2.cancel();
                            count = -1;
                            flag = false;
                        }
                    } else if (Param.ChannelAck == 0) {
                        Log.d("setting", "freq响应nack,t2将被取消");
                        Message msgNOK = new Message();
                        msgNOK.what = 0x05;
                        msgNOK.arg1 = index;
                        t2.cancel();
                        count = -1;
                        flag = false;
                        h1.sendMessage(msgNOK);
                    }
                }

                if (count == 3) {
                    Log.d("setting", "freq无响应,t2将被取消");
                    Message msg = new Message();
                    msg.what = 0x06;
                    msg.arg1 = index;
                    t2.cancel();
                    flag = false;
                    Param.ChannelAck = -2;
                    h1.sendMessage(msg);
                }

                if (flag) {
                    Protocol.sendChannels(Param.mChannels.get(index), index);
                    count++;
                }
            }
        }, 0, 800);
    }

    /***
     * 打开所需的两个串口
     */
    private void openComs() {
        Serial.openCom1();
        Serial.openCom3();
        Log.d(TAG, "openSerials: 通信串口已经打开");
        tv2.setText("通信串口已经打开");
        img2.setImageResource(R.drawable.ok);
    }

    /***
     * 初始化配置文件,以收集参数信息
     */

    private void initConf() {
        Param.perf = new Perf(FirstActivity.this);
        Param.perf.readAll();
        tv1.setText("初始化配置文件完成");
        img1.setImageResource(R.drawable.ok);
    }

    /***
     * 初始化数据库
     */
    private void initDB() {
        Param.dbHelper = new MyDatabaseHelper(this, "SeaMSG.db", null, 1);
        Param.db = Param.dbHelper.getWritableDatabase();
    }

    class FirstInitThread extends Thread {
        public void run() {
            while (!isInterrupted()) { // 非阻塞过程中通过判断中断标志来退出
                int m = HardwareControler.select(com.friendlyarm.Serial.tools.Serial.fd3, 0, 0);
                int n;
                if (m == 1) {
                    while ((n = HardwareControler.read(com.friendlyarm.Serial.tools.Serial.fd3, buf0,
                            buf0.length)) > 0) {
                        String logText = com.friendlyarm.Serial.tools.BytesUtil.bytesToHexString(buf0, n);
                        Log.d("setting串口2init", "接收到的数据长度:" + n);
                        Log.d("setting串口2init", "接收到的数据内容:" + logText);
                        sb0.append(logText);
                    }
                }

                String s = sb0.toString();
                sb0 = sb0.delete(0, sb0.length());

                if (s.length() > 0) {
                    Log.d("setting串口2init", "while结束后接收到的数据是:" + s);
                }

                while (s.length() > 0) {
                    if (s.startsWith("00")) {
                        s = s.substring(2);
                    } else if (s.startsWith("0273313103")) {
                        // SDR正常回复
                        Param.SDRAck = 1;
                        Log.e("setting", "SRD正常回复");
                        if (s.length() >= 10) {
                            s = s.substring(10);
                        } else {
                            break;
                        }
                    } else if (s.startsWith("0273313003")) {
                        // SDR错误回复
                        Param.SDRAck = 0;
                        if (s.length() >= 10) {
                            s = s.substring(10);
                        } else {
                            break;
                        }
                    } else if (s.startsWith("020603")) {
                        // Param.ack = 1;
                        if (Param.ChannelAck == -2) {
                            Log.e("setting串口2init", "信道 ack置为1了");
                            Param.ChannelAck = 1;
                        }
                        if (s.length() >= 6) {
                            s = s.substring(6);
                        } else {
                            break;
                        }
                    } else if (s.startsWith("021503")) {
                        if (Param.ChannelAck == -2) {
                            Log.e("setting串口2init", "信道 ack置为1了");
                            Param.ChannelAck = 1;
                        }
                        if (s.length() >= 6) {
                            s = s.substring(6);
                        } else {
                            break;
                        }
                    } else {
                        // 未知的消息类型
                        Log.e("setting串口2init", "未收集完整的包,跳出继续收集" + s);
                        break;
                    }
                }
                sb0.append(s);
            }
        }
    }
}
