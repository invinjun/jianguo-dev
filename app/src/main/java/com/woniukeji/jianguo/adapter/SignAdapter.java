package com.woniukeji.jianguo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.entity.Jobs;
import com.woniukeji.jianguo.activity.partjob.JobDetailActivity;
import com.woniukeji.jianguo.entity.JoinJob;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.GlideCircleTransform;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignAdapter extends RecyclerView.Adapter<SignAdapter.ViewHolder> {

    private final List<JoinJob> mValues;
    private final Context mContext;



    private int mType;
    public static final int NORMAL = 1;
    public static final int IS_FOOTER = 2;
//    private AnimationDrawable mAnimationDrawable;
    private boolean isFooterChange = false;
    private RecyCallBack mCallBack;

    public SignAdapter(List<JoinJob> items, Context context, int type, RecyCallBack callBack) {
        mValues = items;
        mContext = context;
        mType = type;
        mCallBack = callBack;
    }


    public interface RecyCallBack {
        void RecyOnClick(long id, int admit, int position);
    }

    public void setFooterChange(boolean isChange) {
        isFooterChange = isChange;
    }

    public void mmswoon(ViewHolder holder) {
        if (isFooterChange) {
            holder.loading.setText("已加载全部");
        } else {
            holder.loading.setText("已加载全部");
            holder.animLoading.setVisibility(View.GONE);
//            holder.animLoading.setBackgroundResource(R.drawable.loading_footer);
//            mAnimationDrawable = (AnimationDrawable) holder.animLoading.getBackground();
//            mAnimationDrawable.start();
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder = null;
        switch (viewType) {
            case NORMAL:
                View VoteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_jobitem, parent, false);
                holder = new ViewHolder(VoteView, NORMAL);
                return holder;

            case IS_FOOTER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_footer, parent, false);
                holder = new ViewHolder(view, IS_FOOTER);
                return holder;
            default:
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mValues.size() < 1) {
            holder.itemView.setVisibility(View.GONE);
        }
        if (mValues.size() == position) {
            if (mValues.size() > 4) {
                mmswoon(holder);
                holder.itemView.setVisibility(View.VISIBLE);
            }
        } else {
            final JoinJob jobEntity = mValues.get(position);

            String date = DateUtils.getTime(jobEntity.getStart_date(), jobEntity.getEnd_date());
            holder.tvWorkDate.setText(date);
            holder.businessName.setText(jobEntity.getJob_name());
//            if (jobEntity.getMax()==1){
//                holder.businessName.setCompoundDrawablesWithIntrinsicBounds (null,null,mContext.getResources().getDrawable(R.mipmap.cq),null);
//            }else{
//                holder.businessName.setCompoundDrawables(null,null,null,null);
//            }
            // 期限（0=月结，1=周结，2=日结，3=小时结，4=次，5=义工
            String type="";
            if (jobEntity.getTerm()==0){
                holder.tvJobWages.setText(jobEntity.getMoney()+"/月");
                type="/月";
            }else if(jobEntity.getTerm()==1){
                holder.tvJobWages.setText(jobEntity.getMoney()+"/周");
                type="/周";
            }else if(jobEntity.getTerm()==2){
                holder.tvJobWages.setText(jobEntity.getMoney()+"/日");
                type="/日";
            }else if(jobEntity.getTerm()==3){
                holder.tvJobWages.setText(jobEntity.getMoney()+"/时");
                type="/时";
            }else if(jobEntity.getTerm()==4){
                holder.tvJobWages.setText(jobEntity.getMoney()+"/次");
                type="/次";
            }else if(jobEntity.getTerm()==5){
                holder.tvJobWages.setText("义工");
                type="义工";
            }else if(jobEntity.getTerm()==6){
                holder.tvJobWages.setText("面议");
                type="面议";
            }

            if (jobEntity.getStatus()==0){
                holder.btnCancelActn.setText("取消报名");
                holder.btnCancelActn.setVisibility(View.VISIBLE);
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.imgFinishStatus.setVisibility(View.GONE);
                holder.btnCancelActn.setBackgroundResource(R.drawable.button_sign_background_gray);
                holder.imgJobStatus.setBackgroundResource(R.mipmap.icon_dailuqu);
            }else if(jobEntity.getStatus()==1){
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.imgJobStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_dailuqu));
                holder.btnCancelActn.setVisibility(View.VISIBLE);
//                holder.imgFinishStatus.setVisibility(View.VISIBLE);
//                holder.imgFinishStatus.setBackgroundResource(R.mipmap.icon_dailuqu);
            }else if(jobEntity.getStatus()==2){
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.imgJobStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_yiluqu));
                holder.btnCancelActn.setVisibility(View.GONE);
                holder.imgFinishStatus.setVisibility(View.VISIBLE);
                holder.imgFinishStatus.setBackgroundResource(R.mipmap.icon_shangjia);
            }else if(jobEntity.getStatus()==3){
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.btnCancelActn.setVisibility(View.GONE);
                holder.imgFinishStatus.setVisibility(View.GONE);
                holder.imgJobStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_yiluqu));
                holder.btnConfirmActn.setBackgroundResource(R.drawable.button_sign_background_red);
            }else if(jobEntity.getStatus()==4){
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.imgJobStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_yiluqu));
                holder.btnCancelActn.setVisibility(View.GONE);
                holder.imgFinishStatus.setVisibility(View.VISIBLE);
                holder.imgFinishStatus.setBackgroundResource(R.mipmap.icon_quxiao);
            }else if(jobEntity.getStatus()==5){
                holder.btnConfirmActn.setVisibility(View.GONE);
                holder.btnCancelActn.setVisibility(View.GONE);
                holder.imgFinishStatus.setVisibility(View.VISIBLE);
                holder.imgJobStatus.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_yiluqu));
            }
            Glide.with(mContext).load(jobEntity.getJob_image())
                    .placeholder(R.mipmap.icon_head_defult)
                    .error(R.mipmap.icon_head_defult)
                    .transform(new GlideCircleTransform(mContext))
                    .into(holder.userHead);

            if (type.equals("面议")||type.equals("义工")){

            }else {
                type=jobEntity.getMoney()+ type;
            }
            final String finalType = type;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, JobDetailActivity.class);
                    intent.putExtra("jobbean", jobEntity);
                    intent.putExtra("job",jobEntity.getId());
                    intent.putExtra("jobbean",jobEntity);
                    intent.putExtra("money", finalType);
                    intent.putExtra("count", jobEntity.getCount()+"/"+jobEntity.getSum());
                    intent.putExtra("mername", jobEntity.getContact_name());
                    mContext.startActivity(intent);
                }
            });
            holder.btnCancelActn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (jobEntity.getStatus()==1){
                        new SweetAlertDialog(mContext,SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("确定取消报名吗？")
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        mCallBack.RecyOnClick(jobEntity.getId(), 2,position);
                                        sweetAlertDialog.dismissWithAnimation();
                                        return;
                                    }
                                }).show();
                    }

                }
            });
            holder.btnConfirmActn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mCallBack.RecyOnClick(jobEntity.getId(),5,position);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() > 0 ? mValues.size() + 1 : 0;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == mValues.size()) {
            return IS_FOOTER;
        } else {
            return NORMAL;
        }
    }

    static

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_head) ImageView userHead;
        @BindView(R.id.business_name) TextView businessName;
        @BindView(R.id.tv_job_wages) TextView tvJobWages;

        @BindView(R.id.img_pay) ImageView imgPay;
        @BindView(R.id.tv_work_date) TextView tvWorkDate;
        @BindView(R.id.rl_top) RelativeLayout rlTop;
        @BindView(R.id.btn_cancel_actn) Button btnCancelActn;
        @BindView(R.id.btn_confirm_actn) Button btnConfirmActn;
        @BindView(R.id.ll_button) LinearLayout llButton;
        @BindView(R.id.rl_job) RelativeLayout rlJob;
        @BindView(R.id.img_job_status) ImageView imgJobStatus;
        @BindView(R.id.img_finish_status) ImageView imgFinishStatus;
        private ImageView animLoading;
        private TextView loading;

        public ViewHolder(View view, int type) {
            super(view);

            switch (type) {
                case NORMAL:
                    ButterKnife.bind(this, view);
                    break;
                case IS_FOOTER:
                    animLoading = (ImageView) view.findViewById(R.id.anim_loading);
                    loading = (TextView) view.findViewById(R.id.tv_loading);
                    break;
            }
        }

        @Override
        public String toString() {
            return super.toString()+"";
        }
    }


}
