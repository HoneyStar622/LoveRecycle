package com.example.loverecycle.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.loverecycle.R;
import com.example.loverecycle.ui.fragment.MainFragment;
import com.example.loverecycle.ui.fragment.MessageFragment;
import com.example.loverecycle.ui.fragment.MyFragment;

import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.PREFERENCE_PACKAGE;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup rg_tab_bar;
    private RadioButton rb_main;
    private RadioButton rb_message;
    private RadioButton rb_my;


    private Toolbar mTitleBar;
    private TextView tvToolBarName;

    //3个Fragment
    private Fragment mainFragment;
    private Fragment messageFragment;
    private Fragment myFragment;

    //标记当前显示的Fragment
    private int mFragmentId = 0;

    // 标记三个Fragment
    public static final int FRAGMENT_MAIN = 0;
    public static final int FRAGMENT_MESSAGE = 1;
    public static final int FRAGMENT_MY = 2;

    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager = getSupportFragmentManager();
        mTitleBar = findViewById(R.id.titlebar0);
        tvToolBarName = findViewById(R.id.tv_toolbar_name0);
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(false); //设置返回键可用
        tvToolBarName.setText("爱心回收");
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);
        //获取第一个单选按钮，并设置其为选中状态
        rb_main = (RadioButton) findViewById(R.id.rb_main);
        rb_message = (RadioButton) findViewById(R.id.rb_message);
        rb_my = (RadioButton) findViewById(R.id.rb_my);
        rb_main.setChecked(true);

        if(savedInstanceState == null){
            //设置第一个Fragment默认选中
            setFragment(FRAGMENT_MAIN);
        }



        Intent intent =getIntent();
        Bundle mbundle = intent.getExtras();
        if (mbundle!= null && mbundle.getInt("fragment_flag") == 1){
            Log.d("测试Intent", String.valueOf(1));
            setFragment(1);
        }




    }

    private void test() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.i("bundle", bundle.getString("Token"));
        //Log.i("bundle", String(bundle.getLong("UserId")));

        Context context = null;
        try {
            context = this.createPackageContext(PREFERENCE_PACKAGE,Context.CONTEXT_IGNORE_SECURITY);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final String tokenId = sharedPref.getString("Token", null);
        final Long userId = sharedPref.getLong("UserId", 0);
        Log.i("token", tokenId);
        Log.i("userId",userId.toString());
    }

    private void setFragment(int index){
        //获取Fragment管理器
        FragmentManager mFragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        //隐藏Fragment
        hideFragments(mTransaction);
        switch (index){
            default:
                break;
            case FRAGMENT_MAIN:
                mFragmentId = FRAGMENT_MAIN;
                rb_main.setChecked(true);
                //显示对应Fragment
                if(mainFragment == null){
                    mainFragment = new MainFragment();
                    mTransaction.add(R.id.container, mainFragment, "main_fragment");
                }else {
                    mTransaction.show(mainFragment);
                }
                break;
            case FRAGMENT_MESSAGE:
                mFragmentId = FRAGMENT_MESSAGE;
                rb_message.setChecked(true);
                if(messageFragment == null){
                    messageFragment = new MessageFragment();
                    mTransaction.add(R.id.container, messageFragment, "message_fragment");
                }else {
                    mTransaction.show(messageFragment);
                }
                break;
            case FRAGMENT_MY:
                mFragmentId = FRAGMENT_MY;
                rb_my.setChecked(true);
                if(myFragment == null){
                    myFragment = new MyFragment();
                    mTransaction.add(R.id.container, myFragment, "my_fragment");
                }else {
                    mTransaction.show(myFragment);
                }
                break;
        }
        //提交事务
        mTransaction.commit();
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_main:
                setFragment(FRAGMENT_MAIN);
                rb_main.setChecked(true);
                break;
            case R.id.rb_message:
                setFragment(FRAGMENT_MESSAGE);
                rb_message.setChecked(true);
                break;
            case R.id.rb_my:
                setFragment(FRAGMENT_MY);
                rb_my.setChecked(true);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //通过onSaveInstanceState方法保存当前显示的fragment
        outState.putInt("fragment_id", mFragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        //通过FragmentManager获取保存在FragmentTransaction中的Fragment实例
        mainFragment = mFragmentManager.findFragmentByTag("main_fragment");
        messageFragment = mFragmentManager.findFragmentByTag("message_fragment");
        myFragment = mFragmentManager.findFragmentByTag("my_fragment");
        //恢复销毁前显示的Fragment
        setFragment(savedInstanceState.getInt("fragment_id"));
    }

    private void hideFragments(FragmentTransaction transaction){
        if(mainFragment != null){
            //隐藏Fragment
            transaction.hide(mainFragment);
        }
        if(messageFragment != null){
            transaction.hide(messageFragment);
        }
        if(myFragment != null){
            transaction.hide(myFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            fManager.beginTransaction().detach(mainFragment).commit();
            fManager.beginTransaction().attach(mainFragment).commit();
        }
    }

}
