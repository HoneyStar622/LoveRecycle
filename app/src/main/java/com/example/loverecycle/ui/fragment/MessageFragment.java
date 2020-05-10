package com.example.loverecycle.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loverecycle.R;
import com.example.loverecycle.adapters.ActivityAdapter;
import com.example.loverecycle.adapters.OrderAdapter;
import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.http.HttpRequest_Interface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import static com.example.loverecycle.Constants.MODE;
import static com.example.loverecycle.Constants.PREFERENCE_NAME;
import static com.example.loverecycle.Constants.SERVER_IP;

public class MessageFragment extends Fragment {

    public String TAG = "MessageFragment:debug";
    private List<OrderBean> orderList = new ArrayList<>();
    private OrderAdapter adapter;
    private RecyclerView recyclerView;

    public MessageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        initActivity();
        getHttpActivity();

        if(orderList == null) {
            orderList = new ArrayList<>();
        }
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_order);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        if(adapter == null) {
            adapter = new OrderAdapter(getActivity(),orderList);
        }
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), com.example.loverecycle.ui.OrderInfoActivity.class);
                OrderBean temp = orderList.get(position);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("inOrder",temp);
                intent.putExtras(bundle1);
                startActivityForResult(intent, 3);
            }

        });

        /* 设置动画效果 */
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
        scaleInAnimationAdapter.setDuration(1000);
        recyclerView.setAdapter((scaleInAnimationAdapter));
        recyclerView.setItemAnimator(new OvershootInLeftAnimator());

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
        repo.getOrder(userId,tokenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<OrderBean>>() {
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
                    public void onNext(List<OrderBean> repos) {
                        orderList.clear();
                        for (int i = 0;i < repos.size(); i++){
                            orderList.add(repos.get(i));
                        }
                        if (orderList.size() == 0){
                            Toast.makeText(getActivity(), "没有订单", Toast.LENGTH_SHORT).show();
                        }
                        adapter.refresh(orderList);
                    }

                });

        return view;


    }



    public void initActivity() {

    }

    public void getHttpActivity() {


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




}