package com.example.loverecycle.ui.my;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.loverecycle.R;
import com.example.loverecycle.beans.UserBean;
import com.example.loverecycle.http.HttpHelp;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.example.loverecycle.ui.MainActivity;
import com.example.loverecycle.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;

import java.io.FileInputStream;
import java.io.IOException;
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

public class EditMyInfoActivity extends AppCompatActivity {

    private Toolbar mTitleBar;
    private TextView tvToolBarName;
    private ImageView ivIcon;
    private EditText etInfo;
    private EditText etId;
    private EditText etRealm;
    private Button btn;
    private String imgUrl;
    private String imgOld;
    private PopupWindow pop;
    private View view;
    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);
        setViews();
        mTitleBar = findViewById(R.id.titlebar_edit_my);
        tvToolBarName = findViewById(R.id.tv_toolbar_edit_my_name);
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        ivIcon.setOnClickListener(new View.OnClickListener() {
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String info = etInfo.getText().toString();
                final String studentid = etId.getText().toString();
                final String realm = etRealm.getText().toString();
                if (TextUtils.isEmpty(studentid)) {
                    XToastUtils.warning("学号为空");
                }
                else if (TextUtils.isEmpty(realm)) {
                    XToastUtils.warning("真实姓名为空");
                }
                else if (TextUtils.isEmpty(info)) {
                    XToastUtils.warning("简介不能为空");
                }
                else {

                    final Handler handle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            switch (msg.what) {
                                case 0:
                                    String imgUrl1 = msg.obj.toString();
                                    patchMyInfo(info, imgUrl1,studentid,realm);
                                    break;
                            }
                        }

                        ;
                    };
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!imgOld.equals(imgUrl)) {
                                String alImgUrl = HttpHelp.getInstance().uploadFile(UPLOAD_IP, imgUrl);
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = alImgUrl;
                                handle.sendMessage(msg);
                            }

                        }
                    }).start();

                }
            }
        });




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

        SharedPreferences sharedPref = getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final String tokenId = sharedPref.getString("Token", null);
        final Long userId = sharedPref.getLong("UserId", 0);

        HttpRequest_Interface repo = retrofit.create(HttpRequest_Interface.class);
        repo.getMyInfo(userId,tokenId)
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
                        imgOld = repos.getIcon();
                        Glide.with(mActivity).load(IMG_IP+repos.getIcon()).into(ivIcon);
                        etInfo.setText(repos.getInfo());
                        etId.setText(repos.getStudentId());
                        etRealm.setText(repos.getRealm());

                    }

                });

    }
    private void patchMyInfo(String info,String imgUrlNet,String id, String realm) {
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
        map.put("info",info);
        map.put("icon",imgUrlNet);
        map.put("studentId",id);
        map.put("realm",realm);
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

    private void setViews() {
        ivIcon = findViewById(R.id.iv_edit_my_img);
        etInfo = findViewById(R.id.et_edit_my_info);
        etId = findViewById(R.id.et_edit_my_student_id);
        etRealm = findViewById(R.id.et_edit_my_realm);
        mContext = this;
        mActivity = this;
        btn = findViewById(R.id.btn_edit_my_commit);
    }

    private void showAlbum() {
        //参数很多，根据需要添加
        create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(1)// 最大图片选择数量
                .theme(R.style.picture_white_style)
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //.selectionMedia(selectList)// 是否传入已选图片
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                .rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled()// 裁剪是否可放大缩小图片
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
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

                images = obtainMultipleResult(data);
                imgUrl = images.get(0).getCompressPath();
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(mActivity)
                        .load(imgUrl)
                        .into(ivIcon);

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
