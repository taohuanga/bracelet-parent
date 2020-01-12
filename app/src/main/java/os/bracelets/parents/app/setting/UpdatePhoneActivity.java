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
        TitleBarUtil.setAttr(this, "", getString(R.string.update_phone), titleBar);
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
//            tvCode.setText(millisUntilFinished / 1000 + "秒后获取");
            tvCode.setText(String.format(getString(R.string.code_later),millisUntilFinished/1000));
        }

        @Override
        public void onFinish() {
            tvCode.setEnabled(true);
            tvCode.setTextColor(mContext.getResources().getColor(R.color.blue));
            tvCode.setText(getString(R.string.verification_code));
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
            ToastUtil.showShort(getString(R.string.input_password));
            return;
        }
        if (!MatchUtil.isPhoneLegal(phone)) {
            ToastUtil.showShort(getString(R.string.phone_incorrect));
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
            ToastUtil.showShort(getString(R.string.input_password));
            return;
        }
        if (!MatchUtil.isPhoneLegal(newPhone)) {
            ToastUtil.showShort(getString(R.string.phone_incorrect));
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
