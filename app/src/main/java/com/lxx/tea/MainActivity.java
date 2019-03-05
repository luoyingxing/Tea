package com.lxx.tea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        mStartTV.setOnClickListener(this);
        mResetTV.setOnClickListener(this);

        adapter = new SpAdapter(this, new ArrayList<Tea>());
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTea = adapter.getItem(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private SpAdapter adapter;
    private Tea mTea;

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
    protected void onStart() {
        super.onStart();
        getData();
    }

    public void getData() {
        FinalDb mFinalDb = FinalDb.create(getApplicationContext());
        List<Tea> list = mFinalDb.findAll(Tea.class);

        adapter.clear();
        adapter.addAll(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_start:
                Toast.makeText(getApplicationContext(), "tv_main_start", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_main_reset:
                Toast.makeText(getApplicationContext(), "tv_main_reset", Toast.LENGTH_SHORT).show();
                break;
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
