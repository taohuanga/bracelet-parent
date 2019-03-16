package os.bracelets.parents.app.account;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aio.health2world.utils.MatchUtil;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.R;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/21.
 */

public class ResetPwdActivity extends MVPBaseActivity<ResetPwdContract.Presenter> implements ResetPwdContract.View {

    private TitleBar titleBar;

    private EditText edAccount, edCode, edPwd, edRePwd;

    private Button btnSubmit;

    private TextView tvCode;

    @Override
    protected ResetPwdContract.Presenter getPresenter() {
        return new ResetPwdPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        edAccount = findView(R.id.edAccount);
        edCode = findView(R.id.edCode);
        edPwd = findView(R.id.edPwd);
        edRePwd = findView(R.id.edRePwd);
        tvCode = findView(R.id.tvCode);
        btnSubmit = findView(R.id.btnSubmit);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "找回密码", titleBar);
    }


    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void codeSuccess() {
        tvCode.setEnabled(false);
        tvCode.setTextColor(mContext.getResources().getColor(R.color.black9));
        countDownTimer.start();
    }

    @Override
    public void resetPwdSuccess() {
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvCode:
                String phone1 = edAccount.getText().toString().trim();
                if (TextUtils.isEmpty(phone1)) {
                    ToastUtil.showShort("请输入手机号");
                    return;
                }
                if (!MatchUtil.isPhoneLegal(phone1)) {
                    ToastUtil.showShort("手机号格式不正确");
                    return;
                }
                mPresenter.code(3, phone1);
                break;
            case R.id.btnSubmit:
                String phone = edAccount.getText().toString().trim();
                String code = edCode.getText().toString().trim();
                String pwd = edPwd.getText().toString().trim();
                String rePwd = edRePwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请输入手机号");
                    return;
                }
                if (!MatchUtil.isPhoneLegal(phone)) {
                    ToastUtil.showShort("'手机号格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort("请输入手机验证码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showShort("请输入密码");
                    return;
                }
                if (!TextUtils.equals(pwd, rePwd)) {
                    ToastUtil.showShort("两次密码不一致");
                    return;
                }
                mPresenter.resetPwd(phone, pwd, code);
                break;
        }
    }
}
