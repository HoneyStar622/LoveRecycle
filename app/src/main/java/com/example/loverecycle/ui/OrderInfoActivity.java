package com.example.loverecycle.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loverecycle.R;
import com.example.loverecycle.adapters.ActivityAdapter;
import com.example.loverecycle.adapters.GridImageAdapter;
import com.example.loverecycle.adapters.MyGridImageAdapter;
import com.example.loverecycle.adapters.OrderAdapter;
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
import com.luck.picture.lib.PictureExternalPreviewActivity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;
import com.xuexiang.xui.widget.toast.XToast;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;


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

public class OrderInfoActivity extends AppCompatActivity {
    private String TAG = "OrderInfo";
    private Toolbar mTitleBar;
    private TextView tvActivityInfo;
    private TextView tvToolBarName;
    private TextView tvAssociation;
    private TextView tvDate;
    private TextView tvPlace;
    private EditText etOrderInfo;
    private ImageView activityImg;
    private MaterialSpinner sCategory;
    private RoundButton btnCommit;

    private Bitmap img;
    private int maxSelectNum = 6;
    private List<LocalMedia> selectList = new ArrayList<>();
    private ArrayList<String> alImgUrl = new ArrayList<>();
    private GridImageAdapter adapter;
    private RecyclerView mRecyclerView;
    private OrderBean mOrder;
    private ActivityBean mActivity;
    private AssociationBean mAssociation;
    private String category;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mOrder = (OrderBean) bundle.getSerializable("inOrder");
        mContext = this;
        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        setViews();
        initWidget();
        mTitleBar.setTitle("");
        setSupportActionBar(mTitleBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String isRig = btnCommit.getText().toString();
                if (isRig.equals("修改订单")) {
                    adapter.setEdit();
                    etOrderInfo.setEnabled(true);
                    sCategory.setEnabled(true);
                    adapter.notifyDataSetChanged();
                    btnCommit.setText("提交订单");
                }
                else {
                    if (selectList.size() == 0) {
                        XToastUtils.warning("图片不能为空");
                    }
                    else if (etOrderInfo.getText().toString().equals("")) {
                        XToastUtils.warning("物品描述不能为空");
                    }
                    else {
                        final Handler handle = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 0:
                                        String imgUrl = "";

                                        for (int i = 0; i < alImgUrl.size();i++) {
                                            imgUrl = imgUrl + alImgUrl.get(i) + ";";
                                        }
                                        Log.d("TAGimgUrl", imgUrl);
                                        putOrder(imgUrl);
                                        break;
                                }
                            };
                        };
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                alImgUrl.clear();
                                for (int i = 0; i < adapter.getList().size(); i++) {
                                    if (adapter.getList().get(i).isCompressed()) {
                                        alImgUrl.add(HttpHelp.getInstance().uploadFile(UPLOAD_IP, adapter.getList().get(i).getCompressPath()));
                                    }
                                    else {
                                        alImgUrl.add(adapter.getList().get(i).getPath());
                                    }

                                }
                                Message msg = new Message();
                                msg.what = 0;
                                msg.obj = alImgUrl;
                                handle.sendMessage(msg);
                            }
                        }).start();
                    }
                }

            }
        });

        //sCategory.setItems(ResUtils.getStringArray(R.array.sort_mode_entry));
        sCategory.setEnabled(false);
        sCategory.setSelectedItem(mOrder.getCategory());
        sCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner spinner, int position, long id, Object item) {
                category = item.toString();
                Log.d(TAG, item.toString());
            }
        });
        sCategory.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                category = mOrder.getCategory();
                Log.d(TAG, "nothing");
            }
        });

    }


    private void setViews() {
        mTitleBar = findViewById(R.id.titlebar_order_info);
        tvActivityInfo = (TextView)findViewById(R.id.tv_order_activity_info_content);
        tvActivityInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        sCategory = findViewById(R.id.s_info_category);
        mRecyclerView = findViewById(R.id.rc_info_photo_select);
        tvToolBarName = findViewById(R.id.tv_toolbar_order_name);
        tvAssociation = findViewById(R.id.tv_order_activity_club_content);
        tvDate = findViewById(R.id.tv_order_activity_date);
        tvPlace = findViewById(R.id.tv_order_activity_place);
        etOrderInfo = findViewById(R.id.et_order_info_info);
        btnCommit = findViewById(R.id.btn_order_info_commit);
        activityImg = findViewById(R.id.order_activity_img);

        tvToolBarName.setText("活动");

        //tvAssociation.setText(mActivity);
        etOrderInfo.setText(mOrder.getInfo());
        String date = mOrder.getDate();
        Log.d("TAGdate", date);
        if (date == null || date.equals("")) {
            date = "未定";
        }
        else {
            date = OrderAdapter.UTCStringtODefaultString(mOrder.getDate());
        }
        tvDate.setText("回收时间：" + date);
        String place = mOrder.getPalce();
        if (place == null || place.equals("")) {
            place = "未定";
        }
        tvPlace.setText("回收地点：" + place);
        etOrderInfo.setEnabled(false);
        category = mOrder.getCategory();
        getActivity(mOrder.getActivityId());


    }

    private void getActivity(Long id) {
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
        repo.getActivity(id)
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
                        mActivity = repos;
                        tvToolBarName.setText(mActivity.getName());
                        tvActivityInfo.setText(mActivity.getInfo());
                        Glide.with(mContext).load(IMG_IP+mActivity.getIcon()).into(activityImg);
                        getAssociation(mActivity.getAssociationId());
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
                        tvAssociation.setText(mAssociation.getName());
                    }

                });

    }


    private void putOrder(String imgUrl) {
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

        HashMap<String,String> map = new HashMap<>();
        String info = etOrderInfo.getText().toString();
        Log.d("TAGputOrder", mOrder.getOrderId().toString());

        SharedPreferences sharedPref = this.getSharedPreferences(PREFERENCE_NAME, MODE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        final String tokenId = sharedPref.getString("Token", null);
        final Long userId = sharedPref.getLong("UserId", 0);

        Log.d("TAGcategory", category);
        Log.d("TAGcategory", mOrder.getCategory());


        map.put("info",info);
        map.put("category",category);
        map.put("picture", imgUrl);
        Gson gson = new Gson();
        String registerData = gson.toJson(map);

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),registerData);
        repo.patchOrder(mOrder.getOrderId(),body,tokenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderBean>() {
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
                    public void onNext(OrderBean repos) {
                        XToastUtils.success("成功");
                        Intent intent = new Intent(mContext,MainActivity.class);
                        Bundle bd = new Bundle();
                        bd.putInt("fragment_flag", 1);
                        intent.putExtras(bd);
                        startActivity(intent);
                        Log.d("TAG", repos.toString());
                    }

                });

    }





    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        String imgPathAll = mOrder.getPicture();
        if (!imgPathAll.isEmpty()){
            String[] icons = imgPathAll.split(";");
            for (int i = 0; i < icons.length;i++) {
                LocalMedia temp = new LocalMedia();
                temp.setCompressed(false);
                temp.setCut(false);
                //temp.setCompressPath(IMG_IP+ icons[i]);
                temp.setPath( icons[i]);
                temp.setPosition(i);
                temp.setPictureType("image/jpeg");

                selectList.add(temp);
            }
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();

        }
        adapter.setSelectMax(maxSelectNum);
        adapter.setNoEdit();
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    Log.d(TAG, pictureType);
                    /*
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);

                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(OrderInfoActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(OrderInfoActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(OrderInfoActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }

                     */
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //获取写的权限
            RxPermissions rxPermission = new RxPermissions(OrderInfoActivity.this);
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限

                                showAlbum();
                            } else {
                                Toast.makeText(OrderInfoActivity.this, "拒绝", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    };

    private void showAlbum() {
        //参数很多，根据需要添加
        PictureSelector.create(OrderInfoActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .theme(R.style.picture_white_style)
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);
                selectList.addAll(images);
                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "onActivityResult: "+images.get(0).getPath());


                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

            }
        }
        if (requestCode == 1 && resultCode == 2) {
            setResult(2);
            finish();
        }
    }


}
