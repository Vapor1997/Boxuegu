package cn.edu.gdmec.android.boxuegu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.edu.gdmec.android.boxuegu.R;
import cn.edu.gdmec.android.boxuegu.bean.UserBean;
import cn.edu.gdmec.android.boxuegu.utils.AnalysisUtils;
import cn.edu.gdmec.android.boxuegu.utils.DBUtils;
import cn.edu.gdmec.android.boxuegu.utils.ImageUtils;

import static cn.edu.gdmec.android.boxuegu.R.id.tv_back;
/**
 * Created by student on 17/12/27.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_main_title;
    private TextView tv_nicName, tv_signature, tv_user_name, tv_sex;
    private RelativeLayout rl_nickName, rl_sex, rl_signature, rl_title_bar, rl_head;
    private static final int CHANGE_NICKNAME = 1;
    private static final int CHANGE_SIGNATURE = 2;
    private String spUserName;

    private ImageView iv_head_icon;
    private ImageUtils imageUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        spUserName = AnalysisUtils.redLoginUserName(this);
        init();
        initData();
        setListener();
    }

    private void init() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("个人资料");
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        rl_nickName = (RelativeLayout) findViewById(R.id.rl_nickname);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        tv_nicName = (TextView) findViewById(R.id.tv_nickName);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
    }

    private void initData() {
        UserBean bean = null;
        bean = DBUtils.getInstance(this).getUserInfo(spUserName);
        if (bean == null) {
            bean = new UserBean();
            bean.userName = spUserName;
            bean.nickName = "问答精灵";
            bean.sex = "男";
            bean.signature = "问答精灵";
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
        setValue(bean);
    }

    private void setValue(UserBean bean) {
        tv_nicName.setText(bean.nickName);
        tv_user_name.setText(bean.userName);
        tv_sex.setText(bean.sex);
        tv_signature.setText(bean.signature);
    }

    private void setListener() {
        tv_back.setOnClickListener(this);
        rl_nickName.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
        rl_head.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                this.finish();
                break;
            case R.id.rl_head:
                chooseDialog();
                break;
            case R.id.rl_nickname:
                String name = tv_nicName.getText().toString();
                Bundle bdName = new Bundle();
                bdName.putString("content", name);
                bdName.putString("title", "昵称");
                bdName.putInt("flag", 1);
                enterActivityForResult(ChangeUserInfoActivity.class, CHANGE_NICKNAME, bdName);
                break;
            case R.id.rl_sex:
                String sex = tv_sex.getText().toString();
                sexDialog(sex);
                break;
            case R.id.rl_signature:
                String signature = tv_signature.getText().toString();
                Bundle bdSignature = new Bundle();
                bdSignature.putString("content", signature);
                bdSignature.putString("title", "签名");
                bdSignature.putInt("flag", 2);
                enterActivityForResult(ChangeUserInfoActivity.class, CHANGE_SIGNATURE, bdSignature);
                break;
            default:
                break;
        }
    }

    private void sexDialog(String sex) {
        int sexFlag = 0;
        if ("男".equals(sex)) {
            sexFlag = 0;
        } else if ("女".equals(sex)) {
            sexFlag = 1;
        }
        final String items[] = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("性别");
        builder.setSingleChoiceItems(items, sexFlag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(UserInfoActivity.this, items[which], Toast.LENGTH_SHORT).show();
                setSex(items[which]);
            }
        });
        builder.create().show();
    }

    private void setSex(String sex) {
        tv_sex.setText(sex);
        DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("sex", sex, spUserName);
    }

    private String new_info;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHANGE_NICKNAME:
                if (data != null) {
                    new_info = data.getStringExtra("nickName");
                    if (TextUtils.isEmpty(new_info) || new_info == null) {
                        return;
                    }
                    tv_nicName.setText(new_info);
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("nickName", new_info, spUserName);
                }
                break;
            case CHANGE_SIGNATURE:
                if (data != null) {
                    new_info = data.getStringExtra("signature");
                    if (TextUtils.isEmpty(new_info) || new_info == null) {
                        return;
                    }
                    tv_signature.setText(new_info);
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("signature", new_info, spUserName);
                }
                break;
            case ImageUtils.ACTIVITY_RESULT_CAMERA: // 拍照
                try {
                    if (resultCode == -1) {
                        imageUtils.cutImageByCamera();
                    } else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (imageUtils.picFile != null) {
                            imageUtils.picFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ImageUtils.ACTIVITY_RESULT_ALBUM:
                try {
                    if (resultCode == -1) {
                        Bitmap bm_icon = imageUtils.decodeBitmap();
                        if (bm_icon != null) {
                            iv_head_icon.setImageBitmap(bm_icon);
                        }
                    } else {
                        // 因为在无任何操作返回时，系统依然会创建一个文件，这里就是删除那个产生的文件
                        if (imageUtils.picFile != null) {
                            imageUtils.picFile.delete();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void chooseDialog() {
        imageUtils = new ImageUtils(this);
        new AlertDialog.Builder(this)//
                .setTitle("选择头像")//
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        imageUtils.byAlbum();
                    }
                })

                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {//判断是否存在SD卡
                            imageUtils.byCamera();
                        }

                    }
                }).show();

    }


    public void enterActivityForResult(Class<?> to,int requestCode,Bundle b){
        Intent i=new Intent(this,to);
        i.putExtras(b);
        startActivityForResult(i,requestCode);
    }
}
//5454666