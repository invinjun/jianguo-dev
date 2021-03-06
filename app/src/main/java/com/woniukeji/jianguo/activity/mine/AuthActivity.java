package com.woniukeji.jianguo.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.woniukeji.jianguo.R;
import com.woniukeji.jianguo.activity.BaseActivity;
import com.woniukeji.jianguo.base.Constants;
import com.woniukeji.jianguo.entity.BaseBean;
import com.woniukeji.jianguo.entity.RealName;
import com.woniukeji.jianguo.http.HttpMethods;
import com.woniukeji.jianguo.http.ProgressSubscriber;
import com.woniukeji.jianguo.http.SubscriberOnNextListener;
import com.woniukeji.jianguo.activity.login.BindPhoneActivity;
import com.woniukeji.jianguo.utils.ActivityManager;
import com.woniukeji.jianguo.utils.BitmapUtils;
import com.woniukeji.jianguo.utils.LogUtils;
import com.woniukeji.jianguo.widget.CircleProDialog;
import com.woniukeji.jianguo.utils.DateUtils;
import com.woniukeji.jianguo.utils.EditCheckUtil;
import com.woniukeji.jianguo.utils.FileUtils;
import com.woniukeji.jianguo.utils.MD5Coder;
import com.woniukeji.jianguo.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Response;

public class AuthActivity extends BaseActivity {
    @BindView(R.id.img_back) ImageView imgBack;
    @BindView(R.id.tv_title) TextView title;
    @BindView(R.id.img_front) ImageView imgFront;
    @BindView(R.id.tv_not1) TextView tvNot1;
    @BindView(R.id.tv_not2) TextView tvNot2;
    @BindView(R.id.img_opposite) ImageView imgOpposite;
    @BindView(R.id.ll_top) LinearLayout llTop;
    @BindView(R.id.img_phone) ImageView imgPhone;
    @BindView(R.id.img) ImageView img;
    @BindView(R.id.tv_phone_auth) TextView etPhoneAuth;
    @BindView(R.id.et_real_name) EditText etRealName;
    @BindView(R.id.et_id) EditText etId;
    @BindView(R.id.img_pass) ImageView imgPass;
    @BindView(R.id.rb_man) RadioButton rbMan;
    @BindView(R.id.rb_woman) RadioButton rbWoman;
    @BindView(R.id.check_button) Button checkButton;
    @BindView(R.id.rl_phone) RelativeLayout rlPhone;
    private int MSG_GET_SUCCESS = 2;
    private int MSG_GET_FAIL = 3;
    private String tel;
    private int status;
    private String sex="2";

    private Handler mHandler = new Myhandler(this);
    private Context context = AuthActivity.this;
    private String realFilePath1;
    private String realFilePath2;
    private String url1;
    private String url2;
    private CircleProDialog circleProDialog;

    private SubscriberOnNextListener<String> realSubscriberOnNextListener;
    private SubscriberOnNextListener<RealName> realNameSubscriberOnNextListener;
    private static class Myhandler extends Handler {
        private WeakReference<Context> reference;

        public Myhandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AuthActivity authActivity = (AuthActivity) reference.get();
            switch (msg.what) {
                case 3:
                    if (null!=authActivity.circleProDialog){
                        authActivity.circleProDialog.dismiss();
                    }
                    String sms = (String) msg.obj;
                    Toast.makeText(authActivity, sms, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }


    }
    private void setInf(RealName realName) {
        etRealName.setText(realName.getRealname());
        if (realName.getSex()==1){
            rbMan.setChecked(false);
            rbWoman.setChecked(true);
        }else {
            rbMan.setChecked(true);
            rbWoman.setChecked(false);
        }
          SPUtils.setParam(context,Constants.LOGIN_INFO,Constants.SP_STATUS,realName.getAuth_status());
        if (realName.getAuth_status()==2){//已经认证 可以查询信息
//            PostTask postTask=new PostTask(false,String.valueOf(tel),null,null,null,null,null);
//            postTask.execute();
            imgPass.setVisibility(View.VISIBLE);
            tvNot1.setText("您已认证通过");
            tvNot2.setText("为保证您的信息安全，兼果已为您隐藏个人信息");
            checkButton.setText("审核通过");
            checkButton.setBackgroundResource(R.color.gray);
            checkButton.setClickable(false);
            checkButton.setFocusable(false);
            etRealName.setClickable(false);
            etRealName.setFocusable(false);
            etRealName.setFocusableInTouchMode(false);
            etId.setClickable(false);
            etId.setFocusable(false);
            etId.setFocusableInTouchMode(false);
            rbMan.setClickable(false);
            rbWoman.setClickable(false);
            imgFront.setClickable(false);
            imgOpposite.setClickable(false);
            String id=realName.getIDcard();
            etId.setText(id.substring(0,id.length()-6)+"******");
        }else if(realName.getAuth_status()==3){//审核中
            checkButton.setText("已认证");
            checkButton.setClickable(false);
            checkButton.setFocusable(false);
            checkButton.setBackgroundResource(R.color.gray);
//            PostTask postTask=new PostTask(false,String.valueOf(tel),null,null,null,null,null);
//            postTask.execute();
            etRealName.setClickable(false);
            etRealName.setFocusable(false);
            etRealName.setFocusableInTouchMode(false);
            etId.setClickable(false);
            etId.setFocusable(false);
            etId.setFocusableInTouchMode(false);
            rbMan.setClickable(false);
            rbWoman.setClickable(false);
            imgFront.setClickable(false);
            imgOpposite.setClickable(false);
            String id=realName.getIDcard();
            etId.setText(id.substring(0,id.length()-6)+"******");
            Glide.with(context).load(realName.getFront_img_url()).placeholder(R.mipmap.icon_fanmian).error(R.mipmap.icon_fanmian).into(imgFront);
            Glide.with(context).load(realName.getBehind_img_url()).placeholder(R.mipmap.icon_zhengmian).error(R.mipmap.icon_zhengmian).into(imgOpposite);

        }else if(realName.getAuth_status()==4){//未通过
            checkButton.setText("重新审核");
            etId.setText("");
            checkButton.setBackgroundResource(R.drawable.button_background_login);
        }
    }
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
    }

    @Override
    public void initViews() {
        FileUtils.newFolder(Constants.IMG_PATH);
        title.setText("实名认证");
    }

    @Override
    public void initListeners() {
        realSubscriberOnNextListener=new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String s) {
                SPUtils.setParam(context,Constants.LOGIN_INFO,Constants.SP_STATUS,3);
                showShortToast("实名信息上传成功!");
                finish();
            }
        };


        realNameSubscriberOnNextListener=new SubscriberOnNextListener<RealName>() {
            @Override
            public void onNext(RealName realName) {
               setInf(realName);
            }
        };
    }

    @Override
    public void initData() {


    }

    @Override
    protected void onStart() {
        super.onStart();
        tel = (String) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_TEL, "");
        status = (int) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_STATUS, 0);
        if (tel.equals("0")) {
            etPhoneAuth.setText("请认证手机号");
            rlPhone.setClickable(true);
        } else {
            etPhoneAuth.setText(tel + " (已绑定)");
            etPhoneAuth.setFocusable(false);
            etPhoneAuth.setFocusableInTouchMode(false);
            etPhoneAuth.setClickable(false);
            rlPhone.setClickable(false);
        }
        HttpMethods.getInstance().getAuthInfo(AuthActivity.this,new ProgressSubscriber<RealName>(realNameSubscriberOnNextListener,this),tel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this);
    }

    @Override
    public void addActivity() {
        ActivityManager.getActivityManager().addActivity(AuthActivity.this);
    }

    @OnClick({R.id.rl_phone,R.id.img_back, R.id.img_front, R.id.img_opposite,  R.id.rb_man, R.id.rb_woman, R.id.check_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_phone:
                startActivity(new Intent(context, BindPhoneActivity.class));
//                finish();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.img_front:
                Intent intent2=new Intent(AuthActivity.this,PicTipActivity.class);
                intent2.putExtra("front",false);
                startActivityForResult(intent2,2);
//                CropImage.startPickImageActivity(this,CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE1);
                break;
            case R.id.img_opposite:
                Intent intent3=new Intent(AuthActivity.this,PicTipActivity.class);
                intent3.putExtra("front",false);
                startActivityForResult(intent3,3);
//                CropImage.startPickImageActivity(this,CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE2);
                break;
            case R.id.rb_man:
                sex="2";
                rbMan.setChecked(true);
                rbWoman.setChecked(false);
                break;
            case R.id.rb_woman:
                sex="1";
                rbMan.setChecked(false);
                rbWoman.setChecked(true);
                break;
            case R.id.check_button:
                String phone = etPhoneAuth.getText().toString().trim();
                String name = etRealName.getText().toString().trim();
                String id = etId.getText().toString().trim();

                if (name==null||name.equals("")){
                    showShortToast("请填写真实姓名");
                    break;
                } else if (id==null||id.equals("")){
                    showShortToast("请填写身份证号码");
                    break;
                }else if(!EditCheckUtil.IDCardValidate(id)){
                    showShortToast("身份证号码不正确");
                    break;
                }


                if (realFilePath1 == null || realFilePath2 == null || realFilePath1.equals("") || realFilePath2.equals("")) {
                    HttpMethods.getInstance().postReal(AuthActivity.this,new ProgressSubscriber<String>(realSubscriberOnNextListener,context),String.valueOf(tel),"","",name,id,sex);
                    break;
                }else{
                    circleProDialog = new CircleProDialog(AuthActivity.this);
                    circleProDialog.setCanceledOnTouchOutside(false);
                    circleProDialog.setCanceledOnTouchOutside(false);
                    circleProDialog.show();
                    String key=MD5Coder.getQiNiuName("front"+String.valueOf(tel));
                    url1="http://7xlell.com2.z0.glb.qiniucdn.com/"+key;
                    String key2=MD5Coder.getQiNiuName("behind"+String.valueOf(tel));
                    url2="http://7xlell.com2.z0.glb.qiniucdn.com/"+key2;
                    upLoadQiNiu(this,key , realFilePath1,1);
                    upLoadQiNiu(this,key2 , realFilePath2,2);
                    HttpMethods.getInstance().postReal(AuthActivity.this,new ProgressSubscriber<String>(realSubscriberOnNextListener,context),String.valueOf(tel),url1,url2,name,id,sex);

                }
                 break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            super.onRestoreInstanceState(savedInstanceState);
        }catch(Exception e){
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        try{
            super.onRestoreInstanceState(outState);
        }catch(Exception e){
            outState = null;
        }

    }
    public  void upLoadQiNiu(final Context context, final String key, final String filePath, final int position) {
        String commonUploadToken = (String) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_QNTOKEN, "");
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath, key, commonUploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.error!=null) {
                    LogUtils.e("key","错误："+info.error);
                }else {
                    LogUtils.e("key","错误："+info.isOK());
                }
            }
        }, new UploadOptions(null, null, false,
                new UpProgressHandler(){
                    public void progress(String key, double percent){
                        LogUtils.e("key","进度"+percent);
                    }
                }, null));

    }
    public  void upLoadQiNiu(final Context context, String key, String filePath, final int position, final String name, final String id) {
        String commonUploadToken = (String) SPUtils.getParam(context, Constants.LOGIN_INFO, Constants.SP_QNTOKEN, "");
        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath, key, commonUploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (position==1){
                     url1="http://7xlell.com2.z0.glb.qiniucdn.com/"+key;
                    circleProDialog.setMsg("正在上传第二张……");
                    upLoadQiNiu(context, MD5Coder.getQiNiuName(String.valueOf(tel)), realFilePath2,2,name,id);
                }
                else {
                    url2="http://7xlell.com2.z0.glb.qiniucdn.com/"+key;
                    circleProDialog.dismiss();
                    HttpMethods.getInstance().postReal(context,new ProgressSubscriber<String>(realSubscriberOnNextListener,context),String.valueOf(tel),url1,url2,name,id,sex);
//                    postRealName(String.valueOf(tel),url1,url2,name,id,sex);
                }
            }
        }, new UploadOptions(null, null, false,
                new UpProgressHandler(){
                    public void progress(String key, double percent){
                        circleProDialog.setMsg(percent);
                    }
                }, null));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ADW: sometimes on rotating the phone, some widgets fail to restore its states.... so... damn.
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                File file = new File(path.get(0));
                Uri imgSource = Uri.fromFile(file);
                startCropImageActivity(imgSource,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE1);
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 处理你自己的逻辑 ....
                File file = new File(path.get(0));
                Uri imgSource = Uri.fromFile(file);
                startCropImageActivity(imgSource,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE2);
            }
        }
        if (requestCode==2){//弹出提示 提示返回 然后跳转拍照界面 正面
            if (resultCode == RESULT_OK) {
                MultiImageSelectorActivity.startSelect(AuthActivity.this, 0, 1, 0);
            }
        }
        if (requestCode==3){//弹出提示 提示返回 然后跳转拍照界面 背面
            if (resultCode == RESULT_OK) {
                MultiImageSelectorActivity.startSelect(AuthActivity.this, 1, 1, 0);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE1) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                realFilePath1 = FileUtils.getRealFilePath(AuthActivity.this, result.getUri());
                LogUtils.e("filepath",realFilePath1);
                Bitmap bitmap=BitmapUtils.compressBitmap(realFilePath1,1080, 720);
                imgFront.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE2) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                realFilePath2 = FileUtils.getRealFilePath(AuthActivity.this, result.getUri());
                Bitmap bitmap=BitmapUtils.compressBitmap(realFilePath2,1080, 720);
                imgOpposite.setImageBitmap(bitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri,int requestCode) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this,requestCode,false);
    }


}
