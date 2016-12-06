package com.woniukeji.jianguo.http;


import com.woniukeji.jianguo.entity.Balance;
import com.woniukeji.jianguo.entity.Banner;
import com.woniukeji.jianguo.entity.BindInfo;
import com.woniukeji.jianguo.entity.CityBannerEntity;
import com.woniukeji.jianguo.entity.CityCategory;
import com.woniukeji.jianguo.entity.CityCategoryBase;
import com.woniukeji.jianguo.entity.HttpResult;
import com.woniukeji.jianguo.entity.JobInfo;
import com.woniukeji.jianguo.entity.JobListBean;
import com.woniukeji.jianguo.entity.Jobs;
import com.woniukeji.jianguo.entity.JoinJob;
import com.woniukeji.jianguo.entity.ListTJobEntity;
import com.woniukeji.jianguo.entity.NameAuth;
import com.woniukeji.jianguo.entity.NewUser;
import com.woniukeji.jianguo.entity.PushMessage;
import com.woniukeji.jianguo.entity.RealName;
import com.woniukeji.jianguo.entity.Resume;
import com.woniukeji.jianguo.entity.RxJobDetails;
import com.woniukeji.jianguo.entity.School;
import com.woniukeji.jianguo.entity.User;
import com.woniukeji.jianguo.entity.Version;
import com.woniukeji.jianguo.entity.WageLog;

import java.util.List;
import cn.leancloud.chatkit.LCChatKitUser;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by invinjun on 2016/6/1.
 */

public interface MethodInterface {
        @POST("I_user_login_Insert_Servlet")
        Observable<User> Register(@Query("only") String only, @Query("phone") String phone, @Query("password") String password, @Query("sms_code") String sms_code);
/**
*获取短信验证码
*/
        @GET("I_SMS_Servlet")
        Observable<HttpResult<String>> getSMS(@Query("only") String only, @Query("phone") String phone);
/**
*手机号码密码登陆
*/
        @GET("I_user_login_Login_Servlet")
        Observable<User> Login(@Query("only") String only, @Query("phone") String phone, @Query("password") String password);
/**
*更换手机号码
*@author invinjun
*created at 2016/7/19 10:57
*/
        @POST("T_user_login_ChangeTel_Servlet")
        Observable<HttpResult<String>> changeTel(@Query("only") String only,@Query("login_id") String login_id,@Query("tel") String tel,@Query("sms_code") String sms_code);

/**
*兼职详情获取
*/
        @GET("T_Job_info_Select_Servlet")
        Observable<RxJobDetails> getJobDetail(@Query("only") String only, @Query("login_id") String login_id, @Query("job_id") String job_id);


/**
*拉取城市和兼职类型（兼职列表界面使用）
*/
        @GET("T_Job_Area_City_List_User_Servlet")
        Observable<HttpResult<CityCategory>> getCityCategory(@Query("only") String only);
/**
*微信绑定账户接口
*/
        @POST("T_user_wx_Insert_Servlet")
        Observable<HttpResult<String>> postWX(@Query("only") String only, @Query("login_id") String login_id,@Query("openid") String openid,@Query("nickname") String nickname
        ,@Query("sex") String sex,@Query("province") String provice,@Query("city") String city,@Query("headimgurl") String headimgurl,
        @Query("unionid") String unionid );


        @GET("T_user_money_LoginId_Servlet")
        Observable<Balance> getWallet(@Query("only") String only, @Query("login_id") String login_id);
 /**
 *报名接口
 *@author invinjun
 *created at 2016/7/26 16:43
 */
        @GET("T_enroll_Insert_Servlet")
        Observable<HttpResult<String>> postSign(@Query("only") String only, @Query("login_id") String login_id,@Query("job_id") String job_id);
/**
*查询推送记录接口
*@author invinjun
*created at 2016/7/26 16:44
*/
        @GET("T_push_List_Servlet")
        Observable<HttpResult<PushMessage>> getPush(@Query("only") String only, @Query("login_id") String login_id);




/**
*收藏某兼职
*/
        @POST("T_attent_Insert_Servlet")
        Observable<HttpResult<String>> postAttention(@Query("only") String only, @Query("login_id") String login_id, @Query("follow") String follow, @Query("collection") String collection);
/**
*提现验证码接口
*/
        @GET("IsmsCkeck")
        Observable<HttpResult<String>> checkSms(@Query("only") String only, @Query("tel") String tel);
/**
*获取城市和轮播图 首页
*/
        @GET("T_city_Select_Servlet")
        Observable<HttpResult<CityBannerEntity>> getCityBanner(@Query("only") String only);


/**
*拉取热门兼职列表
*/
        @GET("T_job_List_Servlet")
        Observable<HttpResult<Jobs>> getHotJobs(@Query("only") String only, @Query("hot") String hot,@Query("city_id") String cityid,@Query("count") String count);
/**
*浏览兼职记录
*/
        @POST("T_job_Look_Servlet")
        Observable<Void> postLook(@Query("only") String only, @Query("login_id") String login_id, @Query("job_id") String job_id);
/*------------------------------------------新版分割线--------------------------------------------------------------------------------------------------------------------------------*/
        /**
         *新版接口
         *@author invinjun
         *created at 2016/11/14 12:07
         */

        @POST("sign")
        Observable<HttpResult> sign(@Query("app_id") String app_id,@Query("tel") String tel, @Query("code") String smsCode, @Query("passwd") String password,@Query("type") String type);

        @GET("validateCode")
        Observable<HttpResult> sendCode(@Query("app_id") String app_id,@Query("tel") String tel,@Query("type") String type);

        @POST("login")
        Observable<HttpResult<NewUser>> passwdLogin(@Query("app_id") String app_id, @Query("tel") String tel, @Query("passwd") String password, @Query("type") String type);

        @POST("login")
        Observable<HttpResult<NewUser>> smsLogin(@Query("app_id") String app_id, @Query("tel") String tel, @Query("code") String code, @Query("type") String type);

        @POST("login")
        Observable<HttpResult<NewUser>> autoLogin(@Query("app_id") String app_id, @Query("token") String token,  @Query("type") String type);

        @PUT("{app_id}/passwdReset")
        Observable<HttpResult> passReset(@Path("app_id") String app_id,@Query("tel") String tel, @Query("code") String smsCode, @Query("passwd") String password);
        /**
         *查询兼职种类福利标签等
         */
        @GET("join/label")
        Observable<HttpResult<CityCategoryBase>> getCityCategory();
        /**
         *个人资料上传
         */
        @PUT("user/edit")
        Observable<HttpResult<String>> postResume(@Query("app_id") String app_id,@Query("sign") String sign,@Query("timestamp") String timestamp,
                                                  @Query("name") String name, @Query("nickname") String nickname,
                                                  @Query("school_id") int school,
                                                  @Query("height") String height,
                                                  @Query("is_student") String student, @Query("head_img_url") String name_image,
                                                  @Query("intoschool_date") String intoschool_date, @Query("birth_date") String birth_date,
                                                  @Query("sex") String sex,@Query("email") String email,@Query("qq") int qq,@Query("introduce") String introduce);


        /**
        *编辑用户信息
        */
        @PUT("user/edit")
        Observable<HttpResult> edit(@Query("app_id") String app_id,@Query("sign") String sign,@Query("timestamp") String timestamp,
               @Query("name") String name, @Query("nickname") String nickName,
             @Query("school") String school, @Query("height") String height,@Query("is_student") String student,
           @Query("head_img_url") String head_img_url,@Query("intoschool_date") String intoschool_date,
             @Query("birth_date") String birth,@Query("sex") String sex,@Query("email") String email,@Query("qq") String qq,@Query("introduce") String introduce);
        /**
        *用户信息查询
        */
        @GET("user/info")
        Observable<HttpResult<Resume>> userInfo(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp);


        /**
         *学校信息查询
         */
        @GET("user/school")
        Observable<HttpResult<List<School>>> school(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp, @Query("city_code") String city_id);

        /**
         *兼职详情
         */
        @GET("detail/{job_id}")
        Observable<HttpResult> jobDetail(@Path("job_id") String job_id,@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp);
/**
*兼职报名信息查询
*/
        @GET("join/info/{job_id}")
        Observable<HttpResult> joinInfo(@Path("job_id") String job_id,@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp);


        /**
         *兼职列表查询
         */
        @GET("job/user/list/{city_id}")
        Observable<HttpResult<List<JobListBean>>> jobListOfType(@Path("city_id") String city_id, @Query("area_id") String area_id, @Query("job_type_id") String job_type_id, @Query("order_field") String order_field, @Query("pageNum") String pageNum ,@Query("first_page_type") int first_page_type);

/**
*兼职列表查询
*/
        @GET("job/user/list/{city_id}")
        Observable<HttpResult<List<JobListBean>>> jobList(@Path("city_id") String city_id, @Query("area_id") String area_id, @Query("job_type_id") String job_type_id, @Query("order_field") String order_field, @Query("pageNum") String pageNum );
/**
*兼职详情接口
*/
        @GET("job/user/detail/{job_id}")
        Observable<HttpResult<JobInfo>> getJobDetailNew(@Path("job_id") String job_id,@Query("token") String token);

/**
*报名接口
*/
        @POST("join/status")
        Observable<HttpResult> join(@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp,@Query("job_id") String job_id);
/**
*主页bannner接口
*/
        @GET("banner")
        Observable<HttpResult<List<Banner>>> getBanner();
/**
*兼职报名信息列表
*/
        @GET("join/user")
        Observable<HttpResult<List<JoinJob>>> getSignJob(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp, @Query("pageNum") int pageNum);

        /**
         *取消报名请求
         */
        @PUT("join/status")
        Observable<HttpResult> joinStatus(@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp,@Query("job_id") String job_id,@Query("status") int status);

        @GET("wallet/money")
        Observable<HttpResult<Balance>> getMoney(@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp,@Query("type") int type);

        @GET("wallet/log")
        Observable<HttpResult<List<WageLog>>> getWageLog(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp, @Query("type") int type, @Query("pageNum") int pageNum);

        @GET("wallet/info")
        Observable<HttpResult<List<BindInfo>>> getWalletInfo(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp);

        @POST("wallet/info")
        Observable<HttpResult<String>> putWalletInfo(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp,
                                                            @Query("type") int type, @Query("name") String name,
                                                            @Query("number") String number,  @Query("receive_name") String receive_name);
        @DELETE("wallet/info")
        Observable<HttpResult<String>> deleteWalletInfo(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp,
                                                     @Query("id") long type
                                                     );
        /**
         *提现接口
         */
        @PUT("wallet/money")
        Observable<HttpResult<String>> postMoney(@Query("app_id") String app_id, @Query("sign") String sign,@Query("timestamp") String timestamp,@Query("withdrawCode") String code,@Query("tel") String tel,@Query("param") String param);
        /**
         *实名认证
         */
        @POST("auth/info")
        Observable<HttpResult<String>> postRealName(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp,
                                                    @Query("front_img_url") String front_img_url,@Query("behind_img_url") String behind_img_url,
                                                    @Query("realname") String realname,
                                                    @Query("IDcard") String IDcard,
                                                    @Query("sex") String sex,
                                                    @Query("type") int type//个人 1 机构 2 内部 3 用户实名4
                                                    );

        @GET("auth/info")
        Observable<HttpResult<RealName>> getAuthInfo(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp);

        @POST("user/opinion")
        Observable<HttpResult<String>> postFeedback(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp,@Query("context") String context,@Query("type") int type);


        @GET("version")
        Observable<HttpResult<Version>> getVersion();



        /**
         *查询果聊用户信息
         */
        @GET("user/im")//T_UserGroup_Servlet
        Observable<HttpResult<List<LCChatKitUser>>> getTalkUser(@Query("app_id") String app_id, @Query("sign") String sign, @Query("timestamp") String timestamp,@Query("ids") String id,@Query("type") int type);

}
