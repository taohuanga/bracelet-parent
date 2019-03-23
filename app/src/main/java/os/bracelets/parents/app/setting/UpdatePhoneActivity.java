package os.bracelets.parents.app.setting;

import android.content.Intent;
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
 * Created by lishiyou on 2019/2/20.
 */

public class UpdatePhoneActivity extends MVPBaseActivity<UpdatePhoneContract.Presenter> implements UpdatePhoneContract.View {

    private TitleBar titleBar;

    private String phone;

    private EditText edOldPhone, edCode, edLoginPwd, edNewPhone;

    private Button btnSubmit;

    private TextView tvCode;


    @Override
    protected UpdatePhoneContract.Presenter getPresenter() {
        return new UpdatePhonePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_phone;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        edOldPhone = findView(R.id.edOldPhone);
        edCode = findView(R.id.edCode);
        edLoginPwd = findView(R.id.edLoginPwd);
        edNewPhone = findView(R.id.edNewPhone);
        tvCode = findView(R.id.tvCode);
        btnSubmit = findView(R.id.btnSubmit);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "修改手机号", titleBar);
        if (getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
        }
        if (!TextUtils.isEmpty(phone)) {
            edOldPhone.setText(phone);
            edOldPhone.setSelection(phone.length());
        }
    }


    @Override
    public void securityCodeSuccess() {
        tvCode.setEnabled(false);
        tvCode.setTextColor(mContext.getResources().getColor(R.color.black9));
        countDownTimer.start();
    }

    @Override
    public void updatePhoneSuccess(String phone) {
        Intent intent = new Intent();
        intent.putExtra("newPhone",phone);
        setResult(RESULT_OK,intent);
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
    protected void initListener() {
        btnSubmit.setOnClickListener(this);
        tvCode.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btnSubmit) {
            resetPhone();
        }
        if (v.getId() == R.id.tvCode) {
            getCode();
        }
    }



    /**
     * 获取短信验证码
     */
    private void getCode() {
        String phone = edOldPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort("请输入原手机号");
            return;
        }
        if (!MatchUtil.isPhoneLegal(phone)) {
            ToastUtil.showShort("原手机号格式不正确");
            return;
        }
        mPresenter.securityCode(3, phone);
    }

    /**
     * 重置手机号码
     */
    private void resetPhone(){
        String oldPhone = edOldPhone.getText().toString();
        String code = edCode.getText().toString();
        String loginPwd = edLoginPwd.getText().toString();
        String newPhone = edNewPhone.getText().toString();
        if (TextUtils.isEmpty(loginPwd)) {
            ToastUtil.showShort("请输入登录密码");
            return;
        }
        if (!MatchUtil.isPhoneLegal(newPhone)) {
            ToastUtil.showShort("新手机号格式不正确");
            return;
        }

        mPresenter.updatePhone(oldPhone,code,loginPwd,newPhone);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
