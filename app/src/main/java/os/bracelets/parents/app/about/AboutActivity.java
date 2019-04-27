package os.bracelets.parents.app.about;

import android.view.View;
import android.widget.TextView;

import aio.health2world.http.HttpResult;
import aio.health2world.utils.AppUtils;
import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.http.ApiRequest;
import os.bracelets.parents.http.HttpSubscriber;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/2/20.
 */

public class AboutActivity extends BaseActivity {

    private TitleBar titleBar;

    private TextView tvTitle,tvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
        tvContent = findView(R.id.tvContent);
        tvTitle = findView(R.id.tvTitle);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "关于我们", titleBar);
        tvTitle.setText("衣带保父母端 V"+ AppUtils.getAppVersionName(this));
        tvContent.setText("\r\r\r\r\r\r\r\r" + getResources().getString(R.string.about_content));
    }

    @Override
    protected void initListener() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
