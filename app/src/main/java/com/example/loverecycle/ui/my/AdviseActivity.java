package com.example.loverecycle.ui.my;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.loverecycle.R;
import com.example.loverecycle.ui.LoginActivity;
import com.example.loverecycle.ui.MainActivity;
import com.example.loverecycle.utils.XToastUtils;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdviseActivity extends AppCompatActivity {

    private Toolbar mTitleBar;
    private TextView tvToolBarName;
    private Button btn;
    private EditText etAdvise;
    private EditText etEmail;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advise);
        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        setViews();
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etAdvise.getText().toString();
                String email = etEmail.getText().toString();


                if (TextUtils.isEmpty(email)){//邮箱为空
                    XToastUtils.warning("邮箱不能为空");
                }
                else if (!isEmail(email)) {//邮箱格式验证
                    XToastUtils.warning("邮箱格式不正确");
                }
                else if (TextUtils.isEmpty(content)){
                    XToastUtils.warning("内容不能为空");
                }
                else {
                    XToastUtils.success("提交反馈成功");
                    Intent intent = new Intent(mContext, MainActivity.class);
                    Bundle bd = new Bundle();
                    bd.putInt("fragment_flag", 2);
                    intent.putExtras(bd);
                    startActivity(intent);
                }


            }
        });
    }

    private void setViews() {
        btn = findViewById(R.id.btn_advise_commit);
        etAdvise = findViewById(R.id.et_advise_content);
        etEmail = findViewById(R.id.et_advise_email);
        mTitleBar = findViewById(R.id.titlebar_advise);
        tvToolBarName = findViewById(R.id.tv_toolbar_advise_name);
        mContext = this;
    }

    public boolean isEmail(String email) {
        String str = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
