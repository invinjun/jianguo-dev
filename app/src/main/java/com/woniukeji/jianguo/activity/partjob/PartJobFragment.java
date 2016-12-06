package com.woniukeji.jianguo.activity.partjob;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayfang.dropdownmenu.BaseEntity;
import com.jayfang.dropdownmenu.DropDownMenu;
import com.jayfang.dropdownmenu.OnMenuSelectedListener;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.adapter.GirdDropDownAdapter;
import com.woniukeji.jianguo.adapter.ListDropDownAdapter;
import com.woniukeji.jianguo.adapter.PartJobAdapter;
import com.woniukeji.jianguo.base.BaseFragment;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.db.CityAreaDao;
import com.woniukeji.jianguo.db.TypeDao;
import com.woniukeji.jianguo.entity.AreaBean;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CityBean;
import com.woniukeji.jianguo.entity.CityCategory;
import com.woniukeji.jianguo.entity.CityCategoryBase;
import com.woniukeji.jianguo.entity.JobListBean;
import com.woniukeji.jianguo.entity.Jobs;
import com.woniukeji.jianguo.eventbus.JobFilterEvent;
import com.woniukeji.jianguo.eventbus.MessageEvent;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.ProgressSubscriberOnError;
import com.woniukeji.jianguo.http.SubscriberOnNextErrorListener;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.activity.main.MainActivity;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.LogUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.widget.FixedRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link } subclass.
 */
public class PartJobFragment extends BaseFragment {
    private Context context = getActivity();
    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.list) FixedRecyclerView list;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    private String headers[] = {"职业", "排序", "地区"};
    private List<BaseEntity> jobs = new ArrayList<>();
    private List<BaseEntity> sort = new ArrayList<>();
    private List<BaseEntity> citys = new ArrayList<>();
    private List<View> popupViews = new ArrayList<>();
    private PartJobAdapter adapter;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    public List<JobListBean> jobList = new ArrayList<JobListBean>();
    String typeid = "0";
    String areid = "0";
    String filterid = "0";
    private Handler mHandler = new Myhandler(this.getActivity());
    private DropDownMenu mMenu;
    private boolean DataComplete=false;
    private String cityCode;
    private SubscriberOnNextErrorListener<List<JobListBean>> listSubscriberOnNextListener;
    private boolean isInvisible;
    private boolean isRefesh=true;

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
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.e("fragemnt","isVisibleToUser="+isVisibleToUser);
        if (getUserVisibleHint()){
            isInvisible=true;
        }else {
            isInvisible=false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.e("fragemnt","onAttach="+isInvisible);
        if (isInvisible){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("fragemnt","onCreate="+isInvisible);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.e("fragemnt","onCreateView="+isInvisible);
        View view = inflater.inflate(R.layout.fragment_part_job, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
            initview();
        initDropDownView(view);

        return view;
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_part_job;
    }

    private void initview() {
        tvTitle.setText("兼职");
        imgBack.setVisibility(View.GONE);
        adapter = new PartJobAdapter(jobList, getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
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
                HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,areid,typeid,filterid,"1");

            }
        });
        listSubscriberOnNextListener = new SubscriberOnNextErrorListener<List<JobListBean>>() {
            @Override
            public void onNext(List<JobListBean> listEntities) {
                if (refreshLayout != null && refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                    jobList.clear();
                }
                if (isRefesh){
                    jobList.clear();
                }
                jobList.addAll(listEntities);
                adapter.notifyDataSetChanged();
                DataComplete = true;
            }
        };
    }
    private void initData() {
//        cityCode = (String) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.USER_LOCATION_CODE, "010");
        if(cityCode==null||cityCode.equals("")){
            cityCode="010";
        }
        TypeDao typeDao=new TypeDao(getActivity());
        CityAreaDao cityAreaDao=new CityAreaDao(getActivity());
        CityBean cityBean = cityAreaDao.queryCitySelected();
        List<CityCategoryBase.TypeListBean> listTTypeEntities = typeDao.alterTypeDate();
        cityCode=cityBean.getCode();
        List<AreaBean> areaList = cityAreaDao.alterAreaDate(cityBean.getCode());
        initDrawData(areaList,listTTypeEntities);
        isRefesh=true;
        HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,areid,typeid,filterid,"1");

//        HttpMethods.getInstance().getCityCategory(new ProgressSubscriber<CityCategory>(subscriberOnNextListener,getActivity()));
    }
    private void initDropDownView(View view) {
        //init sex menu
        mMenu = (DropDownMenu) view.findViewById(R.id.menu);
        mMenu.setmMenuCount(3);
        mMenu.setmShowCount(6);
        mMenu.setShowCheck(true);//是否显示展开list的选中项1
        mMenu.setmMenuTitleTextSize(12);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(Color.BLACK);//Menu的文字颜色
        mMenu.setmMenuListTextSize(12);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setmCheckIcon(R.drawable.ico_make);//Menu展开list的勾选图片
//                mMenu.setDefaultMenuTitle(headers);//默认未选择任何过滤的Menu title
        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex,int sortId) {
                if (ColumnIndex == 0) {
                    typeid = String.valueOf(sortId);
                } else if (ColumnIndex == 2) {
                    switch (RowIndex) {
                        case 0:
                            filterid = "0";
                            break;
                        case 1:
                            filterid = "101";
                            break;
                        case 2:
                            filterid = "102";
                            break;
                    }
                } else if (ColumnIndex == 1) {
                    areid= String.valueOf(sortId);
                }
                isRefesh=true;
                HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,areid,typeid,filterid,"1");

//                getJobs(cityCode, typeid, areid, filterid, "0");
            }
        });
    }

    public void initDrawData(List<AreaBean> areaList, List<CityCategoryBase.TypeListBean> typeListBeen) {
        List<List<BaseEntity>> items = new ArrayList<>();
        citys.clear();
        sort.clear();
        jobs.clear();

        BaseEntity baseEntity=new BaseEntity();
        baseEntity.setName("不限地区");
        baseEntity.setId(0);
        citys.add(baseEntity);
        //如果定位信息有误 默认选取第一个城市
        for (AreaBean areaBean : areaList) {
            BaseEntity baseEntity1=new BaseEntity();
            baseEntity1.setName(areaBean.getAreaName());
            baseEntity1.setId(Integer.parseInt(areaBean.getCity_id()));
            citys.add(baseEntity1);
        }
        BaseEntity baseTypeEntity1=new BaseEntity();
        baseTypeEntity1.setName("不限职业");
        baseTypeEntity1.setId(0);
        jobs.add(baseTypeEntity1);
        for (int i = 0; i < typeListBeen.size(); i++) {
            BaseEntity baseTypeEntity=new BaseEntity();
            baseTypeEntity.setName(typeListBeen.get(i).getName());
            baseTypeEntity.setId(Integer.parseInt(String.valueOf(typeListBeen.get(i).getId())));
            jobs.add(baseTypeEntity);
        }
        BaseEntity baseSortEntity=new BaseEntity();
        baseSortEntity.setName("推荐排序");
        baseSortEntity.setId(2);
        BaseEntity baseEntity1=new BaseEntity();
        baseEntity1.setName("最新发布");
        baseEntity1.setId(1);
        BaseEntity baseEntity2=new BaseEntity();
        baseEntity2.setName("工资最高");
        baseEntity2.setId(0);
        sort.add(baseSortEntity);
        sort.add(baseEntity2);
        sort.add(baseEntity1);
        //    sort.add("推荐排序");
        items.add(jobs);
        items.add(citys);
        items.add(sort);
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(14);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(Color.BLACK);//Menu的文字颜色
        mMenu.setmMenuListTextSize(12);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.gray_btn_bg_color));//Menu按下的背景颜色
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setmCheckIcon(R.drawable.ico_make);//Menu展开list的勾选图片
        mMenu.setmMenuItems(items);
//        getJobs(cityCode, typeid, areid, "2", "0");
    }
    public void onEvent(JobFilterEvent event) {
        initData();
    }

    @Override
    public void onStart() {
        LogUtils.e("fragemnt","onStart="+isInvisible);
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogUtils.e("fragemnt","onActivityCreated="+isInvisible);


        initData();
        super.onActivityCreated(savedInstanceState);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (jobList.size() >= 10 && lastVisibleItem == jobList.size() &&DataComplete) {
//                    getJobs(cityCode, typeid, areid, filterid,String.valueOf(lastVisibleItem));
                    isRefesh=false;
                    int pageNum=jobList.size()/10+1;
                    HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,areid,typeid,filterid, String.valueOf(pageNum));
                    DataComplete=false;
                    LogUtils.e("position",lastVisibleItem+"开始");
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
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }



}
