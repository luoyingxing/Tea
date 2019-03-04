package com.lxx.tea;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lyx.frame.adapter.abs.CommonAdapter;
import com.lyx.frame.adapter.abs.ViewHolder;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p/>
 * Created by luoyingxing on 2019/3/4.
 */
public class EditActivity extends AppCompatActivity {
    private ListView listView;

    private CommonAdapter<String> adapter;
    private FinalDb mFinalDb;
    private Tea tea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mFinalDb = FinalDb.create(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            tea = (Tea) bundle.getSerializable("tea");
        }

        if (null != tea) {
            setTitle(tea.getName());
        }

        listView = findViewById(R.id.lv_edit);
        findViewById(R.id.tv_edit_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        findViewById(R.id.tv_edit_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = tea.getTimes();
                if (!TextUtils.isEmpty(string)) {
                    String src[] = string.split(",");

                    String des[] = new String[src.length - 1];

                    if (src.length > 0) {
                        System.arraycopy(src, 0, des, 0, src.length - 1);
                    }

                    adapter.clear();
                    adapter.addAll(Arrays.asList(des));

                    StringBuilder buf = new StringBuilder();
                    for (String str : des) {
                        buf.append(str).append(",");
                    }

                    buf.deleteCharAt(buf.length() - 1);

                    tea.setTimes(buf.toString());
                    mFinalDb.update(tea);
                }
            }
        });

        adapter = new CommonAdapter<String>(this, new ArrayList<String>(), R.layout.item_edit) {
            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                int index = position + 1;
                holder.setText(R.id.tv_edit_index, index + "、   第" + index + "泡");
                holder.setText(R.id.tv_edit_time, item + " s");
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final EditText editText = new EditText(EditActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(EditActivity.this)
                        .setTitle("修改时间")
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
                                String times = editText.getText().toString().trim();

                                if (TextUtils.isEmpty(times)) {
                                    Toast.makeText(getApplicationContext(), "时间不能为空！", Toast.LENGTH_SHORT).show();
                                } else {
                                    String str = tea.getTimes();

                                    if (str != null) {
                                        String s[] = str.split(",");
                                        s[position] = times;

                                        StringBuilder buf = new StringBuilder();
                                        for (String string : s) {
                                            buf.append(string).append(",");
                                        }

                                        buf.deleteCharAt(buf.length() - 1);

                                        tea.setTimes(buf.toString());
                                        mFinalDb.update(tea);

                                        adapter.clear();
                                        adapter.addAll(Arrays.asList(s));
                                    }
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (null != tea) {
            String string = tea.getTimes();

            if (!TextUtils.isEmpty(string)) {
                String s[] = string.split(",");
                adapter.addAll(Arrays.asList(s));
            }
        }
    }

    private void add() {
        if (null != tea) {
            String string = tea.getTimes();

            if (!TextUtils.isEmpty(string)) {
                String s[] = string.split(",");
                if (s.length > 19) {
                    Toast.makeText(EditActivity.this, "添加已达上限！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(EditActivity.this)
                .setTitle("添加一泡")
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
                        String times = editText.getText().toString().trim();

                        if (TextUtils.isEmpty(times)) {
                            Toast.makeText(getApplicationContext(), "时间不能为空！", Toast.LENGTH_SHORT).show();
                        } else {
                            String str = tea.getTimes();

                            if (str == null) {
                                str = times;
                            } else {
                                str = str + "," + times;
                            }

                            tea.setTimes(str);

                            mFinalDb.update(tea);

                            String s[] = str.split(",");
                            adapter.clear();
                            adapter.addAll(Arrays.asList(s));

                            dialog.dismiss();
                        }
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }
}