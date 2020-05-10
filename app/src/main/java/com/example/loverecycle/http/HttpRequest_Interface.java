package com.example.loverecycle.http;

import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.AssistantBean;
import com.example.loverecycle.beans.AssociationBean;
import com.example.loverecycle.beans.OrderBean;
import com.example.loverecycle.beans.TokenBean;
import com.example.loverecycle.beans.UserBean;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HttpRequest_Interface {

    /*用户管理
    -----------------------------------------------------
     */
    @POST("account")
    rx.Observable<UserBean> register(@Body RequestBody route);//注册

    @POST("account/login")
    rx.Observable<TokenBean> login(@Body RequestBody route);  //登录

    @GET("account/{id}")
    rx.Observable<UserBean> getMyInfo(@Path("id") Long id, @Query("access_token") String access_token);  //获取用户信息

    @PATCH("account/{id}")
    rx.Observable<UserBean> patchMyInfo(@Path("id") Long id, @Body RequestBody route,@Query("access_token") String access_token); //修改用户信息

     /*活动管理
    -----------------------------------------------------
     */

    @GET("activity")
    rx.Observable<List<ActivityBean>> getAllActivity(); //获取所有活动

    @GET("activity/{id}")
    rx.Observable<ActivityBean> getActivity(@Path("id") Long id); //获取一个活动

    @GET("association/{id}/activity")
    rx.Observable<List<ActivityBean>> getAssociationActivity(@Path("id") Long id, @Query("access_token") String access_token); //获取社团所有活动

    @GET("account/{id}/leadActivity")
    rx.Observable<List<ActivityBean>> getMyLeaderActivity(@Path("id") Long id,@Query("access_token") String access_token); //获取我负责的所有活动

    @POST("association/{id}/activity")
    rx.Observable<ActivityBean> postActivity(@Path("id") Long id, @Body RequestBody route, @Query("access_token") String access_token); //创建活动

    /*活动成员管理
    -----------------------------------------------------
     */

    //@GET("account/{1}/leadActivity/rel")


    /*社团管理
    -----------------------------------------------------
     */

    @GET("association/{id}")
    rx.Observable<AssociationBean> getAssocaition(@Path("id") Long id); //获取一个社团

    @PATCH("association/{id}")
    rx.Observable<AssociationBean> patchAssocaition(@Path("id") Long id,@Body RequestBody route); //修改社团信息

    @POST("account/{id}/minister")
    rx.Observable<AssociationBean> postAssocaition(@Path("id") Long id,@Body RequestBody route, @Query("access_token") String access_token); //注册社团

    @GET("account/{id}/assistant")
    rx.Observable<List<AssociationBean>> getMyAssocaition(@Path("id") Long id, @Query("access_token") String access_token); //获取用户参与社团

    @GET("account/{id}/minister")
    rx.Observable<List<AssociationBean>> getMyAdminAssocaition(@Path("id") Long id, @Query("access_token") String access_token); //获取用户管理社团

    @GET("association/{id}/minister")
    rx.Observable<UserBean> getAssocaitionAdmin(@Path("id") Long id, @Query("access_token") String access_token); //获取社团管理员


     /*社团成员管理
    -----------------------------------------------------
     */

    @GET("association/{associationid}/assistant")
    rx.Observable<List<UserBean>> getAssistant(@Path("associationid") Long associationid, @Query("access_token") String access_token); //获取社团成员

    @DELETE("association/{associationid}/assistant")
    rx.Observable<String> deleteAllAssistant(@Path("associationid") Long associationid, @Query("access_token") String access_token); //删除所有成员

    @DELETE("association/{associationid}/assistant/rel/{accountid}")
    rx.Observable<AssistantBean> deleteAssistant(@Path("associationid") Long associationid,@Path("accountid") Long accountid, @Query("access_token") String access_token); //删除社团成员

    @POST("association/{associationid}/assistant/rel/{accountid}")
    rx.Observable<AssistantBean> postAssistant(@Path("associationid") Long associationid,@Path("accountid") Long accountid, @Query("access_token") String access_token); //添加社团成员


    /*订单管理
    -----------------------------------------------------
     */

    @POST("account/{id}/orders")
    rx.Observable<OrderBean> postOrder(@Path("id") Long id, @Body RequestBody route, @Query("access_token") String access_token);  //提交订单

    @PATCH("orders/{id}")
    rx.Observable<OrderBean> patchOrder(@Path("id") Long id, @Body RequestBody route,@Query("access_token") String access_token); //修改订单

    @GET("account/{id}/orders")
    rx.Observable<List<OrderBean>> getOrder(@Path("id") Long id, @Query("access_token") String access_token);  //查询用户订单
}
