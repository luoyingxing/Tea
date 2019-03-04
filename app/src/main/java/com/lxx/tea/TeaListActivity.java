package com.lxx.tea;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lyx.frame.adapter.abs.CommonAdapter;
import com.lyx.frame.adapter.abs.ViewHolder;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TeaListActivity
 * <p/>
 * Created by luoyingxing on 2019/3/4.
 */
public class TeaListActivity extends AppCompatActivity {
    private ListView listView;

    private CommonAdapter<Tea> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.lv_list);

        adapter = new CommonAdapter<Tea>(this, new ArrayList<Tea>(), R.layout.item_list) {
            @Override
            protected void convert(ViewHolder holder, final Tea item, int position) {
                holder.setText(R.id.tv_list_index, position + "、");
                holder.setText(R.id.tv_list_name, item.getName());

                holder.getView(R.id.iv_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TeaListActivity.this, EditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("tea", item);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                holder.getView(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FinalDb mFinalDb = FinalDb.create(getApplicationContext());
                        mFinalDb.delete(item);
                        adapter.remove(item);
                    }
                });
            }
        };
        listView.setAdapter(adapter);

        findViewById(R.id.tv_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        FinalDb mFinalDb = FinalDb.create(getApplicationContext());
        List<Tea> list = mFinalDb.findAll(Tea.class);
        Collections.reverse(list); // 倒序排列

        adapter.clear();
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void save(Tea tea) {
        FinalDb mFinalDb = FinalDb.create(getApplicationContext());
        mFinalDb.save(tea);
    }

    private void add() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(TeaListActivity.this)
                .setTitle("添加茶品")
                .setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getApplicationContext(), "茶品名称不能为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            Tea tea = new Tea();
                            tea.setName(name);
                            save(tea);
                            dialog.dismiss();
                            load();
                        }
                    }
                }).setCancelable(false)
                .create().show();
    }


}