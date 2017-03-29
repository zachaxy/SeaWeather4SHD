package com.friendlyarm.serial.seaweather4shd;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.friendlyarm.serial.seaweather4shd.tools.Param;
import com.friendlyarm.serial.seaweather4shd.tools.Perf;
import com.friendlyarm.serial.seaweather4shd.view.ZoomImageView;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

import static com.friendlyarm.serial.seaweather4shd.tools.Param.AREA_NO;
import static com.friendlyarm.serial.seaweather4shd.tools.Perf.sp;
import static java.lang.reflect.Array.getInt;

/**
 * Created by zhangxin on 2017/2/20.
 * <p>
 * Description :
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                //.skip(TestActivity.class)
                .init(this);
        Param.perf = new Perf(this);
        initParamBitmaps();
        initParamAreas();
        initParamAreaNo();
    }


    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            Log.e("zxy", "exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Log.e("zxy", "cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            Log.e("zxy", "exceptionClassName:" + exceptionType);
            Log.e("zxy", "throwClassName:" + throwClassName);
            Log.e("zxy", "throwMethodName:" + throwMethodName);
            Log.e("zxy", "throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }

    private void initParamAreas() {
        String[] aaa = {"area_type", "area_wp", "area_text", "area_time"};
        SharedPreferences sp = getSharedPreferences("SP_AREA", Context.MODE_PRIVATE);
        for (int i = 1; i < Param.weaherDetail.length; i++) {
            int weatherType = sp.getInt(aaa[0] + i, 0);
            Param.weaherDetail[i].weatherType = weatherType;
            Param.weaherDetail[i].wind_power = sp.getString(aaa[1] + i, "");
            Param.weaherDetail[i].text = sp.getString(aaa[2] + i, "");
            Param.weaherDetail[i].time = sp.getString(aaa[3] + i, "");

            Param.seaAreasWeatherType[i] = Param.bitmaps[weatherType];
        }

    }

    private void initParamBitmaps() {

        Param.bitmaps = new Bitmap[39];
        Param.bitmaps[0] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[0]);
        Param.bitmaps[1] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[1]);
        Param.bitmaps[2] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[2]);
        Param.bitmaps[3] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[3]);
        Param.bitmaps[4] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[4]);
        Param.bitmaps[5] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[5]);
        Param.bitmaps[6] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[6]);
        Param.bitmaps[7] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[7]);
        Param.bitmaps[8] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[8]);
        Param.bitmaps[9] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[9]);
        Param.bitmaps[10] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[10]);
        Param.bitmaps[11] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[11]);
        Param.bitmaps[12] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[12]);
        Param.bitmaps[13] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[13]);
        Param.bitmaps[14] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[14]);
        Param.bitmaps[15] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[15]);
        Param.bitmaps[16] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[16]);
        Param.bitmaps[17] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[17]);
        Param.bitmaps[18] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[18]);
        Param.bitmaps[19] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[19]);
        Param.bitmaps[20] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[20]);
        Param.bitmaps[21] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[21]);
        Param.bitmaps[22] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[22]);
        Param.bitmaps[23] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[23]);
        Param.bitmaps[24] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[24]);
        Param.bitmaps[25] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[25]);
        Param.bitmaps[26] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[26]);
        Param.bitmaps[27] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[27]);
        Param.bitmaps[28] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[28]);
        Param.bitmaps[29] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[29]);
        Param.bitmaps[30] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[30]);
        Param.bitmaps[31] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[31]);
        Param.bitmaps[32] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[32]);
        Param.bitmaps[33] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[33]);
        Param.bitmaps[34] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[34]);
        Param.bitmaps[35] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[35]);
        Param.bitmaps[36] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[36]);
        Param.bitmaps[37] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[37]);
        Param.bitmaps[38] = BitmapFactory.decodeResource(
                getResources(),
                Param.weatherTypeMap[38]);

        /*//这里全部初始化成了晴天;
        for (int i = 0; i < Param.seaAreasWeatherType.length; i++) {
            Param.seaAreasWeatherType[i] = Param.bitmaps[0];
        }*/
    }

    private void initParamAreaNo() {
        SharedPreferences sp = getSharedPreferences("SP_AREA_NO", Context.MODE_PRIVATE);
        AREA_NO = sp.getInt("AREA_NO", 0);
    }


}

