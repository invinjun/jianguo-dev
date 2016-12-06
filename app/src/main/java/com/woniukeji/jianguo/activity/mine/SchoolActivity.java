package com.woniukeji.jianguo.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.db.CityAreaDao;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.CityBean;
import com.woniukeji.jianguo.entity.School;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.utils.ActivityManager;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.MD5Util;
import com.woniukeji.jianguo.utils.SPUtils;
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

public class SchoolActivity extends BaseActivity {
    @BindView(R.id.et_search) EditText etSearch;
    @BindView(R.id.btn_clear_search_text) Button btnClearSearchText;
    @BindView(R.id.layout_clear_search_text) LinearLayout layoutClearSearchText;
    @BindView(R.id.ll_search) LinearLayout llSearch;
    @BindView(R.id.lv_search) ListView lvSearch;
    @BindView(R.id.img_back) ImageView imageView;

    private Handler mHandler = new Myhandler(this);
    private Context context = SchoolActivity.this;
    private int MSG_POST_SUCCESS = 0;
    private int MSG_POST_FAIL = 1;
    private List<School> tschools=new ArrayList<School>();
    private Adapter adapter;
    private SubscriberOnNextListener<List<School>> subscriberOnNextListener;
    private String tel;


    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SchoolActivity schoolActivity = (SchoolActivity) reference.get();
            switch (msg.what) {
                case 1:
//                    if (null != authActivity.pDialog) {
//                        authActivity.pDialog.dismiss();
//                    }
                    String ErrorMessage = (String) msg.obj;
                    Toast.makeText(schoolActivity, ErrorMessage, Toast.LENGTH_SHORT).show();
                    break;


                default:
                    break;
            }
        }


    }
    @OnClick(R.id.img_back)
    public void onClick() {
        finish();
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_school);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
         adapter=new Adapter();
        lvSearch.setAdapter(adapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String schoolTemp=tschools.get(i).getName();
                Intent intent=getIntent();
                intent.putExtra("school",schoolTemp);
                intent.putExtra("school_id",tschools.get(i).getId());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void initListeners() {
        //搜索按键 模糊查询
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void initData() {
        subscriberOnNextListener=new SubscriberOnNextListener<List<School>>() {
            @Override
            public void onNext(List<School> schools) {
                tschools.clear();
                tschools.addAll(schools);
                adapter.notifyDataSetChanged();
            }
        };

        tel = (String) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_TEL, "");
        CityAreaDao cityAreaDao=new CityAreaDao(this);
        CityBean cityBean = cityAreaDao.queryCitySelected();
        String cityCode=cityBean.getCode();
        HttpMethods.getInstance().getSchool(SchoolActivity.this,new ProgressSubscriber(subscriberOnNextListener,this), tel,cityCode);

    }

    @Override
    public void addActivity() {
        ActivityManager.getActivityManager().addActivity(SchoolActivity.this);
    }

    @Override
    public void onClick(View view) {

    }

    private  class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tschools.size();
        }

        @Override
        public Object getItem(int i) {
            return tschools.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            School schItem = tschools.get(i);
            ViewHolder holder;
            LayoutInflater layoutInflater = LayoutInflater.from(context);;
            if (convertView==null) {
                convertView = layoutInflater.inflate(R.layout.school_item,null);
                holder=new ViewHolder();
                holder.tvSchool= (TextView) convertView.findViewById(R.id.tv_school);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvSchool.setText(schItem.getName());
            return convertView;
        }
         class ViewHolder{
            TextView tvSchool;

        }
    }



}
