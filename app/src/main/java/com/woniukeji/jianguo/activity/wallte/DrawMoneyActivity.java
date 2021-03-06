package com.woniukeji.jianguo.activity.wallte;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.Balance;
import com.woniukeji.jianguo.entity.BindInfo;
import com.woniukeji.jianguo.entity.PayInfo;
import com.woniukeji.jianguo.entity.SmsCode;
import com.woniukeji.jianguo.entity.WageLog;
import com.woniukeji.jianguo.http.BackgroundSubscriber;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.NoProgressSubscriber;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static cn.pedant.SweetAlert.SweetAlertDialog.OnCancelListener;

public class DrawMoneyActivity extends BaseActivity implements PlatformActionListener {

    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.img_share) ImageView imgShare;
    @BindView(R.id.img_alipay) ImageView imgAlipay;
    @BindView(R.id.tv_alipay) TextView tvAlipay;
    @BindView(R.id.tv_ali_is_bind) TextView tvAliIsBind;
    @BindView(R.id.rl_alipay) RelativeLayout rlAlipay;
    @BindView(R.id.img_yinlian) ImageView imgYinlian;
    @BindView(R.id.tv_yinlian) TextView tvYinlian;
    @BindView(R.id.tv_yinlian_is_bind) TextView tvYinlianIsBind;
    @BindView(R.id.rl_yinlian) RelativeLayout rlYinlian;
    @BindView(R.id.tv_money_title) TextView tvMoneyTitle;
    @BindView(R.id.et_money_sum) EditText etMoneySum;
    @BindView(R.id.btn_post) Button btnPost;
    @BindView(R.id.img_go01) ImageView imgGo01;
    @BindView(R.id.rb_ailipay) RadioButton rbAilipay;
    @BindView(R.id.img_go02) ImageView imgGo02;
    @BindView(R.id.rb_yinlian) RadioButton rbYinlian;
    @BindView(R.id.tv_sms_title) TextView tvSmsTitle;
    @BindView(R.id.et_sms) EditText etSms;
    @BindView(R.id.btn_sms) Button btnSms;
    @BindView(R.id.img_wx) ImageView imgWx;
    @BindView(R.id.tv_wx_is_bind) TextView tvWxIsBind;
    @BindView(R.id.rl_wx) RelativeLayout rlWx;
    @BindView(R.id.rb_wxpay) RadioButton rbWXpay;
    @BindView(R.id.img_go03) ImageView imgGo03;
    @BindView(R.id.tv_tel) TextView tvTel;
    @BindView(R.id.tv_setting) TextView tvSetting;

    private Handler mHandler = new Myhandler(this);
    private String type = "";
    private String tel="";
    private BindInfo YinLInBindInfo;
    private BindInfo AlipayBindInfo;
    private double blanceMoney;
    private TimeCount time;
    private SubscriberOnNextListener<String> weixinSubscriberOnNextListener;
    private SubscriberOnNextListener<String> SmsSubscriberOnNextListener;
    private SubscriberOnNextListener<String> MoneySubscriberOnNextListener;
    private SubscriberOnNextListener<List<BindInfo>> balanceSubscriberOnNextListener;
    private ArrayList<BindInfo> bindInfos=new ArrayList<>();
    private ProgressDialog progressDialog;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;
        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DrawMoneyActivity activity = (DrawMoneyActivity) reference.get();
            switch (msg.what) {
                case 0:
                    String Message = (String) msg.obj;
                    activity.showDialog();
                    Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(activity, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    activity.showShortToast("验证码已经发送，请注意查收");
                    break;
                case 3:
                    String sms1 = (String) msg.obj;
                    Toast.makeText(activity, sms1, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_draw_money);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
    }

    @Override
    public void initListeners() {
        Date date = new Date(System.currentTimeMillis());//当前时间
        int hour = Integer.parseInt(DateUtils.getHHTime(date));
        if (hour < 8 || hour > 20) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DrawMoneyActivity.this);
            builder.setTitle("温馨提示");
            builder.setMessage("尊敬的用户，您的提现申请将会在每天的8:00-21:00为您处理，请您耐心等待提现结果，给您带来的不便，敬请谅解");
            builder.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        weixinSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String httpResult) {
                progressDialog.dismiss();
                showLongToast("微信账户绑定成功！", TastyToast.SUCCESS);
            }
        };
        SmsSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String message) {
                showLongToast("验证码已发送，请注意查收", TastyToast.SUCCESS);
            }
        };
        MoneySubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String message) {
                showLongToast("提现申请成功!", TastyToast.SUCCESS);
                finish();
            }
        };
        balanceSubscriberOnNextListener=new SubscriberOnNextListener<List<BindInfo>>() {
            @Override
            public void onNext(List<BindInfo> balances) {
                bindInfos.clear();
                if (balances.size()==0) {
                    tvSetting.setVisibility(View.GONE);
                }else {
                    tvSetting.setVisibility(View.VISIBLE);
                    bindInfos.addAll(balances);
                        for (BindInfo bindInfo : balances) {
                            if (bindInfo.getType() == 1) {
                                imgGo02.setVisibility(View.GONE);
                                rbYinlian.setVisibility(View.VISIBLE);
                                rbYinlian.setChecked(true);
                                rbAilipay.setChecked(false);
                                tvYinlianIsBind.setText(bindInfo.getEntity().getName());
                                YinLInBindInfo=bindInfo;
                                type="1";
                                rlYinlian.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                            } else if (bindInfo.getType() == 2) {
                                rbAilipay.setChecked(true);
                                rbYinlian.setChecked(false);
                                rbAilipay.setVisibility(View.VISIBLE);
                                imgGo01.setVisibility(View.GONE);
                                tvAliIsBind.setText(bindInfo.getEntity().getReceive_name());
                                AlipayBindInfo=bindInfo;
                                type="2";
                                rlAlipay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                }
            }
        };
        etMoneySum.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etMoneySum.setText(s);
                        etMoneySum.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etMoneySum.setText(s);
                    etMoneySum.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etMoneySum.setText(s.subSequence(0, 1));
                        etMoneySum.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rbWXpay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbAilipay.setChecked(false);
                    rbYinlian.setChecked(false);
                }
            }
        });
        rbYinlian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbAilipay.setChecked(false);
                    rbWXpay.setChecked(false);
                    type="1";
                }
            }
        });
        rbAilipay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbYinlian.setChecked(false);
                    rbWXpay.setChecked(false);
                    type = "2";
                }
            }
        });

    }



    @Override
    public void initData() {
        userId = (long) SPUtils.getParam(DrawMoneyActivity.this, Constants.LOGIN_INFO, Constants.SP_USERID, 0L);
        tel = (String) SPUtils.getParam(DrawMoneyActivity.this, Constants.LOGIN_INFO, Constants.SP_TEL, "");
        Intent intent=getIntent();
        blanceMoney= Double.parseDouble(intent.getStringExtra("balance"));
           }
    @Override
    protected void onResume() {
        super.onResume();
        HttpMethods.getInstance().getWalletInfo(DrawMoneyActivity.this,new ProgressSubscriber<List<BindInfo>>(balanceSubscriberOnNextListener,this),tel);
    }
    public void checkAlipayYinLian(int bindPay) {
        if (bindPay == 1) {
            imgGo02.setVisibility(View.GONE);
            rbYinlian.setVisibility(View.VISIBLE);
            rbYinlian.setChecked(true);
            rbAilipay.setChecked(false);
            tvYinlianIsBind.setText("已绑定");
            type = "1";
        }
        if (bindPay == 0) {
            rbAilipay.setChecked(true);
            rbYinlian.setChecked(false);
            rbAilipay.setVisibility(View.VISIBLE);
            imgGo01.setVisibility(View.GONE);
            tvAliIsBind.setText("已绑定");
            type = "2";
        }
    }

    @Override
    public void addActivity() {

    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnSms.setClickable(false);
            btnSms.setText(millisUntilFinished / 1000 + "秒");
            btnSms.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onFinish() {
            btnSms.setText("验证码");
            Drawable rBlack;
            if (Build.VERSION.SDK_INT >= 21) {
                rBlack = getResources().getDrawable(R.drawable.button_background_login, getTheme());
            } else {
                rBlack = getResources().getDrawable(R.drawable.button_background_login);
            }
            btnSms.setBackgroundDrawable(rBlack);
            btnSms.setClickable(true);
        }
    }

    @OnClick({R.id.tv_setting,R.id.img_back,R.id.rl_wx, R.id.rl_alipay, R.id.rl_yinlian, R.id.btn_post, R.id.btn_sms,R.id.tv_tel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tel:
                Intent intentTel=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tvTel.getText()));
                startActivity(intentTel);
                break;
            case R.id.tv_setting:
                Intent unbindIntent=new Intent(DrawMoneyActivity.this,EditPayMethodActivity.class);
                unbindIntent.putParcelableArrayListExtra("bindinfo",bindInfos);
                startActivity(unbindIntent);
                finish();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_sms:
                time.start();
                HttpMethods.getInstance().sendCode(new BackgroundSubscriber<String>(SmsSubscriberOnNextListener,this),tel,"4");

                break;
            case R.id.rl_alipay:
                Intent intentali=new Intent(DrawMoneyActivity.this, BindAliActivity.class);
                startActivityForResult(intentali,0);
                break;
            case R.id.rl_yinlian:
                Intent intent = new Intent(DrawMoneyActivity.this, BindYinlianActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_wx:
                getWechatInfo();
                break;
            case R.id.btn_post:
                //先判断支付宝和银行卡是否绑定过，再判断选中的是哪个，最后判断金额是否正确
                if (type.equals("")) {
                    showShortToast("请先绑定支付宝或银联卡！");
                    return;
                } else if (etMoneySum.getText().toString() == null || etMoneySum.getText().toString().equals("")) {
                    showShortToast("请输入取现金额");
                    return;
                } else if (blanceMoney < Double.valueOf(etMoneySum.getText().toString())) {
                    showShortToast("提现金额不能大于当前余额");
                    return;
                } else if (etSms.getText().toString().equals("")) {
                    showShortToast("请输入验证码");
                    return;
                }  else if (Double.valueOf(etMoneySum.getText().toString()) < 30) {
                    showShortToast("提现金额必须大于30");
                    return;
                }

                PayInfo payInfo=new PayInfo();
                payInfo.setType(3);
                payInfo.setPay_user_id(userId);
                payInfo.setMoney(Double.valueOf(etMoneySum.getText().toString()));
                if (type.equals("1")){
                    payInfo.setPay_type_id(YinLInBindInfo.getEntity().getId());
                }else {
                    payInfo.setPay_type_id(AlipayBindInfo.getEntity().getId());
                }
                Gson gson=new Gson();
                List<PayInfo> list=new ArrayList();
                list.add(payInfo);
                String json = gson.toJson(list);
                HttpMethods.getInstance().postMoney(DrawMoneyActivity.this,new ProgressSubscriber<String>(MoneySubscriberOnNextListener,this),tel,etSms.getText().toString(),json);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        time.cancel();
        super.onDestroy();
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // ALI
                checkAlipayYinLian(0);
            }
        } else {
            if (resultCode == RESULT_OK) {
                //yinlian
                checkAlipayYinLian(1);
            }
        }


    }
    private void showDialog() {
        new SweetAlertDialog(DrawMoneyActivity.this,SweetAlertDialog.NORMAL_TYPE)
                .setContentText("尊敬的用户，您的提现申请已经提交，工作人员将在24小时内为您处理，请您耐心等待")
                .setTitleText("温馨提示")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                }).show();
    }
    public void getWechatInfo() {
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        Platform wechat = ShareSDK.getPlatform(DrawMoneyActivity.this, Wechat.NAME);
        wechat.setPlatformActionListener(this);
        wechat.showUser(null);//执行登录，登录后在回调里面获取用户资料
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String openid = "";
        String nickname= "";
        String imgurl= "";
        String sex= "";
        String province= "";
        String city= "";
        String unionid= "";
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getKey().equals("openid")) {
                openid= (String) entry.getValue();
            }else if (entry.getKey().equals("nickname")) {
                nickname= (String) entry.getValue();
            } else if (entry.getKey().equals("headimgurl")) {
                imgurl= String.valueOf(entry.getValue());
            } else if (entry.getKey().equals("sex")) {
                sex=(String)entry.getValue().toString();
            }
            else if (entry.getKey().equals("city")) {
                city=(String)entry.getValue().toString();
            } else if (entry.getKey().equals("province")) {
                province=(String)entry.getValue().toString();
            } else if (entry.getKey().equals("unionid")) {
                unionid=(String)entry.getValue().toString();
            }
        }
        HttpMethods.getInstance().bindWX(new NoProgressSubscriber<String>(weixinSubscriberOnNextListener,this,progressDialog),tel,openid,nickname,sex,province,city,imgurl,unionid);
        progressDialog.dismiss();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
