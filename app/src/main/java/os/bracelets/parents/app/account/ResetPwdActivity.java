package os.bracelets.parents.app.account;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
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
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/21.
 */

public class ResetPwdActivity extends MVPBaseActivity<ResetPwdContract.Presenter> implements ResetPwdContract.View {

    private TitleBar titleBar;

    private EditText edAccount, edCode, edPwd, edRePwd;

    private Button btnSubmit;

    private TextView tvCode,tvArea;

    private String[] codeArray = new String[]{"+86", "+1", "+81"};
    private String[] areaArray;
    private String areaCode = "+86";

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
        tvArea = findView(R.id.tvArea);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", getString(R.string.find_password), titleBar);
        areaArray = getResources().getStringArray(R.array.area_code);
    }


    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void codeSuccess() {
        ToastUtil.showShort(getString(R.string.send_success));
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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tvCode:
                String phone1 = edAccount.getText().toString().trim();
                if (TextUtils.isEmpty(phone1)) {
                    ToastUtil.showShort(getString(R.string.input_phone));
                    return;
                }
                if (!MatchUtil.isPhoneLegal(phone1)) {
                    ToastUtil.showShort(getString(R.string.phone_incorrect));
                    return;
                }
                mPresenter.code(3, phone1,areaCode);
                break;
            case R.id.btnSubmit:
                String phone = edAccount.getText().toString().trim();
                String code = edCode.getText().toString().trim();
                String pwd = edPwd.getText().toString().trim();
                String rePwd = edRePwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(getString(R.string.input_phone));
                    return;
                }
                if (!MatchUtil.isPhoneLegal(phone)) {
                    ToastUtil.showShort(getString(R.string.phone_incorrect));
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(getString(R.string.input_code));
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showShort(getString(R.string.input_password));
                    return;
                }
                if (!TextUtils.equals(pwd, rePwd)) {
                    ToastUtil.showShort(getString(R.string.password_not_match));
                    return;
                }
                mPresenter.resetPwd(phone, MD5Util.getMD5String(pwd), code);
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
}
