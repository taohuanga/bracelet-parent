package os.bracelets.parents.app.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import aio.health2world.utils.ToastUtil;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/3/14.
 */

public class InputMsgActivity extends BaseActivity {

    public static final String KEY = "msg";

    private TitleBar titleBar;

    private EditText editText;

    private String title = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_msg;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        TitleBarUtil.setAttr(this, "", "", titleBar);
        editText = findView(R.id.editText);
    }

    @Override
    protected void initData() {
        if (getIntent().hasExtra(KEY)) {
            title = getIntent().getStringExtra(KEY);
            titleBar.setTitle(title);
        }
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.addAction(new TitleBar.TextAction("确定") {
            @Override
            public void performAction(View view) {
                save();
            }
        });
    }

    private void save() {
        String msg = editText.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.showShort("请输入内容");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("data", msg);
        setResult(RESULT_OK, intent);
        finish();
    }
}
