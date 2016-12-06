package com.woniukeji.jianguo.activity.wallte;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BindInfo;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.SPUtils;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditPayMethodActivity extends BaseActivity {

    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.img_share) ImageView imgShare;
    @BindView(R.id.img_alipay) ImageView imgAlipay;
    @BindView(R.id.tv_alipay) TextView tvAlipay;
    @BindView(R.id.tv_ali_account) TextView tvAliAccount;
    @BindView(R.id.img_alipay_unbind) Button imgAlipayUnbind;
    @BindView(R.id.rl_alipay) RelativeLayout rlAlipay;
    @BindView(R.id.img_yinlian) ImageView imgYinlian;
    @BindView(R.id.tv_yinlian) TextView tvYinlian;
    @BindView(R.id.tv_yinlian_account) TextView tvYinlianAccount;
    @BindView(R.id.img_yinlian_unbind) Button imgYinlianUnbind;
    @BindView(R.id.rl_yinlian) RelativeLayout rlYinlian;
    @BindView(R.id.activity_edit_pay_method) LinearLayout activityEditPayMethod;
     private SubscriberOnNextListener<String> stringSubscriberOnNextListener;
    private String tel;
    private ArrayList<BindInfo> bindInfos;
    private long aliId;
    private long yinlianId;
    private boolean isAliPay=true;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_edit_pay_method);
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
                TastyToast.makeText(EditPayMethodActivity.this,"解绑成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                if (isAliPay){
                    rlAlipay.setVisibility(View.GONE);
                }else {
                    rlYinlian.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    public void initData() {
        Intent intent=getIntent();
        bindInfos =intent.getParcelableArrayListExtra("bindinfo");
        tel = (String) SPUtils.getParam(EditPayMethodActivity.this, Constants.LOGIN_INFO, Constants.SP_TEL, "");
        for (BindInfo bindInfo : bindInfos) {
            if (bindInfo.getType() == 1) {
               rlYinlian.setVisibility(View.VISIBLE);
                tvYinlianAccount.setText(bindInfo.getEntity().getName());
                yinlianId=bindInfo.getEntity().getId();
            } else if (bindInfo.getType() == 2) {
                rlAlipay.setVisibility(View.VISIBLE);
                tvAliAccount.setText(bindInfo.getEntity().getReceive_name());
                aliId=bindInfo.getEntity().getId();
            }
        }
    }
    @Override
    public void addActivity() {
    }
    @OnClick({R.id.img_back, R.id.img_alipay_unbind, R.id.img_yinlian_unbind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_alipay_unbind:
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定解绑支付宝？")
                    .setConfirmText("确定")
                    .setCancelText("取消")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            isAliPay=true;
                            HttpMethods.getInstance().deleteWalletInfo(EditPayMethodActivity.this,new ProgressSubscriber<String>(stringSubscriberOnNextListener,EditPayMethodActivity.this), tel,aliId);
                            sDialog.dismissWithAnimation();
                        }
                    }).show();
                    break;
            case R.id.img_yinlian_unbind:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定解绑银行卡？")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                isAliPay=false;
                                HttpMethods.getInstance().deleteWalletInfo(EditPayMethodActivity.this,new ProgressSubscriber<String>(stringSubscriberOnNextListener,EditPayMethodActivity.this), tel,yinlianId);
                                sDialog.dismissWithAnimation();
                            }
                        }).show();
                            break;
        }
    }
}
