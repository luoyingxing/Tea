package com.lxx.tea;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * author:  luoyingxing
 * date: 2019/3/5.
 */
public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";
    private ListView listView;
    private TextView textView;

    private CommonAdapter<BluetoothDevice> adapter;

    protected BluetoothService.ServiceBinder mServiceBinder;
    private ServiceConnect serviceConnection;

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
                mServiceBinder.pin(adapter.getItem(position));
            }
        });

        textView = findViewById(R.id.tv_tip);
        findViewById(R.id.tv_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        unbindService(serviceConnection);
    }

    private class ServiceConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mServiceBinder = (BluetoothService.ServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionManager = new PermissionManager(BluetoothActivity.this);
        permissionManager.addPermission(new Permission() {
            @Override
            public String getPermission() {
                return Manifest.permission.ACCESS_FINE_LOCATION;
            }

            @Override
            public void onApplyResult(boolean succeed) {
                if (succeed) {
                    if (mServiceBinder == null) {
                        Intent intent = new Intent(BluetoothActivity.this, BluetoothService.class);
                        serviceConnection = new ServiceConnect();
                        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, "没有定位权限无法使用蓝牙功能！", Toast.LENGTH_LONG).show();
                }
            }
        }).apply(BluetoothActivity.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(ScanFinish finish) {
        List<BluetoothDevice> list = mServiceBinder.getBluetoothDeviceList();
        if (null != list) {
            adapter.clear();
            adapter.addAll(list);
        }
    }

    private PermissionManager permissionManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onPermissionsResult(requestCode, permissions, grantResults);
    }

}
