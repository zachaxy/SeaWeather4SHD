package com.friendlyarm.serial.seaweather4shd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.friendlyarm.serial.seaweather4shd.R;
import com.friendlyarm.serial.seaweather4shd.dao.MyDatabaseHelper;
import com.friendlyarm.serial.seaweather4shd.tools.BytesUtil;
import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Perf;
import com.friendlyarm.serial.seaweather4shd.tools.Protocol;
import com.friendlyarm.serial.seaweather4shd.tools.Serial;
import com.friendlyarm.serial.seaweather4shd.tools.SysApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;


public class FirstActivity extends Activity {

    String TAG = "###";
    TextView tv1, tv2, tv3, tv4, tv5;
    ImageView img1, img2, img3, img4;
    Button btn1, btn2;
    Handler h1;

    byte[] buf0 = new byte[2048];
    // 用来拼接从串口接收来的数据
    StringBuilder sb0 = new StringBuilder();

    //FirstInitThread mFirstInitThread;  解析数据的线程,不用了

    //#########    与usb相关的部分   ############
    //自定义广播的action,标识用户是否点击了连接usb选项
    final String ACTION_USB_PERMISSION = "com.android.fh.USB_PERMISSION";

    //为handler设置的选项
    final int FH_USB_OK = 100;
    final int FH_USB_NOK = 101;

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;

    //用来接收usb发来的消息.
    private StringBuilder receiveParamAckBuilder = new StringBuilder();

    private ReadThread readThread = new ReadThread();


    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    //用户允许连接usb;
                    connection = usbManager.openDevice(device);
                    //启动usb连接线程
                    new ConnectionThread().start();
                } else {
                    Toast.makeText(FirstActivity.this, "用户拒绝连接USB设备!\n请重新连接USB设备并重启本程序!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        SysApplication.getInstance().addActivity(this);
        initViews();
        //openComs();
        initConf();
        //initDB();
        //注册usb广播
        setFilter();

        //查找设备
        findSerialPortDevice();

        //querySRD();


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
                                                //mFirstInitThread.interrupt();
                                                destroySource();
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
                                .setMessage("接收机内部错误!")
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
                                                //mFirstInitThread.interrupt();
                                                destroySource();
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
                            //mFirstInitThread.interrupt();
                            destroySource();
                            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
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
                                                //mFirstInitThread.interrupt();
                                                destroySource();
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
                                .setMessage("信道设置无法响应,您可以选择继续设置接收机频率,或者退出程序,并联系管理员!")
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
                                                //mFirstInitThread.interrupt();
                                                destroySource();
                                                finish();
                                                System.exit(0);
                                            }
                                        });
                        dialog4.setCancelable(false);
                        dialog4.create();
                        dialog4.show();
                        break;
                    case FH_USB_OK:
                        Toast.makeText(FirstActivity.this, "USB设备已连接!", Toast.LENGTH_LONG).show();
                        tv2.setText("USB通信串口已连接");
                        img2.setImageResource(R.drawable.ok);
                        querySRD();
                        break;
                    case FH_USB_NOK:
                        Toast.makeText(FirstActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                        destroySource();
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);
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
                Log.e("###setting", " " + Param.SDRAck);
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
                    Log.e("setting", "run: sendSDRquery");
                    Protocol.querySDRState();
                    count++;
                }

            }
        }, 0, 1000);
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
                        Log.e("###", "freq:->" + index + "has been set!");
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
                        Log.e("###", "freq响应nack,t2将被取消");
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
                    Log.e("###", "freq无响应,t2将被取消");
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
        }, 0, 1000);
    }

    /***
     * 打开所需的两个串口
     */
    /*private void openComs() {
        Serial.openCom1();
        Serial.openCom3();
        Log.d(TAG, "openSerials: 通信串口已经打开");
        tv2.setText("通信串口已经打开");
        img2.setImageResource(R.drawable.ok);
    }*/

    /***
     * 初始化配置文件,以收集参数信息
     */

    private void initConf() {
        //Param.perf = new Perf(FirstActivity.this);
        if (Param.perf==null) {
            Param.perf = new Perf(getApplicationContext());
        }
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

    /*class FirstInitThread extends Thread {
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
    }*/


    private void findSerialPortDevice() {
        //初始化usb管理器
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //该代码将尝试打开连接遇到的第一个USB设备.但是如果连接了一个hub,那么程序目前无法处理.
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();

                //if条件中满足的情况是一个usb hub,不做处理
                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003)) {
                    // There is a device connected to our Android device. Try to open it as a Serial Port.
                    requestUserPermission();
                    break;
                } else {
                    Toast.makeText(this, "不能识别的USB设备!\n请检查设别连接并重新启动本程序!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            //此时并没有发现usb设备
            Toast.makeText(this, "未检测到USB设备!\n请检查设别连接并重新启动本程序!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void requestUserPermission() {
        //预intent,在接下来展示的是否连接usb的选项中,无论选择是/否,都会发送一个ACTION_USB_PERMISSION的广播,接下来在广播接收函数中再判断是否已连接usb;
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //会弹出确认连接的对话框
        usbManager.requestPermission(device, mPendingIntent);
    }

    private class ConnectionThread extends Thread {

        @Override
        public void run() {
            //得到一个具体的子类 串口设备子类这里是CP2102SerialDevice的对象.
            Param.serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (Param.serialPort != null) {
                if (Param.serialPort.syncOpen()) {
                    Param.serialPort.setBaudRate(Param.BAUD_RATE);
                    Param.serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    Param.serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    Param.serialPort.setParity(UsbSerialInterface.PARITY_NONE);

                    Param.serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    //设置USB的读回调函数
                    Param.serialPort.read(mCallback);

                    readThread.start();


                    h1.obtainMessage(FH_USB_OK).sendToTarget();
                } else {
                    //串口无法打开,可能发生了IO错误或者被认为是CDC,但是并不适合本应用,不做处理.
                    h1.obtainMessage(FH_USB_NOK, "USB设备IO错误,请重新连接!!!").sendToTarget();
                }
            } else {
                //设备无法打开,即便是CDC的驱动也无法加载,不作处理.
                h1.obtainMessage(FH_USB_NOK, "USB设备无法打开,请重新连接!!!").sendToTarget();
            }
        }
    }

    //设置usb读取数据解析的方法.这里只解析接收机ack/nack,该函数不在主线程中,更新UI要注意!!!
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            Log.e("###", "1同步方法不会调用这个函数!!!!!!!!");
            // FIXME: 2016/10/31 使用同步的方法接受数据,绝对不会调用该方法,如果调用将是致命错误!!!
        }
    };

    private class ReadThread extends Thread {
        private AtomicBoolean working = new AtomicBoolean();

        ReadThread() {
            working = new AtomicBoolean(true);
        }

        public void stopReadThread() {
            working.set(false);
        }

        @Override
        public void run() {
            while (working.get()) {
                byte[] buffer = new byte[100];
                int n = Param.serialPort.syncRead(buffer, 0);
                if (n > 0) {
                    byte[] received = new byte[n];
                    System.arraycopy(buffer, 0, received, 0, n);
                    String data = BytesUtil.bytesToHexString(received);
                    if (data == null) {
                        continue;
                    }
                    Log.e("###收到的数据", data);
                    receiveParamAckBuilder.append(data);
                    int begin8383Index = -1;
                    int end8383Index = -1;
                    while ((begin8383Index = receiveParamAckBuilder.indexOf("c0b0b1b2")) >= 0) {

                        end8383Index = receiveParamAckBuilder.indexOf("c0", begin8383Index + 8);
                        //Log.e("###", "run: 判断是否有一个完整的参数包"+receiveParamAckBuilder.toString()+"   endIndex="+end8383Index);
                        while (end8383Index > 0 && end8383Index % 2 != 0) {
                            end8383Index = receiveParamAckBuilder.indexOf("c0", end8383Index + 2);
                        }
                        //Log.e(TAG, "run: 死循环了吗?" );
                        if (end8383Index % 2 == 0) {
                            String paramStr = receiveParamAckBuilder.substring(begin8383Index + 8, end8383Index);
                            // TODO: 2016/9/25 0025 将来考虑把解析的函数放在另一个线程中,该线程中使用一个阻塞队列<String>;
                            parseParamData(paramStr);
                            receiveParamAckBuilder.delete(0, end8383Index + 2);
                        }else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void parseParamData(final String src) {
        if (src.startsWith("02") && src.endsWith("03")) {
            switch (src) {
                case "0273313103": //sdr正常
                    // SDR正常回复
                    Param.SDRAck = 1;
                    Log.e("###parseParamData", "SRD正常回复");
                    break;
                case "0273313003": //sdr异常
                    // SDR错误回复
                    Param.SDRAck = 0;
                    break;
                case "020603":  //ack
                    /*h1.removeMessages(FH_FREQ_NO_RESPONSE);
                    currentFreqIndex++;
                    h1.obtainMessage(FH_FREQ_ACK, currentFreqIndex).sendToTarget();
                    //存在这样一种情况,当前的index==10,原本的想法是也发送给handler,判断10跳转
                    //但是下面的sendFre函数也会执行,异步的...
                    if (currentFreqIndex < 10) {
                        sendFreqs(currentFreqIndex);
                    }*/
                    // Param.ack = 1;
                    if (Param.ChannelAck == -2) {
                        Log.e("###setting串口2init", "信道 ack置为1了");
                        Param.ChannelAck = 1;
                    }
                    break;
                case "021503": //nak
                    if (Param.ChannelAck == -2) {
                        Log.e("###setting串口2init", "信道 ack置为1了");
                        Param.ChannelAck = 1;
                    }
                    break;

            }
        } else {
            //handler:未知的异常回复.
            //h1.obtainMessage(FH_UNKNOWN_MSG).sendToTarget();
            Log.e("###", "parseParamData: 位置的参数回复" + src);
        }

    }

    /***
     * 跳过生命周期的管理;
     */
    void destroySource() {
        unregisterReceiver(usbReceiver);
        //mFHApplication.serialPort.close();
        Log.e("###", "destroySource: 关闭!");
        readThread.stopReadThread();
        Param.serialPort.syncClose();
    }

    // TODO: 2016/9/9 0009 不知道弹框后是否给正在发送消息的信道产生什么影响,所以此处直接退出程序;
    //会执行onDestroy方法;
    @Override
    public void onBackPressed() {
        destroySource();
        super.onBackPressed();
    }
}
