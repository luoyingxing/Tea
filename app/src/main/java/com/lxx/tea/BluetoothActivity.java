package com.lxx.tea;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyx.frame.adapter.abs.CommonAdapter;
import com.lyx.frame.permission.Permission;
import com.lyx.frame.permission.PermissionManager;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * author:  luoyingxing
 * date: 2019/3/5.
 */
public class BluetoothActivity extends AppCompatActivity implements ConnectThread.ConnectCallBack, AcceptThread.AcceptCallBack {
    private static final String TAG = "BluetoothActivity";
    private ListView listView;
    private TextView textView;

    private CommonAdapter<BluetoothDevice> adapter;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setTitle("蓝牙匹配");

        listView = findViewById(R.id.lv_bluetooth);
        adapter = new CommonAdapter<BluetoothDevice>(this, new ArrayList<BluetoothDevice>(), R.layout.item_bluetooth) {
            @Override
            protected void convert(com.lyx.frame.adapter.abs.ViewHolder holder, BluetoothDevice item, int position) {
                String name = item.getName();
                if (TextUtils.isEmpty(name)) {
                    name = "未知名称";
                }

                holder.setText(R.id.tv_item_bluetooth, name + "\t" + item.getAddress() + "   Bond:" + item.createBond());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pin(position);
            }
        });

        textView = findViewById(R.id.tv_tip);
        findViewById(R.id.tv_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionManager = new PermissionManager(BluetoothActivity.this);
                permissionManager.addPermission(new Permission() {
                    @Override
                    public String getPermission() {
                        return Manifest.permission.ACCESS_FINE_LOCATION;
                    }

                    @Override
                    public void onApplyResult(boolean succeed) {
                        if (succeed) {
                            textView.setText("正在搜索...");
                            openBlueTooth();
                        } else {
                            Toast.makeText(BluetoothActivity.this, "没有定位权限无法使用蓝牙功能！", Toast.LENGTH_LONG).show();
                        }
                    }
                }).apply(BluetoothActivity.this);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        registerReceiver();
    }

    private void openBlueTooth() {
        if (mBluetoothAdapter == null) {
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        mBluetoothAdapter.startDiscovery();
    }

    private void registerReceiver() {
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
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.i(TAG, "action = " + action);

            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    textView.setText("开始扫描...");
                    break;
                case BluetoothDevice.ACTION_FOUND: {
                    //发现蓝牙
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!TextUtils.isEmpty(device.getName())) {
                        adapter.add(device);
                    }
                }
                break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    textView.setText("扫描完成");
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    //配对状态变化广播
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            textView.setText("配对失败");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            textView.setText("配对中...");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            textView.setText("配对成功");
                            new ConnectThread(device, mBluetoothAdapter, BluetoothActivity.this).start();
                            break;
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED: {
                    //连接状态变化广播
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (device.getBondState()) {
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            textView.setText("未连接");
                            Log.i(TAG, "--- 未连接 ---");
                            break;
                        case BluetoothAdapter.STATE_CONNECTING:
                            textView.setText("连接中...");
                            Log.i(TAG, "--- 连接中... ---");
                            break;
                        case BluetoothAdapter.STATE_CONNECTED:
                            textView.setText("连接成功");
                            Log.i(TAG, "--- 连接成功 ---");
                            break;
                    }
                }
                break;
            }
        }
    };

    private PermissionManager permissionManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 配对，配对结果通过广播返回
     *
     * @param position
     */
    public void pin(int position) {
        BluetoothDevice device = adapter.getItem(position);

        if (device == null || !mBluetoothAdapter.isEnabled()) {
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean result = (Boolean) createBondMethod.invoke(device);
                Log.i(TAG, "连接状态:" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ConnectedThread connectedThread;

    @Override
    public void onConnectSucceed(BluetoothSocket serverSocket) {
        connectedThread = new ConnectedThread(serverSocket, null);
        connectedThread.start();
    }

    @Override
    public void onAcceptSucceed(BluetoothSocket serverSocket) {
        Log.i("onAcceptSucceed", "" + serverSocket.toString());
    }
}
