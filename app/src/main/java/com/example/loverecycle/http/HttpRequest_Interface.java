package com.example.loverecycle.http;

import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.AssociationBean;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.beans.TokenBean;
import com.example.loverecycle.beans.UserBean;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HttpRequest_Interface {

    @POST("account")
    rx.Observable<UserBean> register(@Body RequestBody route);//注册

    @POST("account/login")
    rx.Observable<TokenBean> login(@Body RequestBody route);  //登录

    @GET("activity")
    rx.Observable<List<ActivityBean>> getAllActivity(); //获取所有活动

    @GET("association/{id}")
    rx.Observable<AssociationBean> getAssocaition(@Path("id") Long id); //获取所有活动

    @POST("account/{id}/orders")
    rx.Observable<OrderBean> postOrder(@Path("id") Long id, @Body RequestBody route, @Query("access_token") String access_token);  //提交订单
}
