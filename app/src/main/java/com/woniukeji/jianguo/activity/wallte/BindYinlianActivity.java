package com.woniukeji.jianguo.activity.wallte;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class BindYinlianActivity extends BaseActivity {

    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.et_yinlian_name) EditText etYinlianName;
    @BindView(R.id.et_bank_name) EditText etBankName;
    @BindView(R.id.ll_bank) LinearLayout llBank;
    @BindView(R.id.et_bank_number) EditText etBankNumber;
    @BindView(R.id.btn_save_yinlian) Button btnSaveYinlian;
    private Handler mHandler = new Myhandler(this);
    private Context context = BindYinlianActivity.this;
    private SubscriberOnNextListener<String> stringSubscriberOnNextListener;
    private String tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }



    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BindYinlianActivity activity = (BindYinlianActivity) reference.get();
            switch (msg.what) {
                case 0:
                    String Message = (String) msg.obj;
                    Toast.makeText(activity, Message, Toast.LENGTH_SHORT).show();
                    Intent intent=activity.getIntent();
                    intent.putExtra("TYPE","1");
                    activity.setResult(RESULT_OK,intent);
                    activity.finish();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(activity, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:

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
        setContentView(R.layout.activity_bind_yinlian);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListeners() {
        stringSubscriberOnNextListener =new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                TastyToast.makeText(context,"账户绑定成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                finish();
            }
        };
    }

    @Override
    public void initData() {
//        SPUtils.setParam(context,Constants.USER_INFO,Constants.USER_MERCHANT_ID,user.getT_merchant().getId());
        tel = (String) SPUtils.getParam(BindYinlianActivity.this, Constants.LOGIN_INFO, Constants.SP_TEL, "");

    }

    @Override
    public void addActivity() {

    }


    @OnClick({R.id.img_back, R.id.btn_save_yinlian})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;

            case R.id.btn_save_yinlian:
                if (etYinlianName.getText().toString()==null||etYinlianName.getText().toString().equals("")){
                    showShortToast("请输入真实姓名");
                    return;
                }else if(etBankName.getText().toString()==null||etBankName.getText().toString().equals("")){
                    showShortToast("请输入开户银行");
                    return;
                }else if(etBankNumber.getText().toString()==null||etBankNumber.getText().toString().equals("")){
                    showShortToast("请输入银行卡号");
                    return;
                }
                HttpMethods.getInstance().putWalletInfo(BindYinlianActivity.this,new ProgressSubscriber<String>(stringSubscriberOnNextListener,context), tel,1,etBankName.getText().toString(),etBankNumber.getText().toString(),etYinlianName.getText().toString());

                break;
        }
    }


}
