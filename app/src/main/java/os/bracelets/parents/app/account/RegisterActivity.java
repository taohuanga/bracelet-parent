package os.bracelets.parents.app.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private TextView tvCode, tvArea;

    private Button btnRegister;

    private LinearLayout llAgreement;

    private String[] codeArray = new String[]{"+86", "+1", "+81"};
    private String[] areaArray;
    private String areaCode = "+86";

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
        tvArea = findView(R.id.tvArea);
        btnRegister = findView(R.id.btnRegister);
        llAgreement = findView(R.id.llAgreement);
    }

    @Override
    protected void initData() {
        areaArray = getResources().getStringArray(R.array.area_code);
    }


    @Override
    protected void initListener() {
        tvCode.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        llAgreement.setOnClickListener(this);
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
            case R.id.llAgreement:
                startActivity(new Intent(this, AgreementActivity.class));
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

    /**
     * 获取手机验证码
     */
    private void getCode() {
        String phone = edAccount.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort(getString(R.string.input_phone));
            return;
        }
        if (!MatchUtil.isPhoneLegal(phone)) {
            ToastUtil.showShort(getString(R.string.phone_incorrect));
            return;
        }
        mPresenter.code(1, phone, areaCode);
    }

    private void register() {
        String phone = edAccount.getText().toString().trim();
        String code = edCode.getText().toString().trim();
        String pwd = edPwd.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showShort(getString(R.string.input_code));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.showShort(getString(R.string.input_password));
            return;
        }
        mPresenter.register(phone, code, areaCode, MD5Util.getMD5String(pwd));
    }

    /**
     * 验证码获取成功
     */
    @Override
    public void codeSuccess() {
        ToastUtil.showShort(getString(R.string.send_success));
        tvCode.setEnabled(false);
        tvCode.setTextColor(mContext.getResources().getColor(R.color.black9));
        countDownTimer.start();
    }

    @Override
    public void registerSuccess(final String phone) {
        ToastUtil.showShort(getString(R.string.register_success));
        //注册环信账号
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    EMClient.getInstance().createAccount(phone, phone);//同步方法
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        finish();
    }

    //计时器
    private CountDownTimer countDownTimer = new CountDownTimer(59000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            tvCode.setText(millisUntilFinished / 1000 + "秒后获取");
            tvCode.setText(String.format(getString(R.string.code_later), millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            tvCode.setEnabled(true);
            tvCode.setTextColor(mContext.getResources().getColor(R.color.blue));
            tvCode.setText(getString(R.string.verification_code));
        }
    };
}
