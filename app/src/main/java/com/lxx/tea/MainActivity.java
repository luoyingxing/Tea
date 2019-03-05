package com.lxx.tea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner mSpinner;
    private TextView mTimesTV;
    private TextView mTimerTV;
    private TextView mStartTV;
    private TextView mResetTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinner = findViewById(R.id.spinner_main);
        mTimesTV = findViewById(R.id.tv_main_times);
        mTimerTV = findViewById(R.id.tv_main_timer);
        mStartTV = findViewById(R.id.tv_main_start);
        mResetTV = findViewById(R.id.tv_main_reset);

        mTimesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO  模拟
                countdown();
            }
        });

        mStartTV.setOnClickListener(this);
        mResetTV.setOnClickListener(this);

        timerList = new ArrayList<>();

        adapter = new SpAdapter(this, new ArrayList<Tea>());
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTea = adapter.getItem(position);
                timerList.clear();

                String str = mTea.getTimes();
                if (!TextUtils.isEmpty(str)) {
                    String s[] = str.split(",");
                    timerList.addAll(Arrays.asList(s));
                }
                resetTeaInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private SpAdapter adapter;
    private Tea mTea;
    private List<String> timerList;
    private int position;
    private int countdownTimer;

    private class SpAdapter extends BaseAdapter {
        private List<Tea> list;
        private LayoutInflater mInflater;

        public SpAdapter(Context context, List<Tea> l) {
            list = l;
            mInflater = LayoutInflater.from(context);
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Tea> l) {
            list.addAll(l);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Tea getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //将布局文件转化为View对象
            @SuppressLint("ViewHolder")
            View view = mInflater.inflate(R.layout.item_spinner, null);

            TextView textView = view.findViewById(R.id.tv_main_item);

            Tea tea = list.get(position);

            textView.setText(tea.getName());

            return view;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    public void getData() {
        FinalDb mFinalDb = FinalDb.create(getApplicationContext());
        List<Tea> list = mFinalDb.findAll(Tea.class);

        adapter.clear();
        adapter.addAll(list);
    }

    private void resetTeaInfo() {
        RxTimerUtils.cancel();

        mTimesTV.setText("第1泡");
        position = 0;

        if (timerList == null || timerList.size() == 0) {
            mTimerTV.setText("0");
        } else {
            mTimerTV.setText(timerList.get(0));
        }

        mStartTV.setVisibility(View.VISIBLE);
    }

    private boolean mReady;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_start:
                mReady = true;
                mStartTV.setVisibility(View.INVISIBLE);
                sendReady();
                break;
            case R.id.tv_main_reset:
                mReady = false;
                resetTeaInfo();
                break;
        }
    }

    /**
     * 告诉硬件，准备好了，可以倒计时了
     */
    private void sendReady() {
        //TODO
    }

    /**
     * 告诉设备，准备下一泡
     */
    private void sendNext() {
        //TODO
    }

    /**
     * 硬件回调，告诉客户端，可以开始倒计时了
     */
    private void countdown() {
        if (mReady) {
            countdownTimer = Integer.parseInt(timerList.get(position));

            RxTimerUtils.interval(1, new RxTimerUtils.IRxNext() {
                @Override
                public void doNext(long number) {
                    if (countdownTimer == 0) {
                        RxTimerUtils.cancel();

                        if (position == timerList.size() - 1) {
                            //最后一泡
                            mTimesTV.setText("结束");
                        } else {
                            position++;
                            sendNext();

                            mTimesTV.setText("第" + (position + 1) + "泡");
                            mTimerTV.setText(timerList.get(position));
                        }
                    } else {
                        countdownTimer--;

                        mTimerTV.setText("" + countdownTimer);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bind_device_menu:
                Toast.makeText(getApplicationContext(), "bind_device_menu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_tea_menu:
                Intent intent = new Intent(MainActivity.this, TeaListActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
