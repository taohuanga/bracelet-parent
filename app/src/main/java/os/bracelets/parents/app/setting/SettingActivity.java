package os.bracelets.parents.app.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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
import os.bracelets.parents.app.about.HelpActivity;
import os.bracelets.parents.app.account.LoginActivity;
import os.bracelets.parents.app.personal.IntegralDetailActivity;
import os.bracelets.parents.app.personal.PersonalMsgActivity;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.bean.UserInfo;
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

    private View layoutUpdatePwd, layoutSensorMsg,layoutDeviceBind, layoutUpdateMsg, layoutFeedBack,
            layoutAbout,layoutDisplay,layoutHelp;

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
        layoutDeviceBind = findView(R.id.layoutDeviceBind);
        layoutAbout = findView(R.id.layoutAbout);
        layoutDisplay = findView(R.id.layoutDisplay);
        layoutHelp = findView(R.id.layoutHelp);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", getString(R.string.setting), titleBar);
        mPresenter.loadBaseInfo();
//        mPresenter.walletInfo();
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
        setOnClickListener(layoutDeviceBind);
        setOnClickListener(layoutDisplay);
        setOnClickListener(layoutHelp);
        titleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        titleBar.addAction(new TitleBar.ImageAction(R.mipmap.icon_msg) {
//            @Override
//            public void performAction(View view) {
//                startActivity(new Intent(SettingActivity.this,SystemMsgActivity.class));
//            }
//        });
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
            case R.id.layoutDeviceBind:
                startActivity(new Intent(this,DeviceBindActivity.class));
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
            case R.id.layoutDisplay:
                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.layoutHelp:
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;
        }
    }


    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.sure_to_log_out))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getInstance().logout(false);
                        logoutHx();
                    }
                })
                .create()
                .show();
    }


//    @Override
//    public void loadWalletInfoSuccess(WalletInfo info) {
//        tvIntegral.setVisibility(View.VISIBLE);
//        tvIntegral.setText(info.getIntegral() + "积分");
//    }

    @Override
    public void loadInfoSuccess(UserInfo info) {
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
