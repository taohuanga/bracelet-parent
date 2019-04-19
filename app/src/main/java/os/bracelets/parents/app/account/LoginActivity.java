package os.bracelets.parents.app.account;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.MD5Util;
import aio.health2world.utils.MatchUtil;
import aio.health2world.utils.SPUtils;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.AppConfig;
import os.bracelets.parents.R;
import os.bracelets.parents.app.main.MainActivity;
import os.bracelets.parents.bean.BaseInfo;
import os.bracelets.parents.common.MVPBaseActivity;
import rx.functions.Action1;

/**
 * Created by lishiyou on 2019/1/24.
 */

public class LoginActivity extends MVPBaseActivity<LoginContract.Presenter> implements LoginContract.View {

    //默认密码登陆
    private boolean isPwdLogin = false;

    private Button btnLogin, btnRegister, btnForgetPwd;

    private EditText edPhone, edCode, edAccount, edPwd;

    private View layoutMsg, layoutPwd, lineMsg, linePwd;

    private TextView tvMsgLogin, tvPwdLogin, tvCode;

    private View layoutPhone, layoutAccount;

    @Override
    protected LoginContract.Presenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

        btnLogin = findView(R.id.btnLogin);
        btnRegister = findView(R.id.btnRegister);
        btnForgetPwd = findView(R.id.btnForgetPwd);
        edPhone = findView(R.id.edPhone);
        edCode = findView(R.id.edCode);
        edAccount = findView(R.id.edAccount);
        edPwd = findView(R.id.edPwd);
        layoutMsg = findView(R.id.layoutMsg);
        layoutPwd = findView(R.id.layoutPwd);
        tvMsgLogin = findView(R.id.tvMsgLogin);
        tvPwdLogin = findView(R.id.tvPwdLogin);
        tvCode = findView(R.id.tvCode);
        lineMsg = findView(R.id.lineMsg);
        linePwd = findView(R.id.linePwd);
        layoutPhone = findView(R.id.layoutPhone);
        layoutAccount = findView(R.id.layoutAccount);
    }

    @Override
    protected void initData() {

//        JPushInterface.init(this);
//        JPushUtil.setJPushAlias(TagAliasOperatorHelper.ACTION_SET, DeviceUtil.getAndroidId(this));
//        Set<String> set = new HashSet<>();
//        set.add("android");
//        JPushUtil.setJPushTags(TagAliasOperatorHelper.ACTION_SET, set);
//        Logger.i("lsy", DeviceUtil.getAndroidId(this));

        edAccount.setSelection(edAccount.getText().length());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                            } else {
                                ToastUtil.showShort("相关权限被拒绝");
                            }
                        }
                    });
        }
    }

    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgetPwd.setOnClickListener(this);
        layoutMsg.setOnClickListener(this);
        layoutPwd.setOnClickListener(this);
    }

    @Override
    public void loginSuccess(BaseInfo info) {
        SPUtils.put(this, AppConfig.IS_LOGIN, true);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("info",info);
        startActivity(intent);
        finish();
    }

    @Override
    public void securityCodeSuccess() {
        tvCode.setEnabled(false);
        tvCode.setTextColor(mContext.getResources().getColor(R.color.black9));
        countDownTimer.start();
    }

    //计时器
    private CountDownTimer countDownTimer = new CountDownTimer(59000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvCode.setText(millisUntilFinished / 1000 + "秒后获取");
        }

        @Override
        public void onFinish() {
            tvCode.setEnabled(true);
            tvCode.setTextColor(mContext.getResources().getColor(R.color.blue));
            tvCode.setText("获取验证码");
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnForgetPwd:
                startActivity(new Intent(this, ResetPwdActivity.class));
                break;
            case R.id.layoutMsg:
            case R.id.layoutPwd:
                isPwdLogin = !isPwdLogin;
                changeView();
                break;
            case R.id.tvCode:
                getCode();
                break;

        }
    }

    private void changeView() {
        if (isPwdLogin) {
            layoutPhone.setVisibility(View.GONE);
            layoutAccount.setVisibility(View.VISIBLE);
            tvPwdLogin.setTextColor(getResources().getColor(R.color.blue));
            tvMsgLogin.setTextColor(getResources().getColor(R.color.black_normal));
            linePwd.setVisibility(View.VISIBLE);
            lineMsg.setVisibility(View.INVISIBLE);
        } else {
            layoutPhone.setVisibility(View.VISIBLE);
            layoutAccount.setVisibility(View.GONE);
            tvPwdLogin.setTextColor(getResources().getColor(R.color.black_normal));
            tvMsgLogin.setTextColor(getResources().getColor(R.color.blue));
            linePwd.setVisibility(View.INVISIBLE);
            lineMsg.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkInput(String account, String pwd) {
        if (TextUtils.isEmpty(account))
            return false;
        if (TextUtils.isEmpty(pwd))
            return false;
        return true;
    }

    private void login() {
        //密码登录
        if (isPwdLogin) {
            String account = edAccount.getText().toString().trim();
            String pwd = edPwd.getText().toString().trim();
            if (checkInput(account, pwd)) {
                mPresenter.login(account, MD5Util.getMD5String(pwd));
            } else {
                ToastUtil.showShort("账号密码校验错误");
            }
        } else {
            //短信登录
            String phone = edPhone.getText().toString().trim();
            String code = edCode.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.showShort("请输入手机号");
                return;
            }
            if (!MatchUtil.isPhoneLegal(phone)) {
                ToastUtil.showShort("手机号格式不正确");
                return;
            }
            if (TextUtils.isEmpty(code)) {
                ToastUtil.showShort("请输入验证码");
                return;
            }
            mPresenter.fastLogin(phone, code);
        }
    }

    /**
     * 获取短信验证码
     */
    private void getCode() {
        String phone = edPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort("请输入手机号");
            return;
        }
        if (!MatchUtil.isPhoneLegal(phone)) {
            ToastUtil.showShort("手机号格式不正确");
            return;
        }
        mPresenter.securityCode(2, phone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
