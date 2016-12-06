package com.woniukeji.jianguo.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.bumptech.glide.Glide;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.base.BaseFragment;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.User;
import com.woniukeji.jianguo.entity.Version;
import com.woniukeji.jianguo.eventbus.HeadImgEvent;
import com.woniukeji.jianguo.eventbus.QuickLoginEvent;
import com.woniukeji.jianguo.activity.login.LoginActivity;
import com.woniukeji.jianguo.activity.main.MainActivity;
import com.woniukeji.jianguo.activity.setting.FeedBackActivity;
import com.woniukeji.jianguo.activity.setting.PereferenceActivity;
import com.woniukeji.jianguo.activity.setting.SettingActivity;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.CommonUtils;
import com.woniukeji.jianguo.utils.GlideCircleTransform;
import com.woniukeji.jianguo.utils.LogUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.activity.wallte.WalletActivity;
import com.woniukeji.jianguo.utils.UpDialog;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

public class MineFragment extends BaseFragment {
    @BindView(R.id.title_bar) TextView titleBar;
    @BindView(R.id.img_head) ImageView imgHead;
    @BindView(R.id.iv_setting) ImageView ivSetting;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.school) TextView school;
    @BindView(R.id.phone) TextView phone;
    @BindView(R.id.lin_info) LinearLayout linInfo;
    @BindView(R.id.account1) RelativeLayout account1;
    @BindView(R.id.hobby) RelativeLayout hobby;
    @BindView(R.id.or_img) ImageView orImg;
    @BindView(R.id.point_img) ImageView pointImg;
    @BindView(R.id.ll_money) LinearLayout llMoney;
    @BindView(R.id.ll_real_name) LinearLayout llRealName;
    @BindView(R.id.ll_wallte_realname) LinearLayout llWallteRealname;
    @BindView(R.id.credit) RelativeLayout credit;
    @BindView(R.id.account) RelativeLayout account;
    @BindView(R.id.rl_evaluation) RelativeLayout rlEvaluation;
    @BindView(R.id.ll_collect) RelativeLayout llCollect;
    @BindView(R.id.rl_point) RelativeLayout rlPoint;
    @BindView(R.id.rl_feedback) RelativeLayout rlFeedback;
    @BindView(R.id.rl_setting) RelativeLayout rlSetting;
    @BindView(R.id.ll_guanli) RelativeLayout llGuanli;
    @BindView(R.id.about) RelativeLayout about;
    @BindView(R.id.refresh) RelativeLayout refresh;

    @BindView(R.id.btn_logout) Button btnLogout;
    private Handler mHandler = new Myhandler(this.getActivity());
    private Context context = getActivity();
    private int status;
    private String tel="";
    private SubscriberOnNextListener<Version> versionSubscriberOnNextListener;


    @OnClick({R.id.refresh,R.id.btn_logout,R.id.about,R.id.iv_setting,R.id.hobby,R.id.ll_guanli, R.id.ll_money,  R.id.ll_real_name, R.id.credit, R.id.rl_evaluation, R.id.ll_collect, R.id.rl_point, R.id.rl_feedback, R.id.rl_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.hobby:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity(), PereferenceActivity.class));
                break;
            case R.id.about:
//                startActivity(new Intent(getActivity(), PereferenceActivity.class));
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.ll_guanli:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                break;
            case R.id.credit:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intentRe = new Intent(getActivity().getApplicationContext(), ResumeActivity.class);
                startActivity(intentRe);
                break;
            case R.id.rl_evaluation:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intentEvluation = new Intent(getActivity().getApplicationContext(), EvaluationActivity.class);
                startActivity(intentEvluation);
                break;
            case R.id.ll_collect:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
//                Intent intentColl = new Intent(getActivity().getApplicationContext(), CollectActivity.class);,暂改收藏简直无商家
                Intent intentColl = new Intent(getActivity().getApplicationContext(), CollTionActivity.class);
                startActivity(intentColl);
                break;
            case R.id.refresh:
                //更新代码
                HttpMethods.getInstance().getVersion(new ProgressSubscriber<Version>(versionSubscriberOnNextListener,getActivity()));

                break;
            case R.id.rl_feedback:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intentFeed = new Intent(getActivity().getApplicationContext(), FeedBackActivity.class);
                startActivity(intentFeed);
                break;
            case R.id.rl_setting:
                Intent intentSet = new Intent(getActivity().getApplicationContext(), SettingActivity.class);
                startActivity(intentSet);
                break;
            case R.id.ll_money:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intentWallte = new Intent(getActivity().getApplicationContext(), WalletActivity.class);
                startActivity(intentWallte);
//                if (status == 1 || status == 0) {//未认证 不可以查询信息
//                    Toast.makeText(getActivity(), "请先实名认证", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intentWallte = new Intent(getActivity().getApplicationContext(), WalletActivity.class);
//                    startActivity(intentWallte);
//                }

                break;
            case R.id.ll_real_name:
                if (tel.equals("")) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), AuthActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_logout:
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定要退出吗?")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                sweetAlertDialog.dismiss();
//                                暂时关闭果聊
                                if(LCChatKit.getInstance().getClient().getClientId()!=null){
                                    LCChatKit.getInstance().close(new AVIMClientCallback() {
                                        @Override
                                        public void done(AVIMClient avimClient, AVIMException e) {
                                        }
                                    });
                                }

                                JPushInterface.stopPush(getActivity());
//                ActivityManager.getActivityManager().finishAllActivity();
                                SPUtils.deleteParams(getActivity());
                                btnLogout.setVisibility(View.GONE);
                                sendEvent();

                                getActivity().finish();
                                startActivity(new Intent(getActivity(),MainActivity.class));
//                                TalkMessageEvent talkMessageEvent = new TalkMessageEvent();
//                                talkMessageEvent.isLogin = false;
//                                EventBus.getDefault().post(talkMessageEvent);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                sDialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }

    private void sendEvent() {
        LCIMIMTypeMessageEvent event = new LCIMIMTypeMessageEvent();
        event.messageNull = true;
        EventBus.getDefault().post(event);
    }
    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = (MainActivity) reference.get();
            switch (msg.what) {
                case 0:
                    BaseBean<User> user = (BaseBean<User>) msg.obj;
                    Intent intent = new Intent(mainActivity, MainActivity.class);
//                    intent.putExtra("user", user);
                    mainActivity.startActivity(intent);
                    mainActivity.finish();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(mainActivity, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:

                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(mainActivity, sms, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_mine, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initListener();
        ButterKnife.bind(this, view);
        return view;


    }

    private void initListener() {

        versionSubscriberOnNextListener =new SubscriberOnNextListener<Version>() {
            @Override
            public void onNext(final Version version) {
                int version1 = CommonUtils.getVersion(getActivity());
                if (Integer.valueOf(version.getAndroid_business_version())>version1){
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("检测到新版本，是否更新？")
                            .setConfirmText("确定")
                            .setCancelText("取消")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    UpDialog upDataDialog = new UpDialog(getActivity(),version.getAndroid_business_url());
                                    upDataDialog.setCanceledOnTouchOutside(false);
                                    upDataDialog.setCanceledOnTouchOutside(false);
                                    upDataDialog.show();
                                }
                            }).show();
                }else
                    TastyToast.makeText(getActivity(),"已经是最新版本！",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);

            }
        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_mine;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    public void onEvent(QuickLoginEvent event) {
        if (event.isQuickLogin){
//          initData(true);
        }

    }

    public void onEvent(HeadImgEvent event) {
        Glide.with(getActivity()).load(event.ImgUrl)
                .placeholder(R.mipmap.icon_head_defult)
                .error(R.mipmap.icon_head_defult)
                .transform(new GlideCircleTransform(getActivity()))
                .into(imgHead);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i("fragment", "mine:onstart");
    }

    public void initData(boolean init) {
        if (init) {
            String nick = (String) SPUtils.getParam(getActivity(), Constants.USER_INFO, Constants.SP_NICK, "");
            String schoolStr = (String) SPUtils.getParam(getActivity(), Constants.USER_INFO, Constants.SP_SCHOOL, "");
            String img = (String) SPUtils.getParam(getActivity(), Constants.USER_INFO, Constants.SP_IMG, "");
//            status = (int) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_STATUS, 0);
            tel = (String) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TEL, "");
            rlSetting.setVisibility(View.GONE);
            if (schoolStr.equals("")) {
                school.setText("未填写");
            } else {
                school.setText(schoolStr);
            }
            if (nick.equals("")) {
                name.setText("未填写");
            } else {
                name.setText(nick);
            }
            if (img != null && !img.equals("")) {
                Glide.with(getActivity()).load(img)
                        .placeholder(R.mipmap.icon_head_defult)
                        .error(R.mipmap.icon_head_defult)
                        .transform(new GlideCircleTransform(getActivity()))
                        .into(imgHead);
            }
        } else {
            Glide.with(getActivity()).load("http//null")
                    .placeholder(R.mipmap.icon_head_defult)
                    .error(R.mipmap.icon_head_defult)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(imgHead);
        }
        if (tel.equals("")) {
            name.setText("登录/注册");
            Glide.with(getActivity()).load("http//null")
                    .placeholder(R.mipmap.icon_head_defult)
                    .error(R.mipmap.icon_head_defult)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(imgHead);
            btnLogout.setVisibility(View.GONE);
            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }else {
            account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initData(true);
        LogUtils.i("fragment", "mine:onresum");
    }

    @Override
    public void onDestroy() {
        LogUtils.i("fragment", "mine:ondestroy");
        super.onDestroy();
    }



}
