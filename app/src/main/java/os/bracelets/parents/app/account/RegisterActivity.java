package os.bracelets.parents.app.account;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aio.health2world.utils.MD5Util;
import aio.health2world.utils.MatchUtil;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.R;
import os.bracelets.parents.common.MVPBaseActivity;

/**
 * Created by lishiyou on 2019/1/27.
 */

public class RegisterActivity extends MVPBaseActivity<RegisterContract.Presenter> implements RegisterContract.View {

    private EditText edAccount, edCode, edPwd;

    private TextView tvCode;

    private Button btnRegister;


    @Override
    protected RegisterContract.Presenter getPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        edAccount = findView(R.id.edAccount);
        edCode = findView(R.id.edCode);
        edPwd = findView(R.id.edPwd);
        tvCode = findView(R.id.tvCode);
        btnRegister = findView(R.id.btnRegister);
    }

    @Override
    protected void initData() {

    }


    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvCode:
                getCode();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    /**
     * 获取手机验证码
     */
    private void getCode() {
        String phone = edAccount.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort("请输入手机号");
            return;
        }
        if (!MatchUtil.isPhoneLegal(phone)) {
            ToastUtil.showShort("手机号格式不正确");
            return;
        }
        mPresenter.code(1, phone);
    }

    private void register() {
        String phone = edAccount.getText().toString().trim();
        String code = edCode.getText().toString().trim();
        String pwd = edPwd.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showShort("请输入短信验证码");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShort("请输入密码");
            return;
        }
        mPresenter.register(phone, code, "", MD5Util.getMD5String(pwd));
    }

    /**
     * 验证码获取成功
     */
    @Override
    public void codeSuccess() {
        tvCode.setEnabled(false);
        tvCode.setTextColor(mContext.getResources().getColor(R.color.black9));
        countDownTimer.start();
    }

    @Override
    public void registerSuccess() {
        finish();
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
}
