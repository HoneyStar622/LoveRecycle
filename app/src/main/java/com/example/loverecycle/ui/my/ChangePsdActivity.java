package com.example.loverecycle.ui.my;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loverecycle.R;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.beans.UserBean;
import com.example.loverecycle.http.HttpHelp;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.example.loverecycle.ui.LoginActivity;
import com.example.loverecycle.ui.MainActivity;
import com.example.loverecycle.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.loverecycle.Constants.CHANGE_PASSWORD_EMAIL;
import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.SERVER_IP;
import static com.example.loverecycle.Constants.UPLOAD_IP;

public class ChangePsdActivity extends AppCompatActivity {

    private Toolbar mTitleBar;
    private TextView tvToolBarName;
    private Button btnCode;
    private Button btnCommit;
    private EditText etEmail;
    private String userEmail;
    private EditText etCode;
    private EditText etPassword1;
    private EditText etPassword2;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psd);
        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        setViews();
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userEmail = bundle.getString("email");

        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();

                if (TextUtils.isEmpty(email)){//邮箱为空
                    XToastUtils.warning("邮箱不能为空");
                }
                else if (!isEmail(email)) {//邮箱格式验证
                    XToastUtils.warning("邮箱格式不正确");
                }
                else if (!email.equals(userEmail)) {
                    XToastUtils.warning("邮箱与注册邮箱不一致");
                }
                else {
                    int num = (int) ((Math.random() * 9 + 1) * 100000);
                    final String code = Integer.toString(num);
                    Log.d("TAGCode", code);
                    final Handler handle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case 0:
                                    XToastUtils.success("发送验证码");
                                    myCountDownTimer.start();
                                    break;
                            }
                        };
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpHelp.getInstance().sendMail(code, email);
                            Message msg = new Message();
                            msg.what = 0;
                            handle.sendMessage(msg);
                        }
                    }).start();


                    etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus){

                            }
                            else {
                                String code1 = etCode.getText().toString();
                                if (code.equals(code1)) {
                                    myCountDownTimer.Success();
                                    XToastUtils.success("验证成功");
                                }
                                else {
                                    XToastUtils.error("验证码错误");
                                }
                            }
                        }
                    });
                }
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password1 = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString();
                if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)){//密码为空
                    XToastUtils.warning("密码不能为空");
                }
                else if (!password1.equals(password2)) {
                    XToastUtils.warning("密码不匹配");
                }
                else {

                    OkHttpClient build = new OkHttpClient.Builder()
                            .connectTimeout(2, TimeUnit.SECONDS)
                            .readTimeout(2, TimeUnit.SECONDS)
                            .writeTimeout(2, TimeUnit.SECONDS)
                            .build();

                    final Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SERVER_IP)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(build)
                            .build();

                    final String email = etEmail.getText().toString();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("email",email);
                    map.put("password",password2);
                    Gson gson = new Gson();
                    String registerData = gson.toJson(map);

                    SharedPreferences sharedPref = mContext.getSharedPreferences(PREFERENCE_NAME, MODE);
                    final SharedPreferences.Editor editor = sharedPref.edit();
                    final String tokenId = sharedPref.getString("Token", null);
                    final Long userId = sharedPref.getLong("UserId", 0);

                    RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),registerData);
                    HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
                    repo.patchMyInfo(userId, body,tokenId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<UserBean>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    if(e instanceof HttpException){
                                        ResponseBody body = ((HttpException) e).response().errorBody();
                                        try {
                                            HashMap<String,Object> error = new Gson().fromJson(body.string(), new TypeToken<HashMap<String,Object>>(){}.getType());
                                            Map<String , Object> embody = ( Map<String , Object>) error.get("error");

                                            Log.d("TAG", embody.get("message").toString());
                                            Log.d("Error",body.string());
                                        } catch (IOException IOe) {
                                            IOe.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onNext(UserBean user1) {
                                    XToastUtils.success("修改成功");
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    Bundle bd = new Bundle();
                                    bd.putInt("fragment_flag", 2);
                                    intent.putExtras(bd);
                                    startActivity(intent);
                                }

                            });
                }
            }
        });

    }

    private void setViews() {
        etEmail = findViewById(R.id.et_change_psd_email);
        etCode = findViewById(R.id.et_change_psd_code);
        etPassword1 = findViewById(R.id.et_change_psd_password1);
        etPassword2 = findViewById(R.id.et_change_psd_password2);
        mTitleBar = findViewById(R.id.titlebar_change_psd);
        tvToolBarName = findViewById(R.id.tv_toolbar_change_psd_name);
        btnCode = findViewById(R.id.btn_change_psd_send);
        btnCommit = findViewById(R.id.btn_change_psd_commit);
        etPassword1.setEnabled(false);
        etPassword2.setEnabled(false);
        mContext = this;
    }

    public boolean isEmail(String email) {
        String str = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            btnCode.setClickable(false);
            btnCode.setText(l/1000+"秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            btnCode.setText("重新获取");
            //设置可点击
            btnCode.setClickable(true);
        }

        public void Success() {
            btnCode.setClickable(false);
            etCode.setEnabled(false);
            btnCode.setText("验证成功");
            etEmail.setEnabled(false);
            etPassword1.setEnabled(true);
            etPassword2.setEnabled(true);
            super.cancel();
        }

    }



}
