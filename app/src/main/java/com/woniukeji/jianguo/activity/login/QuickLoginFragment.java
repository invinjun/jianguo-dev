package com.woniukeji.jianguo.activity.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.base.BaseFragment;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CodeCallback;
import com.woniukeji.jianguo.entity.NewUser;
import com.woniukeji.jianguo.entity.SmsCode;
import com.woniukeji.jianguo.entity.User;
import com.woniukeji.jianguo.eventbus.QuickLoginEvent;
import com.woniukeji.jianguo.activity.main.MainActivity;
import com.woniukeji.jianguo.http.BackgroundSubscriber;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.LogUtils;
import com.woniukeji.jianguo.utils.MD5Util;
import com.woniukeji.jianguo.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.leancloud.chatkit.LCChatKit;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A Register screen that offers Register via email/password.
 */
public class QuickLoginFragment extends BaseFragment {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    @BindView(R.id.phoneNumber) EditText phoneNumber;
    @BindView(R.id.btn_get_code) Button btnGetCode;
    @BindView(R.id.icon_pass) ImageView iconPass;
    @BindView(R.id.phone_code) EditText phoneCode;
    @BindView(R.id.cb_rule) CheckBox cbRule;
    @BindView(R.id.tv_rule) TextView tvRule;
    @BindView(R.id.user_rule) LinearLayout userRule;
    @BindView(R.id.sign_in_button) Button signInButton;
    @BindView(R.id.email_login_form) LinearLayout emailLoginForm;
    @BindView(R.id.login_form) LinearLayout loginForm;


    private Handler mHandler = new Myhandler(getActivity());
    private Context context = getActivity();
    private TimeCount time;
    private SubscriberOnNextListener<NewUser> subscriberOnNextListener;
    private SubscriberOnNextListener<String> smsSubscriberOnNextListener;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (time!=null){
            time.cancel();
        }
    }


    private class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity quickLoginActivity = (MainActivity) reference.get();
            switch (msg.what) {
                case 0:
                    BaseBean<User> user = (BaseBean<User>) msg.obj;
//                    BaseBean<User> user = msg.;
//                    saveToSP(user.getData());

                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(getActivity(), ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    TastyToast.makeText(getActivity().getApplicationContext(),  "验证码已经发送，请注意查收", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(getActivity(), sms, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login_quick, container, false);
        ButterKnife.bind(this, view);
        createLink(tvRule);
        initListeners();
        return view;

    }
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_quick;
    }

    public void initListeners() {
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        subscriberOnNextListener=new SubscriberOnNextListener<NewUser>() {
            @Override
            public void onNext(NewUser merchant) {
                saveToSP(merchant);
                SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TYPE, 2);
            }
        };
        smsSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                TastyToast.makeText(getActivity(), "验证码已发送请注意查收", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        };
    }


    /**
     * 创建一个超链接
     */
    private void createLink(TextView tv) {
        // 创建一个 SpannableString对象
        SpannableString sp = new SpannableString("我已阅读并同意《兼果用户协议》");
        // 设置超链接
        sp.setSpan(new URLSpan("http://101.200.205.243:8080/user_agreement.jsp"), 7, 15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_bg)), 7, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(sp);
        tv.setTextSize(12);
        //设置TextView可点击
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void saveToSP(NewUser user) {
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_WQTOKEN, user.getToken() != null ? user.getToken() : "");
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TEL, user.getTel() != null ? user.getTel() : "");
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_USER_STATUS, user.getAuth_status());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_PERMISSIONS, user.getStatus());
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_QNTOKEN, user.getQiniu_token());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_PERMISSIONS, user.getBusiness_type());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_WQTOKEN, user.getT_user_login().getQqwx_token() != null ? user.getT_user_login().getQqwx_token() : "");
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TEL, user.getT_user_login().getTel() != null ? user.getT_user_login().getTel() : "");
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_PASSWORD, user.getT_user_login().getPassword() != null ? user.getT_user_login().getPassword() : "");
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_USERID, user.getId());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_STATUS, user.getT_user_login().getStatus());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_QNTOKEN, user.getT_user_login().getQiniu());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_APK_URL, user.getApk_url());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_VERSION, user.getVersion());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_CONTENT, user.getContent());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_HOBBY, user.getT_user_login().getHobby());
//        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TYPE, "0");
        SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_RESUMM, user.getResume());
        SPUtils.setParam(getActivity(), Constants.USER_INFO, Constants.SP_NICK, user.getNickName()!= null ? user.getNickName() : "");
        SPUtils.setParam(getActivity(), Constants.USER_INFO, Constants.SP_IMG, user.getHead_img_url() != null ? user.getHead_img_url() : "");
//        SPUtils.setParam(getActivity(), Constants.USER_INFO, Constants.SP_SCHOOL, user.getT_user_info().getSchool() != null ? user.getT_user_info().getSchool() : "");

        if (!TextUtils.isEmpty(String.valueOf(user.getId()))) {
            if (JPushInterface.isPushStopped(getActivity().getApplicationContext())) {
                JPushInterface.resumePush(getActivity().getApplicationContext());
            }
            //登陆leancloud服务器 给极光设置别名
            LCChatKit.getInstance().open(String.valueOf(user.getId()), new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (null == e) {
                        QuickLoginEvent quickLoginEvent = new QuickLoginEvent();
                        quickLoginEvent.isQuickLogin = true;
                        EventBus.getDefault().post(quickLoginEvent);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("login",true);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "聊天服务启动失败，稍后请重新登录", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("login",true);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
            JPushInterface.setAlias(getActivity().getApplicationContext(), "jianguo" + user.getId(), new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    LogUtils.e("jpush", s + ",code=" + i);
                }
            });
        }

    }


    @OnClick({R.id.sign_in_button, R.id.btn_get_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                String tel = phoneNumber.getText().toString().trim();
                String sms = phoneCode.getText().toString().trim();
                if (CheckStatus()) {
                    HttpMethods.getInstance().codeLogin(new ProgressSubscriber<NewUser>(subscriberOnNextListener,getActivity()),tel, sms);
                }
                break;
            case R.id.btn_get_code:
                String phone = phoneNumber.getText().toString();
                boolean isOK = phone.length() == 11;
                if (isOK) {
                    time.start();
                    HttpMethods.getInstance().sendCode(new BackgroundSubscriber<String>(smsSubscriberOnNextListener,getActivity()),phone,"1");
                } else {
                    Toast.makeText(getActivity(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean CheckStatus() {
        if (phoneNumber.getText().toString().trim().length() != 11) {
            Toast.makeText(getActivity(), "手机号码格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneCode.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!cbRule.isChecked()) {
            Toast.makeText(getActivity(), "请阅读并确认《兼果用户协议》", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (btnGetCode!=null){
                btnGetCode.setClickable(false);
                btnGetCode.setBackgroundColor(Color.GRAY);
                btnGetCode.setText(millisUntilFinished / 1000 + "秒");
            }
        }

        @Override
        public void onFinish() {
            if (time!=null){
                btnGetCode.setText("验证码");
            }
            btnGetCode.setText("验证码");
            btnGetCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_login));
            btnGetCode.setClickable(true);
        }
    }

}

