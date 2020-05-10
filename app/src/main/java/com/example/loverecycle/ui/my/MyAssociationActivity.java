package com.example.loverecycle.ui.my;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.loverecycle.R;
import com.example.loverecycle.adapters.ActivityAdapter;
import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.AssociationBean;
import com.example.loverecycle.beans.UserBean;
import com.example.loverecycle.http.HttpHelp;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.example.loverecycle.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
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

import static com.example.loverecycle.Constants.IMG_IP;
import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.SERVER_IP;
import static com.example.loverecycle.Constants.UPLOAD_IP;
import static com.luck.picture.lib.PictureSelector.create;
import static com.luck.picture.lib.PictureSelector.obtainMultipleResult;

public class MyAssociationActivity extends AppCompatActivity {

    private Toolbar mTitleBar;
    private TextView mBarName;
    private TextView tvSelect;
    private MaterialSpinner sSelect;
    private ImageView ivClub;
    private TextView tvClubInfo;
    private EditText etClubInfo;
    private TextView tvClubAdmin;
    private EditText etClubAdmin;
    private TextView tvClubPos;
    private EditText etClubPos;
    private Button btnClubGm;
    private Button btnClubActivity;
    private Button btnClubNew;
    private TextView tvClubActivityHint;
    private RecyclerView rcClubActivity;

    private Context mContext;
    private Activity mActivity;
    private Long clubId;
    private Boolean newRig = false;
    private AssociationBean mAssociation;
    private ActivityAdapter mAdapter;
    private List<ActivityBean> activityList = new ArrayList<>();
    private List<AssociationBean> adminAssociation = new ArrayList<>();
    private List<AssociationBean> myAssociation = new ArrayList<>();
    private HashMap<String,AssociationBean> mAssociationMap = new HashMap<>();
    private List<String> mAssociationName = new ArrayList<>();

    private String imgUrl = "";
    private PopupWindow pop;
    private String TAG = "Club Debug";
    private Long accountId;
    private String token;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_association);
        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        setView();
        setDisable();
        setNoAdmin();
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        SharedPreferences sharedPref = this.getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        token = sharedPref.getString("Token", null);
        accountId = sharedPref.getLong("UserId", 0);
        username = sharedPref.getString("Name", null);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rcClubActivity.setLayoutManager(layoutManager);
        if(mAdapter == null) {
            mAdapter = new ActivityAdapter(this,activityList);
        }
        rcClubActivity.setAdapter(mAdapter);
        if (adminAssociation.size() == 0) {
            getMyAdminAssociation(accountId,token);
        }

        Log.d(TAG, "onCreate: "+adminAssociation.size());


        sSelect.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner spinner, int position, long id, Object item) {
                Log.d("下列框", item.toString());
                if ("注册社团".equals(item.toString())) {
                    newRig = true;
                    mBarName.setText("注册社团");
                    setEnable();
                }
                else {
                    mBarName.setText("我的社团");
                    mAssociation = mAssociationMap.get(item.toString());
                    newRig = false;
                    setDisable();
                    etClubInfo.setText(mAssociation.getInfo());
                    Glide.with(mActivity)
                            .load(IMG_IP+mAssociation.getIcon())
                            .into(ivClub);
                    getAssociationAdmin(mAssociation.getAssociationId(),token);
                    getAssociationActivity(mAssociation.getAssociationId(),token);
                    if (accountId.equals(mAssociation.getAccountId())) {
                        etClubPos.setText("管理员");
                        setAdmin();
                    }
                    else {
                        etClubPos.setText("干事");
                        setNoAdmin();
                        getMyActivity(accountId,token);
                    }

                }
                //clubId = item.;
                //Log.d(TAG, item.toString());
            }
        });
        sSelect.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {

            }
        });

        mAdapter.setOnItemClickListener(new ActivityAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if ("管理员".equals(etClubPos.getText().toString())) {
                    Intent intent = new Intent(mContext, com.example.loverecycle.ui.OrderCheckActivity.class);
                    ActivityBean temp = activityList.get(position);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("Activity",temp);
                    intent.putExtras(bundle1);
                    startActivityForResult(intent, 1);
                }
                else {
                    Intent intent = new Intent(mContext, com.example.loverecycle.ui.ActivityMemberActivity.class);
                    ActivityBean temp = activityList.get(position);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("Activity",temp);
                    intent.putExtras(bundle1);
                    startActivityForResult(intent, 0);
                }

            }

        });

        ivClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 激活系统图库，选择一张图片
                RxPermissions rxPermission = new RxPermissions(mActivity);
                rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) {
                                if (permission.granted) {// 用户已经同意该权限
                                    //showAlbum();
                                    showPop();

                                } else {
                                    XToastUtils.success("拒绝访问");
                                }
                            }
                        });
            }
        });

        btnClubNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = etClubPos.getText().toString();
                final String info = etClubInfo.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    XToastUtils.warning("社团名不能为空");
                }
                else if (TextUtils.isEmpty(info)) {
                    XToastUtils.warning("简介不能为空");
                }
                else if (TextUtils.isEmpty(imgUrl)) {
                    XToastUtils.warning("图片不能为空");
                }
                else {
                    final Handler handle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case 0:
                                    String imgUrl1 = msg.obj.toString();
                                    postAssociation(accountId, token,name,info, imgUrl1);
                                    break;
                            }
                        }

                        ;
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String alImgUrl = HttpHelp.getInstance().uploadFile(UPLOAD_IP, imgUrl);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = alImgUrl;
                            handle.sendMessage(msg);

                        }
                    }).start();
                }



            }
        });

        btnClubGm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, com.example.loverecycle.ui.AssociationMemberActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("inAssociation",mAssociation);
                intent.putExtras(bundle1);
                startActivityForResult(intent, 2);
            }
        });

        btnClubActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, com.example.loverecycle.ui.NewActivityActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("inAssociation",mAssociation);
                intent.putExtras(bundle1);
                startActivityForResult(intent, 3);
            }
        });
    }

    private void showPop() {
        View bottomView = View.inflate(mContext, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        create(mActivity)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(1)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .theme(R.style.picture_white_style)
                                .enableCrop(true)// 是否裁剪
                                .compress(true)// 是否压缩
                                .selectionMode(PictureConfig.SINGLE)
                                .forResult(111);
                        break;
                    case R.id.tv_camera:
                        //拍照
                        create(mActivity)
                                .openCamera(PictureMimeType.ofImage())
                                .theme(R.style.picture_white_style)
                                .compress(true)
                                .enableCrop(true)// 是否裁剪
                                .forResult(111);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    private void getMyAdminAssociation(Long userId, final String tokenid) {
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

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getMyAdminAssocaition(userId, tokenid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AssociationBean>>() {
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
                    public void onNext(List<AssociationBean> repos) {
                        adminAssociation.clear();

                        for (int i = 0;i < repos.size(); i++){
                            //Log.d(TAG, repos.get(i).getName());
                            adminAssociation.add(repos.get(i));
                            mAssociationMap.put(adminAssociation.get(i).getName(),adminAssociation.get(i));
                            mAssociationName.add(adminAssociation.get(i).getName());
                        }
                        getMyAssociation(accountId,token);

                        Log.d(TAG, adminAssociation.get(0).getName());
                        Log.d(TAG, adminAssociation.get(1).getName());
                    }

                });
    }

    private void getAssociationAdmin(Long asId, String token) {

        //网络请求
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


        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getAssocaitionAdmin(asId,token)
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
                    public void onNext(UserBean repos) {
                        etClubAdmin.setText(repos.getUsername());
                    }

                });
    }

    private void getMyAssociation(Long userId, String token) {
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

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getMyAssocaition(userId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AssociationBean>>() {
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
                    public void onNext(List<AssociationBean> repos) {

                        myAssociation.clear();

                        for (int i = 0;i < repos.size(); i++){
                            //Log.d(TAG, repos.get(i).getName());
                            myAssociation.add(repos.get(i));
                            mAssociationMap.put(myAssociation.get(i).getName(),myAssociation.get(i));
                            mAssociationName.add(myAssociation.get(i).getName());
                        }
                        mAssociationName.add("注册社团");
                        sSelect.setItems(mAssociationName);
                        sSelect.setText("选择社团");

                    }

                });
    }

    private void getAssociationActivity(Long userId, String token) {
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

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getAssociationActivity(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ActivityBean>>() {
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
                    public void onNext(List<ActivityBean> repos) {

                        activityList.clear();

                        for (int i = 0;i < repos.size(); i++){
                            //Log.d(TAG, repos.get(i).getName());
                            activityList.add(repos.get(i));
                        }
                        if (activityList.size() == 0){
                            XToastUtils.warning("没有活动");
                        }
                        mAdapter.refresh(activityList);
                    }

                });
    }

    private void getMyActivity(Long userId, String token) {
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

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getMyLeaderActivity(userId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ActivityBean>>() {
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
                    public void onNext(List<ActivityBean> repos) {
                        List<ActivityBean> temp = activityList;
                        activityList.clear();

                        for (int i = 0;i < repos.size(); i++){
                            //Log.d(TAG, repos.get(i).getName());
                            for (int t = 0; t < temp.size(); t++) {
                                if (temp.get(t).getActivityId().equals(repos.get(i).getActivityId())) {
                                    activityList.add(repos.get(i));
                                }
                            }
                        }
                        if (activityList.size() == 0){
                            XToastUtils.warning("没有活动");
                        }
                        mAdapter.refresh(activityList);
                    }

                });
    }

    private void getAssociation(Long id) {
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

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getAssocaition(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AssociationBean>() {
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
                    public void onNext(AssociationBean repos) {
                        mAssociation = repos;
                        mBarName.setText(mAssociation.getName());
                    }

                });

    }

    private void postAssociation(Long id, String token, String name, String info, String imgNetUrl) {
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

        map.put("name",name);
        map.put("info",info);
        map.put("accountId",id.toString());
        map.put("icon",imgNetUrl);
        Gson gson = new Gson();
        String registerData = gson.toJson(map);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),registerData);

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.postAssocaition(id,body,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AssociationBean>() {
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
                    public void onNext(AssociationBean repos) {
                        mAssociation = repos;
                        mBarName.setText("我的社团");
                        XToastUtils.success("社团注册成功");
                        mAssociationMap.put(repos.getName(),repos);
                        mAssociationName.add(repos.getName());
                        sSelect.setSelectedItem(repos.getName());
                        setDisable();
                    }

                });
    }



    private void setView() {
        mTitleBar = findViewById(R.id.titlebar_club);
        mBarName = findViewById(R.id.tv_toolbar_club_name);
        tvSelect = findViewById(R.id.tv_club_select);
        sSelect = findViewById(R.id.s_club_select);
        ivClub = findViewById(R.id.iv_club);
        tvClubInfo = findViewById(R.id.tv_club_info);
        etClubInfo = findViewById(R.id.et_club_info);
        tvClubAdmin = findViewById(R.id.tv_club_admin);
        etClubAdmin = findViewById(R.id.et_club_admin);
        tvClubPos = findViewById(R.id.tv_club_postion);
        etClubPos = findViewById(R.id.et_club_postion);
        btnClubGm = findViewById(R.id.btn_club_gm);
        btnClubActivity = findViewById(R.id.btn_club_activity);
        tvClubActivityHint = findViewById(R.id.tv_club_activity_hint);
        rcClubActivity = findViewById(R.id.rc_club_acvitity);
        btnClubNew = findViewById(R.id.btn_club_new);
        mContext = this;
        mActivity = this;
    }
    private void setEnable() {
        //etClubAdmin.setEnabled(true);
        etClubInfo.setEnabled(true);
        etClubPos.setEnabled(true);
        ivClub.setEnabled(true);
        ivClub.setClickable(true);
        tvClubPos.setText("社团名");
        etClubPos.setHint("社团名");
        etClubPos.setText("");
        etClubInfo.setText("");
        ivClub.setImageResource(R.drawable.add_img);
        btnClubNew.setVisibility(View.VISIBLE);
        btnClubActivity.setVisibility(View.GONE);
        btnClubGm.setVisibility(View.GONE);
        tvClubActivityHint.setVisibility(View.GONE);
        rcClubActivity.setVisibility(View.GONE);
    }
    private void setDisable() {
        etClubAdmin.setEnabled(false);
        etClubInfo.setEnabled(false);
        etClubPos.setEnabled(false);
        ivClub.setEnabled(false);
        ivClub.setClickable(false);
        tvClubPos.setText("职位");
        etClubPos.setHint("职位");
        btnClubNew.setVisibility(View.GONE);
        btnClubActivity.setVisibility(View.VISIBLE);
        btnClubGm.setVisibility(View.VISIBLE);
        tvClubActivityHint.setVisibility(View.VISIBLE);
        rcClubActivity.setVisibility(View.VISIBLE);

    }
    private void setNoAdmin() {
        btnClubActivity.setVisibility(View.GONE);
        btnClubGm.setVisibility(View.GONE);
        tvClubActivityHint.setText("我负责的活动");
    }

    private void setAdmin() {
        btnClubActivity.setVisibility(View.VISIBLE);
        btnClubGm.setVisibility(View.VISIBLE);
        tvClubActivityHint.setText("我管理的活动");
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == 111) {// 图片选择结果回调
                imgUrl = "";
                images = obtainMultipleResult(data);
                imgUrl = images.get(0).getCompressPath();
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(mActivity)
                        .load(imgUrl)
                        .into(ivClub);

                Log.d("TAG", "onActivityResult: "+imgUrl);


                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

            }
        }

    }
}
