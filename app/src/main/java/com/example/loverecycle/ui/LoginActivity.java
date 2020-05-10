package com.example.loverecycle.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.loverecycle.R;
import com.example.loverecycle.beans.TokenBean;
import com.example.loverecycle.beans.UserBean;
import com.example.loverecycle.http.HttpHelp;
import com.example.loverecycle.http.HttpRequest_Interface;
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

import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.PREFERENCE_PACKAGE;
import static com.example.loverecycle.Constants.SERVER_IP;

public class LoginActivity extends AppCompatActivity {

    private boolean isRig = false;
    private boolean codeRig = false;
    private Bitmap bitmap;
    private EditText user;
    private Button btnSignIn;
    private Button btnCode;
    private EditText etCode;
    private EditText etEmail;
    private EditText new_email;
    private EditText password;
    private EditText new_password;
    private EditText con_password;
    private RadioGroup radioGroup;
    private RadioButton btnLogin;
    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        setViews();

        Context context = null;
        try {
            context = this.createPackageContext(PREFERENCE_PACKAGE,Context.CONTEXT_IGNORE_SECURITY);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = new_email.getText().toString();

                if (TextUtils.isEmpty(email)){//邮箱为空
                    XToastUtils.warning("邮箱不能为空");
                }
                else if (!isEmail(email)) {//邮箱格式验证
                    XToastUtils.warning("邮箱格式不正确");
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
                                    codeRig = true;
                                    btnCode.setClickable(false);
                                    etCode.setEnabled(false);
                                    new_email.setEnabled(false);
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton rbutton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                if (rbutton.getText().toString().equals("Register")) {
                    btnSignIn.setText("注册");
                    findViewById(R.id.ll_login).setVisibility(View.GONE);
                    findViewById(R.id.ll_register).setVisibility(View.VISIBLE);
                    isRig = true;
                }
                else {
                    findViewById(R.id.ll_login).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_register).setVisibility(View.GONE);
                    btnSignIn.setEnabled(true);
                    etEmail.setEnabled(true);
                    password.setEnabled(true);
                    btnSignIn.setText("登录");
                    isRig = false;
                }
            }
        });





        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //注册时
                String username = user.getText().toString();
                String userEmail = etEmail.getText().toString();
                String newEmail = new_email.getText().toString();
                String passwords = password.getText().toString();//字符串密码
                String password1 = new_password.getText().toString();
                String password2 = con_password.getText().toString();

                if(isRig) {
                    if (TextUtils.isEmpty(newEmail)){//邮箱为空
                        XToastUtils.warning("邮箱不能为空");
                    }
                    else if (!isEmail(newEmail)) {//邮箱格式验证
                        XToastUtils.warning("邮箱格式不正确");
                    }
                    else if (TextUtils.isEmpty(username)){//用户名为空
                        XToastUtils.warning("用户名不能为空");
                    }
                    else if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)){//密码为空
                        XToastUtils.warning("密码不能为空");
                    }
                    else if (!password1.equals(password2)) {
                        XToastUtils.warning("密码不匹配");
                    }
                    else if (!codeRig) {
                        XToastUtils.warning("邮箱未验证");
                    }
                    else {
                        //网络请求，查询数据库
                        if(isConn(LoginActivity.this) == false) {
                            XToastUtils.error("网络连接失败");
                        }
                        else {
                            XToastUtils.toast("注册中");

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

                            HashMap<String,String> map = new HashMap<>();
                            map.put("email",newEmail);
                            map.put("password",password2);
                            map.put("username",username);
                            Gson gson = new Gson();
                            String registerData = gson.toJson(map);

                            RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),registerData);
                            HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
                            repo.register(body)
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
                                                    XToastUtils.error("邮箱或用户名已存在");
                                                    Log.d("TAG", embody.get("message").toString());
                                                    Log.d("Error",body.string());
                                                } catch (IOException IOe) {
                                                    IOe.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onNext(UserBean user1) {

                                            findViewById(R.id.ll_login).setVisibility(View.VISIBLE);
                                            findViewById(R.id.ll_register).setVisibility(View.GONE);
                                            btnSignIn.setText("登录");
                                            isRig = false;
                                            btnLogin.setChecked(true);
                                            XToastUtils.success(user1.getUsername()+"注册成功");
                                        }

                                    });
                        }
                    }
                }
                //登陆时
                else {
                    userEmail = "1299927852@qq.com";
                    passwords = "wanggh8";
                    if (TextUtils.isEmpty(userEmail)){//邮箱为空
                        XToastUtils.warning("邮箱不能为空");
                    }
                    else if (!isEmail(userEmail)) {//邮箱格式验证
                        XToastUtils.warning("邮箱格式不正确");
                    }
                    else if (passwords.equals("")) {//密码为空
                        XToastUtils.warning("密码不能为空");
                    }
                    else {//都不为空

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

                        HashMap<String,String> map = new HashMap<>();
                        map.put("email",userEmail);
                        map.put("password",passwords);
                        Gson gson = new Gson();
                        String loginData = gson.toJson(map);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),loginData);
                        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
                        repo.login(body)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<TokenBean>() {
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
                                                XToastUtils.error("邮箱或密码不正确");
                                                btnSignIn.setEnabled(true);
                                                etEmail.setEnabled(true);
                                                password.setEnabled(true);
                                            } catch (IOException IOe) {
                                                IOe.printStackTrace();
                                            }
                                        }
                                    }


                                    @Override
                                    public void onNext(TokenBean ctoken) {
                                        String token = ctoken.getId();
                                        Long cid = ctoken.getUserId();

                                        editor.putString("Token", token);
                                        editor.putLong("UserId", cid);
                                        editor.commit();
                                        XToastUtils.success("登录成功");
                                        //Toast.makeText(LoginActivity.this, ctoken.getId(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        //Toast.makeText(LoginActivity.this, "Invalid Password.", Toast.LENGTH_SHORT).show();
                                        Bundle bd = new Bundle();
                                        bd.putString("Token", token);
                                        bd.putLong("UserId", cid);
                                        intent.putExtras(bd);
                                        startActivity(intent);
                                    }

                                });

                    }
                }
            }
        });


    }

    private void setViews() {
        user = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        new_email = findViewById(R.id.newEmail);
        password = findViewById(R.id.password);
        new_password = findViewById(R.id.newPassword);
        con_password = findViewById(R.id.confirmPassword);
        radioGroup = findViewById(R.id.radioGroup);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCode = findViewById(R.id.btn_login_send);
        etCode = findViewById(R.id.et_login_code);
        btnSignIn.setClickable(false);
        btnLogin = findViewById(R.id.login);
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
            new_email.setEnabled(false);
            btnSignIn.setClickable(true);
            super.cancel();
        }

    }



    public static boolean isConn(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isEmail(String email) {
        String str = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


}