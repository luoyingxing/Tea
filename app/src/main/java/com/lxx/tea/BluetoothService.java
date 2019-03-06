package com.lxx.tea;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * <p/>
 * Created by luoyingxing on 2019/3/5.
 */
public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mList;

    @Override
    public void onCreate() {
        super.onCreate();

        mList = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //扫描蓝牙广播
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //配对蓝牙广播
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //连接蓝牙广播
        IntentFilter filter6 = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
        registerReceiver(receiver, filter5);
        registerReceiver(receiver, filter6);

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.startDiscovery();
                }
            }).start();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "开始扫描...");
                    break;
                case BluetoothDevice.ACTION_FOUND: {
                    Log.i(TAG, "发现蓝牙");
                    //发现蓝牙
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!TextUtils.isEmpty(device.getName())) {
                        mList.add(device);
                        EventBus.getDefault().post(new ScanFinish());
                    }
                }
                break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "扫描完成");
                    EventBus.getDefault().post(new ScanFinish());

                    Set<BluetoothDevice> set = mBluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : set) {
                        Log.w(TAG, "BondedDevices: " + device.getName() + "  " + device.getAddress() + "  " + device.getBondState());
                    }

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    //配对状态变化广播
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            Log.i(TAG, "配对失败");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.i(TAG, "配对中...");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.i(TAG, "配对成功: " + device.getName() + "  " + device.getAddress());
//                            new ConnectThread(device, mBluetoothAdapter, BluetoothActivity.this).start();
                            break;
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                    //连接状态变化广播
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            Log.i(TAG, "未连接");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            Log.i(TAG, "连接中...");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            Log.i(TAG, "连接成功");
                            break;
                    }
                }
                break;
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private BluetoothSocket socket;
    private BluetoothSocket mBTSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public class ServiceBinder extends Binder {
        public List<BluetoothDevice> getBluetoothDeviceList() {
            return mList;
        }

        /**
         * 配对，配对结果通过广播返回
         *
         * @param device
         */
        public void pin(final BluetoothDevice device) {
//            mBluetoothAdapter.enable();
//
//            if (device == null || !mBluetoothAdapter.isEnabled()) {
//                return;
//            }
//
//            mBluetoothAdapter.disable();
//
//            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
//                try {
//                    Method createBondMethod = device.getClass().getMethod("createBond");
//                    Boolean result = (Boolean) createBondMethod.invoke(device);
//                    Log.i(TAG, "连接状态:" + result);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    new ConnectThread(device, mBluetoothAdapter, new ConnectThread.ConnectCallBack() {
//                        @Override
//                        public void onConnectSucceed(BluetoothSocket serverSocket) {
//                            Log.i(TAG, "连接状态onConnectSucceed:" + serverSocket.isConnected());
//                        }
//                    }).run();
//                }
//            }).start();


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        BluetoothSocket mBluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                        mBluetoothSocket.connect();
//
//                        OutputStream out = mBluetoothSocket.getOutputStream();
//
//                        out.write(8);
//                        out.flush();
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
        }


        public void connectDevice(final BluetoothDevice device) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
//                        mBluetoothAdapter.stopLeScan(new BluetoothAdapter.LeScanCallback() {
//                            @Override
//                            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//                            }
//                        });

                        socket = device.createRfcommSocketToServiceRecord(UUID.fromString("7d9272e4-820f-42e4-ba53-b8791bb31e95"));

                        socket.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
