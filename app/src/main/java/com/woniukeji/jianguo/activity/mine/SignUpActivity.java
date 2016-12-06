package com.woniukeji.jianguo.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.adapter.SignAdapter;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.HttpResult;
import com.woniukeji.jianguo.entity.Jobs;
import com.woniukeji.jianguo.entity.JoinJob;
import com.woniukeji.jianguo.eventbus.SignEvent;
import com.woniukeji.jianguo.activity.main.MainActivity;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.MD5Util;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.widget.FixedRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SignUpActivity extends BaseActivity implements SignAdapter.RecyCallBack {

    @BindView(R.id.img_renwu) ImageView imgRenwu;
    @BindView(R.id.rl_null) RelativeLayout rlNull;
    @BindView(R.id.list) FixedRecyclerView list;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.img_share) ImageView imgShare;
    private Context mContext = SignUpActivity.this;
    private Handler mHandler = new Myhandler(mContext);
    private List<JoinJob> modleList = new ArrayList<>();
    private SignAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private int mPosition;
    private int type = 0;
    private int loginId;
    private int lastVisibleItem;
 private SubscriberOnNextListener<List<JoinJob>> stringSubscriberOnNextListener;
    private SubscriberOnNextListener<HttpResult> baseBeanSubscriberOnNextListener;
    private String tel;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
    //1 报名  2未录取 3 录取 4 取消报名
    @Override
    public void RecyOnClick(long jobid, int offer, int position) {
        HttpMethods.getInstance().cancelJoin(SignUpActivity.this,new ProgressSubscriber<HttpResult>(baseBeanSubscriberOnNextListener,this), tel,String.valueOf(jobid),4);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.img_back)
    public void onClick() {
//        mContext.startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            mContext.startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
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
            switch (msg.what) {
                case 0:
//                    BaseBean<Jobs> jobsBaseBean = (BaseBean<Jobs>) msg.obj;
//                    int count = msg.arg1;
//                    if (count == 0) {
//                        modleList.clear();
//                    }
//                    if (refreshLayout != null && refreshLayout.isRefreshing()) {
//                        refreshLayout.setRefreshing(false);
//                    }
//                    modleList.addAll(jobsBaseBean.getData().getList_t_job());
//                    if (modleList.size() > 0) {
//                        rlNull.setVisibility(View.GONE);
//                    } else {
//                        rlNull.setVisibility(View.VISIBLE);
//                    }
//                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(mContext, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(mContext, sms, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    break;
                case 5:
                    String me = (String) msg.obj;
                    Toast.makeText(mContext, me, Toast.LENGTH_SHORT).show();
                    SignEvent event = new SignEvent();
                    EventBus.getDefault().post(event);
                    break;
                case 6:
                    String mes = (String) msg.obj;
                    Toast.makeText(mContext, mes, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void setContentView() {
        EventBus.getDefault().register(this);
        setContentView(R.layout.fragment_sign);
//        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        tvTitle.setText("我的兼职");
        adapter = new SignAdapter(modleList, mContext, type, this);
        mLayoutManager = new LinearLayoutManager(mContext);
//设置布局管理器
        list.setLayoutManager(mLayoutManager);
//设置adapter
        list.setAdapter(adapter);
//设置Item增加、移除动画
        list.setItemAnimator(new DefaultItemAnimator());
//添加分割线
//        recycleList.addItemDecoration(new RecyclerView.ItemDecoration() {
//        });
//        recycleList.addItemDecoration(new DividerItemDecoration(
//                mContext, DividerItemDecoration.VERTICAL_LIST));
        refreshLayout.setColorSchemeResources(R.color.app_bg);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int pageNum=1;
                HttpMethods.getInstance().getJoinJob(SignUpActivity.this,new ProgressSubscriber<List<JoinJob>>(stringSubscriberOnNextListener,SignUpActivity.this), tel,pageNum);
            }
        });

    }

    public void onEvent(SignEvent signEvent) {
        int pageNum=1;
        HttpMethods.getInstance().getJoinJob(SignUpActivity.this,new ProgressSubscriber<List<JoinJob>>(stringSubscriberOnNextListener,SignUpActivity.this), tel,pageNum);

    }

    @Override
    public void initListeners() {
        baseBeanSubscriberOnNextListener=new SubscriberOnNextListener<HttpResult>() {
            @Override
            public void onNext(HttpResult baseBean) {
                if (baseBean.getCode()==200) {
                    TastyToast.makeText(SignUpActivity.this,"操作成功！", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                }
            }
        };


        stringSubscriberOnNextListener=new SubscriberOnNextListener<List<JoinJob>>() {
            @Override
            public void onNext(List<JoinJob> s) {
                TastyToast.makeText(SignUpActivity.this,"操作成功！", TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
               if (refreshLayout.isRefreshing()){
                   modleList.clear();
               }
                modleList.addAll(s);
                adapter.notifyDataSetChanged();
            }
        };


        list.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (modleList.size() > 5 && lastVisibleItem == modleList.size()) {
                    int pageNum=modleList.size()/10+1;
                    refreshLayout.setRefreshing(true);
                    HttpMethods.getInstance().getJoinJob(SignUpActivity.this,new ProgressSubscriber<List<JoinJob>>(stringSubscriberOnNextListener,SignUpActivity.this), tel,pageNum);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void initData() {
        tel = (String) SPUtils.getParam(this, Constants.LOGIN_INFO, Constants.SP_TEL, "");
        refreshLayout.setRefreshing(true);
        HttpMethods.getInstance().getJoinJob(this,new ProgressSubscriber<List<JoinJob>>(stringSubscriberOnNextListener,this), tel, 1);
    }

    @Override
    public void addActivity() {

    }





}
