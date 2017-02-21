package com.friendlyarm.serial.seaweather4shd.tools;


import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.friendlyarm.serial.seaweather4shd.MainActivity;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangxin on 2016/5/31.
 * 不用继承Application,只当做一个工具类来实现吧
 */
//public class SysApplication extends Application {
public class SysApplication {
    private List<Activity> mList = new LinkedList<Activity>();
    private static SysApplication instance ;//= new SysApplication();

    //我感觉没有必要弄这个单例;
    private SysApplication() {}

    public synchronized static SysApplication getInstance() {
        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /*@Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }*/
}
