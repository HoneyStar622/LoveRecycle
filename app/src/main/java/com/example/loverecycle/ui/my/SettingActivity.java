package com.example.loverecycle.ui.my;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.loverecycle.R;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mTitleBar;
    private TextView tvToolBarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mTitleBar = findViewById(R.id.titlebar_setting);
        tvToolBarName = findViewById(R.id.tv_toolbar_setting_name);
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
    }
}
