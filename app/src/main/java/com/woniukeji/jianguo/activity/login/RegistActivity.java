package com.woniukeji.jianguo.activity.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CodeCallback;
import com.woniukeji.jianguo.entity.SmsCode;
import com.woniukeji.jianguo.entity.User;
import com.woniukeji.jianguo.http.BackgroundSubscriber;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.ActivityManager;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.MD5Util;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.utils.TimeCount;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class RegistActivity extends BaseActivity {

    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.phoneNumber) EditText phoneNumber;
    @BindView(R.id.btn_get_code) Button btnGetCode;
    @BindView(R.id.phoneCode) EditText phoneCode;
    @BindView(R.id.passWord1) EditText passWord1;
    @BindView(R.id.passWord2) EditText passWord2;
    @BindView(R.id.phone_sign_in_button) Button phoneSignInButton;
    @BindView(R.id.email_login_form) LinearLayout emailLoginForm;
    @BindView(R.id.user_rule) LinearLayout userRule;
    @BindView(R.id.cb_rule) CheckBox cbRule;
    @BindView(R.id.tv_rule) TextView tvRule;
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";
    private SubscriberOnNextListener<String> subscriberOnNextListener;
    private SubscriberOnNextListener<String> smsSubscriberOnNextListener;
    SmsCode smsCode;

    private int MSG_USER_SUCCESS = 0;
    private int MSG_USER_FAIL = 1;
    private int MSG_PHONE_SUCCESS = 2;
    private int MSG_REGISTER_SUCCESS = 3;
    private Handler mHandler = new Myhandler(this);
    private Context context=RegistActivity.this;

    @OnClick(R.id.tv_rule)
    public void onClick() {
    }

    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegistActivity registActivity = (RegistActivity) reference.get();
            switch (msg.what) {
                case 0:
//                    BaseBean<User> user = (BaseBean<User>) msg.obj;
                    TastyToast.makeText(registActivity, "注册成功，请登陆", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    registActivity.finish();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    TastyToast.makeText(registActivity, ErrorMessage, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    break;
                case 2:
                    registActivity.smsCode = (SmsCode) msg.obj;
                    if (registActivity.smsCode.getIs_tel().equals("1")) {
                        registActivity.showShortToast("该手机号码已经注册，不能重复注册");
                    } else {
                        TastyToast.makeText(registActivity, "验证码已经发送，请注意查收", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    }

                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(registActivity, sms, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        title.setText("注册");
        userRule.setVisibility(View.VISIBLE);
        createLink(tvRule);
    }

    @Override
    public void initListeners() {
        smsSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                TastyToast.makeText(RegistActivity.this, "验证码已发送请注意查收", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
            }
        };
        subscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String merchantBean) {
                TastyToast.makeText(RegistActivity.this, "注册成功，请登录", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                finish();
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    public void addActivity() {
        ActivityManager.getActivityManager().addActivity(RegistActivity.this);
    }


    /**
     * 创建一个超链接
     */
    private void createLink(TextView tv) {
        SpannableString sp = new SpannableString("我已阅读并同意《兼果用户协议》");
        // 设置超链接
        sp.setSpan(new URLSpan("http://inke.tv/privacy/privacy.html"), 7, 15,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_bg)), 7, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setTextSize(12);
        tv.setText(sp);
        //设置TextView可点击
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick({R.id.img_back, R.id.btn_get_code, R.id.phone_sign_in_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_get_code:
                String tel = phoneNumber.getText().toString();
                boolean isOK = tel.length()==11;
                if (isOK) {
//                    showShortToast("正在发送验证码，请稍后");
                    new TimeCount(60000, 1000,btnGetCode).start();//构造CountDownTimer对象
                    HttpMethods.getInstance().sendCode(new BackgroundSubscriber<String>(smsSubscriberOnNextListener,this),tel,"3");
                } else {
                    showLongToast("请输入正确的手机号");
                }

                break;
            case R.id.phone_sign_in_button:
                String phone = phoneNumber.getText().toString();
                String pass = passWord2.getText().toString();
                String sms = phoneCode.getText().toString();
                if (CheckStatus()) {
                    HttpMethods.getInstance().register(new ProgressSubscriber<String>(subscriberOnNextListener,this),phone,sms ,pass);
                }
                break;
        }
    }

    private boolean CheckStatus() {

        if (phoneNumber.getText().toString().equals("")) {
            showShortToast("手机号不能为空");
            return false;
        } else if (phoneNumber.getText().toString().length()!=11) {
            showShortToast("手机号码格式不正确");
            return false;
        } else if (passWord1.getText().toString().equals("")) {
            showShortToast("密码不能为空");
            return false;
        } else if (passWord2.getText().toString().equals("")) {
            showShortToast("请再次输入密码");
            return false;
        } else if (passWord1.getText().toString().trim().length() < 6) {
            showShortToast("您的密码太短了");
            return false;
        } else if (!passWord1.getText().toString().trim().equals(passWord2.getText().toString().trim())) {
            showShortToast("您两次输入的密码不同！");
            return false;
        } else if (phoneCode.getText().toString().equals("")) {
            showShortToast("验证码不能为空");
            return false;
        }
        return true;
    }


    }


