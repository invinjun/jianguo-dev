package com.woniukeji.jianguo.activity.wallte;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdsmdg.tastytoast.TastyToast;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.adapter.WallteInAdapter;
import com.woniukeji.jianguo.base.BaseFragment;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.WageLog;
import com.woniukeji.jianguo.eventbus.AttentionCollectionEvent;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.SPUtils;
import com.woniukeji.jianguo.widget.FixedRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class WallteInFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.img_renwu) ImageView imgRenwu;
    @BindView(R.id.list) FixedRecyclerView list;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rl_null) RelativeLayout rlNull;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WallteInAdapter adapter;
    private int MSG_GET_SUCCESS = 0;
    private int MSG_GET_FAIL = 1;
    public List<WageLog> wagesList = new ArrayList<WageLog>();
    private Handler mHandler = new Myhandler(this.getActivity());
    private Context mContext = this.getActivity();
    private String tel;
    private int delePosition;
    private int lastVisibleItem;
    private LinearLayoutManager mLayoutManager;
    private SubscriberOnNextListener<List<WageLog>> listSubscriberOnNextListener;

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
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
                case 1:
                    rlNull.setVisibility(View.VISIBLE);
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(getActivity(), ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    break;
                case 3:
                    String sms = (String) msg.obj;
                    Toast.makeText(getActivity(), sms, Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    wagesList.remove(delePosition);
                    adapter.notifyDataSetChanged();
                    break;
                case 6:
                    String mes = (String) msg.obj;
                    Toast.makeText(getActivity(), mes, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理长按事件
     */
    public void onEvent(AttentionCollectionEvent event) {
        if (event.listTJob!=null){
            delePosition=event.position;
//            DeleteTask deleteTask=new DeleteTask(String.valueOf(tel),String.valueOf( event.listTJob.getId()));
//            deleteTask.execute();
        }
    }
    // TODO: Rename and change types and number of parameters
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)    {
        View view = inflater.inflate(R.layout.fragment_wallte, container, false);
        ButterKnife.bind(this, view);
        initview();
        initListener();
        EventBus.getDefault().register(this);
        return view;

    }

    private void initListener() {
        listSubscriberOnNextListener=new SubscriberOnNextListener<List<WageLog>>() {
            @Override
            public void onNext(List<WageLog> wageLogs) {
                if (refreshLayout!=null&&refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                    wagesList.clear();
                }
                wagesList.addAll(wageLogs);
                adapter.notifyDataSetChanged();
                if (wagesList.size()>0){
                    rlNull.setVisibility(View.GONE);
                }
                TastyToast.makeText(getActivity(),"获取成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
            }
        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_wallte;
    }
    private void initview() {
        tel = (String) SPUtils.getParam(getActivity(), Constants.LOGIN_INFO, Constants.SP_TEL, "");
//        tvTitle.setText("兼职");
//        imgBack.setVisibility(View.GONE);
        adapter = new WallteInAdapter(wagesList, getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity());
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
//                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpMethods.getInstance().getWageLog(getActivity(),new ProgressSubscriber<List<WageLog>>(listSubscriberOnNextListener,getActivity()),tel,1,1);

            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (wagesList.size() >=10 && lastVisibleItem == wagesList.size() ) {
                    int pageNum=wagesList.size()/10+1;
                    HttpMethods.getInstance().getWageLog(getActivity(),new ProgressSubscriber<List<WageLog>>(listSubscriberOnNextListener,getActivity()),tel,1,pageNum);

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        HttpMethods.getInstance().getWageLog(getActivity(),new ProgressSubscriber<List<WageLog>>(listSubscriberOnNextListener,getActivity()),tel,1,1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

//
}
