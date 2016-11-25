package com.woniukeji.jianguo.activity.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.adapter.HomeJobAdapter;
import com.woniukeji.jianguo.adapter.LooperPageAdapter;
import com.woniukeji.jianguo.base.BaseFragment;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.db.CityAreaDao;
import com.woniukeji.jianguo.entity.Banner;
import com.woniukeji.jianguo.entity.CityBannerEntity;
import com.woniukeji.jianguo.entity.CityBean;
import com.woniukeji.jianguo.entity.JobListBean;
import com.woniukeji.jianguo.eventbus.CityEvent;
import com.woniukeji.jianguo.eventbus.JobFilterEvent;
import com.woniukeji.jianguo.eventbus.LoginEvent;
import com.woniukeji.jianguo.eventbus.MessageEvent;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriberOnError;
import com.woniukeji.jianguo.http.SubscriberOnNextErrorListener;
import com.woniukeji.jianguo.listener.PageClickListener;
import com.woniukeji.jianguo.activity.partjob.PartJobActivity;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.widget.CircleImageView;
import com.woniukeji.jianguo.widget.FixedRecyclerView;
import com.woniukeji.jianguo.widget.LoopViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
import me.relex.circleindicator.CircleIndicator;

public class HomeFragment extends BaseFragment implements   View.OnClickListener ,PageClickListener , AMapLocationListener {
    @BindView(R.id.list) FixedRecyclerView recycleList;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_location) TextView tvLocation;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.rl_top) RelativeLayout rl_top;
    @BindView(R.id.circle_dot) CircleImageView circleDot;
    @BindView(R.id.rl_message) RelativeLayout rlMessage;
    private View headerView;
    private HomeJobAdapter adapter;
    private List<JobListBean> jobList = new ArrayList<JobListBean>();
    RelativeLayout mHeader;
    LinearLayout mLlPartTop;
    RelativeLayout mImgGiftsJob;
    RelativeLayout mImgDayJob;
    private RelativeLayout mImgMyJob;
    LinearLayout mLlPartBottom;
    RelativeLayout mImgTravelJob;
    TextView mTvPart3;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private String cityName;
    public String cityCode;
    private boolean DataComplete = false;
    private int totalDy;
    private String apkurl;
    private View view;
    private Unbinder bind;
    private SubscriberOnNextErrorListener<List<JobListBean>> listSubscriberOnNextListener;
    private SubscriberOnNextErrorListener<List<Banner>> bannerSubscriberOnNextListener;
//    private CityBannerEntity mCityBannerEntity;
    private int[] drawables = new int[]{R.mipmap.lead1, R.mipmap.lead2, R.mipmap.lead3};
    private LoopViewPager viewpager;
    private CircleIndicator indicator;
    private int currentItem  = 0;//当前页面
    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what == 100){
                viewpager.setCurrentItem(currentItem);
            }
        }
    };
    private String tel;
    private CityAreaDao cityAreaDao;
    private boolean isRefresh=true;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        int version = (int) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_VERSION, 0);
//        apkurl = (String) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.LOGIN_APK_URL, "");
//        if (version>getVersion()) {//大于当前版本升级
//            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("检测到新版本，是否更新？")
//                    .setConfirmText("确定")
//                    .setCancelText("取消")
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.dismissWithAnimation();
//                            UpDialog upDataDialog = new UpDialog(getActivity(),apkurl);
//                            upDataDialog.setCanceledOnTouchOutside(false);
//                            upDataDialog.setCanceledOnTouchOutside(false);
//                            upDataDialog.show();
//                        }
//                    }).show();
//        }
        int choose= (int) SPUtils.getParam(getActivity(),Constants.LOGIN_INFO,Constants.USER_CHOOSED_LOCATION,0);
        if (choose==0){
            initLocationListener();
        }
    }

    private void initLocationListener() {
        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        locationOption = new AMapLocationClientOption();
        locationClient.setLocationListener(this);
        //初始化定位参数
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
//设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true);

        if(locationOption.isOnceLocationLatest()){
            locationOption.setOnceLocationLatest(true);
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
//如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        }

//设置是否强制刷新WIFI，默认为强制刷新
        locationOption.setWifiActiveScan(true);
//设置是否允许模拟位置,默认为false，不允许模拟位置
        locationOption.setMockEnable(false);
//设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(2000);
//给定位客户端对象设置定位参数
        locationClient.setLocationOption(locationOption);
//启动定位
        locationClient.startLocation();
        SPUtils.setParam(getActivity(),Constants.LOGIN_INFO,Constants.USER_CHOOSED_LOCATION,1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_jobitem_list, container, false);
        headerView = inflater.inflate(R.layout.home_header_view, container, false);
        bind = ButterKnife.bind(this, view);//绑定framgent
        RelativeLayout rlMessage = (RelativeLayout) view.findViewById(R.id.rl_message);
        rlMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleDot.setVisibility(View.GONE);
                startActivity(new Intent(getContext(), PushMessageActivity.class));
            }
        });
        assignViews(headerView);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        adapter = new HomeJobAdapter(jobList, getActivity());
        adapter.addHeaderView(headerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recycleList.setLayoutManager(mLayoutManager);
        recycleList.setAdapter(adapter);
        recycleList.setItemAnimator(new DefaultItemAnimator());
        refreshLayout.setColorSchemeResources(R.color.app_bg);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh =true;
                HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,null,null,null, "1");
            }
        });
        initData();
        return view;
    }
    @Override
    public int getContentViewId() {
        return view.getId();
    }

    private void assignViews(View view) {
        mHeader = (RelativeLayout) view.findViewById(R.id.header);
        viewpager = (LoopViewPager) view.findViewById(R.id.viewpager);
        indicator = (CircleIndicator) view.findViewById(R.id.indicator_default_circle);
        mLlPartTop = (LinearLayout) view.findViewById(R.id.ll_part_top);
        mImgGiftsJob = (RelativeLayout) view.findViewById(R.id.img_gifts_job);
        mImgDayJob = (RelativeLayout) view.findViewById(R.id.img_day_job);
        mImgTravelJob = (RelativeLayout) view.findViewById(R.id.img_travel_job);
        mImgMyJob = (RelativeLayout) view.findViewById(R.id.img_my_job);
        mLlPartBottom = (LinearLayout) view.findViewById(R.id.ll_part_bottom);
        mTvPart3 = (TextView) view.findViewById(R.id.tv_part3);
        mImgGiftsJob.setOnClickListener(this);
        mImgDayJob.setOnClickListener(this);
        mImgTravelJob.setOnClickListener(this);
        mImgMyJob.setOnClickListener(this);
        tvLocation.setOnClickListener(this);

    }
    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
    }
/**
*banner图选中点击的时候
*@param
*@param
*@author invinjun
*created at 2016/8/26 11:39
*/
    @Override
    public void onPageClickListener(String url) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewpager) {
                currentItem = (currentItem+1)%drawables.length;
                handler.sendEmptyMessage(100);
            }
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (jobList.size() > 5 && lastVisibleItem == jobList.size() + 1 && DataComplete) {
                    isRefresh=false;
                    int page=jobList.size()/10+1;
                    HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,null,null,null, String.valueOf(page));     DataComplete = false;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                totalDy = totalDy + dy;
                int distance = totalDy;
                if (distance > 0 && distance < 500) {
                    rl_top.getBackground().mutate().setAlpha(distance / 2);
                } else if (distance > 500) {
                    rl_top.getBackground().mutate().setAlpha(255);
                } else {
                    rl_top.getBackground().mutate().setAlpha(0);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_gifts_job:
                Intent intent = new Intent(getActivity(), PartJobActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("cityid", cityCode);
                startActivity(intent);
                break;
            case R.id.img_day_job:
                Intent dayIntent = new Intent(getActivity(), PartJobActivity.class);
                dayIntent.putExtra("type", 5);
                dayIntent.putExtra("cityid", cityCode);
                startActivity(dayIntent);
                break;
            case R.id.img_travel_job:
                Intent travelIntent = new Intent(getActivity(), PartJobActivity.class);
                travelIntent.putExtra("type", 3);
                travelIntent.putExtra("cityid", cityCode);
                startActivity(travelIntent);
                break;
            case R.id.img_my_job:
                Intent LongIntent = new Intent(getActivity(), PartJobActivity.class);
                LongIntent.putExtra("type", 6);
                LongIntent.putExtra("cityid", cityCode);
                startActivity(LongIntent);
                break;
            case R.id.tv_location:
                startActivity(new Intent(getActivity(), CityActivity.class));
        }
    }

    private void initData() {
        cityAreaDao=new CityAreaDao(getActivity());
        CityBean cityBean = cityAreaDao.queryCitySelected();
        cityName = cityBean.getCityName();
        cityCode= cityBean.getCode();
        tvLocation.setText(cityName);

        bannerSubscriberOnNextListener =new SubscriberOnNextErrorListener<List<Banner>>() {
            @Override
            public void onNext(List<Banner> banners) {
                super.onNext(banners);
                initBannerData(banners);
            }

            @Override
            public void onError(String mes) {
                super.onError(mes);
            }
        };
        HttpMethods.getInstance().getBanner(new ProgressSubscriberOnError<List<Banner>>(bannerSubscriberOnNextListener,getActivity()));

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
        tel = (String) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TEL, "");
        isRefresh=true;
        HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,null,null,null,"1");
    }

    private void initBannerData(List<Banner> banners) {
        List<String> strings=new ArrayList<>();
        for (int i = 0; i < banners.size(); i++) {
            strings.add(banners.get(i).getImage());
        }
        LooperPageAdapter pageAdapter=new LooperPageAdapter(this,banners);
        viewpager.setAdapter(pageAdapter);
        indicator.setViewPager(viewpager);
        startPlay();
           }
    @Override
    public void onResume() {
        super.onResume();
    }
    /**
     * 顯示極光推送過來的消息提醒
     * 首页信封險是紅點
     */
    public void onEvent(MessageEvent event) {
        circleDot.setVisibility(View.VISIBLE);
    }
    public void onEvent(LoginEvent event) {
        initData();
    }
    public void onEvent(final CityEvent event) {
  //首先判断是否是同一城市如果不是同一个城市， 则判断是不是gps自动定位的切换请求，如果是gps则提示用户是否切换
 if (!event.city.getCode().equals(cityCode)) {
        if (event.isGPS){

        }else {//如果是用户手动选择的直接切换即可
            cityName=event.city.getCityName();
            cityCode=event.city.getCode();
            tvLocation.setText(event.city.getCityName());
            cityAreaDao.updateData(cityCode);
            isRefresh=true;
            HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,null,null,null,"1");
            JobFilterEvent jobFilterEvent = new JobFilterEvent();
            jobFilterEvent.cityId = cityCode;
            EventBus.getDefault().post(jobFilterEvent);
        }
     //无论如何到保存最近设置的城市属性
//     SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.USER_LOCATION_CODE, cityCode);
//     SPUtils.setParam(getActivity(), Constants.LOGIN_INFO, Constants.USER_LOCATION_NAME, cityName);
     }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
        EventBus.getDefault().unregister(this);
                if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        String cityName1="北京";
        String cityCode1="010";
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                Log.e("AmapError","location success, CityCode:"
                        + aMapLocation.getCityCode() + ", Province:"
                        + aMapLocation.getProvince());
                cityCode1=aMapLocation.getCityCode();
                cityName1= aMapLocation.getCity().substring(0,aMapLocation.getCity().length()-1);
                final String finalCityName = cityName1;
                final String finalCityCode = cityCode1;
                if (!cityCode1.equals(cityCode)){
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("切换城市到" +cityName1+ "?")
                            .setConfirmText("确定")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    if (tvLocation != null) {
                                        tvLocation.setText(finalCityName);
                                    }
                                    cityCode = finalCityCode;
                                    cityName= finalCityName;
                                    isRefresh=true;
                                    cityAreaDao.updateData(cityCode);
                                    HttpMethods.getInstance().getJobList(new ProgressSubscriberOnError<List<JobListBean>>(listSubscriberOnNextListener,getActivity()),cityCode,null,null,null,"1");
                                    JobFilterEvent jobFilterEvent = new JobFilterEvent();
                                    EventBus.getDefault().post(jobFilterEvent);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelText("取消").show();
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
            //定位后将定位信息发送给homeFragement
            CityEvent event = new CityEvent();
            event.city .setCityName(cityName);
            event.city.setCode(cityCode);
            event.isGPS=true;
            EventBus.getDefault().post(event);
        }
    }
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
