package com.woniukeji.jianguo.activity.wallte;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.BaseCallback;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

public class BindAliActivity extends BaseActivity {

    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_confirm) TextView tvConfirm;
    @BindView(R.id.et_alipay_name) EditText etAlipayName;
    @BindView(R.id.ll_change) LinearLayout llChange;
    @BindView(R.id.et_alipay_account) EditText etAlipayAccount;
    @BindView(R.id.btn_change_pay_pass) Button btnChangePayPass;
    @BindView(R.id.ll_changed) LinearLayout llChanged;
    private Context context = BindAliActivity.this;
    private String tel;
    private SubscriberOnNextListener<String> stringSubscriberOnNextListener;


    @OnClick({R.id.img_back, R.id.btn_change_pay_pass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_change_pay_pass:
                if (etAlipayAccount.getText().toString()==null||etAlipayAccount.getText().toString().equals("")){
                    showShortToast("请输支付宝账户");
                    return;
                }else if(etAlipayName.getText().toString()==null||etAlipayName.getText().toString().equals("")){
                    showShortToast("请输入真实姓名");
                    return;
                }
                HttpMethods.getInstance().putWalletInfo(BindAliActivity.this,new ProgressSubscriber<String>(stringSubscriberOnNextListener,context),tel,2,null,etAlipayAccount.getText().toString(),etAlipayName.getText().toString());
                break;
        }
    }


    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BindAliActivity activity = (BindAliActivity) reference.get();
            switch (msg.what) {
                case 0:
//                    SPUtils.setParam(activity.context, Constants.USER_INFO,Constants.USER_PAY_PASS,activity.newPassWord);
                    String Message = (String) msg.obj;
                    Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
                    Intent intent=activity.getIntent();
                    intent.putExtra("TYPE","0");
                    activity.setResult(RESULT_OK,intent);
                    activity.finish();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(activity, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(activity, sms, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bind_alipay);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        stringSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                TastyToast.makeText(context,"账户绑定成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                finish();
            }
        };
    }

    @Override
    public void initData() {
        tel = (String) SPUtils.getParam(BindAliActivity.this, Constants.LOGIN_INFO, Constants.SP_TEL, "");

    }

    @Override
    public void addActivity() {

    }



}
