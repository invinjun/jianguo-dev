package com.woniukeji.jianguo.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.db.CityAreaDao;
import com.woniukeji.jianguo.db.TypeDao;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CityCategoryBase;
import com.woniukeji.jianguo.entity.JobListBean;
import com.woniukeji.jianguo.entity.NewUser;
import com.woniukeji.jianguo.entity.User;
import com.woniukeji.jianguo.activity.main.MainActivity;
import com.woniukeji.jianguo.eventbus.CityEvent;
import com.woniukeji.jianguo.http.BackgroundSubscriber;
import com.woniukeji.jianguo.http.BackgroundSubscriberOnerror;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriberOnError;
import com.woniukeji.jianguo.http.SubscriberOnNextErrorListener;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.ActivityManager;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.LogUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.leancloud.chatkit.LCChatKit;
import cn.sharesdk.framework.ShareSDK;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

public class SplashActivity extends BaseActivity  {

    @BindView(R.id.img_splash) ImageView imgSplash;
    private Handler mHandler = new Myhandler(this);
    private Context context = SplashActivity.this;
    private String mCityId ="0";
    private String mCityName ="";
    private SubscriberOnNextErrorListener<NewUser> newUserSubscriberOnNextErrorListener;
    private SubscriberOnNextListener<CityCategoryBase> cityBannerEntitySubscriberOnNextListener;
    private CityAreaDao cityAreaDao;

    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity splashActivity = (SplashActivity) reference.get();
            switch (msg.what) {
                case 1:
                    //如果本地的登录信息登录失败 则删除本地缓存的用户信息，并跳转到首页
                    splashActivity.startActivity(new Intent(splashActivity, MainActivity.class));
                    SPUtils.deleteParams(splashActivity);
                    String ErrorMessage = (String) msg.obj;
                    TastyToast.makeText(splashActivity, ErrorMessage, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    splashActivity.finish();
                    break;
                case 2:
                    break;
                case 3:
                    String sms = (String) msg.obj;
                    TastyToast.makeText(splashActivity, sms, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setContentView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        //初始化SDK
        ShareSDK.initSDK(this);

//        Picasso.with(context).load(R.mipmap.splash).into(imgSplash);
    }

    @Override
    public void initListeners() {


        //接口监听回调
        newUserSubscriberOnNextErrorListener=new SubscriberOnNextErrorListener<NewUser>() {
            @Override
            public void onNext(NewUser newUser) {
                super.onNext(newUser);
                saveToSP(newUser);
            }

            @Override
            public void onError(String mes) {
                super.onError(mes);
                TastyToast.makeText(SplashActivity.this, mes, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };
//城市和职业类型保存至数据库，并设置背景为默认选择城市
        cityAreaDao = new CityAreaDao(SplashActivity.this);
        cityBannerEntitySubscriberOnNextListener= new SubscriberOnNextListener<CityCategoryBase>() {
            @Override
            public void onNext(CityCategoryBase cityEntity) {
                TypeDao typeDao=new TypeDao(SplashActivity.this);
                for (int i = 0; i < cityEntity.getCity_list().size(); i++) {
                    cityAreaDao.addCityDate(cityEntity.getCity_list().get(i).getCityName(),cityEntity.getCity_list().get(i).getCode());
                    for (CityCategoryBase.CityListBean.AreaListBean areaListBean : cityEntity.getCity_list().get(i).getAreaList()) {
                        cityAreaDao.addAreaDate(areaListBean.getAreaName(), String.valueOf(areaListBean.getId()),cityEntity.getCity_list().get(i).getCode());
                    }
                }
                for (int i = 0; i < cityEntity.getType_list().size(); i++) {
                    typeDao.addTypeDate(cityEntity.getType_list().get(i).getName(), cityEntity.getType_list().get(i).getId());
                }
                cityAreaDao.updateData(cityEntity.getCity_list().get(0).getCode());
            }

        };
        if (cityAreaDao.queryData()==0){
            HttpMethods.getInstance().getCityAndCategory(new BackgroundSubscriber<CityCategoryBase>(cityBannerEntitySubscriberOnNextListener,this));
        }

    }

    @Override
    public void initData() {

    }

    @Override
    public void addActivity() {
        ActivityManager.getActivityManager().addActivity(SplashActivity.this);
    }

    /**
    *获取到位置信息
    *@author invinjun
    *created at 2016/7/1 15:31
    */
    //以下为后者的举例：


    private void saveToSP(NewUser user) {
        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_WQTOKEN, user.getToken() != null ? user.getToken() : "");
        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_TEL, user.getTel() != null ? user.getTel() : "");
        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_USER_STATUS, user.getAuth_status());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_PERMISSIONS, user.getStatus());
        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_QNTOKEN, user.getQiniu_token());
        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_RESUMM, user.getResume());
        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_NICK, user.getNickName()!= null ? user.getNickName() : "");
        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_IMG, user.getHead_img_url() != null ? user.getHead_img_url() : "");



//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_WQTOKEN, user.getT_user_login().getQqwx_token() != null ? user.getT_user_login().getQqwx_token() : "");
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_TEL, user.getT_user_login().getTel() != null ? user.getT_user_login().getTel() : "");
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_PASSWORD, user.getT_user_login().getPassword() != null ? user.getT_user_login().getPassword() : "");
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_USERID, user.getT_user_login().getId());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_STATUS, user.getT_user_login().getStatus());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_QNTOKEN, user.getT_user_login().getQiniu());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.SP_RESUMM, user.getT_user_login().getResume());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.LOGIN_APK_URL, user.getApk_url());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.LOGIN_VERSION, user.getVersion());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.LOGIN_CONTENT, user.getContent());
//        SPUtils.setParam(context, Constants.LOGIN_INFO, Constants.LOGIN_HOBBY, user.getT_user_login().getHobby());
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_NICK, user.getT_user_info().getNickname() != null ? user.getT_user_info().getNickname() : "");
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_NAME, user.getT_user_info().getName() != null ? user.getT_user_info().getName() : "");
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_IMG, user.getT_user_info().getName_image() != null ? user.getT_user_info().getName_image() : "");
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_SCHOOL, user.getT_user_info().getSchool() != null ? user.getT_user_info().getSchool() : "");
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_CREDIT, user.getT_user_info().getCredit());
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.SP_INTEGRAL, user.getT_user_info().getIntegral());
//        SPUtils.setParam(context, Constants.USER_INFO, Constants.USER_SEX, user.getT_user_info().getUser_sex());
        if (!TextUtils.isEmpty(String.valueOf(user.getId()))) {
            //登陆leancloud服务器 给极光设置别名
            LCChatKit.getInstance().open(String.valueOf(user.getId()), new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        TastyToast.makeText(getApplicationContext(), "聊天服务启动失败，稍后请重新登录!", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
// Toast.makeText(SplashActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            chatManager.setupManagerWithUserId(this, String.valueOf(user.getT_user_login().getId()));
            LogUtils.e("jpush","调用jpush");
            if (JPushInterface.isPushStopped(getApplicationContext())){
                JPushInterface.resumePush(getApplicationContext());
            }

            JPushInterface.setAlias(getApplicationContext(),"jianguo"+user.getId(), new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    LogUtils.e("jpush",s+",code="+i);
                }
            });
        }


    }




    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onStart() {
        // 在当前的界面变为用户可见的时候调用的方法
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
             chooseActivity();
            }
        }.start();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * chooseActivity
     * 根据保存的登陆信息 跳转不同界面
     */
    private void chooseActivity() {
        int loginType = (int) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_TYPE, 0);
        if (mCityId.equals("0")||mCityName.equals("")){
            //如果定位失败，则获取上次登陆保存在sp的地理位置信息
            mCityId = (String) SPUtils.getParam(context, Constants.USER_INFO, Constants.USER_LOCATION_CODE, "0");
            mCityName = (String) SPUtils.getParam(context, Constants.USER_INFO, Constants.USER_LOCATION_NAME, "");
        }

        if (loginType==0){
            startActivity(new Intent(context, LeadActivity.class));
            finish();
        }else if(loginType==1){
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }else if(loginType==2){
            String phone= (String) SPUtils.getParam(context,Constants.LOGIN_INFO,Constants.SP_TEL,"");
            String token= (String) SPUtils.getParam(context,Constants.LOGIN_INFO,Constants.SP_WQTOKEN,"");
            HttpMethods.getInstance().autoLogin(new BackgroundSubscriberOnerror<NewUser>(newUserSubscriberOnNextErrorListener,this),phone,token);
        }else {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {



    }

}
