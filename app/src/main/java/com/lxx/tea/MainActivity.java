package com.lxx.tea;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
                Toast.makeText(getApplicationContext(), "add_tea_menu", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
