package os.bracelets.parents.app.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import aio.health2world.glide_transformations.CropCircleTransformation;
import aio.health2world.utils.AppManager;
import aio.health2world.utils.SPUtils;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.MyApplication;
import os.bracelets.parents.R;
import os.bracelets.parents.app.about.AboutActivity;
import os.bracelets.parents.app.about.FeedBackActivity;
import os.bracelets.parents.app.account.LoginActivity;
import os.bracelets.parents.app.personal.IntegralDetailActivity;
import os.bracelets.parents.app.personal.PersonalMsgActivity;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.WalletInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/18.
 */

public class SettingActivity extends MVPBaseActivity<SettingContract.Presenter> implements SettingContract.View {

    private TitleBar titleBar;

    private ImageView ivImage;

    private TextView tvName, tvIntegral;

    private Button btnLogout;

    private View layoutUpdatePwd, layoutSensorMsg, layoutUpdateMsg, layoutFeedBack, layoutAbout;

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
        tvIntegral = findView(R.id.tvIntegral);
        btnLogout = findView(R.id.btnLogout);
        layoutUpdatePwd = findView(R.id.layoutUpdatePwd);
        layoutSensorMsg = findView(R.id.layoutSensorMsg);
        layoutUpdateMsg = findView(R.id.layoutUpdateMsg);
        layoutFeedBack = findView(R.id.layoutFeedBack);
        layoutAbout = findView(R.id.layoutAbout);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "设置", titleBar);
        mPresenter.loadBaseInfo();
        mPresenter.walletInfo();
    }


    @Override
    protected void initListener() {

        setOnClickListener(btnLogout);
        setOnClickListener(layoutUpdatePwd);
        setOnClickListener(layoutSensorMsg);
        setOnClickListener(layoutUpdateMsg);
        setOnClickListener(layoutFeedBack);
        setOnClickListener(layoutAbout);
        setOnClickListener(tvIntegral);
        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == 0x01)
            mPresenter.loadBaseInfo();
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
                startActivityForResult(new Intent(this, PersonalMsgActivity.class), 0x01);
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
            case R.id.tvIntegral:
                startActivity(new Intent(this, IntegralDetailActivity.class));
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
//                        SPUtils.put(SettingActivity.this, AppConfig.IS_LOGIN, false);
//                        AppManager.getInstance().finishAllActivity();
//                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
//                        finish();
                        MyApplication.getInstance().logout();
                        logoutHx();
                    }
                })
                .create()
                .show();
    }


    @Override
    public void loadWalletInfoSuccess(WalletInfo info) {
        tvIntegral.setText("积分" + info.getIntegral());
    }

    @Override
    public void loadInfoSuccess(BaseInfo info) {
        if (!TextUtils.isEmpty(info.getName())) {
            tvName.setText(info.getNickName() + "(" + info.getName() + ")");
        } else {
            tvName.setText(info.getNickName());
        }
        Glide.with(this)
                .load(info.getPortrait())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_default_portrait)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(ivImage);
    }

    private void logoutHx() {
//        EMClient.getInstance()
//                .logout(true, new EMCallBack() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//
//                    @Override
//                    public void onProgress(int i, String s) {
//
//                    }
//                });
    }
}
