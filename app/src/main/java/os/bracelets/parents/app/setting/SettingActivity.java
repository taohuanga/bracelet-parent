package os.bracelets.parents.app.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.utils.AppManager;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.app.about.AboutActivity;
import os.bracelets.parents.app.about.FeedBackActivity;
import os.bracelets.parents.app.account.LoginActivity;
import os.bracelets.parents.app.personal.PersonalMsgActivity;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/18.
 */

public class SettingActivity extends MVPBaseActivity<SettingContract.Presenter> implements SettingContract.View {

    private TitleBar titleBar;

    private ImageView ivImage;

    private TextView tvName, tvTime;

    private Button btnLogout;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");

    private View layoutUpdatePwd, layoutSensorMsg, layoutUpdateMsg, layoutFeedBack,layoutAbout;

    @Override
    protected SettingContract.Presenter getPresenter() {
        return new SettingPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        ivImage = findView(R.id.ivImage);
        tvName = findView(R.id.tvName);
        tvTime = findView(R.id.tvTime);
        btnLogout = findView(R.id.btnLogout);
        layoutUpdatePwd = findView(R.id.layoutUpdatePwd);
        layoutSensorMsg = findView(R.id.layoutSensorMsg);
        layoutUpdateMsg = findView(R.id.layoutUpdateMsg);
        layoutFeedBack = findView(R.id.layoutFeedBack);
        layoutAbout = findView(R.id.layoutAbout);
    }

    @Override
    protected void initData() {
//        mPresenter.loadBaseInfo();
        TitleBarUtil.setAttr(this, "", "设置", titleBar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String nickName = (String) SPUtils.get(this, AppConfig.USER_NICK, "");
        String userImage = (String) SPUtils.get(this, AppConfig.USER_IMG, "");
        tvName.setText(nickName);
        tvTime.setText(format.format(new Date(System.currentTimeMillis())));
        Glide.with(this)
                .load(userImage)
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(ivImage);
    }

    @Override
    protected void initListener() {

        setOnClickListener(btnLogout);
        setOnClickListener(layoutUpdatePwd);
        setOnClickListener(layoutSensorMsg);
        setOnClickListener(layoutUpdateMsg);
        setOnClickListener(layoutFeedBack);
        setOnClickListener(layoutAbout);
        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnLogout:
                logout();
                break;
            case R.id.layoutUpdatePwd:
                startActivity(new Intent(this, UpdatePwdActivity.class));
                break;
            case R.id.layoutUpdateMsg:
                startActivity(new Intent(this, PersonalMsgActivity.class));
                break;
            case R.id.layoutSensorMsg:
                startActivity(new Intent(this, SensorMsgActivity.class));
                break;
            case R.id.layoutFeedBack:
                startActivity(new Intent(this, FeedBackActivity.class));
                break;
            case R.id.layoutAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }


    private void logout() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage("确认注销登录吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(SettingActivity.this, AppConfig.IS_LOGIN, false);
                        AppManager.getInstance().finishAllActivity();
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void loadInfoSuccess(BaseInfo info) {

    }
}
