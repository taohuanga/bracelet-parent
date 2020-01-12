package os.bracelets.parents.app.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aio.health2world.utils.MatchUtil;
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
    public static final String TYPE = "type";

    private TitleBar titleBar;

    private EditText editText;

    private String title = "";

    private int type = -1;

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
        if (getIntent().hasExtra(TYPE))
            type = getIntent().getIntExtra(TYPE, -1);

        if (type == PersonalMsgActivity.ITEM_HEIGHT || type == PersonalMsgActivity.ITEM_WEIGHT
                || type == PersonalMsgActivity.ITEM_PHONE) {
            editText.setFilters(new InputFilter[]{numberFilter, new InputFilter.LengthFilter(5)});
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        titleBar.addAction(new TitleBar.TextAction(getString(R.string.sure)) {
            @Override
            public void performAction(View view) {
                save();
            }
        });
    }

    private void save() {
        String msg = editText.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            ToastUtil.showShort(getString(R.string.input_content));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("data", msg);
        setResult(RESULT_OK, intent);
        finish();
    }


    private InputFilter numberFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern p = Pattern.compile("[.0-9]");
            Matcher m = p.matcher(source.toString());
            if (!m.matches()) return "";
            return null;
        }
    };


}
