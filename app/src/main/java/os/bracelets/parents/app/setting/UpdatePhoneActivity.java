package os.bracelets.parents.app.setting;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        }
        if (v.getId() == R.id.tvCode) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
