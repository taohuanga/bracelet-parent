package os.bracelets.parents.app.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import aio.health2world.utils.MD5Util;
import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.R;
import os.bracelets.parents.common.MVPBaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/20.
 */

public class UpdatePwdActivity extends MVPBaseActivity<UpdatePwdContract.Presenter> implements UpdatePwdContract.View {

    private TitleBar titleBar;

    private EditText edOldPwd, edNewPwd, edRePwd;

    private Button btnOk;

    @Override
    protected UpdatePwdContract.Presenter getPresenter() {
        return new UpdatePwdPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        edOldPwd = findView(R.id.edOldPwd);
        edNewPwd = findView(R.id.edNewPwd);
        edRePwd = findView(R.id.edRePwd);
        btnOk = findView(R.id.btnOk);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "修改密码", titleBar);
    }


    @Override
    protected void initListener() {
        btnOk.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.btnOk:
                updatePwd();
                break;
        }
    }

    private void updatePwd() {
        String oldPwd = edOldPwd.getText().toString().trim();
        String newPwd = edNewPwd.getText().toString().trim();
        String rePwd = edRePwd.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.showShort("请输入原密码");
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtil.showShort("请输入新密码");
            return;
        }
        if (TextUtils.equals(oldPwd, newPwd)) {
            ToastUtil.showShort("新密码不能与原密码一样");
            return;
        }
        if (!TextUtils.equals(newPwd, rePwd)) {
            ToastUtil.showShort("两次输入的新密码不一致");
            return;
        }
        mPresenter.updatePwd(MD5Util.getMD5String(oldPwd), MD5Util.getMD5String(newPwd));
    }

    @Override
    public void updateSuccess() {
        finish();
    }
}
