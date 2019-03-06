package com.lxx.tea;

import android.app.Application;

import com.clj.fastble.BleManager;

/**
 * author:  luoyingxing
 * date: 2019/3/6.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        BleManager.getInstance().init(this);

//        BleManager.getInstance()
//                .enableLog(true)
//                .setReConnectCount(1, 5000)
//                .setSplitWriteNum(20)
//                .setConnectOverTime(10000)
//                .setOperateTimeout(5000);
    }
}
