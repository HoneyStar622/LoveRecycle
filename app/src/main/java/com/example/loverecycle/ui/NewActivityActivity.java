package com.example.loverecycle.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.loverecycle.R;
import com.example.loverecycle.adapters.ActivityAdapter;
import com.example.loverecycle.adapters.GridImageAdapter;
import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.AssociationBean;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.http.HttpHelp;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.example.loverecycle.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
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

public class NewActivityActivity extends AppCompatActivity {


    private Toolbar mTitleBar;
    private TextView tvToolBarName;
    private EditText etActivityInfo;
    private EditText etActivityName;
    private TextView tvAssociation;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private ImageView activityImg;
    private RoundButton btnCommit;

    private AssociationBean mAssociation;

    private Context mContext;
    private Activity mActivity;
    private String imgUrl = "";
    private PopupWindow pop;
    private String TAG = "Activity Debug";
    private Long accountId;
    private String token;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
        XToast.Config.get()
                .setGravity(Gravity.CENTER);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mAssociation = (AssociationBean) bundle.getSerializable("inAssociation");


        setViews();
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = etActivityName.getText().toString();
                final String info = etActivityInfo.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    XToastUtils.warning("活动名不能为空");
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
                                    postActivity(mAssociation.getAssociationId(), token,name,info, imgUrl1);
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


        activityImg.setOnClickListener(new View.OnClickListener() {
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



    }



    private void setViews() {
        mTitleBar = findViewById(R.id.titlebar_new_activity);
        etActivityInfo = findViewById(R.id.et_new_activity_info_content);
        etActivityInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        etActivityName = findViewById(R.id.et_new_activity_name);
        tvToolBarName = findViewById(R.id.tv_new_activity_toolbar_name);
        tvAssociation = findViewById(R.id.tv_new_activity_club_content);
        tvStartDate = findViewById(R.id.tv_new_activity_start_date);
        tvEndDate = findViewById(R.id.tv_new_activity_end_date);
        btnCommit = findViewById(R.id.btn_new_activity_commit);
        activityImg = findViewById(R.id.new_activity_img);

        tvToolBarName.setText("创建活动");
        etActivityInfo.setText("");
        tvAssociation.setText(mAssociation.getName());
        tvStartDate.setText("开始日期：");
        tvEndDate.setText("结束日期：");

        mContext = this;
        mActivity = this;
    }

    private void postActivity(Long id, String token, String name, String info, String imgNetUrl) {
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
        map.put("icon",imgNetUrl);
        Gson gson = new Gson();
        String registerData = gson.toJson(map);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),registerData);

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.postActivity(id,body,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ActivityBean>() {
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
                    public void onNext(ActivityBean repos) {

                        XToastUtils.success("活动创建成功");


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



    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //获取写的权限
            RxPermissions rxPermission = new RxPermissions(mActivity);
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限
                                showPop();
                            } else {
                                XToastUtils.error("没有权限");
                            }
                        }
                    });
        }
    };

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
                        .into(activityImg);

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
