package com.woniukeji.jianguo.activity.partjob;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayfang.dropdownmenu.DropDownMenu;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.adapter.PartJobAdapter;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CityCategory;
import com.woniukeji.jianguo.entity.JobListBean;
import com.woniukeji.jianguo.entity.Jobs;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriberOnError;
import com.woniukeji.jianguo.http.SubscriberOnNextErrorListener;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.widget.FixedRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link } subclass.
 */
public class PartJobActivity extends BaseActivity {

    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.list) FixedRecyclerView list;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.img_share) ImageView imgShare;
    @BindView(R.id.img_menu) ImageView imgMenu;
    @BindView(R.id.img_renwu) ImageView imgRenwu;
    @BindView(R.id.rl_null) RelativeLayout rlNull;
    private PartJobAdapter adapter;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    public List<JobListBean> jobList = new ArrayList<JobListBean>();
    private int MSG_GET_SUCCESS = 0;
    private int MSG_GET_FAIL = 1;
    BaseBean<CityCategory> cityCategoryBaseBean;
    private Handler mHandler = new Myhandler(this);
    private String cityid="010";
    private int type= 1;
    private SubscriberOnNextErrorListener<List<JobListBean>> listSubscriberOnNextListener;
    private boolean isRefresh=true;
    private boolean DataComplete;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PartJobActivity mainActivity = (PartJobActivity) reference.get();
            switch (msg.what) {
                case 0:
                    if (refreshLayout!=null&&refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                    int count=msg.arg1;
                    if (count==0){
                        jobList.clear();
                    }
                    BaseBean<Jobs> jobs = (BaseBean<Jobs>) msg.obj;
                    if (jobs.getData().getList_t_job().size() == 0) {
                        rlNull.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    break;
                case 2:
                    rlNull.setVisibility(View.VISIBLE);
                    cityCategoryBaseBean = (BaseBean<CityCategory>) msg.obj;
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
    public void addActivity() {

    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_part_job);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        adapter = new PartJobAdapter(jobList, this);
        mLayoutManager = new LinearLayoutManager(this);
//设置布局管理器
        list.setLayoutManager(mLayoutManager);
//设置adapter
        list.setAdapter(adapter);
//设置Item增加、移除动画
        list.setItemAnimator(new DefaultItemAnimator());
//添加分割线
        refreshLayout.setColorSchemeResources(R.color.app_bg);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh=true;
                HttpMethods.getInstance().getJobListOfType(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,PartJobActivity.this),cityid,null,null,null, "1",type);
            }
        });
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        cityid = intent.getStringExtra("cityid");
        type = intent.getIntExtra("type", 1);
        //精品 1长期 2旅行 3日结 4
        if (type==3){
            tvTitle.setText("兼职旅行");
        }else if(type==4){
            tvTitle.setText("日结兼职");
        }else if(type==1){//2
            tvTitle.setText("精品兼职");
        }else{
            tvTitle.setText("长期兼职");
        }
        isRefresh=true;
        HttpMethods.getInstance().getJobListOfType(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,PartJobActivity.this),cityid,null,null,null, "1",type);

    }

    @Override
    public void initListeners() {
        listSubscriberOnNextListener=new SubscriberOnNextErrorListener<List<JobListBean>>() {
            @Override
            public void onNext(List<JobListBean> listEntities) {
                if (refreshLayout != null && refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                    jobList.clear();
                }
                if (isRefresh){
                    jobList.clear();
                }
                jobList.addAll(listEntities);
                adapter.notifyDataSetChanged();
                DataComplete = true;
            }

            @Override
            public void onError(String mes) {
                super.onError(mes);
                if (refreshLayout != null && refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (jobList.size() > 5 && lastVisibleItem == jobList.size() + 1) {
                    isRefresh=false;
                    int page=jobList.size()/10+1;
                    HttpMethods.getInstance().getJobListOfType(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,PartJobActivity.this),cityid,null,null,null, String.valueOf(page),type);
                    DataComplete = false;

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
    protected void onDestroy() {
        super.onDestroy();

//        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }




}
