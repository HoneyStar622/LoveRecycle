package com.example.loverecycle.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.loverecycle.R;
import com.example.loverecycle.adapters.GridImageAdapter;
import com.example.loverecycle.adapters.MyAdapter;
import com.example.loverecycle.adapters.OrderAdapter;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.beans.UserBean;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.example.loverecycle.ui.FullyGridLayoutManager;
import com.example.loverecycle.ui.OrderInfoActivity;
import com.example.loverecycle.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;
import com.xuexiang.xui.widget.toast.XToast;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.example.loverecycle.Constants.IMG_IP;
import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.SERVER_IP;
import static com.luck.picture.lib.PictureSelector.*;

public class MyFragment extends Fragment {


    public MyFragment() {
    }


    public String TAG = "MyFragment:debug";
    private List<String> myList = new ArrayList<>();
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView ivUser;
    private TextView tvUser;
    private TextView tvId;
    private TextView tvEmail;
    private TextView tvTime;
    private String imgUrl;
    private PopupWindow pop;
    private View view;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my,container,false);
        XToast.Config.get()
                //位置设置为居中
                .setGravity(Gravity.CENTER);
        ivUser = view.findViewById(R.id.user_img);
        mContext = getActivity();
        tvEmail = view.findViewById(R.id.user_email);
        tvTime = view.findViewById(R.id.user_time_hint);
        tvUser = view.findViewById(R.id.user_name);
        tvId = view.findViewById(R.id.user_id);

        if(myList == null) {
            myList = new ArrayList<>();
        }
        myList.add("修改信息");
        myList.add("修改密码");
        myList.add("我的社团");
        myList.add("应用设置");
        myList.add("意见反馈");
        myList.add("关于我们");
        recyclerView = view.findViewById(R.id.recycler_view_my);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        if(adapter == null) {
            adapter = new MyAdapter(getActivity(),myList);
        }
        recyclerView.setAdapter(adapter);
        adapter.refresh(myList);



        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                String temp = myList.get(position);

                switch (temp) {
                    case "修改信息":

                        startActivityForResult(new Intent(getActivity(),com.example.loverecycle.ui.my.EditMyInfoActivity.class), 4);
                        break;
                    case "修改密码":
                        Intent intent1 = new Intent(getActivity(),com.example.loverecycle.ui.my.ChangePsdActivity.class);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("email",tvEmail.getText().toString());
                        intent1.putExtras(bundle1);
                        startActivityForResult(intent1, 4);
                        break;
                    case "我的社团":
                        startActivityForResult(new Intent(getActivity(),com.example.loverecycle.ui.my.MyAssociationActivity.class), 4);
                        break;
                    case "应用设置":
                        startActivityForResult(new Intent(getActivity(),com.example.loverecycle.ui.my.SettingActivity.class), 4);
                        break;
                    case "意见反馈":
                        startActivityForResult(new Intent(getActivity(),com.example.loverecycle.ui.my.AdviseActivity.class), 4);
                        break;
                    case "关于我们":
                        startActivityForResult(new Intent(getActivity(),com.example.loverecycle.ui.my.InfoActivity.class), 4);
                        break;
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

        SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFERENCE_NAME, MODE);
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

                        Glide.with(getActivity()).load(IMG_IP+repos.getIcon()).into(ivUser);

                        tvEmail.setText(repos.getEmail());
                        //tvTime.setText(repos.);
                        tvUser.setText(repos.getUsername());
                        tvId.setText("ID: "+repos.getAccountId().toString());
                        editor.putString("Name", repos.getUsername());
                        editor.putString("Email", repos.getEmail());
                        editor.commit();
                    }

                });

        return view;


    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




}