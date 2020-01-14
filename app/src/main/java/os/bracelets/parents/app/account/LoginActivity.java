package os.bracelets.parents.app.account;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aio.health2world.rx.rxpermissions.RxPermissions;
import aio.health2world.utils.MD5Util;
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

    private TextView tvMsgLogin, tvPwdLogin, tvCode,tvArea;

    private View layoutPhone, layoutAccount;

    private String[] codeArray = new String[]{"86","81"};
    private String[] areaArray;
    private String areaCode = "86";


    @Override
    protected LoginContract.Presenter getPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
        tvArea = findView(R.id.tvArea);
        lineMsg = findView(R.id.lineMsg);
        linePwd = findView(R.id.linePwd);
        layoutPhone = findView(R.id.layoutPhone);
        layoutAccount = findView(R.id.layoutAccount);
    }

    @Override
    protected void initData() {

        edAccount.setSelection(edAccount.getText().length());

        edPhone.setText((String) SPUtils.get(this, AppConfig.USER_PHONE, ""));
        edPhone.setSelection(edPhone.getText().length());

        areaArray = getResources().getStringArray(R.array.area_code);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                            } else {
                                ToastUtil.showShort(getString(R.string.permission_denied));
                            }
                        }
                    });
        }
        if (getIntent().hasExtra("flag")) {
            boolean flag = getIntent().getBooleanExtra("flag", false);
            if (flag) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.account_login_other_device))
                        .setPositiveButton(getString(R.string.confirm_quit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnForgetPwd.setOnClickListener(this);
        layoutMsg.setOnClickListener(this);
        layoutPwd.setOnClickListener(this);
    }

    @Override
    public void loginSuccess(BaseInfo info) {
        SPUtils.put(this, AppConfig.IS_LOGIN, true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("info", info);
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
            tvCode.setText(getString(R.string.verification_code));
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
                startActivity(new Intent(this, AgreementActivity.class));
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
            case R.id.tvArea:
                //选择区号
                new AlertDialog.Builder(this)
                        .setItems(areaArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                areaCode = codeArray[which];
                                tvArea.setText(areaCode);
                            }
                        })
                        .create()
                        .show();
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
                ToastUtil.showShort(getString(R.string.account_or_password_incorrect));
            }
        } else {
            //短信登录
            String phone = edPhone.getText().toString().trim();
            String code = edCode.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.showShort(getString(R.string.input_phone));
                return;
            }
            if(phone.length()!=11){
                ToastUtil.showShort(getString(R.string.phone_incorrect));
                return;
            }
//            if (!MatchUtil.isPhoneLegal(phone)) {
//                ToastUtil.showShort(getString(R.string.phone_incorrect));
//                return;
//            }
            if (TextUtils.isEmpty(code)) {
                ToastUtil.showShort(getString(R.string.input_code));
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
            ToastUtil.showShort(getString(R.string.input_phone));
            return;
        }
        if(phone.length()!=11){
            ToastUtil.showShort(getString(R.string.phone_incorrect));
            return;
        }
//        if (!MatchUtil.isPhoneLegal(phone)) {
//            ToastUtil.showShort(getString(R.string.phone_incorrect));
//            return;
//        }
        mPresenter.securityCode(2, phone,areaCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
