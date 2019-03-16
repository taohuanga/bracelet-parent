package os.bracelets.parents.app.setting;

import android.view.View;

import os.bracelets.parents.R;
import os.bracelets.parents.common.BaseActivity;
import os.bracelets.parents.utils.TitleBarUtil;
import os.bracelets.parents.view.TitleBar;

/**
 * Created by lishiyou on 2019/3/16.
 */

public class SensorMsgActivity extends BaseActivity {

    private TitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sensor_msg;
    }

    @Override
    protected void initView() {
        titleBar = findView(R.id.titleBar);
    }

    @Override
    protected void initData() {
        TitleBarUtil.setAttr(this, "", "G-SENSOR检测", titleBar);
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
